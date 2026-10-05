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
    public List<CartItem> getCartItems(int userId) throws SQLException, ClassNotFoundException {
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
            } else if (!q.isAvailable()) {
                return UNAVAILABLE;
            } else{
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
