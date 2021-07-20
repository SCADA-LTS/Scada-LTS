<template>
	<v-app>
		<v-row id="chart-settings">
			<v-col cols="5">
				<v-menu offset-y :close-on-content-click="false">
					<template v-slot:activator="{ on }">
						<v-text-field v-on="on" label="Start Date" :value="startDate"></v-text-field>
					</template>
					<v-date-picker first-day-of-week="1" v-model="startDate"> </v-date-picker>
				</v-menu>
			</v-col>
			<v-col cols="5">
				<v-menu offset-y :close-on-content-click="false">
					<template v-slot:activator="{ on }">
						<v-text-field v-on="on" label="End Date" :value="endDate"></v-text-field>
					</template>
					<v-date-picker first-day-of-week="1" v-model="endDate"> </v-date-picker>
				</v-menu>
			</v-col>
			<v-col cols="2">
				<v-btn icon @click="reload()"> 
					<v-icon>mdi-refresh</v-icon>
				</v-btn>
			</v-col>
		</v-row>
		<v-row id="chart-container">
			<v-col cols="12">
				<div ref="chartReference"></div>
			</v-col>
		</v-row>
	</v-app>
</template>
<script>
import LineChart from './LineChart';

export default {
	name: 'RangeChartComponent',
	props: [
		'pointId',
		'pointXid',
		'color',
		'width',
		'height',
		'polylineStep',
		'rangeValue',
		'rangeColor',
		'rangeLabel',
		'showScrollbarX',
		'showScrollbarY',
		'showLegend',
		'aggregation',
	],
	data() {
		return {
			errorMessage: undefined,
			chartClass: undefined,
			isExportId: false,
			viewId: 0,
			startDate: '',
			endDate: '',
		};
	},
	mounted() {
		this.initConfiguration();
		this.generateChart();
	},
	methods: {
		async generateChart() {
			if (Number(this.polylineStep) > 1) {
				LineChart.setPolylineStep(Number(this.polylineStep));
			}
			this.chartClass = new LineChart(this.$refs.chartReference, this.color);
			this.chartClass.displayControls(
				this.showScrollbarX,
				this.showScrollbarY,
				this.showLegend
			);
			// Provide value aggregation mechanism to 
			// improve the performance for huge amount of data
			this.chartClass.aggregation = this.aggregation;

			const pointPromises = await Promise.all(this.loadPoints());
			const notLoadedPointIds = pointPromises.filter((res) =>  res !== 'done');
			this.errorMessage = `Points with index [${notLoadedPointIds.join(", ")}] has not been loaded!`;

			this.chartClass.showChart();

			if (!!this.rangeValue) {
				this.chartClass.addRangeValue(
					Number(this.rangeValue),
					this.rangeColor,
					this.rangeLabel
				);
			}
		},

		reload() {
			this.saveToLocalStorage();
			this.generateChart();
		},

		/**
		 * Load Points using ID or XID
		 *
		 * Load Points data based on them ID or Export ID if present.
		 * @returns pointPromises - response from API
		 */
		loadPoints() {
			this.isExportId = !!this.pointXid && !this.pointId;
			let points = this.isExportId ? this.pointXid.split(',') : this.pointId.split(',');
			let pointPromises = points.map((point) => {
				return this.chartClass.loadData(point, this.startDate, this.endDate, this.isExportId);
			});
			return pointPromises;
		},

		/**
		 * Get Graphical View ID
		 * Util method for saving unique chart data.
		 */
		getViewId() {
			const href = window.location.href.split('?')[1];
			if (!!href) {
				this.viewId = href.match(/\d+/)[0];
			}
		},

		initConfiguration() {
			this.getViewId();
			let data = this.loadFromLocalStorage()
			if(!!data[0] && !!data[1]) {
				this.startDate = data[0];
				this.endDate = data[1];
			} else {
				this.initialTime();
			}
		},

		saveToLocalStorage() {
			let baseKey = `GVRC_${this.viewId}`;
			localStorage.setItem(`${baseKey}-start-date`, this.startDate);
			localStorage.setItem(`${baseKey}-end-date`,  this.endDate);
		},

		loadFromLocalStorage() {
			let baseKey = `GVRC_${this.viewId}`;
			let startDate = localStorage.getItem(`${baseKey}-start-date`);
			let endDate = localStorage.getItem(`${baseKey}-end-date`);
			return [startDate, endDate];
		},

		initialTime() {
			let today = new Date();
			this.endDate = today.toISOString().slice(0, 10);
			today.setDate(today.getDate() - 1);
			this.startDate = today.toISOString().slice(0, 10);
		},
	},
};
</script>
<style scoped>
#chart-settings {
	flex: none;
}
#chart-container > * > div {
	min-width: 650px;
	height: 500px;
}
</style>
