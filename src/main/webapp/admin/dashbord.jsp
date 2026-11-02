<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Admin Dashboard - Cookie Cloud Kitchen</title>
</head>
<body>

<h1>Admin Dashboard</h1>
<p>
    Logged in as <c:out value="${sessionScope.user.name}"/> |
    <a href="${pageContext.request.contextPath}/index.jsp">Back to site</a> |
    <a href="${pageContext.request.contextPath}/logout">Logout</a>
</p>
<p>
    <a href="${pageContext.request.contextPath}/admin/orders">Manage Orders</a>
</p>
<h2>Overview</h2>
<ul>
    <li>Total orders: <c:out value="${totalOrders}"/></li>
    <li>Total customers: <c:out value="${totalCustomer}"/></li>
    <li>Revenue (excluding cancelled):
        <fmt:formatNumber value="${revenue}" type="currency" currencySymbol="₹"/>
    </li>
</ul>

<h2>Orders by status</h2>
<form method="get" action="${pageContext.request.contextPath}/admin/dashbord">
    <label for="status">Status:</label>
    <select name="status" id="status">
        <c:forTokens var="s" items="PLACED,BAKING,OUT_FOR_DELIVERY,DELIVERED,CANCELLED" delims=",">
            <option value="${s}" ${s == selectedStatus.name() ? 'selected' : ''}>
                <c:out value="${s}"/>
            </option>
        </c:forTokens>
    </select>
    <button type="submit">Show</button>
</form>
<p>
    Orders with status <strong><c:out value="${selectedStatus}"/></strong>:
    <c:out value="${totalOrdersOnStatus}"/>
</p>

<h2>Low stock products (<c:out value="${lowStockProduct.size()}"/>)</h2>
<c:choose>
    <c:when test="${empty lowStockProduct}">
        <p>All products are well stocked.</p>
    </c:when>
    <c:otherwise>
        <table border="1" cellpadding="6">
            <thead>
                <tr><th>Product</th><th>Stock left</th></tr>
            </thead>
            <tbody>
                <c:forEach var="p" items="${lowStockProduct}">
                    <tr>
                        <td><c:out value="${p.name}"/></td>
                        <td><c:out value="${p.stock}"/></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:otherwise>
</c:choose>

</body>
</html>