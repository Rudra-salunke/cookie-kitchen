package servlet;

import dao.OrderDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/order-success")
public class OrderSuccessServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException {
        res.setContentType("text/html");
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            res.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        int id;
        try {
            id = Integer.parseInt(req.getParameter("id"));
        } catch (NumberFormatException e) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        User u=(User) session.getAttribute("user");
        OrderDAO o=new OrderDAO();
        try {
            Order order = o.getOrderById(id,u.getId());
            if (order==null){
                res.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            List<OrderItem> items = o.getItemsByOrder(order.getId());
            Address add=o.getAddressById(order.getAddressId());
            req.setAttribute("order",order);
            req.setAttribute("address",add);
            req.setAttribute("items",items);
            req.getRequestDispatcher("/order-success.jsp").forward(req,res);
        }catch (SQLException | ClassNotFoundException e) {
            throw new ServletException("Database error", e);
        }
    }
}
