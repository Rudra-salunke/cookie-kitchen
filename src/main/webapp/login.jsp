<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Login</title>
</head>
<body>
<c:if test="${not empty error}">
    <p style="color:red;">${error}</p>
</c:if>
<h1>Login</h1>
<form method="post" action="login">
    <table>
        <tr>
            <td>Email:</td>
            <td><input type="email" name="email" required/></td>
        </tr>
        <tr>
            <td>Password:</td>
            <td><input type="password" name="password" required/></td>
        </tr>
        <tr>
            <td><input type="submit" value="Login"/></td>
        </tr>
    </table>
</form>
</body>
</html>