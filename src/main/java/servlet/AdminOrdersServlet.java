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

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@WebServlet("/admin/orders")
public class AdminOrdersServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException{
        HttpSession session = req.getSession();
        OrderDAO o=new OrderDAO();
        try {
            List<Order> orders = o.getAllOrders();
            Map<Integer, List<OrderItem>> itemsByOrder = new HashMap<>();
            for(Order i : orders){
                itemsByOrder.put(i.getId(), o.getItemsByOrder(i.getId()));
            }
            Object flash = session.getAttribute("flash");
            if (flash != null) {
                req.setAttribute("flash", flash);
                session.removeAttribute("flash");
            }
            String token = UUID.randomUUID().toString();
            session.setAttribute("adminToken", token);
            req.setAttribute("orders", orders);
            req.setAttribute("itemsByOrder", itemsByOrder);
            req.setAttribute("adminToken", token);
            req.getRequestDispatcher("/admin/orders.jsp").forward(req, res);
        }catch (SQLException | ClassNotFoundException e) {
            throw new ServletException("Database error", e);
        }
    }
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException{
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            res.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        String sessionToken= session.getAttribute("adminToken").toString();
        String submittedToken = req.getParameter("adminToken");
        if (!sessionToken.equals(submittedToken)||req.getParameter("adminToken")==null||session.getAttribute("adminToken")==null){
            res.sendError(400);
            return;
        }
        int orderId;
        Order.Status expected;
        Order.Status newStatus;
        try {
            orderId = Integer.parseInt(req.getParameter("orderId"));
            expected = Order.Status.valueOf(req.getParameter("expected"));
            newStatus = Order.Status.valueOf(req.getParameter("status"));
        } catch (IllegalArgumentException | NullPointerException e) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        try {
            OrderDAO orderDAO = new OrderDAO();
            boolean updated = orderDAO.updateStatus(orderId, expected, newStatus);
            if (updated) {
                session.setAttribute("flash", "Order #" + orderId + " moved to " + newStatus + ".");
            } else {
                session.setAttribute("flash", "Order #" + orderId
                        + " could not be updated. It may have been changed already. Please check the current status.");
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new ServletException("Database error", e);
        }
        res.sendRedirect(req.getContextPath() + "/admin/orders");
    }
}
