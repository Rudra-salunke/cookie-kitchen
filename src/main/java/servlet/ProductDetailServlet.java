package servlet;

import dao.ProductDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Product;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/product")
public class ProductDetailServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException{
        jakarta.servlet.http.HttpSession session = req.getSession(false);
        try {
            String idParam = req.getParameter("id");
            int id = 0;
            if (idParam == null || !idParam.matches("\\d+")) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST);   // 400
                return;
            }
            try {
                id = Integer.parseInt(idParam);
            } catch (NumberFormatException e) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            ProductDAO p = new ProductDAO();
            Product product = p.getProductById(id);
            if (product == null) {
                res.sendError(HttpServletResponse.SC_NOT_FOUND);     // 404
                return;
            }
            req.setAttribute("product", product);
            req.getRequestDispatcher("/product-detail.jsp").forward(req, res);
        } catch (SQLException | ClassNotFoundException e) {
            throw new ServletException("Database error", e);
        }
    }
}
