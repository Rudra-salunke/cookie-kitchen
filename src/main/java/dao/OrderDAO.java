package dao;

import exception.OrderException;
import model.*;
import util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

public class OrderDAO {

    private final CartDAO cartDAO = new CartDAO();   // (used inside the transaction via its Connection overloads)

    // ===================== PUBLIC: the one big transaction =====================
    public int placeOrder(int userId, Address address, Order.PaymentMethod method)
            throws SQLException, ClassNotFoundException, OrderException {

        // (open ONE connection with try-with-resources; every step below uses this same con)
        try (Connection con= DBConnection.getConnection()){
            con.setAutoCommit(false);
            try {
                List<CartItem> items = cartDAO.getCartItems(con, userId);
                if (items.isEmpty()){
                    throw new OrderException("Your cart is empty");
                }
                items.sort(Comparator.comparingInt(CartItem::getProductId));
                BigDecimal total = BigDecimal.ZERO;
                for(CartItem item:items){
                    if (!item.isAvailable()){
                        throw new OrderException(item.getName()+" is currently unavailable");
                    }
                    total = total.add(item.getSubtotal());
                }
                address.setUser_id(userId);
                int addressId=insertAddress(con,address);
                for(CartItem item:items){
                    decreaseStock(con,item.getProductId(),item.getQuantity(), item.getName());
                }
                int orderId = insertOrder(con, new Order(userId, addressId, total, method));
                for(CartItem item:items){
                    insertOrderItem(con, orderId, item);
                }
                cartDAO.clearCart(con, userId);
                con.commit();
                return orderId;
            } catch (SQLException | OrderException | RuntimeException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    // ===================== PRIVATE helpers: all take the Connection =====================

    private int insertAddress(Connection con, Address a) throws SQLException {
        String sql = "INSERT INTO addresses (user_id, line1, line2, city, pincode, is_default) VALUES (?,?,?,?,?,0)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, a.getUser_id());
            ps.setString(2, a.getLine1());
            ps.setString(3, a.getLine2());
            ps.setString(4, a.getCity());
            ps.setString(5, a.getPincode());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
                throw new SQLException("Address insert returned no id");
            }
        }
    }

    private int insertOrder(Connection con, Order o) throws SQLException {
        String sql = "INSERT INTO orders (user_id, address_id, total, status, payment_method) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, o.getUserId());
            ps.setInt(2, o.getAddressId());
            ps.setBigDecimal(3, o.getTotal());
            ps.setString(4, o.getStatus().name());
            ps.setString(5, o.getPaymentMethod().name());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
                throw new SQLException("Order insert returned no id");
            }
        }
    }

    private void decreaseStock(Connection con, int productId, int qty, String productName)
            throws SQLException, OrderException {
        String insertSql="UPDATE products SET stock = stock - ? WHERE id = ? AND stock >= ?";
        try (PreparedStatement ps = con.prepareStatement(insertSql)){
            ps.setInt(1,qty);
            ps.setInt(2,productId);
            ps.setInt(3,qty);
            int rows=ps.executeUpdate();
            if (rows==0){
                throw new OrderException(productName+" is out of stock");
            }
        }
    }
    private void insertOrderItem(Connection con, int orderId, CartItem item) throws SQLException {
        String insertSql="INSERT INTO order_items (order_id, product_id, quantity, price_at_purchase) VALUES (?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(insertSql)){
            ps.setInt(1,orderId);
            ps.setInt(2,item.getProductId());
            ps.setInt(3,item.getQuantity());
            ps.setBigDecimal(4,item.getUnitPrice());
            ps.executeUpdate();
        }
    }
    private Order mapRowToOrder(ResultSet rs) throws SQLException {
        return new Order(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getInt("address_id"),
                rs.getBigDecimal("total"),
                Order.Status.valueOf(rs.getString("status")),
                Order.PaymentMethod.valueOf(rs.getString("payment_method")),
                rs.getTimestamp("created_at")
        );
    }
    private OrderItem mapRowToOrderItem(ResultSet rs) throws SQLException{
        return new OrderItem(
                rs.getInt("id"),
                rs.getInt("order_id"),
                rs.getInt("product_id"),
                rs.getInt("quantity"),
                rs.getBigDecimal("price_at_purchase"),
                rs.getString("name"),
                rs.getString("image_url")
        );
    }

    // ===================== PUBLIC: read methods, written after placeOrder works =====================

    public Order getOrderById(int orderId, int userId) throws SQLException, ClassNotFoundException {
        try(Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT id, user_id, address_id, total, status, payment_method, created_at  " +
                    "FROM orders WHERE id = ? AND user_id = ?")) {
            ps.setInt(1,orderId);
            ps.setInt(2,userId);
            try (ResultSet rs =ps.executeQuery()){
                if (rs.next()){
                    return mapRowToOrder(rs);
                }
                return null;
            }
        }
    }

    public List<Order> getOrdersByUser(int userId) throws SQLException, ClassNotFoundException {
        // (WHERE user_id = ? ORDER BY created_at DESC, id DESC)
        List<Order> o=new ArrayList<>();
        try(Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM orders WHERE user_id = ? ORDER BY created_at DESC, id DESC")) {
            ps.setInt(1,userId);
            try (ResultSet rs =ps.executeQuery()){
                while(rs.next()){
                    o.add(mapRowToOrder(rs));
                }
                return o;
            }
        }
    }

    public List<OrderItem> getItemsByOrder(int orderId) throws SQLException, ClassNotFoundException {
        // (JOIN products to get name and image_url; fills OrderItem.name and imageUrl)
        List<OrderItem> o=new ArrayList<>();
        try(Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT oi.id, oi.order_id, oi.product_id, oi.quantity, oi.price_at_purchase, p.name, p.image_url " +
                    "FROM order_items oi JOIN products p ON oi.product_id = p.id WHERE oi.order_id = ? ORDER BY oi.id")) {
            ps.setInt(1,orderId);
            try (ResultSet rs =ps.executeQuery()){
                while(rs.next()){
                    o.add(mapRowToOrderItem(rs));
                }
                return o;
            }
        }
    }

    public Address getAddressById(int addressId) throws SQLException, ClassNotFoundException {
        // (for the success page; the order's address_id came from a row you just validated)
        try(Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT id, user_id, line1, line2, city, pincode, is_default FROM addresses WHERE id = ?")) {
            ps.setInt(1,addressId);
            try (ResultSet rs =ps.executeQuery()){
                if (rs.next()){
                    return new Address(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("line1"),
                            rs.getString("line2"),
                            rs.getString("city"),
                            rs.getString("pincode"),
                            rs.getBoolean("is_default")
                    );
                }
                return null;
            }
        }
    }
}