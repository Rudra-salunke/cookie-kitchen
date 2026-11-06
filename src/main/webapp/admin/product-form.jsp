<%--
  Created by IntelliJ IDEA.
  User: RUDRA
  Date: 06-11-2026
  Time: 13:02
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>
        <c:out value="${empty product ? 'Add product' : 'Edit product'}"/> - Cookie Cloud Kitchen
    </title>
</head>
<body>

<h1>
    <c:choose>
        <c:when test="${empty product}">Add product</c:when>
        <c:otherwise>Edit product #<c:out value="${product.id}"/></c:otherwise>
    </c:choose>
</h1>

<c:if test="${not empty flash}">
    <p><strong><c:out value="${flash}"/></strong></p>
</c:if>

<form method="post" action="${pageContext.request.contextPath}/admin/products">
    <input type="hidden" name="adminToken" value="${adminToken}">
    <input type="hidden" name="action" value="${empty product ? 'add' : 'update'}">
    <c:if test="${not empty product}">
        <input type="hidden" name="id" value="${product.id}">
    </c:if>

    <p>
        <label>Name<br>
            <input type="text" name="name" maxlength="100" required
                   value="<c:out value='${product.name}'/>">
        </label>
    </p>

    <p>
        <label>Description<br>
            <textarea name="description" rows="4" cols="40" maxlength="1000"><c:out value="${product.description}"/></textarea>
        </label>
    </p>

    <p>
        <label>Price<br>
            <input type="number" name="price" step="0.01" min="0.01" required
                   value="<c:out value='${product.price}'/>">
        </label>
    </p>

    <p>
        <label>Stock<br>
            <input type="number" name="stock" min="0" step="1" required
                   value="<c:out value='${product.stock}'/>">
        </label>
    </p>

    <p>
        <label>Category<br>
            <select name="categoryId" required>
                <c:forEach var="c" items="${categories}">
                    <option value="${c.id}" ${c.id == product.categoryId ? 'selected' : ''}>
                        <c:out value="${c.name}"/>
                    </option>
                </c:forEach>
            </select>
        </label>
    </p>

    <p>
        <label>Image URL<br>
            <input type="text" name="imageUrl" size="50"
                   value="<c:out value='${product.imageUrl}'/>">
        </label>
    </p>

    <p>
        <label>
            <input type="checkbox" name="available"
                   ${empty product or product.available ? 'checked' : ''}>
            Available on the menu
        </label>
    </p>

    <button type="submit">Save</button>
    <a href="${pageContext.request.contextPath}/admin/products">Cancel</a>
</form>

</body>
</html>
