<template>
	<div style="min-height: 60vh">
		<h4>Example of the use of the AmCharts component</h4>

		<div class="container">
			<div class="row">
				<div class="col-xs-12">
					Describe: <br />A simple test to see if the value of the component is displayed.
				</div>
				<div class="col-xs-12">
					<RangeChartComponent pointIds="DP_107518,DP_469105" :useXid="true"  :aggregation="0"/>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
import RangeChartComponent from '../../components/amcharts/RangeChartComponent';
import LineChartComponent from '../../components/amcharts/LineChartComponent';

export default {
	name: 'example-line-chart',
	components: {
		LineChartComponent,
		RangeChartComponent,
	},
};
</script>

<style scoped></style>
