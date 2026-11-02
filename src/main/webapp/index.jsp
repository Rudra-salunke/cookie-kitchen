<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<body>
<h2>Cookie Kitchen</h2>
<c:choose>
    <c:when test="${not empty sessionScope.user}">
        <p>Welcome, ${sessionScope.user.name}!</p>
        <a href="${pageContext.request.contextPath}/my-orders">MyOrders</a>
        <c:if test="${sessionScope.user.role == 'ADMIN'}">
            <a href="${pageContext.request.contextPath}/admin/dashbord">Admin</a>
        </c:if>
        <a href="logout">Logout</a>
    </c:when>
    <c:otherwise>
        <a href="login.jsp">Login</a> | <a href="register.jsp">Register</a>
    </c:otherwise>
</c:choose>
<p>
    <a href="menu">Cookies menu</a>
</p>
</body>
</html>