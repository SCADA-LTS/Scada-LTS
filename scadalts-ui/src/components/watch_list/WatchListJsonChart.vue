<template>
	<div>
		<div class="container-fluid">
			<div class="col-xs-12">
				<btn-group class="col-xs-3">
					<btn
						class="col-xs-4"
						input-type="radio"
						input-value="live"
						v-model="chartType"
						id="live-btn-1"
						>{{ $t('modernwatchlist.chart.panel.live') }}</btn
					>
					<tooltip
						:text="$t('modernwatchlist.chart.panel.live.tooltip')"
						target="#live-btn-1"
					/>
					<btn
						class="col-xs-4"
						input-type="radio"
						input-value="static"
						v-model="chartType"
						id="static-btn-1"
						>{{ $t('modernwatchlist.chart.panel.static') }}</btn
					>
					<tooltip
						:text="$t('modernwatchlist.chart.panel.static.tooltip')"
						target="#static-btn-1"
					/>
					<btn
						class="col-xs-4"
						input-type="radio"
						input-value="compare"
						v-model="chartType"
						id="compare-btn-1"
						>{{ $t('modernwatchlist.chart.panel.compare') }}</btn
					>
					<tooltip
						:text="$t('modernwatchlist.chart.panel.compare.tooltip')"
						target="#compare-btn-1"
					/>
				</btn-group>
				<div v-show="chartType === 'live'" class="col-xs-8">
					<ChartSettingsLiveComponent
						ref="csLiveComponent"
						:watchListName="watchlistName"
					></ChartSettingsLiveComponent>
				</div>
				<div v-show="chartType === 'static'" class="col-xs-8">
					<ChartSettingsStaticComponent
						ref="csStaticComponent"
						:watchListName="watchlistName"
					></ChartSettingsStaticComponent>
				</div>
				<div v-show="chartType === 'compare'" class="col-xs-8">
					<ChartSettingsCompareComponent
						ref="csCompareComponent"
						:watchListName="watchlistName"
						:pointId="pointId"
					></ChartSettingsCompareComponent>
				</div>
				<div class="col-xs-1">
					<btn class="dropdown-toggle" @click="updateSettings()" id="updateSettingsBtn">
						<i class="glyphicon glyphicon-refresh"></i>
					</btn>
					<tooltip
						:text="$t('modernwatchlist.chart.panel.apply.tooltip')"
						target="#updateSettingsBtn"
					/>
					<div v-if="chartSeries">
						<ChartSeriesSettingsComponent
							:series="chartSeries"
							:watchListName="watchlistName"
							@saved="seriesDetailsSaved"
						></ChartSeriesSettingsComponent>
					</div>
				</div>
			</div>
		</div>
		<div
			class="hello"
			v-bind:style="{ height: 600 + 'px', width: this.width + 'px' }"
			ref="chartdiv"
		></div>
	</div>
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

	props: [
		'pointId',
		'watchlistName',
		'rangeValue',
		'rangeColor',
		'rangeLabel',
		'showReload',
		'jsonConfig',
	],

	data() {
		return {
			chartType: 'live',
			chartClass: undefined,
			chartProperties: undefined,
			chartConfiguration: undefined,
			chartSeries: [],
			activeColor: 0,
		};
	},

	mounted() {
		this.init();
	},

	methods: {
		async initChart() {
			this.chartConfiguration = this.copyObject(CHART_CONFIGURATION_TEMPLATE);
			let chartSeriesData = this.loadChart();
			console.log(chartSeriesData);

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
				this.chartConfiguration,
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
					let pointArray = this.pointId.split(',');
					pointArray.forEach((id) => {
						pointPromises.push(this.initChartSeriesPoint(id));
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
							this.chartClass.loadData(p.pointId, p.startDate, p.endDate, false),
						);
					});
				} else {
					let pointArray = this.pointId.split(',');
					pointArray.forEach((id) => {
						pointPromises.push(
							this.chartClass.loadData(
								id,
								this.chartProperties.startDate,
								this.chartProperties.endDate,
								false,
							),
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
			let loadedData = JSON.parse(localStorage.getItem(`MWL_${this.watchlistName}`));
			if (!!loadedData) {
				let chartType = JSON.parse(localStorage.getItem(`MWL_${this.watchlistName}_P`))
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
						`MWL_${this.watchlistName}`,
						JSON.stringify(this.chartSeries),
					);
				}
			}
			console.debug('WLJCH::saveChart::Saved!');
		},

		loadAggregation() {
			let loadedData = JSON.parse(localStorage.getItem(`MWL_${this.watchlistName}_A`));
			if (!!loadedData) {
				this.chartConfiguration.xAxes[0].groupData = loadedData.aggregate;
				this.chartConfiguration.xAxes[0].groupCount = loadedData.count;
				console.debug('WLJCH::loadAggregation::Aggregation detected!', loadedData);
			} else {
				console.debug(
					'WLJCH::loadAggregation::Aggregation not detected - using default settings.',
				);
			}
		},

		applySettings() {
			if (this.chartType === 'live') {
				this.chartProperties = this.copyObject(
					this.$refs.csLiveComponent.applySettings(),
				);
			} else if (this.chartType === 'static') {
				this.chartProperties = this.copyObject(
					this.$refs.csStaticComponent.applySettings(),
				);
			} else if (this.chartType === 'compare') {
				this.chartProperties = this.copyObject(
					this.$refs.csCompareComponent.applySettings(),
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
					this.pointId = null;
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
