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
  <form action="<%=request.getContextPath()%>/user/import" method="post" enctype="multipart/form-data">
    <div class="loginBox" style="background:#fff;opacity:0.9">
      <div class="layui-header">
        <h2>添加用户</h2>
        <p style="padding: 10px;color: red;font-size: 14px;height: 14px">${param.f.equals("age")?'年龄不能为负数':(param.f.equals("uname")?'用户名重复':(param.f.equals("zjm")?'助记码重复':(param.f.equals("phone")?'电话重复':(param.f.equals("mail")?'邮箱重复':''))))}</p>
      </div>
      <div class="layui-form">
        <div class="layui-form-item">
          <input id="excel" onchange="toShowPath()" type="file" name="excel" style="width: 100px;height: 30px;position: absolute;opacity: 0">
          <button class="layui-btn layui-btn-normal layui-btn-sm">
            <i class="layui-icon layui-icon-upload-drag"></i>上传文件
          </button>
          <span id="file_path_msg" style="margin-left: 20px"></span>
        </div>
        <div class="layui-form-item">
          <a href="<%=request.getContextPath()%>/excel/users.xlsx" class="layui-btn layui-btn-primary layui-border-blue layui-btn-sm">
            <i class="layui-icon layui-icon-download-circle"></i>下载模板
          </a>
        </div>

        <div class="layui-form-item">
          <button class="layui-btn layui-btn-fluid" lay-submit lay-filter="user-login-submit">提 交</button>
        </div>
      </div>
    </div>
  </form>

</div>


<script src="<%=request.getContextPath()%>/layui/layui.js"></script>
<script>
function toShowPath(){
  layui.use('jquery',function(){
    var $=layui.$;
    var value=$('#excel').val();
    var i=value.lastIndexOf("\\");
    value=value.substring(i+1);
    $('#file_path_msg').html(value);
  })
}
</script>
</body>
</html>