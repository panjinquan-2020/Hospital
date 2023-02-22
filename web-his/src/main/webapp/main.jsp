<%@ page pageEncoding="UTF-8" %>
<html>
    <head>
		<meta charset="utf-8" />
        <title>主页</title>
        <link rel="stylesheet" href="layui/css/layui.css"/>
        <style>
            .layui-tree-txt , .layui-tree-txt a{
                color:#eee;
            }
        </style>
    </head>
    <body>
    <div class="layui-layout layui-layout-admin">
        <div class="layui-header">
            <div class="layui-logo layui-hide-xs layui-bg-black">信息管理系统</div>
            <ul class="layui-nav layui-layout-right">
                <li class="layui-nav-item layui-hide layui-show-md-inline-block" style="margin-right:10px;">
                    <a href="javascript:;">
                        <img src="http://tva1.sinaimg.cn/crop.0.0.118.118.180/5db11ff4gw1e77d3nqrv8j203b03cweg.jpg" class="layui-nav-img">
                        欢迎：${sessionScope.user.uname}
                    </a>
                    <dl class="layui-nav-child">
                        <dd><a href="view/user/updatePwd.jsp" target="content">修改密码</a></dd>
                        <dd><a href="javascript:toExit()">退出</a></dd>
                    </dl>
                </li>
                
            </ul>
        </div>

        <div class="layui-side layui-bg-black  layui-side-menu">
            <!-- <div id="test1" class="demo-tree demo-tree-box"></div> -->
			<!-- 侧边菜单 -->
     
     
          <div class="layui-side-scroll">
          <ul class="layui-nav layui-nav-tree"   lay-shrink="all" lay-filter="layadmin-system-side-menu" >
            <li class="layui-nav-item">
              <a href="home.jsp" target="content" class="layui-this">
                <i class="layui-icon layui-icon-home"></i>
                <cite>主页</cite>
              </a>
            </li>
            
            <li class="layui-nav-item">
              <a href="javascript:;" lay-direction="2">
                <i class="layui-icon layui-icon-user"></i>
                <cite>权限管理</cite>
              </a>
              <dl class="layui-nav-child">
                <dd >
                  <a href="<%=request.getContextPath()%>/user/list?page=1&rows=1" target="content">用户管理</a>
                </dd>
                <dd >
                  <a href="list2.html" target="content">角色管理</a>
                </dd>
                <dd >
                  <a href="treeList.html" target="content">功能管理</a>
                </dd>
              </dl>
            </li>
			
			<li data-name="home2" class="layui-nav-item">
              <a href="javascript:;" >
                <i class="layui-icon layui-icon-list"></i>
                <cite>数据中心</cite>
              </a>
              <dl class="layui-nav-child">
                <dd >
                  <a lay-href="home/console.html">科室管理</a>
                </dd>
                <dd >
                  <a lay-href="home/homepage1.html">计量单位</a>
                </dd>
                <dd >
                  <a lay-href="home/homepage2.html">药品管理</a>
                </dd>
                <dd >
                  <a lay-href="home/homepage2.html">药品类别</a>
                </dd>
                <dd >
                  <a lay-href="home/homepage2.html">项目管理</a>
                </dd>
                <dd >
                  <a lay-href="home/homepage2.html">供应商管理</a>
                </dd>
                <dd >
                  <a lay-href="home/homepage2.html">仓库管理</a>
                </dd>
                <dd >
                  <a lay-href="home/homepage2.html">床位管理</a>
                </dd>
              </dl>
            </li>
			
			<li class="layui-nav-item">
              <a href="javascript:;">
                <i class="layui-icon layui-icon-template-1"></i>
                <cite>仓库管理</cite>
              </a>
              <dl class="layui-nav-child">
                <dd >
                  <a lay-href="home/console.html">进货单</a>
                </dd>
                <dd >
                  <a lay-href="home/homepage1.html">入库单</a>
                </dd>
                <dd >
                  <a lay-href="home/homepage2.html">出库单</a>
                </dd>
                <dd >
                  <a lay-href="home/homepage2.html">库存查询</a>
                </dd>
              </dl>
            </li>
  
            <li class="layui-nav-item">
              <a href="javascript:;">
                <i class="layui-icon layui-icon-app"></i>
                <cite>门诊管理</cite>
              </a>
              <dl class="layui-nav-child">
                <dd >
                  <a lay-href="home/console.html">挂号</a>
                </dd>
                <dd >
                  <a lay-href="home/homepage1.html">处方划价</a>
                </dd>
                <dd >
                  <a lay-href="home/homepage2.html">缴费</a>
                </dd>
              </dl>
            </li>
			
			<li class="layui-nav-item">
              <a href="javascript:;">
                <i class="layui-icon layui-icon-template"></i>
                <cite>药房管理</cite>
              </a>
              <dl class="layui-nav-child">
                <dd >
                  <a lay-href="home/console.html">发药</a>
                </dd>
                <dd >
                  <a lay-href="home/homepage1.html">退药</a>
                </dd>
                <dd >
                  <a lay-href="home/homepage2.html">药品申请</a>
                </dd>
                </dd>
                <dd >
                  <a lay-href="home/homepage2.html">药品返还</a>
                </dd>
              </dl>
            </li>
			
          </ul>
		  </div>

      
        </div>

        <div class="layui-body">
            <!-- 内容主体区域 -->
            <iframe width="100%" height="100%" frameborder="0" name="content" src="home.jsp" ></iframe>
		
        </div>

        <div class="layui-footer">
            <!-- 底部固定区域 -->
           <p align="center">&copy; 版权所有：</p>
        </div>
    </div>

	<script src="layui/layui.js"></script>
	<script>
		function toExit() {
            var b = confirm('食肉确认退出');
            if(b){
                location.href='exit';
            }
        }
	</script>
    <script>
        data1 = [{
            title: '功能1'
            ,id: 1
            ,children: [{
                title: '<a href="list1.html" target="content">功能11</a>'
                ,id: 1000
            },{
                title: '<a href="list2.html" target="content">功能12</a>'
                ,id: 1001
            },{
                title: '<a href="treeList.html" target="content">功能13</a>'
                ,id: 1002
            }]
        },{
            title: '功能2'
            ,id: 2
            ,children: [{
                title: '功能21'
                ,id: 2000
            },{
                title: '功能22'
                ,id: 2001
            },{
                title: '功能23'
                ,id: 2002
            }]
        },{
            title: '功能3'
            ,id: 3
            ,children: [{
                title: '功能31'
                ,id: 3000
            },{
                title: '功能32'
                ,id: 3001
            }]
        }];

        layui.use(['tree','jquery'],function(){
            var tree = layui.tree ;
            var $ = layui.$ ;
           
            tree.render({
                elem: '#test1' 
                ,data: data1   
            });
        })
    </script>
    
    </body>
</html>