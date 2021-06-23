<template>
	<v-app>
		<v-row id="chart-settings">
			<v-col cols="12" v-if="!!errorMessage">
				<v-alert
					:type="errorMessage.type"
					dismissible
					v-if="!!errorMessage"
					transition="scale-transition"
					dense
					@input="updatedAlert"
				>
					{{ errorMessage.message }}
				</v-alert>
			</v-col>

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
import AmCharts from './AmChart.js';

export default {
	name: 'RangeChartComponent',

	props: {
		pointIds: { type: String, required: true },
		useXid: { type: Boolean },
		width: { type: String, default: '500' },
		height: { type: String, default: '400' },
		aggregation: { type: Number },
		showBullets: { type: Boolean },
		showExportMenu: { type: String },
		smoothLine: { type: Number },
	},

	data() {
		return {
			chartClass: undefined,
			errorMessage: undefined,
			viewId: 0,
			startDate: '',
			endDate: '',
		};
	},

	mounted() {
		this.initConfiguration();
		this.initChart();
	},
	methods: {
		initChart() {
			this.chartClass = new AmCharts(this.$refs.chartReference, 'xychart', this.pointIds)
				.showCursor()
				.startTime(this.startDate)
				.endTime(this.endDate)
				.showScrollbar()
				.showLegend();

			if (!!this.useXid) {
				this.chartClass.xid();
			}
			if (!!this.aggregation) {
				if (this.aggregation === 0) {
					this.chartClass.useAggregation();
				} else {
					this.chartClass.useAggregation(this.aggregation);
				}
			}
			if (!!this.showBullets) {
				this.chartClass.showBullets();
			}
			if (!!this.showExportMenu) {
				this.chartClass.showExportMenu(this.showExportMenu);
			}
			if (!!this.smoothLine) {
				this.chartClass.smoothLine(this.smoothLine);
			}
			this.chartClass = this.chartClass.build();

			this.chartClass.createChart().catch((e) => {
				if (e.message === 'No data from that range!') {
					this.errorMessage = {
						type: 'warning',
						message: e.message,
					};
				} else {
					this.errorMessage = {
						type: 'error',
						message: `Failed to load chart!: ${e.message}`,
					};
				}
			});
		},

		reload() {
			this.chartClass.disposeChart();
			this.saveToLocalStorage();
			this.initChart();
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
			let data = this.loadFromLocalStorage();
			if (!!data[0] && !!data[1]) {
				this.startDate = data[0];
				this.endDate = data[1];
			} else {
				this.initialTime();
			}
		},

		saveToLocalStorage() {
			let baseKey = `GVRC_${this.viewId}`;
			localStorage.setItem(`${baseKey}-start-date`, this.startDate);
			localStorage.setItem(`${baseKey}-end-date`, this.endDate);
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

		updatedAlert(event) {
			if (!event) {
				this.errorMessage = null;
			}
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
