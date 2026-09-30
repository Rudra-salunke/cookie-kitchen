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
            while(rs.next()){
                Product p=new Product(
                        rs.getInt("id"),
                        rs.getInt("category_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getString("image_url"),
                        rs.getInt("stock"),
                        rs.getBoolean("is_available")
                );
                a.add(p);
            }
            return a;
        }
    }
}
