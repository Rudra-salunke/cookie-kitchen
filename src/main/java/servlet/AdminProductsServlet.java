package servlet;

import dao.CategoryDAO;
import dao.ProductDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Category;
import model.Product;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.UUID;

@WebServlet("/admin/products")
public class AdminProductsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        String action = req.getParameter("action");
        if (action == null || action.isBlank()) action = "list";

        HttpSession session = req.getSession();
        ProductDAO productDAO = new ProductDAO();
        CategoryDAO categoryDAO = new CategoryDAO();

        try {
            String token = UUID.randomUUID().toString();
            session.setAttribute("adminToken", token);
            req.setAttribute("adminToken", token);
            Object flash = session.getAttribute("flash");
            if (flash != null) {
                req.setAttribute("flash", flash);
                session.removeAttribute("flash");
            }

            switch (action) {
                case "list":
                    req.setAttribute("products", productDAO.getAllProductsForAdmin());
                    req.getRequestDispatcher("/admin/products.jsp").forward(req,res);
                    break;
                case "new":
                    req.setAttribute("categories", categoryDAO.getALLCategory());
                    req.getRequestDispatcher("/admin/product-form.jsp").forward(req,res);
                    break;
                case "edit":
                    try {
                        int id= Integer.parseInt(req.getParameter("id"));
                        Product p = productDAO.getProductById(id);
                        if (p==null){
                            res.sendError(404);
                            return;
                        }
                        req.setAttribute("product", p);
                        req.setAttribute("categories",categoryDAO.getALLCategory());
                        req.getRequestDispatcher("/admin/product-form.jsp").forward(req,res);
                    } catch (NumberFormatException e) {
                        res.sendError(400);
                        return;
                    }
                    break;

                default:
                    res.sendError(400);
                    return;
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new ServletException("Database error", e);
        }
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            res.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        Object t = session.getAttribute("adminToken");
        String sessionToken = (t == null) ? null : t.toString();
        String submittedToken = req.getParameter("adminToken");
        if (sessionToken == null || submittedToken == null || !sessionToken.equals(submittedToken)) {
            res.sendError(403);
            return;
        }

        String action = req.getParameter("action");
        if (action == null) { res.sendError(400); return; }
        String base = req.getContextPath() + "/admin/products";
        String flash;
        try {
            ProductDAO productDAO = new ProductDAO();
            switch (action) {
                case "add":
                case "update": {
                    boolean isUpdate = action.equals("update");
                    int id = 0;
                    if (isUpdate) {
                        try {
                            id = Integer.parseInt(req.getParameter("id"));
                        } catch (NumberFormatException e) {
                            res.sendError(400);
                            return;
                        }
                    }
                    Product p;
                    try {
                        p = buildProduct(req, id);
                    } catch (IllegalArgumentException e) {
                        // the message is written by buildProduct, never copied from the request
                        session.setAttribute("flash", e.getMessage());
                        res.sendRedirect(isUpdate ? base + "?action=edit&id=" + id
                                : base + "?action=new");
                        return;
                    }
                    if (isUpdate) {
                        flash = productDAO.updateProduct(p)
                                ? "Product #" + id + " updated."
                                : "Product #" + id + " could not be updated. It may not exist, or nothing changed.";
                    } else {
                        int newId = productDAO.addNewProduct(p);
                        flash = (newId > 0)
                                ? "Product #" + newId + " added."
                                : "Product could not be added.";
                    }
                    break;
                }
                case "toggle": {
                    int id;
                    try {
                        id = Integer.parseInt(req.getParameter("id"));
                    } catch (NumberFormatException e) {
                        res.sendError(400);
                        return;
                    }
                    // the NEW value the button asks for, as the text "true" or "false"
                    boolean available = Boolean.parseBoolean(req.getParameter("available"));

                    flash = productDAO.setAvailability(id, available)
                            ? "Product #" + id + (available ? " restored." : " retired.")
                            : "Product #" + id + " could not be updated. Please check its current state.";
                    break;
                }
                default:
                    res.sendError(400);
                    return;
            }
            session.setAttribute("flash", flash);
        } catch (SQLException | ClassNotFoundException e) {
            throw new ServletException("Database error", e);
        }
        res.sendRedirect(req.getContextPath() + "/admin/products");
    }
    private Product buildProduct(HttpServletRequest req, int id)
            throws SQLException, ClassNotFoundException {

        String name = req.getParameter("name");
        if (name == null || name.isBlank() || name.trim().length() > 100) {
            throw new IllegalArgumentException("Name is required and must be at most 100 characters.");
        }

        String description = req.getParameter("description");
        description = (description == null) ? "" : description.trim();
        if (description.length() > 1000) {
            throw new IllegalArgumentException("Description must be at most 1000 characters.");
        }

        BigDecimal price;
        try {
            price = new BigDecimal(req.getParameter("price").trim());
        } catch (NumberFormatException | NullPointerException e) {
            throw new IllegalArgumentException("Price must be a valid number.");
        }
        if (price.compareTo(BigDecimal.ZERO) <= 0 || price.scale() > 2) {
            throw new IllegalArgumentException("Price must be greater than 0, with at most 2 decimal places.");
        }

        int stock;
        try {
            stock = Integer.parseInt(req.getParameter("stock").trim());
        } catch (NumberFormatException | NullPointerException e) {
            throw new IllegalArgumentException("Stock must be a whole number.");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative.");
        }

        int categoryId;
        try {
            categoryId = Integer.parseInt(req.getParameter("categoryId"));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Please choose a category.");
        }
        boolean categoryExists = false;
        CategoryDAO category=new CategoryDAO();
        for (Category c :category.getALLCategory() ) {
            if (c.getId() == categoryId) { categoryExists = true; break; }
        }
        if (!categoryExists) {
            throw new IllegalArgumentException("That category does not exist.");
        }

        String imageUrl = req.getParameter("imageUrl");
        imageUrl = (imageUrl == null) ? "" : imageUrl.trim();
        if (!imageUrl.isEmpty() && !(imageUrl.startsWith("http://") || imageUrl.startsWith("https://"))) {
            throw new IllegalArgumentException("Image URL must start with http:// or https://.");
        }
        boolean available = req.getParameter("available") != null;

        Product p = new Product();
        p.setId(id);
        p.setName(name.trim());
        p.setDescription(description);
        p.setPrice(price);
        p.setStock(stock);
        p.setCategoryId(categoryId);
        p.setImageUrl(imageUrl);
        p.setAvailable(available);
        return p;
    }
}
