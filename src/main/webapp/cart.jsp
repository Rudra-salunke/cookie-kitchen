<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Your Cart - Cookie Cloud Kitchen</title>
</head>
<body>
<h1>Your Cart</h1>

<c:if test="${not empty flash}">
    <p><c:out value="${flash}"/></p>
</c:if>

<c:choose>
    <c:when test="${empty cartItems}">
        <p>Your cart is empty.</p>
        <a href="${pageContext.request.contextPath}/menu">Browse the menu</a>
    </c:when>

    <c:otherwise>
        <c:set var="hasProblem" value="false"/>
        <table border="1" cellpadding="8">
            <tr>
                <th>Item</th><th>Price</th><th>Quantity</th><th>Subtotal</th><th></th>
            </tr>
            <c:forEach var="item" items="${cartItems}">
                <tr>
                    <td>
                        <img src="${item.imageUrl}" alt="<c:out value='${item.name}'/>" width="60">
                        <c:out value="${item.name}"/>
                        <c:if test="${!item.available}">
                            <br><strong>Unavailable. Please remove this item.</strong>
                            <c:set var="hasProblem" value="true"/>
                        </c:if>
                        <c:if test="${item.available && item.quantity > item.stock}">
                            <br><strong>Only <c:out value="${item.stock}"/> left. Please reduce the quantity.</strong>
                            <c:set var="hasProblem" value="true"/>
                        </c:if>
                    </td>
                    <td><fmt:formatNumber value="${item.unitPrice}" minFractionDigits="2" maxFractionDigits="2"/></td>
                    <td>
                        <form method="post" action="${pageContext.request.contextPath}/cart">
                            <input type="hidden" name="action" value="update">
                            <input type="hidden" name="cartItemId" value="${item.id}">
                            <input type="number" name="quantity" value="${item.quantity}" min="1" max="20" required>
                            <button type="submit">Update</button>
                        </form>
                    </td>
                    <td><fmt:formatNumber value="${item.subtotal}" minFractionDigits="2" maxFractionDigits="2"/></td>
                    <td>
                        <form method="post" action="${pageContext.request.contextPath}/cart">
                            <input type="hidden" name="action" value="remove">
                            <input type="hidden" name="cartItemId" value="${item.id}">
                            <button type="submit">Remove</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
            <tr>
                <td colspan="3"><strong>Total</strong></td>
                <td colspan="2"><strong><fmt:formatNumber value="${GrandTotal}" minFractionDigits="2" maxFractionDigits="2"/></strong></td>
            </tr>
        </table>

        <p>
            <a href="${pageContext.request.contextPath}/menu">Continue shopping</a>
            <c:choose>
                <c:when test="${hasProblem}">
                    <button disabled>Fix the items above to check out</button>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/checkout">Proceed to checkout</a>
                </c:otherwise>
            </c:choose>
        </p>
    </c:otherwise>
</c:choose>
</body>
</html>