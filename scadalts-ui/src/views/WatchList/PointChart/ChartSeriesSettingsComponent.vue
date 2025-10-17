<template>
	<div class="chartVuetify">
		<v-btn small icon @click.stop="openModal()">
			<v-icon>mdi-cog</v-icon>
		</v-btn>
		<v-dialog v-model="isModalVisible" width="800">
			<v-card>
				<v-card-title> {{ $t('modernwatchlist.chartseries.title') }} </v-card-title>

				<v-card-text class="card-content">
					<v-row id="general-settings">
						<v-col>
							<v-select
								:label="$t('modernwatchlist.chartseries.valueslimit.label')"
								:hint="$t('modernwatchlist.chartseries.valueslimit.hint')"
								persistent-hint
								v-model="chartConfig.valuesLimit"
								:items="liveValuesLimits"
								@change="updateValuesLimit"
							></v-select>
						</v-col>
						<v-col cols="6" v-if="chartType==='static'">
							<v-switch v-model="chartConfig.chartApiAggregation" label="Server side aggregation">
							</v-switch>
						</v-col>
						<!-- <v-col cols="4" v-if="chartType==='static'">
							<v-text-field
								v-model="chartConfig.apiLimitFactor"
								type="number"
								:label="$t('systemsettings.amchart.limitFactor')"
								:hint="$t('systemsettings.amchart.limitFactor.hint')"
								persistent-hint
							></v-text-field>
						</v-col> -->
					</v-row>
					<v-row id="chart-series-settings">
						<v-col cols="12">
							<v-tabs v-model="tab" background-color="primary" dark>
								<v-tab v-for="(s, index) in series" :key="index">
									{{ s.name }}
								</v-tab>
							</v-tabs>
							<v-tabs-items v-model="tab">
								<v-tab-item v-for="(s, index) in series" :key="index">
									<v-card class="paggin-top-small series-settings limit-height">
										<v-row>
											<v-col md="6" sm="12" xs="12">
												<v-text-field
													v-model="s.name"
													:label="$t('modernwatchlist.chartseries.name')"
													dense
												></v-text-field>
											</v-col>
											<v-col md="6" sm="12" xs="12" class="button-space-double">
												<v-btn-toggle v-model="s.type" dense mandatory>
													<v-btn value="LineSeries">
														<v-icon>mdi-chart-areaspline-variant</v-icon>
															{{
														$t('modernwatchlist.chartseries.series.line')
													}}</v-btn>
													<v-btn value="StepLineSeries">
														<v-icon>mdi-chart-histogram</v-icon>
														{{
														$t('modernwatchlist.chartseries.series.stepline')
													}}</v-btn>
												</v-btn-toggle>
											</v-col>
										</v-row>

										<v-row>
											<v-col md="6" sm="12" xs="12">
												<v-row>
													<v-col md="12" sm="6" xs="6">
														<p>{{ $t('modernwatchlist.chartseries.xaxis') }}</p>
													</v-col>
													<v-col md="12" sm="6" xs="6" class="button-space-double">
														<v-btn-toggle v-model="s.xAxis" dense mandatory>
															<v-btn value="dateAxis1">1</v-btn>
															<v-btn value="dateAxis2">2</v-btn>
														</v-btn-toggle>
													</v-col>
												</v-row>
											</v-col>
											<v-col md="6" sm="12" xs="12">
												<v-row>
													<v-col md="12" sm="6" xs="6">
														<p>{{ $t('modernwatchlist.chartseries.yaxis') }}</p>
													</v-col>
													<v-col md="12" sm="6" xs="6">
														<v-btn-toggle v-model="s.yAxis" dense mandatory>
															<v-btn value="valueAxis1">1</v-btn>
															<v-btn value="valueAxis2">2</v-btn>
															<v-btn value="logAxis">{{
																$t('modernwatchlist.chartseries.yaxis.logarithmic')
															}}</v-btn>
															<v-btn value="binAxis">{{
																$t('modernwatchlist.chartseries.yaxis.binary')
															}}</v-btn>
														</v-btn-toggle>
													</v-col>
												</v-row>
											</v-col>
										</v-row>

										<v-row>
											<v-col md="6" sm="12" xs="12">
												<v-row>
													<v-col cols="12">
														<p>
															{{ $t('modernwatchlist.chartseries.stroke.color') }}
														</p>
													</v-col>
													<v-col cols="2">
														<v-menu offset-y :close-on-content-click="false">
															<template v-slot:activator="{ on }">
																<v-btn :color="s.stroke" v-on="on" block> </v-btn>
															</template>
															<v-color-picker
																v-model="s.stroke"
																:close-on-content-click="false"
															>
															</v-color-picker>
														</v-menu>
													</v-col>
													<v-col cols="4">
														<v-text-field
															v-model="s.strokeWidth"
															type="number"
															:label="$t('modernwatchlist.chartseries.stroke.width')"
															dense
														></v-text-field>
													</v-col>
													<v-col cols="6">
														<v-slider
															v-model="s.tensionX"
															persistent-hint
															:hint="$t('modernwatchlist.chartseries.stroke.tension')"
															max="1"
															min="0"
															step="0.1"
															thumb-label
															ticks
														></v-slider>
													</v-col>
												</v-row>
											</v-col>

											<v-col md="6" sm="12" xs="12">
												<v-row>
													<v-col cols="12">
														<p>{{ $t('modernwatchlist.chartseries.fill.color') }}</p>
													</v-col>
													<v-col cols="2">
														<v-menu offset-y :close-on-content-click="false">
															<template v-slot:activator="{ on }">
																<v-btn :color="s.fill" v-on="on" block> </v-btn>
															</template>
															<v-color-picker
																v-model="s.fill"
																:close-on-content-click="false"
															>
															</v-color-picker>
														</v-menu>
													</v-col>
													<v-col cols="10">
														<v-slider
															v-model="s.fillOpacity"
															persistent-hint
															:hint="$t('modernwatchlist.chartseries.fill.opacity')"
															max="1"
															min="0"
															step="0.01"
															thumb-label
														></v-slider>
													</v-col>
												</v-row>
											</v-col>
										</v-row>
									</v-card>
								</v-tab-item>
							</v-tabs-items>
						</v-col>
					</v-row>
				</v-card-text>
				<v-card-actions>
					<v-spacer></v-spacer>
					<v-btn text @click="restore()">{{
						$t('modernwatchlist.chartseries.close')
					}}</v-btn>
					<v-btn text @click="deleteFromStorage()">{{
						$t('modernwatchlist.chartseries.delete')
					}}</v-btn>
					<v-btn text color="primary" @click="save()">{{
						$t('modernwatchlist.chartseries.save')
					}}</v-btn>
				</v-card-actions>
			</v-card>
		</v-dialog>
	</div>
