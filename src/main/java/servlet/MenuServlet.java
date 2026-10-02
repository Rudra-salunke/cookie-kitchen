package servlet;

import dao.CategoryDAO;
import dao.ProductDAO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Category;
import model.Product;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/menu")
public class MenuServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException{
        jakarta.servlet.http.HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            res.sendRedirect("login.jsp");
            return;
        }
        res.setContentType("text/html");
        ProductDAO menu= new ProductDAO();
        CategoryDAO cat= new CategoryDAO();
        String q = req.getParameter("search");
        if (q != null) q = q.trim();

        String catParam = req.getParameter("category");
        int category = 0;
        if (catParam != null && catParam.matches("\\d+")) {
            category = Integer.parseInt(catParam);
        }
        List<Product> products;
        try {
            if (q != null && !q.isEmpty()) {
                    products = menu.searchProduct(q);
                    req.setAttribute("query", q);
            } else if (category != 0) {
                    products = menu.getProductsByCategory(category);
                req.setAttribute("selectedCategory", category);
            } else {
                    products = menu.getALLAvailableProducts();
                    req.setAttribute("products", products);
            }
            List<Category> categories = cat.getALLCategory();
            req.setAttribute("products", products);
            req.setAttribute("categories", categories);
            RequestDispatcher rd = req.getRequestDispatcher("menu.jsp");
            rd.forward(req, res);
        }catch (SQLException | ClassNotFoundException e) {
            throw new ServletException("Database error", e);
        }
    }
}
