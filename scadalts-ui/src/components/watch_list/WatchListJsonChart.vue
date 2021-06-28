<template>
	<v-app>
		<v-row>
			<v-col id="wl-chart-type-select" md="3" xs="3" class="button-space-start">
				<v-btn-toggle v-model="chartType">
					<v-tooltip bottom>
						<template v-slot:activator="{ on, attrs }">
							<v-btn value="live" v-bind="attrs" v-on="on">
								{{ $t('modernwatchlist.chart.panel.live') }}
							</v-btn>
						</template>
						<span>{{ $t('modernwatchlist.chart.panel.live.tooltip') }}</span>
					</v-tooltip>

					<v-tooltip bottom>
						<template v-slot:activator="{ on, attrs }">
							<v-btn value="static" v-bind="attrs" v-on="on">
								{{ $t('modernwatchlist.chart.panel.static') }}
							</v-btn>
						</template>
						<span>{{ $t('modernwatchlist.chart.panel.static.tooltip') }}</span>
					</v-tooltip>

					<v-tooltip bottom>
						<template v-slot:activator="{ on, attrs }">
							<v-btn value="compare" v-bind="attrs" v-on="on">
								{{ $t('modernwatchlist.chart.panel.compare') }}
							</v-btn>
						</template>
						<span>{{ $t('modernwatchlist.chart.panel.compare.tooltip') }}</span>
					</v-tooltip>
				</v-btn-toggle>
			</v-col>

			<v-col id="wl-chart-type-settings" md="7" xs="7" v-if="!!watchListData">
				<ChartSettingsLiveComponent
					ref="csLiveComponent"
					:watchListName="watchListData.id"
					v-show="chartType === 'live'"
				></ChartSettingsLiveComponent>

				<ChartSettingsStaticComponent
					ref="csStaticComponent"
					:watchListName="watchListData.id"
					v-show="chartType === 'static'"
				></ChartSettingsStaticComponent>

				<ChartSettingsCompareComponent
					ref="csCompareComponent"
					:watchListName="watchListData.id"
					:pointArray="watchListData.pointList"
					v-show="chartType === 'compare'"
				></ChartSettingsCompareComponent>
			</v-col>

			<v-col id="wl-chart-settings" md="2" xs="2" class="button-space">
				<v-tooltip bottom>
					<template v-slot:activator="{ on, attrs }">
						<v-btn fab @click="updateSettings()" v-bind="attrs" v-on="on">
							<v-icon>mdi-refresh</v-icon>
						</v-btn>
					</template>
					<span>{{ $t('modernwatchlist.chart.panel.apply.tooltip') }}</span>
				</v-tooltip>

				<div v-if="config">
					<ChartSeriesSettingsComponent
						:series="config.getSeriesConfiguration()"
						:chartConfig="config.configuration"
						:watchListName="watchListData.id"
						@saved="onSettingsSaved"
						@deleted="onSettingsDeleted"
					></ChartSeriesSettingsComponent>
				</div>	
			</v-col>

			<v-col cols="12"  id="wl-chart-container">
				<div class="chartContainer" ref="chartdiv"></div>
			</v-col>
		</v-row>
		
	</v-app>
</template>
<script>
import ChartSettingsLiveComponent from './components/ChartSettingsLiveComponent';
import ChartSettingsStaticComponent from './components/ChartSettingsStaticComponent';
import ChartSeriesSettingsComponent from './components/ChartSeriesSettingsComponent';
import ChartSettingsCompareComponent from './components/ChartSettingsCompareComponent';

import AmChartConfigurator from '../amcharts/AmChartConfigurator';
import AmChart from '../amcharts/AmChart';

