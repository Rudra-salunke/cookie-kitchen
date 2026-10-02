package dao;
import model.Product ;
import util.DBConnection;
import java.sql.* ;
import java.util.*;
public class ProductDAO {
    public List<Product> getALLProducts() throws SQLException, ClassNotFoundException {
        List <Product> a=new ArrayList<>();
        try(Connection con= DBConnection.getConnection();
            PreparedStatement ps=con.prepareStatement("SELECT * FROM Products");
            ResultSet rs = ps.executeQuery();
        ) {
            while (rs.next()) {
                a.add(mapRowToProduct(rs));
            }
            return a;
        }
    }
    public List<Product> getALLAvailableProducts() throws SQLException, ClassNotFoundException {
        List <Product> a=new ArrayList<>();
        try(Connection con= DBConnection.getConnection();
            PreparedStatement ps=con.prepareStatement("SELECT * FROM Products where is_available=1");
            ResultSet rs = ps.executeQuery();
        ) {
            while (rs.next()) {
                a.add(mapRowToProduct(rs));
            }
            return a;
        }
    }
    public List<Product> getProductsByCategory(int categoryId) throws SQLException, ClassNotFoundException {
        List <Product> a=new ArrayList<>();
        try(Connection con= DBConnection.getConnection();
            PreparedStatement ps=con.prepareStatement("SELECT * FROM products where category_id= ? and is_available=1")
        ){
            ps.setInt(1,categoryId);
            try(ResultSet rs = ps.executeQuery();) {
                while (rs.next()) {
                    a.add(mapRowToProduct(rs));
                }
                return a;
            }
        }
    }
    public List<Product> searchProduct(String keyword) throws SQLException, ClassNotFoundException {
        List <Product> a=new ArrayList<>();
        try(Connection con= DBConnection.getConnection();
            PreparedStatement ps=con.prepareStatement("SELECT * FROM products where name like ? and is_available=1 order by name")){
            ps.setString(1, "%" + keyword + "%");
            try(ResultSet rs = ps.executeQuery();){
                while (rs.next()) {
                    a.add(mapRowToProduct(rs));
                }
                return a;
            }
        }
    }
    public Product getProductById(int id) throws SQLException, ClassNotFoundException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM products WHERE id = ? and is_available=1 ")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRowToProduct(rs) : null;
            }
        }
    }
    private Product mapRowToProduct(ResultSet rs) throws SQLException {
        return new Product(
                rs.getInt("id"),
                rs.getInt("category_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDouble("price"),
                rs.getString("image_url"),
                rs.getInt("stock"),
                rs.getBoolean("is_available")
        );
    }
}