</template>
<script>
export default {
	name: 'ChartSeriesSettingsComponent',

	props: ['watchListName', 'series', 'chartConfig', 'chartType'],

	data() {
		return {
			tempSeries: undefined,
			tab: null,
			isModalVisible: false,
			liveValuesLimits: [100, 500, 1000, 5000, 10000, 20000],
		};
	},

	methods: {
		openModal() {
			this.tempSeries = this.copyObject(this.series);
			this.isModalVisible = true;
		},

		restore() {
			this.series = this.tempSeries;
			this.close();
		},

		close() {
			this.isModalVisible = false;
		},

		save() {
			this.close();
			this.$emit('saved');
		},

		deleteFromStorage() {
			this.$emit('deleted');
			this.close();
		},

		copyObject(object) {
			return JSON.parse(JSON.stringify(object));
		},

		watchDateAxisChagne(series) {
			if (series.xAxis == 'dateAxis2') {
				series.dataFields.dateX = 'date';
			} else if (series.xAxis == 'dateAxis1') {
				series.dataFields.dateX = 'date2';
			}
		},

		updateValuesLimit() {
			this.chartConfig.apiLimitValues = this.chartConfig.valuesLimit;
		}
	},
};
</script>
<style scoped>
.paggin-top-small {
	padding-top: 20px;
}
.series-settings p {
	padding: 5px;
	margin: 0;
	margin-bottom: 0;
}
.limit-height {
	height: 400px;
	overflow-y: auto;
	overflow-x: hidden;
}
</style>
<style>
.chartVuetify .v-application--wrap {
	min-height: 0;
}

.button-space-double > .v-item-group {
	width: 100%;
	display: flex;
}
.button-space-double > .v-item-group > button {
	width: 50%;
}
.card-content {
	max-height: 70vh;
	overflow: auto;
}
</style>
