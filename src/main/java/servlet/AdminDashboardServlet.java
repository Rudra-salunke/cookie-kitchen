package servlet;

import dao.OrderDAO;
import dao.ProductDAO;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Order;

import java.io.IOException;

import java.sql.SQLException;


@WebServlet("/admin/dashbord")
public class AdminDashboardServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException{
        res.setContentType("text/html");
        OrderDAO orders= new OrderDAO();
        UserDAO users=new UserDAO();
        ProductDAO products=new ProductDAO();
        String statusParam = req.getParameter("status");
        if (statusParam==null){
            statusParam= String.valueOf(Order.Status.PLACED);
        }
        try{
            req.setAttribute("totalOrders",orders.totalOrders());
            req.setAttribute("totalOrdersOnStatus",orders.totalOrdersStatus(Order.Status.valueOf(statusParam)));
            req.setAttribute("totalCustomer",users.totalUsers());
            req.setAttribute("revenue",orders.revenue());
            req.setAttribute("lowStockProduct",products.productLowStock());
        }catch (SQLException | ClassNotFoundException | IllegalArgumentException e) {
            throw new ServletException("Database error", e);
        }
        req.getRequestDispatcher("/admin/dashbord.jsp").forward(req, res);
    }
}
