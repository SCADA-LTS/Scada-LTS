<template>
	<div>
		<div>
			<btn @click="openModal">
				<i class="glyphicon glyphicon-cog" />
			</btn>
		</div>
		<div v-if="isModalVisible" class="modal-block">
			<div class="modal-body-block">
				<div class="container">
					<div class="col-xs-12">
						<btn-group class="col-xs-8">
							<div class="col-xs-6">
								<btn
									block
									input-type="radio"
									v-model="aggregation.aggregate"
									:input-value="false"
									>{{ $t('modernwatchlist.chartseries.aggregation.all') }}</btn
								>
							</div>
							<div class="col-xs-6">
								<btn
									block
									input-type="radio"
									v-model="aggregation.aggregate"
									:input-value="true"
									>{{ $t('modernwatchlist.chartseries.aggregation.aggregate') }}</btn
								>
							</div>
						</btn-group>
						<div class="col-xs-4">
							<input class="form-control" type="number" v-model="aggregation.count" />
						</div>
					</div>
				</div>
				<div class="container margin-top" v-if="tempSeries">
					<div class="col-xs-12 justify-content-md-center">
						<tabs justified>
							<tab v-for="s in tempSeries" :title="s.name" :key="s.id">
								<div class="col-xs-12">
									<p class="col-xs-6">
										{{ $t('modernwatchlist.chartseries.name') }}
									</p>
									<div class="col-xs-6">
										<input class="form-control" v-model="s.name" />
									</div>
								</div>
								<div class="col-xs-12">
									<p class="col-xs-6">
										{{ $t('modernwatchlist.chartseries.series') }}
									</p>
									<btn-group class="col-xs-6">
										<btn
											class="col-xs-6"
											input-type="radio"
											input-value="LineSeries"
											v-model="s.type"
											>{{ $t('modernwatchlist.chartseries.series.line') }}</btn
										>
										<btn
											class="col-xs-6"
											input-type="radio"
											input-value="StepLineSeries"
											v-model="s.type"
											>{{ $t('modernwatchlist.chartseries.series.stepline') }}</btn
										>
									</btn-group>
								</div>
								<div class="col-xs-12">
									<p class="col-xs-6">
										{{ $t('modernwatchlist.chartseries.yaxis') }}
									</p>
									<btn-group class="col-xs-6">
										<btn
											class="col-xs-3"
											input-type="radio"
											input-value="valueAxis1"
											v-model="s.yAxis"
											>1</btn
										>
										<btn
											class="col-xs-3"
											input-type="radio"
											input-value="valueAxis2"
											v-model="s.yAxis"
											>2</btn
										>
										<btn
											class="col-xs-3"
											input-type="radio"
											input-value="logAxis"
											v-model="s.yAxis"
											>{{ $t('modernwatchlist.chartseries.yaxis.logarithmic') }}</btn
										>
										<btn
											class="col-xs-3"
											input-type="radio"
											input-value="binAxis"
											v-model="s.yAxis"
											>{{ $t('modernwatchlist.chartseries.yaxis.binary') }}</btn
										>
									</btn-group>
								</div>
								<div class="col-xs-12">
									<p class="col-xs-6">
										{{ $t('modernwatchlist.chartseries.xaxis') }}
									</p>
									<btn-group class="col-xs-6">
										<btn
											class="col-xs-6"
											input-type="radio"
											input-value="dateAxis1"
											@click="watchDateAxisChagne(s)"
											v-model="s.xAxis"
											>1</btn
										>
										<btn
											class="col-xs-6"
											input-type="radio"
											input-value="dateAxis2"
											@click="watchDateAxisChagne(s)"
											v-model="s.xAxis"
											>2</btn
										>
									</btn-group>
								</div>
								<div class="col-xs-12">
									<p class="col-xs-6">
										{{ $t('modernwatchlist.chartseries.stroke.color') }}
									</p>
									<div class="col-xs-6">
										<verte
											picker="square"
											v-model="s.stroke"
											model="hex"
											:showHistory="null"
											menuPosition="top"
										></verte>
									</div>
								</div>
								<div class="col-xs-12">
									<p class="col-xs-6">
										{{ $t('modernwatchlist.chartseries.stroke.width') }}
									</p>
									<div class="col-xs-6">
										<input class="form-control" type="number" v-model="s.strokeWidth" />
									</div>
								</div>
								<div class="col-xs-12">
									<p class="col-xs-6">
										{{ $t('modernwatchlist.chartseries.stroke.tension') }}
									</p>
									<div class="col-xs-6">
										<input
											class="form-control"
											type="number"
											:min="0"
											:max="1"
											v-model="s.tensionX"
										/>
									</div>
								</div>
								<div class="col-xs-12">
									<p class="col-xs-6">
										{{ $t('modernwatchlist.chartseries.fill.color') }}
									</p>
									<div class="col-xs-6">
										<verte
											picker="square"
											v-model="s.fill"
											model="hex"
											:showHistory="null"
											menuPosition="top"
										></verte>
									</div>
								</div>
								<div class="col-xs-12">
									<p class="col-xs-6">
										{{ $t('modernwatchlist.chartseries.fill.opacity') }}
									</p>
									<div class="col-xs-6">
										<input
											class="form-control"
											type="number"
											:min="0"
											:max="1"
											v-model="s.fillOpacity"
										/>
									</div>
								</div>
								<div class="col-xs-12">
									<p class="col-xs-6">
										{{ $t('modernwatchlist.chartseries.bullets') }}
									</p>
									<btn-group class="col-xs-6">
										<btn
											class="col-xs-6"
											input-type="radio"
											:input-value="5"
											v-model="s.bullets[0].circle.radius"
											>{{ $t('modernwatchlist.chartseries.show') }}</btn
										>
										<btn
											class="col-xs-6"
											input-type="radio"
											:input-value="0"
											v-model="s.bullets[0].circle.radius"
											>{{ $t('modernwatchlist.chartseries.hide') }}</btn
										>
									</btn-group>
								</div>
							</tab>
						</tabs>
					</div>
					<div class="col-xs-12 margin-top">
						<div class="col-xs-4">
							<btn block @click="close">{{
								$t('modernwatchlist.chartseries.close')
							}}</btn>
						</div>
						<div class="col-xs-4">
							<btn block @click="restore">{{
								$t('modernwatchlist.chartseries.restore')
							}}</btn>
						</div>
						<div class="col-xs-4">
							<btn type="primary" block @click="save">{{
								$t('modernwatchlist.chartseries.save')
							}}</btn>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>
<script>
import verte from 'verte';

export default {
	name: 'ChartSeriesSettingsComponent',

	components: {
		verte,
	},

	props: ['watchListName', 'series'],

	data() {
		return {
			tempSeries: undefined,
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
.modal-block {
	position: fixed;
	top: 0;
	left: 0;
	width: 100vw;
	height: 100vh;
	z-index: 100;
	background-color: rgba(0, 0, 0, 0.357);
}
.modal-body-block {
	margin: 5%;
	padding: 15px;
	background-color: white;
	border-radius: 10px;
	box-shadow: 1px 1px 6px 0px black;
}
.margin-top {
	margin-top: 15px;
}
</style>
