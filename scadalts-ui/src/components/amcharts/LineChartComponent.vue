<template>
	<div>
		<p>{{ label }}</p>
		<div
			class="hello"
			v-bind:style="{ height: this.height + 'px', width: this.width + 'px' }"
			ref="chartdiv"
		></div>
		<div v-if="errorMessage">
			<p class="error">{{ errorMessage }}</p>
		</div>
		<div v-if="showReload">
			<button v-on:click="reload()">Reload</button>
		</div>
	</div>
</template>
<script>
import LineChart from './LineChart';

export default {
	name: 'LineChartComponent',
	props: [
		'pointId',
		'pointXid',
		'color',
		'label',
		'startDate',
		'endDate',
		'refreshRate',
		'width',
		'height',
		'polylineStep',
		'rangeValue',
		'rangeColor',
		'rangeLabel',
		'showScrollbarX',
		'showScrollbarY',
		'showLegend',
		'showReload',
	],
	data() {
		return {
			errorMessage: undefined,
			chartClass: undefined,
			isExportId: false,
		};
	},
	mounted() {
		this.generateChart();
	},
	methods: {
		generateChart() {
			if (Number(this.polylineStep) > 1) {
				LineChart.setPolylineStep(Number(this.polylineStep));
			}
			this.chartClass = new LineChart(this.$refs.chartdiv, this.color);
			this.chartClass.displayControls(
				this.showScrollbarX,
				this.showScrollbarY,
				this.showLegend,
			);
			if (
				this.pointXid !== undefined &&
				this.pointXid !== null &&
				(this.pointId === null || this.pointId === undefined)
			) {
				this.isExportId = true;
			}
			let promises = [];
			let points;
			if (this.isExportId) {
				points = this.pointXid.split(',');
			} else {
				points = this.pointId.split(',');
			}
			for (let i = 0; i < points.length; i++) {
				promises.push(
					this.chartClass.loadData(
						points[i],
						this.startDate,
						this.endDate,
						this.isExportId,
					),
				);
			}

			Promise.all(promises).then((response) => {
				for (let i = 0; i < response.length; i++) {
					if (response[i] !== 'done') {
						this.errorMessage = 'Point given with index [' + i + '] has not been loaded!';
					}
				}
				this.chartClass.showChart(); // Display Chart
				if (this.rangeValue !== undefined) {
					this.chartClass.addRangeValue(
						Number(this.rangeValue),
						this.rangeColor,
						this.rangeLabel,
					);
				}
				if (this.refreshRate != undefined) {
					this.chartClass.startLiveUpdate(Number(this.refreshRate), this.isExportId);
				}
			});
		},
		reload() {
			this.generateChart();
		},
	},
};
</script>
<style scoped>
.hello {
	min-width: 650px;
	height: 500px;
}
p {
	text-align: center;
	padding-top: 10px;
}
.error {
	color: red;
}
</style>
