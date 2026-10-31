package servlet;

import dao.OrderDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Order;
import model.OrderItem;
import model.User;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/my-orders")
public class MyOrdersServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException {
        res.setContentType("text/html");
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            res.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        User u=(User) session.getAttribute("user");
        OrderDAO o=new OrderDAO();
        try {
            List<Order> orders = o.getOrdersByUser(u.getId());
            Map<Integer, List<OrderItem>> itemsByOrder = new HashMap<>();
            for(Order i : orders){
                itemsByOrder.put(i.getId(), o.getItemsByOrder(i.getId()));
            }
            req.setAttribute("orders", orders);
            req.setAttribute("itemsByOrder", itemsByOrder);
            req.getRequestDispatcher("/my-orders.jsp").forward(req, res);
        }catch (SQLException | ClassNotFoundException e) {
            throw new ServletException("Database error", e);
        }
    }
}
