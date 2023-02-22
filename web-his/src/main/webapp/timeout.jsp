<%--
  Created by IntelliJ IDEA.
  User: HW
  Date: 2022/12/10
  Time: 19:52
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>登录失效</title>
</head>
<body>
<h2>还未登录或会话超时，请重新登录.....</h2>
<script>
    setTimeout(function (){
        location.href='login.jsp';
    },1500)
</script>
</body>
</html>
