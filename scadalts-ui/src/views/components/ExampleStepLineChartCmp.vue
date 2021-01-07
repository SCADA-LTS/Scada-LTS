<template>
	<div>
		<h4>Example of the use of the StepLineChart component</h4>

		<div class="container">
			<div class="row">
				<div class="col-xs-4">
					Describe: <br />A simple test to see if the chart is displayed.
				</div>
				<div class="col-xs-8">
					<StepLineChartComponent />
				</div>
			</div>
		</div>
	</div>
</template>

<script>
import StepLineChartComponent from '../../components/amcharts/StepLineChartComponent';

export default {
	name: 'example-step-line-chart-cmp',
	components: {
		StepLineChartComponent,
	},
};
</script>

<style scoped></style>
