package dao;
import model.Product ;
import util.DBConnection;
import java.sql.Statement;

import java.math.BigDecimal;
import java.sql.* ;
import java.util.*;
public class ProductDAO {
    public List<Product> getALLProducts() throws SQLException, ClassNotFoundException {
        List <Product> a=new ArrayList<>();
        try(Connection con= DBConnection.getConnection();
            PreparedStatement ps=con.prepareStatement("SELECT * FROM products");
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
            PreparedStatement ps=con.prepareStatement("SELECT * FROM products where is_available=1");
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
             PreparedStatement ps = con.prepareStatement("SELECT * FROM products WHERE id = ?")) {
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
                rs.getBigDecimal("price"),
                rs.getString("image_url"),
                rs.getInt("stock"),
                rs.getBoolean("is_available")
        );
    }
    public List<Product> productLowStock()throws SQLException, ClassNotFoundException {
        List<Product> lowOnStock=new ArrayList<>();
        try(Connection con= DBConnection.getConnection();
            PreparedStatement ps=con.prepareStatement("SELECT * FROM products where stock<=5 and is_available=1 order by name");
            ResultSet rs = ps.executeQuery();
        ) {
            while (rs.next()) {
                lowOnStock.add(mapRowToProduct(rs));
            }
            return lowOnStock;
        }
    }
    public int addNewProduct(Product p)throws SQLException, ClassNotFoundException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("INSERT INTO products (category_id, name, description, price, image_url, stock, is_available) VALUES (?,?,?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS);) {
            ps.setInt(1,p.getCategoryId());
            ps.setString(2,p.getName());
            ps.setString(3,p.getDescription());
            ps.setBigDecimal(4,p.getPrice());
            ps.setString(5,p.getImageUrl());
            ps.setInt(6,p.getStock());
            ps.setBoolean(7,p.isAvailable());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }else{
                    return -1;
                }
            }
        }
    }
    public List<Product> getAllProductsForAdmin()throws SQLException, ClassNotFoundException{
        List <Product> a=new ArrayList<>();
        try(Connection con= DBConnection.getConnection();
            PreparedStatement ps=con.prepareStatement("SELECT * FROM products");
            ResultSet rs = ps.executeQuery();
        ) {
            while (rs.next()) {
                a.add(mapRowToProduct(rs));
            }
            return a;
        }
    }
    public boolean updateProduct(Product p)throws SQLException, ClassNotFoundException{
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE products SET category_id = ?, name = ?, description = ?, price = ?, image_url = ?, stock = ?, is_available = ? WHERE id = ?")) {
            ps.setInt(1,p.getCategoryId());
            ps.setString(2,p.getName());
            ps.setString(3,p.getDescription());
            ps.setBigDecimal(4,p.getPrice());
            ps.setString(5,p.getImageUrl());
            ps.setInt(6,p.getStock());
            ps.setBoolean(7,p.isAvailable());
            ps.setInt(8,p.getId());
            return ps.executeUpdate()==1;
        }
    }
    public boolean setAvailability(int id, boolean available)throws SQLException, ClassNotFoundException{
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE products SET is_available = ? WHERE id = ?")){
            ps.setBoolean(1,available);
            ps.setInt(2,id);
            return ps.executeUpdate()==1;
        }
    }
}
