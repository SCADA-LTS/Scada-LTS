<template>
	<div style="min-height: 60vh">
		<h4>Example of the use of the AmCharts component</h4>

		<div class="container">
			<div class="row">
				<div class="col-xs-12">
					Describe: <br />A simple test to see if the value of the component is displayed.
				</div>
				<div class="col-xs-12" ref="chartdiv" style="height:60vh;">
					<!-- <RangeChartComponent pointIds="DP_107518,DP_469105" :useXid="true"  :aggregation="0"/> -->
				</div>
				<div @click="play">
					Start
				</div>
				<div @click="stop">
					Strop
				</div>
				<div @click="close">
					Close
				</div>
				<div @click="save">
					Save
				</div>
				<!-- <line-chart-component pointIds="1,2">
				</line-chart-component> -->
			</div>
		</div>
	</div>
</template>

<script>
// import RangeChartComponent from '../../components/amcharts/RangeChartComponent';
// import LineChartComponent from '../../components/amcharts/LineChartComponent';
import AmChartConfigurator from '../../components/amcharts/AmChartConfigurator';
import AmChart from '../../components/amcharts/AmChart';
import LineChartComponent from '../../components/amcharts/LineChartComponent.vue';

export default {
	name: 'example-line-chart',
	components: {
LineChartComponent
		// LineChartComponent,
		// RangeChartComponent,
	},

	data() {
		return {
			chartType: 'live',
			chartClass: null,
			chartProperties: {
				type: 'live',
				startDate: '2021-06-24',
				endDate: '2021-06-26',
				refreshRate: 6000,
			},
			pointIds: '1,2',
			watchListData: {id: 1, pointList: [{id:1},{id:2}]},
			aggegation: 0,
			config: null,
		};
	},

	mounted() {
		this.initChart()
	},

	methods: {

		

		async initDefaultConfiguration() {
			this.config = new AmChartConfigurator(this.watchListData.id)
				.createXAxis('dateAxis1', this.aggegation)
				.createXAxis('dateAxis2', this.aggegation, 'date2')
				.createYAxis('valueAxis1')
				.createYAxis('valueAxis2', 'valueAxis1')
				.createYAxis('logAxis', null, false, true)
				.createYAxis('binAxis', null, true);

			const pl =  this.watchListData.pointList;
			for(let i = 0; i < pl.length; i++) {
				await this.config.createSeries(pl[i].id)
			};

			this.config = this.config.build();
		},

		async initChart() {
			await this.initDefaultConfiguration();

			this.chartClass = new AmChart(this.$refs.chartdiv, "xychart", this.pointIds)
				.startTime(this.chartProperties.startDate)
				.endTime(this.chartProperties.endDate)
				.makeFromConfig(this.config.getConfiguration());

			const refreshRate = this.chartProperties.refreshRate;
			if(!!refreshRate && refreshRate >= 5000) {
				this.chartClass.withLiveUpdate(refreshRate);
			}
			this.chartClass = this.chartClass.build();

			this.renderChart();
		},

		renderChart() {
			this.chartClass.createChart();
		},

		play() {
			this.renderChart()
			// this.chartClass.startLiveUpdate();
		},

		stop() {
			this.chartClass.stopLiveUpdate()
		}, 

		close() {
			this.chartClass.disposeChart();

		},

		save() {
			this.config.saveChartConfiguration()
		}

	}
};
</script>

<style scoped></style>
