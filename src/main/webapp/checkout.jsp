<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Checkout</title>
</head>
<body>
<h1>Checkout</h1>

<c:if test="${not empty flash}">
    <p><c:out value="${flash}"/></p>
</c:if>

<table border="1" cellpadding="8">
    <tr>
        <th>Name</th><th>Quantity</th><th>Unit Price</th><th>Subtotal</th>
    </tr>
    <c:forEach var="item" items="${cartItems}">
        <tr>
            <td><c:out value="${item.name}"/></td>
            <td><c:out value="${item.quantity}"/></td>
            <td><fmt:formatNumber value="${item.unitPrice}" minFractionDigits="2" maxFractionDigits="2"/></td>
            <td><fmt:formatNumber value="${item.subtotal}" minFractionDigits="2" maxFractionDigits="2"/></td>
        </tr>
    </c:forEach>
    <tr>
        <td colspan="3"><strong>Total</strong></td>
        <td><strong><fmt:formatNumber value="${grandTotal}" minFractionDigits="2" maxFractionDigits="2"/></strong></td>
    </tr>
</table>

<p><a href="${pageContext.request.contextPath}/cart">Back to cart</a></p>

<form method="post" action="${pageContext.request.contextPath}/checkout">
    <input type="hidden" name="token" value="${token}">
    <p>
        <label for="line1">Address Line 1 *</label><br>
        <input type="text" id="line1" name="line1" required maxlength="255">
    </p>
    <p>
        <label for="line2">Address Line 2</label><br>
        <input type="text" id="line2" name="line2" maxlength="255">
    </p>
    <p>
        <label for="city">City *</label><br>
        <input type="text" id="city" name="city" required maxlength="80">
    </p>
    <p>
        <label for="pincode">Pincode *</label><br>
        <input type="text" id="pincode" name="pincode" required pattern="\d{6}" maxlength="6" title="6 digits">
    </p>
    <p>
        <label><input type="radio" name="paymentMethod" value="COD" checked> Cash on delivery</label><br>
        <label><input type="radio" name="paymentMethod" value="ONLINE"> Online (demo)</label><br>
        <small>No card details are collected on this page.</small>
    </p>
    <button type="submit" id="submitBtn">Place Order</button>
</form>
</body>
</html>