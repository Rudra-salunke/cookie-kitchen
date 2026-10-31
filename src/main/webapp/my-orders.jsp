<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>My Orders - Cookie Cloud Kitchen</title>
</head>
<body>

<%-- Reuse your navbar/header here, same way as menu.jsp / cart.jsp --%>

<h1>My Orders</h1>

<c:if test="${empty orders}">
    <p>You haven't placed any orders yet.</p>
    <a href="${pageContext.request.contextPath}/menu">Browse the menu</a>
</c:if>

<c:forEach var="order" items="${orders}">
    <div class="order-cart">
        <h3>Order #<c:out value="${order.id}"/></h3>

        <p>
            Placed on:
            <fmt:formatDate value="${order.createdAt}" pattern="dd MMM yyyy, hh:mm a"/>
        </p>
        <p>Status: <c:out value="${order.status}"/></p>
        <p>Payment: <c:out value="${order.paymentMethod}"/></p>
        <p>
            Total:
            <fmt:formatNumber value="${order.total}" type="currency" currencySymbol="₹"/>
        </p>

        <table>
            <thead>
                <tr>
                    <th>Item</th>
                    <th>Qty</th>
                    <th>Price</th>
                </tr>
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

        <a href="${pageContext.request.contextPath}/order-success?id=${order.id}">View details</a>
    </div>
</c:forEach>

</body>
</html>