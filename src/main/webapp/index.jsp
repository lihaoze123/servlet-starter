<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>Servlet Starter</title>
</head>
<body>
<h1>Servlet Starter</h1>
<p>Server Time: <%= java.time.LocalDateTime.now() %></p>
<h2>API Endpoints</h2>
<ul>
    <li>GET <a href="${pageContext.request.contextPath}/api/users">/api/users</a> (List all users)</li>
    <li>GET <a href="${pageContext.request.contextPath}/api/users?id=1">/api/users?id=1</a> (Get user by ID)</li>
    <li>POST <code>/api/users</code> (JSON body: <code>{"username":"charlie","email":"charlie@example.com"}</code>)</li>
</ul>
</body>
</html>
