<%@page pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
    <head>
		<meta charset="utf-8" />
        <link rel="stylesheet" href="<%=request.getContextPath()%>/layui/css/layui.css"/>
		<style>
			.layui-table{margin:0;}
            fieldset{padding:10px;border:0;border-top:1px solid #ddd;margin:10px;}
            fieldset legend{font-size:14px;}
		</style>
    </head>
    <body style="padding: 20px;">
		<fieldset>
			<legend>用户列表</legend>
		</fieldset>

        <!-- ===============表格头部过滤组件+按钮组件===================== -->
        <div class="layui-form-item">
            <div class="layui-inline">
                <label class="layui-form-label">用户名：</label>
                <div class="layui-input-inline" style="width: 120px;">
                    <input type="text" id="uname" name="uname" value="${requestScope.vo.filter.uname}" class="layui-input">
                </div>
            </div>

            <div class="layui-inline">
                <label class="layui-form-label">电话：</label>
                <div class="layui-input-inline" style="width: 120px;">
                    <input type="text" id="phone"  name="phone" value="${requestScope.vo.filter.phone}" class="layui-input">
                </div>
            </div>

            <div class="layui-inline">
                <button onclick="toQuery()" class="layui-btn layui-btn-primary layui-border-blue layui-btn-sm" >
                    <i class="layui-icon">&#xe615;</i>    查询
                </button>

                <button onclick="toClearQuery()" class="layui-btn layui-btn-primary layui-border-blue layui-btn-sm">
                    <i class="layui-icon">&#xe615;</i>    清空查询
                </button>
				
				<a href="<%=request.getContextPath()%>/view/user/add.jsp" class="layui-btn layui-btn-primary layui-border-blue layui-btn-sm">
					<i class="layui-icon">&#xe61f;</i>    新建
				</a>

                <button onclick="allDelete()" class="layui-btn layui-btn-primary layui-border-blue layui-btn-sm">
                    <i class="layui-icon layui-icon-delete"></i>    批量删除
                </button>

                <a href="<%=request.getContextPath()%>/view/user/import.jsp" class="layui-btn layui-btn-primary layui-border-blue layui-btn-sm">
                    <i class="layui-icon">&#xe681;</i>    导入
                </a>

                <a href="" class="layui-btn layui-btn-primary layui-border-blue layui-btn-sm">
                    <i class="layui-icon">&#xe67d;</i>    导出
                </a>
            </div>
        </div>

        <!-- =====================表格===================== -->
        <div style="top:140px;position:absolute;right:20px;left:20px;border:1px solid #ddd;border-bottom:0;padding-right:18px;">
        <table class="layui-table" >
            <colgroup>
                <col width="4%">
                <col width="6%">
                <col width="8%">
                <col width="8%">
                <col width="8%">
                <col width="6%">
                <col width="6%">
                <col width="8%">
                <col width="8%">
                <col width="10%">
                <col width="10%">
                <col width="16%">
            </colgroup>
            <thead>
            <tr>
                <th class="layui-form"><input id="allBtn" type="checkbox" lay-skin="primary" /></th>
                <th>序号</th>
                <th>用户名</th>
                <th>助记码</th>
                <th>真实姓名</th>
                <th>年龄</th>
                <th>性别</th>
                <th>创建人</th>
                <th>创建时间</th>
                <th>修改人</th>
                <th>修改时间</th>
                <th>操作</th>
            </tr>
            </thead>
        </table>
        </div>
        <div style="top:180px;bottom:65px;position:absolute;right:20px;left:20px;border:1px solid #ddd;overflow-y:scroll;border-top:0;padding-right:0px;">
        <table class="layui-table" >
            <colgroup>
                <col width="4%">
                <col width="6%">
                <col width="8%">
                <col width="8%">
                <col width="8%">
                <col width="6%">
                <col width="6%">
                <col width="8%">
                <col width="8%">
                <col width="10%">
                <col width="10%">
                <col width="16%">
            </colgroup>
            <tbody>
            <c:forEach items="${requestScope.vo.data}" var="user" varStatus="i">


                <tr>
                    <th class="layui-form"><input class="uid" value="${user.uid}" type="checkbox" lay-skin="primary" /></th>
                    <td>${i.index+1}</td>
                    <td>${user.uname}</td>
                    <td>${user.zjm}</td>
                    <td>${user.truename}</td>
                    <td>${user.age}</td>
                    <td>${user.sex}</td>
                    <td>${user.create_uname}</td>
                    <td>${user.create_time}</td>
                    <td>${user.update_uname}</td>
                    <td>${user.update_time}</td>
                    <td>
                        <a href="<%=request.getContextPath()%>/user/editSelect?uid=${user.uid}"  class="layui-btn layui-btn-primary layui-border-orange    layui-btn-xs">
                            <i class="layui-icon">&#xe642;</i>    编辑
                        </a>
                        <button onclick="toDelete('${user.uid}')" class="layui-btn layui-btn-primary layui-border-red    layui-btn-xs">
                            <i class="layui-icon">&#xe640;</i>    删除
                        </button>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
		</div>
        <!-- ===============展示分页栏目===================== -->
		<div style="position:absolute;bottom:10px;">
			<div id="pagebar" ></div>
		</div>
    </body>
	
	
	<script src="<%=request.getContextPath()%>/layui/layui.js"></script>
	
    <script>
        layui.use(['laypage','jquery'],function(){
            var laypage = layui.laypage;
            var $=layui.$;

            laypage.render({
                elem:'pagebar',
                count:${requestScope.vo.total},
				limit:${requestScope.vo.rows},
                curr:${requestScope.vo.page},
                limits:[1,3,5,10],
				layout:['prev','page','next','count','limit'],
                jump:function(obj,first){
                    if(first){
                        //首次触发，啥也不做
                        return ;
                    }
                    //分页查询 可以获得要访问的页码
					//alert('翻页');
                    query(obj.curr,obj.limit,$('#uname').val(),$('#phone').val());
                }
            });
        });
        function toClearQuery(){
            layui.use('jquery',function () {
                var $=layui.$;
                $('#uname').val('');
                $('#phone').val('');
                query(1,${requestScope.vo.rows},$('#uname').val(),$('#phone').val());
            })
        }
        function toQuery(){
            //过滤查询 从第一页重新查
            layui.use('jquery',function () {
                var $=layui.$;
                query(1,${requestScope.vo.rows},$('#uname').val(),$('#phone').val());
            })

        }
        function query(page,rows,uname,phone){
            location.href='<%=request.getContextPath()%>/user/list?page='+page+'&rows='+rows+'&uname='+uname+'&phone='+phone;
        }
        function toDelete(uid){
                if(confirm('确定要删除么')){
                    location.href='<%=request.getContextPath()%>/user/delete?uid='+uid;
                }
        }
        layui.use('jquery',function (){
            var $=layui.$;
            $('#allBtn').next('div').click(function (){
                var class_str = $(this).attr('class');
                if(class_str.indexOf('layui-form-checked')!=-1){
                    $('.uid').next('div').addClass('layui-form-checked');
                }else {
                    $('.uid').next('div').removeClass('layui-form-checked');

                }

            })
        })
        function allDelete(){
            layui.use('jquery',function (){
                var $ = layui.$;
                var length = $('.uid~div.layui-form-checked').length;
                if (length==0){
                    alert('请选择要删除 的记录');
                    return;
                }
                if (confirm('是否确认删除所选中的'+length+'条记录')){
                    var uids='';
                    var inputs = $('.uid~div.layui-form-checked').prev();
                    inputs.each(function (i,e){
                        var uid = e.value;
                        uids+=uid+","
                    })
                    location.href='<%=request.getContextPath()%>/user/deletes?uids='+uids;
                }
            })
        }
    </script>
</html>