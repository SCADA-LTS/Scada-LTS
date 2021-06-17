<template>
	<v-app>
		<v-row v-if="chartLoaded">
			<v-col xs="12" cols="12">
				<p>Modern Chart</p>
			</v-col>
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

			<v-col id="wl-chart-type-settings" md="7" xs="7">
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

				<div v-if="chartSeries">
					<ChartSeriesSettingsComponent
						:series="chartSeries"
						:watchListName="watchListData.id"
						@saved="seriesDetailsSaved"
					></ChartSeriesSettingsComponent>
				</div>
			</v-col>

			<v-col cols="12" xs="12" id="wl-chart-container">
				<div class="chartContainer" ref="chartdiv"></div>
			</v-col>
		</v-row>
		<v-row v-else>
			<v-skeleton-loader type="list-item-two-line"></v-skeleton-loader>
		</v-row>
	</v-app>
</template>
<script>
import Axios from 'axios';

import ChartSettingsLiveComponent from './components/ChartSettingsLiveComponent';
import ChartSettingsStaticComponent from './components/ChartSettingsStaticComponent';
import ChartSeriesSettingsComponent from './components/ChartSeriesSettingsComponent';
import ChartSettingsCompareComponent from './components/ChartSettingsCompareComponent';

import JsonChart from '../amcharts/JsonChart';

