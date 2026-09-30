package servlet;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import dao.ProductDAO;
import model.Product;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/test")
public class TestServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        res.setContentType("text/html");
        try{
            ProductDAO test= new ProductDAO();
            List<Product> products = test.getALLProducts();
            req.setAttribute("products",products);
            RequestDispatcher rd = req.getRequestDispatcher("test.jsp");
            rd.forward(req, res);
        }catch (SQLException | ClassNotFoundException e) {
            throw new ServletException("Database error", e);
        }
    }
}
