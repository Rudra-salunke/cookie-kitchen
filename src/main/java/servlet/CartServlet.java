package servlet;

import dao.CartDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.CartItem;
import model.User;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import static java.lang.Integer.parseInt;


@WebServlet("/cart")
public class CartServlet extends HttpServlet {
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
        CartDAO cart=new CartDAO();
        BigDecimal grandTotal=BigDecimal.ZERO;
        try {
            List<CartItem> items=cart.getCartItems(u.getId());
            for(CartItem item:items){
                grandTotal=grandTotal.add(item.getSubtotal());
            }
            Object flash = session.getAttribute("flash");
            if (flash != null) {
                req.setAttribute("flash", flash);
                session.removeAttribute("flash");
            }
            req.setAttribute("cartItems",items);
            req.setAttribute("GrandTotal",grandTotal);
            req.getRequestDispatcher("/cart.jsp").forward(req, res);
        }catch (SQLException | ClassNotFoundException e) {
            throw new ServletException("Database error", e);
        }
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException{
        res.setContentType("text/html");
        jakarta.servlet.http.HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            res.sendRedirect("login.jsp");
            return;
        }
        CartDAO cart=new CartDAO();
        User user = (User) session.getAttribute("user");
        int userId = user.getId();
        String action = req.getParameter("action");
        if (action == null) action = "";
        try{
            switch (action.toLowerCase()){
                case "add":
                    int productId = parseInt(req.getParameter("productId"));
                    int quantity  = parseInt(req.getParameter("quantity"));
                    if (productId < 1 || quantity < 1) {
                        res.sendError(400); return;
                    }
                    int result=cart.addToCart(userId,productId,quantity);
                    session.setAttribute("flash", messageFor(result, "Added to your cart"));
                    break;
                case "update":
                    int cartItemId = parseInt(req.getParameter("cartItemId"));
                    int newQuantity   = parseInt(req.getParameter("quantity"));
                    if (cartItemId < 1 || newQuantity < 1) {
                        res.sendError(400); return;
                    }
                    int result1 = cart.updateQuantity(userId, cartItemId, newQuantity);
                    session.setAttribute("flash", messageFor(result1, "Cart updated"));
                    break;
                case "remove":
                    int cartItemId1 = parseInt(req.getParameter("cartItemId"));
                    if (cartItemId1 < 1) {
                        res.sendError(400); return;
                    }
                    boolean removed = cart.removeItem(userId, cartItemId1);
                    session.setAttribute("flash", removed ? "Item removed" : "Item not found");
                    break;
                default:
                    res.sendError(400);
                    return;
            }
        }catch (SQLException | ClassNotFoundException e) {
            throw new ServletException("Database error", e);
        }
        res.sendRedirect(req.getContextPath() + "/cart");
    }
    private String messageFor(int result, String okMessage) {
        switch (result) {
            case CartDAO.OK:
                return okMessage;
            case CartDAO.NOT_FOUND:
                return "Item not found";
            case CartDAO.UNAVAILABLE:
                return "Sorry, that item is unavailable";
            case CartDAO.LIMIT_EXCEEDED:
                return "Quantity limit reached";
            default:
                return "Something went wrong";
        }
    }
}
