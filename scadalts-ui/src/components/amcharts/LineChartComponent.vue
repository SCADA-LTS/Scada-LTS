<template>
	<div>
		<v-alert
			:type="errorMessage.type"
			dismissible
			v-if="!!errorMessage"
			transition="scale-transition"
			dense
		>
			{{ errorMessage.message }}
		</v-alert>
		<div
			v-bind:style="{ height: this.height + 'px', width: this.width + 'px' }"
			ref="chartdiv"
		></div>
	</div>
</template>
<script>
import AmCharts from './AmChart';

export default {
	name: 'LineChartComponent',

	props: {
		pointIds: { type: String, required: true },
		useXid: { type: Boolean },
		startDate: { type: String },
		endDate: { type: String },
		refreshRate: { type: Number },
		width: { type: String, default: "500" },
		height: { type: String, default: "400" },
		aggregation: { type: Number },
		showScrollbar: { type: Boolean },
		showLegend: { type: Boolean },
		showBullets: { type: Boolean },
		showExportMenu: { type: String },
		smoothLine: { type: Number },
	},

	data() {
		return {
			chartClass: undefined,
			errorMessage: undefined,
		};
	},

	mounted() {
		this.initChart();
	},

	beforeDestroy() {
		this.close()
	},

	methods: {
		initChart() {
			this.chartClass = new AmCharts(
				this.$refs.chartdiv,
				'xychart',
				this.pointIds
			).showCursor();

			if (!!this.useXid) {
				this.chartClass.xid();
			}
			if (!!this.startDate) {
				this.chartClass.startTime(this.startDate);
			}
			if (!!this.endDate) {
				this.chartClass.endTime(this.endDate);
			}
			if (!!this.refreshRate) {
				this.chartClass.withLiveUpdate(this.refreshRate);
			}
			if (!!this.aggregation) {
				if (this.aggregation === 0) {
					this.chartClass.useAggregation();
				} else {
					this.chartClass.useAggregation(this.aggregation);
				}
			}
			if (!!this.showScrollbar) {
				this.chartClass.showScrollbar();
			}
			if (!!this.showLegend) {
				this.chartClass.showLegend();
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
				if(e.message === 'No data from that range!') {
					this.errorMessage = {
						type: 'warning',
						message: e.message
					}
				} else {
					this.errorMessage = {
						type: 'error',
						message: `Failed to load chart!: ${e.message}`
					}
				}
			});
		},

		close() {
			this.chartClass.disposeChart();
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
/* .error {
	color: red;
} */
</style>
