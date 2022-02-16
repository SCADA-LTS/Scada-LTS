<template>
	<div class="datapointList">
		<v-row class="wl-chart-settings" dense>
			<v-col cols="12" class="flex jc--space-between">
				<v-btn-toggle v-model="chartType" dense>
					<v-tooltip bottom>
						<template v-slot:activator="{ on, attrs }">
							<v-btn value="live" v-bind="attrs" v-on="on">
								<v-icon>mdi-chart-line</v-icon>
								{{ $t('modernwatchlist.chart.panel.live') }}
							</v-btn>
						</template>
						<span>{{ $t('modernwatchlist.chart.panel.live.tooltip') }}</span>
					</v-tooltip>

					<v-tooltip bottom>
						<template v-slot:activator="{ on, attrs }">
							<v-btn value="static" v-bind="attrs" v-on="on">
								<v-icon>mdi-chart-bar</v-icon>
								{{ $t('modernwatchlist.chart.panel.static') }}
							</v-btn>
						</template>
						<span>{{ $t('modernwatchlist.chart.panel.static.tooltip') }}</span>
					</v-tooltip>

					<v-tooltip bottom>
						<template v-slot:activator="{ on, attrs }">
							<v-btn value="compare" v-bind="attrs" v-on="on">
								<v-icon>mdi-chart-gantt</v-icon>
								{{ $t('modernwatchlist.chart.panel.compare') }}
							</v-btn>
						</template>
						<span>{{ $t('modernwatchlist.chart.panel.compare.tooltip') }}</span>
					</v-tooltip>
				</v-btn-toggle>

				<div class="flex jc--flex-end">
					<v-tooltip bottom>
						<template v-slot:activator="{ on, attrs }">
							<v-btn small icon @click="updateSettings()" v-bind="attrs" v-on="on">
								<v-icon>mdi-refresh</v-icon>
							</v-btn>
						</template>
						<span>{{ $t('modernwatchlist.chart.panel.apply.tooltip') }}</span>
					</v-tooltip>

					<div v-if="config && !!config.getSeriesConfiguration">
						<ChartSeriesSettingsComponent
							:series="config.getSeriesConfiguration()"
							:chartConfig="config.configuration"
							:watchListName="activeWatchList.id"
							@saved="onSettingsSaved"
							@deleted="onSettingsDeleted"
						></ChartSeriesSettingsComponent>
					</div>
				</div>
			</v-col>

			<v-col id="wl-chart-type-settings" cols="12" v-if="!!activeWatchList">
				<ChartSettingsLiveComponent
					ref="csLiveComponent"
					v-show="chartType === 'live'"
				></ChartSettingsLiveComponent>

				<ChartSettingsStaticComponent
					ref="csStaticComponent"
					v-show="chartType === 'static'"
				></ChartSettingsStaticComponent>

				<ChartSettingsCompareComponent
					ref="csCompareComponent"
					:pointArray="activeWatchList.pointList"
					v-show="chartType === 'compare'"
				></ChartSettingsCompareComponent>
			</v-col>
		</v-row>
		<v-row no-gutters>
			<v-col cols="12" id="wl-chart-container">
				<div class="chartContainer" ref="chartdiv"></div>
			</v-col>
		</v-row>
	</div>
</template>
<script>
import AmChart from '../../../components/amcharts/AmChart';
import AmChartConfigurator from '../../../components/amcharts/AmChartConfigurator';

import ChartSettingsLiveComponent from './ChartSettingsLiveComponent';
import ChartSettingsStaticComponent from './ChartSettingsStaticComponent';
import ChartSeriesSettingsComponent from './ChartSeriesSettingsComponent';
import ChartSettingsCompareComponent from './ChartSettingsCompareComponent';

/**
 *
 *
 * @author Radoslaw Jajko <rjajko@softq.pl>
 * @version 1.0.0
 */
