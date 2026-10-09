<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Order placed - Cookie Cloud Kitchen</title>
</head>
<body>
<h1>Order placed!</h1>
<p>Thank you for your order. Your order number is <strong>#<c:out value="${order.id}"/></strong>.</p>

<h2>Order details</h2>
<p>
    Date: <fmt:formatDate value="${order.createdAt}" type="both" dateStyle="medium" timeStyle="short"/><br>
    Status: <c:out value="${order.status}"/><br>
    Payment:
    <c:choose>
        <c:when test="${order.paymentMethod == 'COD'}">Pay on delivery</c:when>
        <c:otherwise>Paid online (demo)</c:otherwise>
    </c:choose>
</p>

<h2>Items</h2>
<table border="1" cellpadding="8">
    <tr>
        <th>Item</th><th>Quantity</th><th>Price</th><th>Subtotal</th>
    </tr>
    <c:forEach var="item" items="${items}">
        <tr>
            <td>
                <c:if test="${not empty item.imageUrl}">
                    <img src="${item.imageUrl}" alt="<c:out value='${item.name}'/>" width="50">
                </c:if>
                <c:out value="${item.name}"/>
            </td>
            <td><c:out value="${item.quantity}"/></td>
            <td><fmt:formatNumber value="${item.priceAtPurchase}" minFractionDigits="2" maxFractionDigits="2"/></td>
            <td><fmt:formatNumber value="${item.subtotal}" minFractionDigits="2" maxFractionDigits="2"/></td>
        </tr>
    </c:forEach>
    <tr>
        <td colspan="3"><strong>Total</strong></td>
        <td><strong><fmt:formatNumber value="${order.total}" minFractionDigits="2" maxFractionDigits="2"/></strong></td>
    </tr>
</table>

<h2>Shipping address</h2>
<p>
    <c:out value="${address.line1}"/><br>
    <c:if test="${not empty address.line2}">
        <c:out value="${address.line2}"/><br>
    </c:if>
    <c:out value="${address.city}"/> - <c:out value="${address.pincode}"/>
</p>

<p>
    <a href="${pageContext.request.contextPath}/my-orders">View my orders</a> |
    <a href="${pageContext.request.contextPath}/menu">Continue shopping</a>
</p>
</body>
</html>