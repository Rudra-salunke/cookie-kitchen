package servlet;

import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet{
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException{
        res.setContentType("text/html");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        try{
            UserDAO userDAO = new UserDAO();
            User user = userDAO.getUserByEmail(email);
            if (user==null){
                req.setAttribute("error", "User does not exist.");
                req.getRequestDispatcher("login.jsp").forward(req, res);
            }else{
                if(BCrypt.checkpw(password, user.getPassword_hash())){
                    HttpSession session = req.getSession();
                    session.setAttribute("user", user);
                    res.sendRedirect("index.jsp");
                }else{
                    req.setAttribute("error", "password is wrong.");
                    req.getRequestDispatcher("login.jsp").forward(req, res);
                }
            }
        }catch (SQLException | ClassNotFoundException e){
            throw new ServletException("Database error", e);
        }
    }
}
