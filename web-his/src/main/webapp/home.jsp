<%@page pageEncoding="UTF-8" %>
<html>
    <head>
		<meta charset="utf-8" />
		
        <link rel="stylesheet" href="layui/css/layui.css"/>
	<script src="layui/layui.js"></script>
		<style>
			.base-info .layui-panel{
				padding:30px;
				height:18px;
			}
			.title{
				float: left;
			}
			.result{
				float: right;
			}
		</style>
    </head>
    <body >
	
 

		<div class="layui-fluid" style="padding:15px ;">

			<div class="base-info layui-row layui-col-space15">
			  <div class="layui-col-md3">
				<div class="layui-block1 layui-card">
					<div class="layui-panel">
						<div class="title">个人接诊人数</div>
						<div class="result"><span class="layui-badge">20</span></div>
					</div>
				</div>
			  </div>
			  <div class="layui-col-md3">
				<div class="layui-block1">
					<div class="layui-panel">
						<div class="title">科室接诊人数</div>
						<div class="result"><span class="layui-badge">120</span></div>
					</div>
					
					
				</div>
			  </div>
			  <div class="layui-col-md3">
				<div class="layui-block1">
					<div class="layui-panel">
						<div class="title">全院接诊人数</div>
						<div class="result"><span class="layui-badge">520</span></div>
					</div>
				</div>
			  </div>
			  <div class="layui-col-md3">
				<div class="layui-block1">
					<div class="layui-panel">
						<div class="title">时间</div>
						<div class="result" style="font-weight:bold;font-size:16px;">2021-08-08 星期一</div>
					</div>
				</div>
			  </div>
			</div>

			<div class="layui-row" style="margin-top:20px;">
				<div class="layui-card">
					<div class="layui-card-header"><b>任务提醒</b><span class="layui-badge" style="left:5px;">10+</span></div>
					<div class="layui-card-body" style="height:200px;overflow: auto">
						<table class="layui-table">
							<colgroup>
								<col width="10%">
								<col width="90%">
							</colgroup>
							<thead>
							<tr>
								<th>序号</th>
								<th>内容</th>
							</tr>
							</thead>
							<tbody>
							<tr>
								<td>1</td>
								<td>提交08-04日的汇总报表</td>
							</tr>
							<tr>
								<td>2</td>
								<td>现有3个暂停的挂号任务</td>
							</tr>
							<tr>
								<td>3</td>
								<td>现有4个未结算的退费任务</td>
							</tr>
							</tbody>
						</table>
					</div>
				</div>
			</div>

			<div class="layui-row" style="margin-top:20px;">
				<div class="layui-col-md12">
					<div class="layui-panel">
						<div id="ech1" style="height:300px;background-color: #fff;"></div>
					</div>
				</div>
			</div>

			<div class="layui-row layui-col-space15" style="margin-top:20px;">
				<div class="layui-col-md8">
					<div class="layui-panel">
						<div id="ech2" style="height:300px;background-color: #fff;"></div>
					</div>
				</div>

				<div class="layui-col-md4">
					<div class="layui-panel">
						<div id="ech3" style="height:300px;background-color: #fff;"></div>
					</div>
				</div>
			</div>
		</div>		
	
	</body>
</html>

<script src="echarts/dist/echarts.min.js"></script>
<script>
	var chartDom = document.getElementById('ech1');
	var myChart = echarts.init(chartDom);
	var option;

	option = {
		title:{
			text:'7日内的就诊人数统计',
			// subtext:'',
			x: 'center',
			y: '17px',
			textStyle: {
				color: '#3A7BD5',
				fontSize: 16
			}
		},
		tooltip: {
			trigger: 'none',
			axisPointer: {
				type: 'cross'
			}
		},
		xAxis: {
			type: 'category',
			data: ['08-01', '08-02', '08-03', '08-04', '08-05', '08-06', '08-07']
		},
		yAxis: {
			type: 'value'
		},
		series: [{
			data: [500, 730, 424, 618, 535, 847, 560],
			type: 'line'
		}]
	};

	option && myChart.setOption(option);

	//-----------------------------------------

	var chartDom2 = document.getElementById('ech2');
	var myChart2 = echarts.init(chartDom2);
	var option2;

	option2 = {
		title:{
			text:'7日内科室接诊人数统计',
			// subtext:'',
			x: 'center',
			y: '17px',
			textStyle: {
				color: '#3A7BD5',
				fontSize: 16
			}
		},
		tooltip: {
			trigger: 'none',
			axisPointer: {
				type: 'cross'
			}
		},
		xAxis: {
			type: 'category',
			data: ['儿科', '内科', '外科', '脑外科', '妇科', '骨科']
		},
		yAxis: {
			type: 'value'
		},
		series: [{
			data: [542, 317, 256, 123, 422, 80],
			type: 'bar'
		}]
	};

	option2 && myChart2.setOption(option2);

	//---------------------------------------------------
	var chartDom3 = document.getElementById('ech3');
	var myChart3 = echarts.init(chartDom3);
	var option3;
	option3 = {
		title: {
			text: '7日内流行疾病患者统计',
			x:'center',
			y:'17px',
			textStyle: {
				color: '#3A7BD5',
				fontSize: 16
			}
		},
		tooltip: {
			trigger: 'item'
		},
		legend: {
			orient: 'vertical',
			left: 'left',
		},
		series: [
			{
				name: '疾病',
				type: 'pie',
				radius: '50%',
				data: [
					{value: 1048, name: '流行性感冒'},
					{value: 735, name: '过敏性鼻炎'},
					{value: 580, name: '小儿肺炎'},
					{value: 484, name: '心脑血管'},
					{value: 300, name: '其他'}
				],
				emphasis: {
					itemStyle: {
						shadowBlur: 10,
						shadowOffsetX: 0,
						shadowColor: 'rgba(0, 0, 0, 0.5)'
					}
				}
			}
		]
	};

	option3 && myChart3.setOption(option3);
</script>