<%@ page pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>添加用户</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/layui/css/layui.css" media="all">
    <style>
        html {
            background: #f2f2f2;
            height: 100%;
            position: relative;
        }

        .loginBox {
            width: 380px;
            margin: 110px auto;
        }

        .layui-header {
            text-align: center;
            padding: 20px;
            padding-bottom: 0;
            font-size: 20px;
        }

        .layui-form {
            padding: 20px;
        }

        .layui-input {
            padding-left: 38px;
            border-color: #eee;
            border-radius: 2px;
        }

        label.layui-icon {
            position: relative;
            width: 38px;
            height: 38px;
            top: 28px;
            left: 10px;
        }

    </style>
</head>
<body>

<div class="loginWin">
    <form action="<%=request.getContextPath()%>/user/save" method="post">
        <div class="loginBox" style="background:#fff;opacity:0.9">
            <div class="layui-header">
                <h2>添加用户</h2>
                <p style="padding: 10px;color: red;font-size: 14px;height: 14px">${param.f.equals("age")?'年龄不能为负数':(param.f.equals("uname")?'用户名重复':(param.f.equals("zjm")?'助记码重复':(param.f.equals("phone")?'电话重复':(param.f.equals("mail")?'邮箱重复':''))))}</p>
            </div>
            <div class="layui-form">
                <div class="layui-form-item">
                    <label class="layui-icon layui-icon-username" for="user-login-uname"></label>
                    <input type="text" name="uname" id="user-login-uname" lay-verify="required"
                           placeholder="用户名" class="layui-input" value="${requestScope.user==null?'':requestScope.user.uname}">
                </div>
                <div class="layui-form-item">
                    <label class="layui-icon layui-icon-note" for="user-login-truename"></label>
                    <input type="text" name="truename" id="user-login-truename" lay-verify="required"
                           placeholder="真实姓名" class="layui-input" value="${requestScope.user==null?'':requestScope.user.truename}">
                </div>
                <div class="layui-form-item">
                    <label class="layui-icon layui-icon-fonts-u" for="user-login-age"></label>
                    <input type="number" name="age" id="user-login-age" lay-verify="required"
                           placeholder="年龄" class="layui-input" value="${requestScope.user==null?'':requestScope.user.age}">
                </div>
                <div class="layui-form-item">
                    <label class="layui-icon layui-icon-male" for="user-login-sex" style="z-index: 999"></label>
                    <select name="sex" lay-verify="required" id="user-login-sex">
                        <option value="">性别</option>
                        <option value="0" ${requestScope.user==null&&requestScope.user.sex.equals("男")?"selected":''}>男</option>
                        <option value="1" ${requestScope.user==null&&requestScope.user.sex.equals("女")?"selected":''}>女</option>
                    </select>
                </div>
                <div class="layui-form-item">
                    <label class="layui-icon layui-icon-cellphone" for="user-login-phone"></label>
                    <input type="text" name="phone" id="user-login-phone" lay-verify="required"
                           placeholder="电话" class="layui-input" value="${requestScope.user==null?'':requestScope.user.phone}">
                </div>
                <div class="layui-form-item">
                    <label class="layui-icon layui-icon-email" for="user-login-mail"></label>
                    <input type="email" name="mail" id="user-login-mail" lay-verify="required"
                           placeholder="邮箱" class="layui-input" value="${requestScope.user==null?'':requestScope.user.mail}">
                </div>


                <div class="layui-form-item">
                    <button class="layui-btn layui-btn-fluid" lay-submit lay-filter="user-login-submit">确 认</button>
                </div>
            </div>
        </div>
    </form>

</div>


<script src="<%=request.getContextPath()%>/layui/layui.js"></script>
<script>

</script>
</body>
</html>