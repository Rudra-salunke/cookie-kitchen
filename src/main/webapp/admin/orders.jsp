<%--
  Created by IntelliJ IDEA.
  User: RUDRA
  Date: 02-11-2026
  Time: 17:55
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Manage Orders - Cookie Cloud Kitchen</title>
</head>
<body>

<h1>Manage Orders</h1>
<p>
    <a href="${pageContext.request.contextPath}/admin/dashbord">Dashboard</a> |
    <a href="${pageContext.request.contextPath}/logout">Logout</a>
</p>

<c:if test="${not empty flash}">
    <p><strong><c:out value="${flash}"/></strong></p>
</c:if>

<c:if test="${empty orders}">
    <p>No orders yet.</p>
</c:if>

<c:forEach var="order" items="${orders}">
    <div class="order-card">
        <h3>Order #<c:out value="${order.id}"/></h3>
        <p>Customer: <c:out value="${order.customerName}"/></p>
        <p>
            Placed on:
            <fmt:formatDate value="${order.createdAt}" pattern="dd MMM yyyy, hh:mm a"/>
        </p>
        <p>Status: <strong><c:out value="${order.status}"/></strong></p>
        <p>Payment: <c:out value="${order.paymentMethod}"/></p>
        <p>
            Total:
            <fmt:formatNumber value="${order.total}" type="currency" currencySymbol="₹"/>
        </p>

        <table border="1" cellpadding="6">
            <thead>
                <tr><th>Item</th><th>Qty</th><th>Price</th></tr>
            </thead>
            <tbody>
                <c:forEach var="item" items="${itemsByOrder[order.id]}">
                    <tr>
                        <td><c:out value="${item.name}"/></td>
                        <td><c:out value="${item.quantity}"/></td>
                        <td>
                            <fmt:formatNumber value="${item.priceAtPurchase}"
                                              type="currency" currencySymbol="₹"/>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>

        <%-- status buttons: only the legal next moves, Cancel skipped until cancelOrder exists --%>
        <c:set var="nextList" value="${order.status.nextStatuses()}"/>
        <c:choose>
            <c:when test="${empty nextList}">
                <p><em>Final status. No further changes.</em></p>
            </c:when>
            <c:otherwise>
                <form method="post" action="${pageContext.request.contextPath}/admin/orders">
                    <input type="hidden" name="adminToken" value="${adminToken}">
                    <input type="hidden" name="orderId" value="${order.id}">
                    <input type="hidden" name="expected" value="${order.status.name()}">
                    <c:forEach var="next" items="${nextList}">
                        <c:if test="${next.name() ne 'CANCELLED'}">
                            <button type="submit" name="status" value="${next.name()}">
                                Move to <c:out value="${next}"/>
                            </button>
                        </c:if>
                    </c:forEach>
                </form>
            </c:otherwise>
        </c:choose>
    </div>
    <hr>
</c:forEach>

</body>
</html>
