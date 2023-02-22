<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script>
    alert('新增成功');
    location.href='<%=request.getContextPath()%>/user/list?page=1&rows=1';
</script>
