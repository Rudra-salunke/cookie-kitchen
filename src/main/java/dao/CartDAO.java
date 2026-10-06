package dao;

import model.CartItem;
import model.Product;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class CartDAO {
    public static final int OK=0;
    public static final int NOT_FOUND=1;
    public static final int UNAVAILABLE=2;
    public static final int LIMIT_EXCEEDED=3;
    public static final int MAX_PER_ITEM=20;
    public List<CartItem> getCartItems(int userId)
            throws SQLException, ClassNotFoundException {
        List <CartItem> items=new ArrayList<>();
        try(Connection con= DBConnection.getConnection();
            PreparedStatement ps=con.prepareStatement("SELECT ci.id,ci.product_id,ci.quantity,p.name,p.image_url,p.price,p.stock,p.is_available FROM cart_items as ci join products as p on ci.product_id=p.id where ci.user_id=? ORDER BY ci.id")
        ) {
            ps.setInt(1, userId);
            try(ResultSet rs = ps.executeQuery()){
                while (rs.next()) {
                    items.add(mapRowToCartItem(rs));
                }
                return items;
            }
        }
    }
    public int addToCart(int userId,int productId,int quantity)throws SQLException, ClassNotFoundException {
        String insertSql = "INSERT INTO cart_items (user_id,product_id,quantity) VALUES ( ?, ?, ?) "+" ON DUPLICATE KEY UPDATE quantity = quantity + VALUES(quantity)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(insertSql)
        ){
            ProductDAO p=new ProductDAO();
            int current_quantity;
            Product q=p.getProductById(productId);
            if(q==null){
                return NOT_FOUND;
            }
            if (!q.isAvailable()) {
                return UNAVAILABLE;
            }
            current_quantity=getCurrentQuantity(userId, productId);
            if(quantity+current_quantity>Math.min(MAX_PER_ITEM, q.getStock())){
                return LIMIT_EXCEEDED;
            }
            ps.setInt(1,userId);
            ps.setInt(2,productId);
            ps.setInt(3,quantity);
            ps.executeUpdate();
            return OK;
        }
    }
    public int clearCart(int userId)
            throws SQLException, ClassNotFoundException{
        try(Connection con= DBConnection.getConnection();
            PreparedStatement ps=con.prepareStatement("DELETE FROM cart_items WHERE user_id = ?")
        ){
            ps.setInt(1,userId);
            int rows=ps.executeUpdate();
            return rows;
        }
    }
    public int getItemCount(int userId)
            throws SQLException, ClassNotFoundException{
        try(Connection con= DBConnection.getConnection();
            PreparedStatement ps=con.prepareStatement("SELECT COALESCE(SUM(quantity), 0) FROM cart_items WHERE user_id = ?")
        ){
            ps.setInt(1,userId);
            try(ResultSet rs = ps.executeQuery();){
                if(rs.next()){
                    return rs.getInt(1);
                }
                return 0;
            }
        }
    }
    public int updateQuantity(int userId, int cartItemId, int newQuantity)
            throws SQLException, ClassNotFoundException {
        int productId = getProductIdForCartItem(userId, cartItemId);
        if (productId == -1) {
            return NOT_FOUND;
        }
        Product product = new ProductDAO().getProductById(productId);
        if (product == null) {
            return NOT_FOUND;
        }
        if (!product.isAvailable()) {
            return UNAVAILABLE;
        }
        if (newQuantity > Math.min(MAX_PER_ITEM, product.getStock())) {
            return LIMIT_EXCEEDED;
        }
        String sql = "UPDATE cart_items SET quantity = ? WHERE id = ? AND user_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, newQuantity);
            ps.setInt(2, cartItemId);
            ps.setInt(3, userId);
            ps.executeUpdate();
            return OK;
        }
    }
    public boolean removeItem(int userId, int cartItemId)
            throws SQLException, ClassNotFoundException {
        String sql = "DELETE FROM cart_items WHERE id = ? AND user_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cartItemId);
            ps.setInt(2, userId);
            int rows = ps.executeUpdate();
            return rows > 0;
        }
    }
    private int getCurrentQuantity(int userId, int productId) throws SQLException, ClassNotFoundException {
        String sql = "SELECT quantity FROM cart_items WHERE user_id = ? AND product_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("quantity");
                }
                return 0;
            }
        }
    }
    private int getProductIdForCartItem(int userId, int cartItemId)
            throws SQLException, ClassNotFoundException {
        String sql = "SELECT product_id FROM cart_items WHERE id = ? AND user_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cartItemId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("product_id");
                }
                return -1; // no such row for this user
            }
        }
    }
    private CartItem mapRowToCartItem(ResultSet rs) throws SQLException {
        return new CartItem(
                rs.getInt("id"),
                rs.getInt("product_id"),
                rs.getInt("quantity"),
                rs.getString("name"),
                rs.getString("image_url"),
                rs.getBigDecimal("price"),
                rs.getInt("stock"),
                rs.getBoolean("is_available"));
    }
}
