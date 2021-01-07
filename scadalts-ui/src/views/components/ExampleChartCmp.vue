<template>
	<div style="min-height: 60vh">
		<h4>Example of the use of the AmCharts component</h4>

		<div class="container">
			<div class="row">
				<div class="col-xs-4">
					Describe: <br />A simple test to see if the value of the component is displayed.
				</div>
				<div class="col-xs-8">
					<LineChartComponent point-id="1" refresh-rate="10000" />
				</div>
			</div>
		</div>
	</div>
</template>

<script>
import LineChartComponent from '../../components/amcharts/LineChartComponent';

export default {
	name: 'example-line-chart',
	components: {
		LineChartComponent,
	},
};
</script>

<style scoped></style>
