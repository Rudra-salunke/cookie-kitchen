<%--
  Created by IntelliJ IDEA.
  User: RUDRA
  Date: 06-11-2026
  Time: 13:02
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Manage Products - Cookie Cloud Kitchen</title>
</head>
<body>

<h1>Manage Products</h1>
<p>
    <a href="${pageContext.request.contextPath}/admin/dashbord">Dashboard</a> |
    <a href="${pageContext.request.contextPath}/admin/orders">Manage Orders</a> |
    <a href="${pageContext.request.contextPath}/logout">Logout</a>
</p>

<c:if test="${not empty flash}">
    <p><strong><c:out value="${flash}"/></strong></p>
</c:if>

<p><a href="${pageContext.request.contextPath}/admin/products?action=new">+ Add new product</a></p>

<c:if test="${empty products}">
    <p>No products yet.</p>
</c:if>

<c:if test="${not empty products}">
    <table border="1" cellpadding="6">
        <thead>
            <tr>
                <th>ID</th><th>Name</th><th>Category ID</th><th>Price</th>
                <th>Stock</th><th>Status</th><th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="p" items="${products}">
                <tr>
                    <td><c:out value="${p.id}"/></td>
                    <td><c:out value="${p.name}"/></td>
                    <td><c:out value="${p.categoryId}"/></td>
                    <td><fmt:formatNumber value="${p.price}" type="currency" currencySymbol="₹"/></td>
                    <td>
                        <c:out value="${p.stock}"/>
                        <c:if test="${p.stock <= 5}"> (low)</c:if>
                    </td>
                    <td>
                        <c:choose>
                            <c:when test="${p.available}">Available</c:when>
                            <c:otherwise><em>Retired</em></c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <a href="${pageContext.request.contextPath}/admin/products?action=edit&id=${p.id}"><button type="submit">Edit</button></a>

                        <form method="post" action="${pageContext.request.contextPath}/admin/products"
                              style="display:inline">
                            <input type="hidden" name="adminToken" value="${adminToken}">
                            <input type="hidden" name="action" value="toggle">
                            <input type="hidden" name="id" value="${p.id}">
                            <input type="hidden" name="available" value="${not p.available}">
                            <c:choose>
                                <c:when test="${p.available}">
                                    <button type="submit"
                                            onclick="return confirm('Retire this product? It will disappear from the menu.');">
                                        Retire
                                    </button>
                                </c:when>
                                <c:otherwise>
                                    <button type="submit">Restore</button>
                                </c:otherwise>
                            </c:choose>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</c:if>

</body>
</html>
