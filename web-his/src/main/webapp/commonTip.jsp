<%--
  Created by IntelliJ IDEA.
  User: HW
  Date: 2022/12/15
  Time: 17:42
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script>
    alert('${param.msg}');
    location.href='<%=request.getContextPath()%>${param.url}';
</script>
