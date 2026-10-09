package servlet;

import dao.CartDAO;
import dao.OrderDAO;
import exception.OrderException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Address;
import model.CartItem;
import model.Order;
import model.User;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException{
        res.setContentType("text/html");
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            res.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        User u=(User) session.getAttribute("user");
        CartDAO c=new CartDAO();
        try {
            List<CartItem> cart= c.getCartItems(u.getId());
            if (cart.isEmpty()){
                session.setAttribute("flash","your cart is empty");
                res.sendRedirect(req.getContextPath()+"/cart");
                return;
            }
            BigDecimal total=BigDecimal.ZERO;
            for(CartItem item:cart){
                total=total.add(item.getSubtotal());
            }
            String token = UUID.randomUUID().toString();
            session.setAttribute("checkoutToken", token);
            session.setAttribute("cartItems",cart);
            session.setAttribute("grandTotal",total);
            req.setAttribute("token", token);
            req.getRequestDispatcher("/checkout.jsp").forward(req,res);
        }catch (SQLException | ClassNotFoundException e) {
            throw new ServletException("Database error", e);
        }
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            res.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        User user = (User) session.getAttribute("user");
        int userId = user.getId();
        String sessionToken = (String) session.getAttribute("checkoutToken");
        String formToken = req.getParameter("token");
        if (sessionToken==null||!sessionToken.equals(formToken)){
            res.sendRedirect(req.getContextPath() + "/cart");
            return;
        }
        String line1 = trim(req.getParameter("line1"));
        String line2 = trim(req.getParameter("line2"));
        String city = trim(req.getParameter("city"));
        String pincode = trim(req.getParameter("pincode"));
        String methodParam = trim(req.getParameter("paymentMethod"));
        String error=null;
        if (line1.isEmpty() || line1.length() > 255) {
            error = "Please enter a valid address line 1";
        } else if (line2.length() > 255) {
            error = "Address line 2 is too long";
        } else if (city.isEmpty() || city.length() > 80) {
            error = "Please enter a valid city";
        } else if (!pincode.matches("\\d{6}")) {
            error = "Pincode must be 6 digits";
        }
        Order.PaymentMethod method;
        try {
            method = Order.PaymentMethod.valueOf(methodParam);
        } catch (IllegalArgumentException e) {
            res.sendError(400);
            return;
        }
        if (error != null) {
            session.setAttribute("flash", error);
            res.sendRedirect(req.getContextPath() + "/checkout");
            return;
        }
        session.removeAttribute("checkoutToken");
        try {
            Address address = new Address(userId, line1, line2.isEmpty() ? null : line2, city, pincode);
            int orderId = new OrderDAO().placeOrder(userId, address, method);
            res.sendRedirect(req.getContextPath() + "/order-success?id=" + orderId);
        } catch (OrderException e) {
            session.setAttribute("flash", e.getMessage());
            res.sendRedirect(req.getContextPath() + "/cart");
        } catch (SQLException | ClassNotFoundException e) {
            throw new ServletException("Database error", e);
        }
    }
    private String trim(String s) {
        return s == null ? "" : s.trim();
    }
}
