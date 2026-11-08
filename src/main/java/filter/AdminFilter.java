package filter;

import jakarta.servlet.*;
import model.User;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter("/admin/*")
public class AdminFilter implements Filter {
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);
        if ( session == null|| session.getAttribute("user")==null) {
            res.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        User user=(User) session.getAttribute("user");
        if(!"ADMIN".equals(user.getRole())){
            res.sendError(403);
            return;
        }else {
            chain.doFilter(request, response);
        }
    }
}