export default {
	name: 'PointChart',

	components: {
		ChartSettingsLiveComponent,
		ChartSettingsStaticComponent,
		ChartSeriesSettingsComponent,
		ChartSettingsCompareComponent,
	},

	mixins: [],

	props: {},

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
			// watchListData: {id: 1, pointList: [{id:1},{id:2}]}, -> this.activeWatchList.id
			pointCompare: '',
		};
	},

	computed: {
		pointList() {
			return this.$store.getters.getWatchListChartPoints;
		},

		activeWatchList() {
			return this.$store.state.watchListModule.activeWatchList;
		},
	},

	mounted() {},

	beforeDestroy() {
		this.disposeChart();
		this.config = null;
	},

	watch: {
		pointList(oldValue, newValue) {
			console.log(oldValue);
			if (oldValue.length !== newValue.length) {
				console.debug('ChartLOADDDED');
				this.init();
			}
		},
	},

	methods: {
		async init() {
			this.chartLoading = true;
			this.initSettings();
			this.loadSettings();
			this.chartLoading = false;
			await this.initDefaultConfiguration();
			this.initChart();
			this.renderChart();
		},

		async initDefaultConfiguration() {
			this.config = new AmChartConfigurator(this.activeWatchList.id)
				.createXAxis('dateAxis1', this.aggegation)
				.createXAxis('dateAxis2', this.aggegation, 'date2')
				.createYAxis('valueAxis1')
				.createYAxis('valueAxis2', 'valueAxis1')
				.createYAxis('logAxis', null, false, true)
				.createYAxis('binAxis', null, true);

			if (this.chartProperties.type === 'compare') {
				const pl = this.chartProperties.comparePoints;
				await this.config.createSeries(pl[0].pointId);
				await this.config.createSeries(pl[1].pointId, 'valueAxis2', 'dateAxis2', 'date2');
			} else {
				const pl = this.pointList;
				for (let i = 0; i < pl.length; i++) {
					await this.config.createSeries(pl[i].id);
				}
			}
			this.config = this.config.build();
		},

		initChart() {
			let pointIds = this.pointList.map((p) => p.id).join(',');
			this.chartClass = new AmChart(this.$refs.chartdiv, 'xychart', pointIds)
				.startTime(this.chartProperties.startDate)
				.endTime(this.chartProperties.endDate)
				.makeFromConfig(this.config.getConfiguration());

			const refreshRate = this.chartProperties.refreshRate;
			if (!!refreshRate && refreshRate >= 5000) {
				this.chartClass.withLiveUpdate(refreshRate);
			} else if (!!refreshRate && refreshRate === -1) {
				this.chartClass.withWebSocketUpdate();
			}
			if (this.chartProperties.type === 'compare') {
				this.chartClass.compare();
			}
			this.chartClass.setLiveValuesLimit(
				this.config.configuration.valuesLimit,
				this.onLimitExceeded,
			);
			this.chartClass = this.chartClass.build();
		},

		renderChart() {
			this.chartClass.createChart().catch((e) => {
				if (e.message === 'No data from that range!') {
					this.$store.dispatch('showCustomNotification', {
						text: e.message,
						type: 'warning',
					})
				} else {
					this.$store.dispatch('showErrorNotification', `Failed to load chart!: ${e.message}`);
				}
			});
		},

		disposeChart() {
			if (!!this.chartClass) {
				this.chartClass.disposeChart();
			}
		},

		saveConfiguration() {
			this.config.saveChartConfiguration();
		},

		deleteConfiguration() {
			this.config.deleteChartConfiguration();
		},

		initSettings() {
			let loadedData = JSON.parse(
				localStorage.getItem(`MWL_${this.activeWatchList.id}_P`)
			);
			this.chartType = !!loadedData ? loadedData.type : 'live';
		},

		loadSettings() {
			let component = this.getComponentType(this.chartType);
			this.chartProperties = component.loadSettings(this.activeWatchList.id);
		},

		saveSettings() {
			let component = this.getComponentType(this.chartType);
			component.saveSettings(this.activeWatchList.id);
		},

		async updateSettings() {
			this.saveSettings();
			this.disposeChart();
			this.loadSettings();
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

		getComponentType(type) {
			switch (type) {
				case 'live':
					return this.$refs.csLiveComponent;
				case 'static':
					return this.$refs.csStaticComponent;
				case 'compare':
					return this.$refs.csCompareComponent;
				default:
					throw new Error('Chart type not recognized!');
			}
		},
	},
};
</script>
<style scoped>
.chartContainer {
	min-width: 550px;
	height: 600px;
}
.flex {
	display: flex;
}
.jc--space-between {
	justify-content: space-between;
}
.jc--flex-end {
	justify-content: flex-end;
}
.wl-chart-settings {
	margin: 2px;
}
</style>
