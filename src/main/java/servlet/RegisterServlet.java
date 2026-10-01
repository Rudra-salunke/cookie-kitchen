package servlet;
import dao.UserDAO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        res.setContentType("text/html");
        // 1. get parameters
        String name = req.getParameter("name");
        String phone = req.getParameter("phone");
        String email = req.getParameter("email");
        String Password = req.getParameter("password");
        String chk1 = req.getParameter("chk1");
        // 2. check for duplicate email (needs a DAO method)
        try{
            UserDAO users= new UserDAO();
            List<String> existingEmails = users.getALLEmail();
            boolean emailExists = false;
            for (String dbemail : existingEmails) {
                if (email.equals(dbemail)) {
                    emailExists = true;
                    break;
                }
            }
            if (emailExists) {
                req.setAttribute("error", "Email already registered.");
                req.getRequestDispatcher("register.jsp").forward(req, res);
                return;
            }
            // 3. hash password
            String hashedPassword = BCrypt.hashpw(Password, BCrypt.gensalt());
            // 4. insert user (needs a DAO method)
            User u = new User(0, name,email,hashedPassword,phone,null,null);
            int rowsInserted =users.registerUser(u);
            // 5. forward/redirect
            if (rowsInserted > 0) {
                res.sendRedirect("login.jsp");
            } else {
                req.setAttribute("error", "Registration failed. Please try again.");
                req.getRequestDispatcher("register.jsp").forward(req, res);
            }
        }catch (SQLException | ClassNotFoundException e) {
            throw new ServletException("Database error", e);
        }
    }
}
