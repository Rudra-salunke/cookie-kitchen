package dao;

import model.Category;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    public List<Category> getALLCategory() throws SQLException, ClassNotFoundException {
        List <Category> a=new ArrayList<>();
        try(Connection con= DBConnection.getConnection();
            PreparedStatement ps=con.prepareStatement("SELECT * FROM categories");
            ResultSet rs = ps.executeQuery();
        ) {
            while(rs.next()){
                Category u=new Category(
                        rs.getInt("id"),
                        rs.getString("name")
                );
                a.add(u);
            }
            return a;
        }
    }
}
