<template>
	<v-app class="chartVuetify">
		<btn @click.stop="openModal()">
			<i class="glyphicon glyphicon-cog" />
		</btn>
		<v-dialog v-model="isModalVisible" width="1200">
			<v-card>
				<v-card-title> {{ $t('modernwatchlist.chartseries.title') }} </v-card-title>
				<v-card-text>
					<v-row>
						<v-col cols="3">
							<p>{{ $t('modernwatchlist.chartseries.aggregation.label') }}</p>
						</v-col>
						<v-col cols="6">
							<v-btn-toggle v-model="aggregation.aggregate" dense mandatory>
								<v-btn block :value="false">{{
									$t('modernwatchlist.chartseries.aggregation.all')
								}}</v-btn>
								<v-btn block :value="true">{{
									$t('modernwatchlist.chartseries.aggregation.aggregate')
								}}</v-btn>
							</v-btn-toggle>
						</v-col>
						<v-col cols="3">
							<v-text-field
								v-model="aggregation.count"
								type="number"
								:label="$t('modernwatchlist.chartseries.aggregation.count')"
								:disabled="!aggregation.aggregate"
								dense
							></v-text-field>
						</v-col>
					</v-row>
					<v-tabs v-model="tab" background-color="primary" dark>
						<v-tab v-for="s in tempSeries" :key="s">
							{{ s.name }}
						</v-tab>
					</v-tabs>
					<v-tabs-items v-model="tab">
						<v-tab-item v-for="s in tempSeries" :key="s">
							<v-card class="paggin-top-small series-settings limit-height">
								<v-row>
									<v-col cols="6">
										<v-text-field
											v-model="s.name"
											:label="$t('modernwatchlist.chartseries.name')"
											dense
										></v-text-field>
									</v-col>
									<v-col cols="6">
										<v-btn-toggle v-model="s.type" dense mandatory>
											<v-btn block value="LineSeries">{{
												$t('modernwatchlist.chartseries.series.line')
											}}</v-btn>
											<v-btn block value="StepLineSeries">{{
												$t('modernwatchlist.chartseries.series.stepline')
											}}</v-btn>
										</v-btn-toggle>
									</v-col>
									<v-col cols="6">
										<v-row>
											<v-col cols="3">
												<p>{{ $t('modernwatchlist.chartseries.xaxis') }}</p>
											</v-col>
											<v-col cols="9">
												<v-btn-toggle v-model="s.xAxis" dense mandatory>
													<v-btn block value="dateAxis1">1</v-btn>
													<v-btn block value="dateAxis2">2</v-btn>
												</v-btn-toggle>
											</v-col>
										</v-row>
									</v-col>
									<v-col cols="6">
										<v-row>
											<v-col cols="3">
												<p>{{ $t('modernwatchlist.chartseries.yaxis') }}</p>
											</v-col>
											<v-col cols="9">
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
									<v-col cols="6">
										<v-row>
											<v-col cols="12">
												<p>{{ $t('modernwatchlist.chartseries.stroke.color') }}</p>
											</v-col>

											<v-col cols="12">
												<v-color-picker
													v-model="s.stroke"
													dot-size="20"
													mode="rgba"
													:hide-canvas="!canvansVisible"
													width="500"
													height="100"
												></v-color-picker>
											</v-col>
											<v-col cols="4">
												<v-text-field
													v-model="s.strokeWidth"
													type="number"
													:label="$t('modernwatchlist.chartseries.stroke.width')"
													dense
												></v-text-field>
											</v-col>
											<v-col cols="8">
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
									<v-col cols="6">
										<v-row>
											<v-col cols="4">
												<p>{{ $t('modernwatchlist.chartseries.fill.color') }}</p>
											</v-col>
											<v-col cols="8">
												<v-spacer></v-spacer>
												<v-btn text small @click="canvansVisible = !canvansVisible">
													Toggle color selector
												</v-btn>
											</v-col>
											<v-col cols="12">
												<v-color-picker
													v-model="s.fill"
													dot-size="20"
													mode="rgba"
													:hide-canvas="!canvansVisible"
													width="500"
													height="100"
												></v-color-picker>
											</v-col>
											<v-col cols="12">
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
											<v-col cols="6">
												<p>{{ $t('modernwatchlist.chartseries.bullets') }}</p>
											</v-col>
											<v-col cols="6">
												<v-btn-toggle
													v-model="s.bullets[0].circle.radius"
													dense
													mandatory
												>
													<v-btn block :value="5">{{
														$t('modernwatchlist.chartseries.show')
													}}</v-btn>
													<v-btn block :value="0">{{
														$t('modernwatchlist.chartseries.hide')
													}}</v-btn>
												</v-btn-toggle>
											</v-col>
										</v-row>
									</v-col>
								</v-row>
							</v-card>
						</v-tab-item>
					</v-tabs-items>
				</v-card-text>
				<v-card-actions>
					<v-spacer></v-spacer>
					<v-btn text @click="close()">{{
						$t('modernwatchlist.chartseries.close')
					}}</v-btn>
					<v-btn text @click="restore()">{{
						$t('modernwatchlist.chartseries.restore')
					}}</v-btn>
					<v-btn color="primary" @click="save()">{{
						$t('modernwatchlist.chartseries.save')
					}}</v-btn>
				</v-card-actions>
			</v-card>
		</v-dialog>
	</v-app>
</template>
<script>
export default {
	name: 'ChartSeriesSettingsComponent',

	props: ['watchListName', 'series'],

	data() {
		return {
			tempSeries: undefined,
			tab: null,
			canvansVisible: false,
			isModalVisible: false,
			aggregation: {
				aggregate: true,
				count: 500,
			},
		};
	},

	mounted() {
		this.loadAggregation();
	},

	computed: {},

	methods: {
		openModal() {
			this.restore();
			this.isModalVisible = true;
		},

		restore() {
			this.tempSeries = this.copyObject(this.series);
		},

		close() {
			this.isModalVisible = false;
			this.saveAggregation();
		},

		save() {
			this.close();
			this.$emit('saved', this.tempSeries);
		},

		copyObject(object) {
			return JSON.parse(JSON.stringify(object));
		},

		watchDateAxisChagne(series) {
			console.log(series);
			if (series.xAxis == 'dateAxis2') {
				series.dataFields.dateX = 'date';
			} else if (series.xAxis == 'dateAxis1') {
				series.dataFields.dateX = 'date2';
			}
		},

		saveAggregation() {
			localStorage.setItem(
				`MWL_${this.watchListName}_A`,
				JSON.stringify(this.aggregation),
			);
		},

		loadAggregation() {
			let loadedData = JSON.parse(localStorage.getItem(`MWL_${this.watchListName}_A`));
			if (!!loadedData) {
				this.aggregation = loadedData;
			}
		},
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
</style>
