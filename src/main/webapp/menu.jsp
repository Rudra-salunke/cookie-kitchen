<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Menu - Cookie Kitchen</title>
</head>
<body>
<h1>Our Cookies</h1>

<form method="get" action="menu">
    <input type="text" name="search" placeholder="Search cookies..."/>
    <button type="submit">Search</button>
</form>

<nav>
    <a href="${pageContext.request.contextPath}/menu"
       class="${empty selectedCategory && empty query ? 'active' : ''}">All</a>
    <c:forEach var="cat" items="${categories}">
        <a href="${pageContext.request.contextPath}/menu?category=${cat.id}"
           class="${selectedCategory == cat.id ? 'active' : ''}">
            <c:out value="${cat.name}"/>
        </a>
    </c:forEach>
</nav>

<c:choose>
    <c:when test="${empty products}">
        <p>No products found.</p>
    </c:when>
    <c:otherwise>
        <div style="display:flex; flex-wrap:wrap; gap:20px;">
            <c:forEach var="p" items="${products}">
                <div style="border:1px solid #ccc; padding:10px; width:200px;">
                    <img src="${p.imageUrl}" alt="${p.name}" style="width:100%;"/>
                    <h3>${p.name}</h3>
                    <p>${p.description}</p>
                    <p><strong>₹${p.price}</strong></p>
                    <p>Stock: ${p.stock}</p>
                    <a href="product?id=${p.id}">View Details</a>
                </div>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>

</body>
</html>