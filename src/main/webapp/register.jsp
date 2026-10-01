<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Register</title>
</head>
<body>
<c:if test="${not empty error}">
    <p style="color:red;">${error}</p>
</c:if>
<form name="detail" method="post" action="register">
    <table>
        <tr>
            <td>Name:</td>
            <td><input type="text" name="name" required/></td>
        </tr>
        <tr>
            <td>Phone:</td>
            <td><input type="text" name="phone"/></td>
        </tr>
        <tr>
            <td>Email:</td>
            <td><input type="email" name="email" required/></td>
        </tr>
        <tr>
            <td>Password:</td>
            <td><input type="password" name="password" required/></td>
        </tr>
        <tr>
            <td><input type="checkbox" name="chk1" value="check1" required/> Agree to terms and conditions.</td>
        </tr>
        <tr>
            <td><input type="submit" name="submit" value="Register"/>
                <input type="reset" name="reset" value="Reset"/></td>
        </tr>
    </table>
</form>
</body>
</html>