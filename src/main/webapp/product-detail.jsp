<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title><c:out value="${product.name}"/> - Cookie Cloud Kitchen</title>
</head>
<body>

<a href="${pageContext.request.contextPath}/menu">&larr; Back to menu</a>

<div style="display:flex; gap:30px; margin-top:20px;">
    <img src="${product.imageUrl}" alt="<c:out value='${product.name}'/>" style="width:300px;"/>

    <div>
        <h1><c:out value="${product.name}"/></h1>
        <p><c:out value="${product.description}"/></p>
        <p><strong>
            <fmt:formatNumber value="${product.price}" type="currency" currencySymbol="₹"/>
        </strong></p>

        <c:choose>
            <c:when test="${product.available}">
                <form action="${pageContext.request.contextPath}/cart" method="post">
                    <input type="hidden" name="action" value="add">
                    <input type="hidden" name="productId" value="${product.id}">
                    <input type="number" name="quantity" value="1" min="1" max="20" required>
                    <input type="submit" name="submit" value="add to cart">
                </form>
            </c:when>
            <c:otherwise>
                <p>Currently unavailable</p>
            </c:otherwise>
        </c:choose>
    </div>
</div>

</body>
</html>