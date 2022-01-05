<template>
	<v-app>
		<v-row class="wl-chart-settings">
			<v-col
				id="wl-chart-type-select"
				cols="8"
				md="3"
				order="1"
				order-md="0"
				class="button-space-start"
			>
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

			<v-col
				id="wl-chart-type-settings"
				cols="12"
				md="7"
				order="2"
				order-md="0"
				v-if="!!watchListData"
			>
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
					:pointArray="watchListData.pointList"
					v-show="chartType === 'compare'"
				></ChartSettingsCompareComponent>
			</v-col>

			<v-col
				id="wl-chart-settings"
				cols="4"
				md="2"
				order="1"
				order-md="0"
				class="button-space"
			>
				<v-tooltip bottom>
					<template v-slot:activator="{ on, attrs }">
						<v-btn fab @click="updateSettings()" v-bind="attrs" v-on="on">
							<v-icon>mdi-refresh</v-icon>
						</v-btn>
					</template>
					<span>{{ $t('modernwatchlist.chart.panel.apply.tooltip') }}</span>
				</v-tooltip>

				<v-tooltip bottom>
					<template v-slot:activator="{ on, attrs }">
						<v-menu
							open-on-hover
							:close-on-content-click="false"
							bottom
							offset-y
							v-bind="attrs"
							v-on="on"
						>
							<template v-slot:activator="{ on, attrs }">
								<v-btn fab @click="toggleResolution" v-bind="attrs" v-on="on">
									<v-icon v-if="groupEnabled">mdi-chart-bar-stacked</v-icon>
									<v-icon v-else>mdi-chart-bar</v-icon>
								</v-btn>
							</template>
							<v-card v-if="!!config && !!config.configuration && groupEnabled">
								<v-card-text>
									<v-text-field
										v-model="config.configuration.xAxes[0].groupCount"
										type="number"
										:label="$t('modernwatchlist.chartseries.aggregation.count')"
										:hint="$t('modernwatchlist.chartseries.aggregation.count.hint')"
										persistent-hint
										:disabled="!config.configuration.xAxes[0].groupData"
									></v-text-field>
								</v-card-text>
								<v-card-actions>
									<v-btn block text color="primary" @click="updateValuesLimit"
										>Apply</v-btn
									>
								</v-card-actions>
							</v-card>
						</v-menu>
					</template>
					<span>{{ $t('modernwatchlist.chart.panel.resolution.tooltip') }}</span>
				</v-tooltip>

				<div v-if="config">
					<ChartSeriesSettingsComponent
						:series="config.getSeriesConfiguration()"
						:chartConfig="config.configuration"
						:watchListName="watchListData.id"
						:chartType="chartType"
						@saved="onSettingsSaved"
						@deleted="onSettingsDeleted"
					></ChartSeriesSettingsComponent>
				</div>
			</v-col>
		</v-row>
		<v-row v-if="!!warnMessage">
			<v-col cols="12">
				<v-alert
					@click="warnMessage = null"
					type="warning"
					transition="scale-transition"
					dense
				>
					{{ warnMessage }}
				</v-alert>
			</v-col>
		</v-row>
		<v-row no-gutters>
			<v-col cols="12" id="wl-chart-container">
				<div class="chartContainer" ref="chartdiv"></div>
			</v-col>
		</v-row>
		<v-snackbar v-model="response.status" :color="response.color">
			{{ response.message }}
		</v-snackbar>
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
			watchListData: { id: 1, pointList: [{ id: 1 }, { id: 2 }] },
			watchListPoins: [],
			pointCompare: '',
			response: {
				status: false,
				color: '',
				message: '',
			},
			warnMessage: null,
			liveValuesLimits: [500, 1000, 5000, 10000],
		};
	},

	computed: {
		groupEnabled() {
			if (!!this.config && !!this.config.configuration) {
				return this.config.configuration.xAxes[0].groupData;
			}
			return false;
		},
	},

	mounted() {
		this.$nextTick(() => {
			window.addEventListener('watchListChanged', this.onWatchListChanged);
		});
	},

	methods: {
		async initDefaultConfiguration() {
			this.config = new AmChartConfigurator(
				`${this.watchListData.id}${this.watchListData.xid}`,
				this.watchListPoins.length,
			)
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
				const pl = this.watchListPoins;
				for (let i = 0; i < pl.length; i++) {
					await this.config.createSeries(pl[i].id);
				}
			}

			this.config = this.config.build();
		},

		initChart() {
			this.chartClass = new AmChart(this.$refs.chartdiv, 'xychart', this.pointIds)
				.startTime(this.chartProperties.startDate)
				.endTime(this.chartProperties.endDate)
				.makeFromConfig(this.config.getConfiguration());

			const refreshRate = this.chartProperties.refreshRate;
			if (this.chartProperties.type === 'static') {
				this.chartClass.setApiAggregation(
					this.config.configuration.apiLimitValues,
					this.config.configuration.apiLimitFactor,
				);
			}
			if (!!refreshRate && refreshRate >= 5000) {
				this.chartClass.withLiveUpdate(refreshRate);
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
					this.response = {
						status: true,
						color: 'warning',
						message: e.message,
					};
				} else {
					this.response = {
						status: true,
						color: 'error',
						message: `Failed to load chart!: ${e.message}`,
					};
				}
			});
		},

		disposeChart() {
			if (!!this.chartClass) {
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
			if (this.watchListData.pointList.length === 0) {
				localStorage.removeItem(`MWL_${this.watchListData.id}${this.watchListData.xid}_P`);
			}
			this.initSettings();
			this.updateSettings(true);
		},

		updatePointList() {
			let points = [];
			this.watchListPoins = this.watchListData.pointList.filter(
				(p) => document.getElementById(`p${p.id}ChartCB`).checked,
			);
			this.watchListPoins.forEach((point) => {
				points.push(point.id);
			});
			this.pointIds = points.join(',');
		},

		saveConfiguration() {
			this.config.saveChartConfiguration();
		},

		deleteConfiguration() {
			this.config.deleteChartConfiguration();
		},

		initSettings() {
			let loadedData = JSON.parse(localStorage.getItem(`MWL_${this.watchListData.id}${this.watchListData.xid}_P`));
			this.chartType = !!loadedData ? loadedData.type : 'live';
		},

		loadSettings() {
			let component = this.getComponentType(this.chartType);
			this.chartProperties = component.loadSettings(`${this.watchListData.id}${this.watchListData.xid}`);
		},

		saveSettings() {
			let component = this.getComponentType(this.chartType);
			component.saveSettings(`${this.watchListData.id}${this.watchListData.xid}`);
		},

		async updateSettings(lazyLoading = false) {
			let initialLoad = true;
			if (lazyLoading) {
				initialLoad = !!localStorage.getItem(`MWL_${this.watchListData.id}${this.watchListData.xid}_P`);
			}
			if (initialLoad) {
				this.warnMessage = null;
				this.saveSettings();
				this.disposeChart();
				this.watchListData = await this.$store.dispatch(
					'getWatchListDetails',
					this.watchListData.id,
				);
				this.loadSettings();
				this.updatePointList();
				await this.initDefaultConfiguration();
				this.initChart();
				this.renderChart();
			}
		},

		onSettingsSaved() {
			this.saveConfiguration();
			this.updateSettings();
		},

		onSettingsDeleted() {
			this.deleteConfiguration();
			this.disposeChart();
			localStorage.removeItem(`MWL_${this.watchListData.id}_P`);
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

		onLimitExceeded(lastEntry) {
			this.warnMessage = `${this.$t('modernwatchlist.chart.error.livelimit')} Last entry from ${new Date(lastEntry.date).toLocaleString()}`;
		},

		toggleResolution() {
			if (!!this.config && !!this.config.configuration) {
				this.config.configuration.xAxes[0].groupData = !this.config.configuration.xAxes[0]
					.groupData;
				this.onSettingsSaved();
			}
		},

		updateValuesLimit() {
			this.onSettingsSaved();
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
.wl-chart-settings {
	max-height: 140px;
	margin-top: 10px;
}
@media (max-width: 960px) {
	.wl-chart-settings {
		max-height: 190px;
	}
}
</style>