import {
	CHART_CONFIGURATION_TEMPLATE,
	CHART_DEFAULT_COLORS,
	CHART_SERIES_TEMPLATE,
} from './configuration';

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
			chartType: 'live',
			chartClass: null,
			chartProperties: null,
			chartConfiguration: null,
			chartSeries: [],
			activeColor: 0,
			watchListData: null,
			pointCompare: '',
			chartLoaded: false,
		};
	},

	mounted() {
		this.$nextTick(() => {
			window.addEventListener('watchListChanged', this.onWatchListChanged);
		});
	},

	methods: {
		onWatchListChanged(event) {
			this.chartLoaded = false;
			this.reset().then(() => {
				this.loadWatchList(Number(event.detail.wlId));
			});
		},

		loadWatchList(watchListId) {
			this.$store.dispatch('getWatchListDetails', watchListId).then((r) => {
				this.watchListData = r;
				this.pointCompare = r.pointList.join(',');
				this.chartLoaded = true;
				this.$nextTick(() => {
					this.init();
				});
			});
		},

		async initChart() {
			this.chartConfiguration = this.copyObject(CHART_CONFIGURATION_TEMPLATE);
			let chartSeriesData = this.loadChart();

			this.loadAggregation();

			if (!!chartSeriesData) {
				this.chartSeries = this.copyObject(chartSeriesData);
				this.chartConfiguration.series = this.copyObject(chartSeriesData);
			} else {
				await this.initChartSeries();
			}

			//Chart series must be defined to create the JsonChart!
			this.chartClass = new JsonChart(
				this.$refs.chartdiv,
				null,
				'.',
				this.chartConfiguration
			);

			await this.initChartData();

			this.chartClass.setupChart(this.chartType);

			if (!!this.chartProperties.refreshRate) {
				this.initLiveChart();
			}
		},

		initChartSeries() {
			console.debug('WLJCH::initChartSeries::Initializing chart series...');
			return new Promise((resolve) => {
				let pointPromises = [];

				if (this.chartProperties.type == 'compare') {
					this.chartProperties.comparePoints.forEach((p) => {
						pointPromises.push(this.initChartSeriesPoint(p.pointId));
					});
				} else {
					let pointArray = this.watchListData.pointList;
					pointArray.forEach((id) => {
						pointPromises.push(this.initChartSeriesPoint(id.id));
					});
				}

				Promise.all(pointPromises).then(() => {
					console.debug('WLJCH::initChartSeries::Series loaded!');
					this.chartConfiguration.series = this.copyObject(this.chartSeries);
					resolve(true);
				});
			});
		},

		initChartSeriesPoint(pointId) {
			return new Promise((resolve) => {
				Axios.get(`./api/point_value/getValue/id/${pointId}`).then((r) => {
					let s = this.copyObject(CHART_SERIES_TEMPLATE);
					s.id = `s${pointId}`;
					s.stroke = CHART_DEFAULT_COLORS[this.activeColor % CHART_DEFAULT_COLORS.length];
					s.fill = CHART_DEFAULT_COLORS[this.activeColor % CHART_DEFAULT_COLORS.length];
					if (r.data.type == 'MultistateValue') {
						s.tooltipText = '{name}: [bold]{valueY}[/]';
					} else {
						s.tooltipText = '{name}: [bold]{valueY}[/] ' + r.data.textRenderer.suffix;
					}

					s.name = r.data.name;
					s.dataFields.valueY = r.data.name;
					this.activeColor++;

					if (r.data.type == 'NumericValue') {
						s.yAxis = 'valueAxis1';
						s.type = 'LineSeries';
					} else if (r.data.type == 'BinaryValue') {
						s.yAxis = 'binAxis';
						s.type = 'StepLineSeries';
					}
					if (this.chartType == 'compare' && this.dualAxis) {
						s.xAxis = 'dateAxis2';
						s.dataFields.dateX = 'date2';
						this.dualAxis = false;
					}
					if (this.chartType == 'compare' && !this.dualAxis) {
						this.dualAxis = true;
					}

					this.chartSeries.push(s);
					resolve(true);
				});
			});
		},

		initChartData() {
			console.debug('WLJCH::initChartData::Initializing chart data...');
			return new Promise((resolve) => {
				let pointPromises = [];

				if (this.chartProperties.type == 'compare') {
					this.chartProperties.comparePoints.forEach((p) => {
						pointPromises.push(
							this.chartClass.loadData(p.pointId, p.startDate, p.endDate, false)
						);
					});
				} else {
					let pointArray = this.watchListData.pointList;
					pointArray.forEach((id) => {
						pointPromises.push(
							this.chartClass.loadData(
								id.id,
								this.chartProperties.startDate,
								this.chartProperties.endDate,
								false
							)
						);
					});
				}

				Promise.all(pointPromises).then(() => {
					console.debug('WLJCH::initChartData::Data loaded!');
					resolve(true);
				});
			});
		},

		initLiveChart() {
			this.chartClass.startLiveUpdate(Number(this.chartProperties.refreshRate), false);
		},

		loadChart() {
			console.debug('WLJCH::loadChart::Loading chart from memory...');
			let loadedData = JSON.parse(localStorage.getItem(`MWL_${this.watchListData.id}`));
			if (!!loadedData) {
				let chartType = JSON.parse(localStorage.getItem(`MWL_${this.watchListData.id}_P`))
					.type;
				if (!!chartType) {
					this.chartType = chartType;
				}
				if (this.chartType == 'live') {
					this.$refs.csLiveComponent.loadSettings();
				} else if (this.chartType == 'static') {
					this.$refs.csStaticComponent.loadSettings();
				} else if (this.chartType == 'compare') {
					this.$refs.csCompareComponent.loadSettings();
				}
				this.applySettings();
				console.debug('WLJCH::loadChart::Chart loaded!');
			} else {
				console.debug('WLJCH::loadChart::Chart was not loaded :(');
			}
			return loadedData;
		},

		saveChart() {
			console.debug('WLJCH::saveChart::Saving chart to memory...');
			if (!!this.chartSeries) {
				if (this.chartSeries.length != 0) {
					localStorage.setItem(
						`MWL_${this.watchListData.id}`,
						JSON.stringify(this.chartSeries)
					);
				}
			}
			console.debug('WLJCH::saveChart::Saved!');
		},

		loadAggregation() {
			let loadedData = JSON.parse(localStorage.getItem(`MWL_${this.watchListData.id}_A`));
			if (!!loadedData) {
				this.chartConfiguration.xAxes[0].groupData = loadedData.aggregate;
				this.chartConfiguration.xAxes[0].groupCount = loadedData.count;
				console.debug('WLJCH::loadAggregation::Aggregation detected!', loadedData);
			} else {
				console.debug(
					'WLJCH::loadAggregation::Aggregation not detected - using default settings.'
				);
			}
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
				this.chartProperties = this.copyObject(
					this.$refs.csCompareComponent.applySettings()
				);
			}
		},

		updateSettings() {
			this.applySettings();
			this.saveChart();

			if (this.chartSeries) {
				this.chartSeries = [];
			}

			this.initChart();
		},

		seriesDetailsSaved(series) {
			this.chartSeries = this.copyObject(series);
			this.updateSettings();
		},

		copyObject(object) {
			return JSON.parse(JSON.stringify(object));
		},

		init() {
			if (!!this.loadChart()) {
				this.initChart();
			}
		},

		//TODO: DELETE CHART FROM MEMORY (clear localStorage)

		reset() {
			return new Promise((resolve, reject) => {
				try {
					if (!!this.chartClass) {
						this.chartClass.chart.dispose();
					}
					this.chartClass = null;
					this.watchListData = null;
					this.chartSeries = [];
					resolve(true);
				} catch (error) {
					reject(error);
				}
			});
		},
	},
	beforeDestroy() {
		this.chartClass.chart.dispose();
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
