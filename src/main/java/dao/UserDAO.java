package dao;

import model.Product;
import model.User;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    public List<User> getALLUsers() throws SQLException, ClassNotFoundException {
        List <User> a=new ArrayList<>();
        try(Connection con= DBConnection.getConnection();
            PreparedStatement ps=con.prepareStatement("SELECT * FROM users");
            ResultSet rs = ps.executeQuery();
        ) {
            while (rs.next()) {
                a.add(mapRowToUsers(rs));
            }
            return a;
        }
    }
    public List<String> getALLEmail() throws SQLException, ClassNotFoundException {
        List<String> emailList = new ArrayList<>();
        try(Connection con= DBConnection.getConnection();
            PreparedStatement ps=con.prepareStatement("SELECT email FROM users");
            ResultSet rs = ps.executeQuery();
        ){
            while (rs.next()) {
                emailList.add(rs.getString("email"));
            }
            return emailList;
        }
    }
    public int registerUser(User user) throws SQLException, ClassNotFoundException {
        String insertSql = "INSERT INTO users (name, email, password_hash, phone) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(insertSql)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword_hash());
            ps.setString(4, user.getPhone());

            return ps.executeUpdate();
        }
    }
    public User getUserByEmail(String email) throws SQLException, ClassNotFoundException {
        try(Connection con= DBConnection.getConnection();
            PreparedStatement ps=con.prepareStatement("SELECT * FROM users where email= ?");){
                ps.setString(1,email);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() ? mapRowToUsers(rs) : null;
                }
        }
    }
    private User mapRowToUsers(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getString("phone"),
                rs.getString("role"),
                rs.getString("created_at")
        );
    }
}