export default {
	name: 'WatchListJsonChart',

	components: {
		ChartSettingsLiveComponent,
		ChartSettingsStaticComponent,
		ChartSeriesSettingsComponent,
		ChartSettingsCompareComponent,
	},

	props: [],

	data() {
		return {
			chartLoading: true,
			chartType: 'live',
			chartTypeBefore: null,
			chartClass: null,
			chartProperties: {
				type: 'live',
				startDate: '1-hour',
				endDate: null,
				refreshRate: 0,
			},
			pointIds: null,
			config: null,
			watchListData: {id: 1, pointList: [{id:1},{id:2}]},
			pointCompare: '',
		};
	},

	mounted() {
		this.$nextTick(() => {
			window.addEventListener('watchListChanged', this.onWatchListChanged);
		});
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

			if(this.chartProperties.type === 'compare') {
				const pl = this.chartProperties.comparePoints;
				await this.config.createSeries(pl[0].pointId);
				await this.config.createSeries(pl[1].pointId, 'valueAxis2', 'dateAxis2', 'date2');
			} else {
				const pl =  this.watchListData.pointList;
				for(let i = 0; i < pl.length; i++) {
					await this.config.createSeries(pl[i].id)
				};
			}
			
			this.config = this.config.build();
		},

		initChart() {
			this.chartClass = new AmChart(this.$refs.chartdiv, "xychart", this.pointIds)
				.startTime(this.chartProperties.startDate)
				.endTime(this.chartProperties.endDate)
				.makeFromConfig(this.config.getConfiguration());
			
			const refreshRate = this.chartProperties.refreshRate;
			if(!!refreshRate && refreshRate >= 5000) {
				this.chartClass.withLiveUpdate(refreshRate);
			}
			if(this.chartProperties.type === 'compare') {
				this.chartClass.compare();
			}
			this.chartClass = this.chartClass.build();
		},

		renderChart() {
			this.chartClass.createChart();
		},

		disposeChart() {
			if(!!this.chartClass) {
				this.chartClass.disposeChart();
			}
		},

		onWatchListChanged(event) {
			this.chartLoading = true;
			this.disposeChart();
			this.loadWatchList(Number(event.detail.wlId));
		},

		async loadWatchList(watchListId) {
			this.watchListData = await this.$store.dispatch('getWatchListDetails', watchListId);
			let points = [];
			this.watchListData.pointList.forEach(point => {
				points.push(point.id);
			})
			this.pointIds = points.join(',');
			this.pointCompare = this.watchListData.pointList.join(',');
			this.chartLoading = false;
			await this.initDefaultConfiguration();
			this.initChart();
			this.renderChart();
		},

		saveConfiguration() {
			this.config.saveChartConfiguration();
		},

		deleteConfiguration() {
			this.config.deleteChartConfiguration();
		},

		applySettings() {
			if (this.chartType === 'live') {
				this.chartProperties = this.copyObject(
					this.$refs.csLiveComponent.applySettings()
				);
			} else if (this.chartType === 'static') {
				this.chartProperties = this.copyObject(
					this.$refs.csStaticComponent.applySettings()
				);
			} else if (this.chartType === 'compare') {
				this.deleteConfiguration();
				this.chartProperties = this.copyObject(
					this.$refs.csCompareComponent.applySettings()
				);
			}
		},

		async updateSettings() {
			this.applySettings();
			this.disposeChart();
			await this.initDefaultConfiguration();
			this.initChart();
			this.renderChart();
		},

		onSettingsSaved() {
			this.saveConfiguration();
			this.updateSettings();
			
		},

		onSettingsDeleted() {
			this.deleteConfiguration();
			this.updateSettings();
		},

		copyObject(object) {
			return JSON.parse(JSON.stringify(object));
		},

		
	},
	beforeDestroy() {
		this.disposeChart();
	},
};
</script>
<style scoped>
.chartContainer {
	min-width: 650px;
	height: 600px;
}
p {
	text-align: center;
	padding-top: 10px;
}
.error {
	color: red;
}
.button-space {
	display: flex;
	justify-content: space-evenly;
}
.button-space-start {
	display: flex;
	justify-content: flex-start;
	padding-left: 30px;
}
.button-space-start > .v-item-group {
	width: 100%;
	display: flex;
}
.button-space-start > .v-item-group > button {
	width: 33%;
}
</style>
