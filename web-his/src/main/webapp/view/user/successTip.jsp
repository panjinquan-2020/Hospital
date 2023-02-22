<%--
  Created by IntelliJ IDEA.
  User: HW
  Date: 2022/12/15
  Time: 17:36
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script>
    alert('新增成功');
    location.href='<%=request.getContextPath()%>/user/list?page=1&rows=1';
</script>
