<template>
	<div class="col-xs-12">
		<div class="col-xs-12">
			<div class="col-xs-3">
				<select id="comp-el-1" v-model="pointSettings[0].dataPoint" class="form-control">
					<option v-for="p in pointList" v-bind:value="p" v-bind:key="p.id">
						{{ p.name }}: {{ p.xid }}
					</option>
				</select>
				<tooltip
					:text="$t('modernwatchlist.settings.compare.point1.tooltip')"
					target="#comp-el-1"
				/>
			</div>
			<div v-if="pointSettings[0].dataPoint" class="col-xs-9">
				<div class="col-xs-6">
					<dropdown class="form-group col-xs-11">
						<div class="input-group">
							<input
								class="form-control"
								type="text"
								v-model="pointSettings[0].startDate"
							/>
							<div class="input-group-btn">
								<btn class="dropdown-toggle">
									<i class="glyphicon glyphicon-calendar"></i>
								</btn>
							</div>
						</div>
						<template slot="dropdown">
							<li>
								<datepicker
									v-model="pointSettings[0].startDate"
									:format="formatDate"
									:inline="true"
									:monday-first="true"
								/>
							</li>
						</template>
					</dropdown>
					<dropdown class="form-group row">
						<div class="input-group">
							<div class="input-group-btn">
								<btn class="dropdown-toggle">
									<i class="glyphicon glyphicon-time"></i>
								</btn>
							</div>
						</div>
						<template slot="dropdown">
							<li style="padding: 10px">
								<time-picker
									v-model="pointSettings[0].startTime"
									:show-meridian="false"
								/>
							</li>
						</template>
					</dropdown>
				</div>
				<div class="col-xs-6">
					<dropdown class="form-group col-xs-11">
						<div class="input-group">
							<input
								class="form-control"
								type="text"
								v-model="pointSettings[0].endDate"
							/>
							<div class="input-group-btn">
								<btn class="dropdown-toggle">
									<i class="glyphicon glyphicon-calendar"></i>
								</btn>
							</div>
						</div>
						<template slot="dropdown">
							<li>
								<datepicker
									v-model="pointSettings[0].endDate"
									:format="formatDate"
									:inline="true"
									:monday-first="true"
								/>
							</li>
						</template>
					</dropdown>
					<dropdown class="form-group row">
						<div class="input-group">
							<div class="input-group-btn">
								<btn class="dropdown-toggle">
									<i class="glyphicon glyphicon-time"></i>
								</btn>
							</div>
						</div>
						<template slot="dropdown">
							<li style="padding: 10px">
								<time-picker v-model="pointSettings[0].endTime" :show-meridian="false" />
							</li>
						</template>
					</dropdown>
				</div>
			</div>
		</div>
		<div class="col-xs-12">
			<div class="col-xs-3">
				<select id="comp-el-2" v-model="pointSettings[1].dataPoint" class="form-control">
					<option v-for="p in pointList" v-bind:value="p" v-bind:key="p.id">
						{{ p.name }}: {{ p.xid }}
					</option>
				</select>
				<tooltip
					:text="$t('modernwatchlist.settings.compare.point2.tooltip')"
					target="#comp-el-2"
				/>
			</div>
			<div v-if="pointSettings[1].dataPoint" class="col-xs-9">
				<div class="col-xs-6">
					<dropdown class="form-group col-xs-11">
						<div class="input-group">
							<input
								class="form-control"
								type="text"
								v-model="pointSettings[1].startDate"
							/>
							<div class="input-group-btn">
								<btn class="dropdown-toggle">
									<i class="glyphicon glyphicon-calendar"></i>
								</btn>
							</div>
						</div>
						<template slot="dropdown">
							<li>
								<datepicker
									v-model="pointSettings[1].startDate"
									@selected="formatDate"
									:inline="true"
									:monday-first="true"
								/>
							</li>
						</template>
					</dropdown>
					<dropdown class="form-group row">
						<div class="input-group">
							<div class="input-group-btn">
								<btn class="dropdown-toggle">
									<i class="glyphicon glyphicon-time"></i>
								</btn>
							</div>
						</div>
						<template slot="dropdown">
							<li style="padding: 10px">
								<time-picker
									v-model="pointSettings[1].startTime"
									:show-meridian="false"
								/>
							</li>
						</template>
					</dropdown>
				</div>
				<div class="col-xs-6">
					<dropdown class="form-group col-xs-11">
						<div class="input-group">
							<input
								class="form-control"
								type="text"
								v-model="pointSettings[1].endDate"
							/>
							<div class="input-group-btn">
								<btn class="dropdown-toggle">
									<i class="glyphicon glyphicon-calendar"></i>
								</btn>
							</div>
						</div>
						<template slot="dropdown">
							<li>
								<datepicker
									v-model="pointSettings[1].endDate"
									@selected="formatDate"
									:inline="true"
									:monday-first="true"
								/>
							</li>
						</template>
					</dropdown>
					<dropdown class="form-group row">
						<div class="input-group">
							<div class="input-group-btn">
								<btn class="dropdown-toggle">
									<i class="glyphicon glyphicon-time"></i>
								</btn>
							</div>
						</div>
						<template slot="dropdown">
							<li style="padding: 10px">
								<time-picker v-model="pointSettings[1].endTime" :show-meridian="false" />
							</li>
						</template>
					</dropdown>
				</div>
			</div>
		</div>
	</div>
</template>
<script>
import Axios from 'axios';
import Datepicker from 'vuejs-datepicker';

export default {
	name: 'ChartSettingsCompareComonent',

	components: {
		Datepicker,
	},

	props: ['watchListName', 'pointId'],

	data() {
		return {
			CHART_TYPE: 'compare',
			pointList: [],
			pointSettings: [
				{
					dataPoint: undefined,
					startDate: new Date(),
					startTime: new Date(),
					endDate: new Date(),
					endTime: new Date(),
				},
				{
					dataPoint: undefined,
					startDate: new Date(),
					startTime: new Date(),
					endDate: new Date(),
					endTime: new Date(),
				},
			],
		};
	},

	mounted() {
		this.loadSettings();
		this.init();
	},

	computed: {},

	methods: {
		applySettings() {
			let chartProperties = {
				type: this.CHART_TYPE,
				refreshRate: null,
				startDate: null,
				endDate: null,
				comparePoints: [
					{
						pointId: this.pointSettings[0].dataPoint.id,
						startDate: this.convertDate(
							this.pointSettings[0].startDate,
							this.pointSettings[0].startTime,
						),
						endDate: this.convertDate(
							this.pointSettings[0].endDate,
							this.pointSettings[0].endTime,
						),
					},
					{
						pointId: this.pointSettings[1].dataPoint.id,
						startDate: this.convertDate(
							this.pointSettings[1].startDate,
							this.pointSettings[1].startTime,
						),
						endDate: this.convertDate(
							this.pointSettings[1].endDate,
							this.pointSettings[1].endTime,
						),
					},
				],
			};
			this.saveSettings();
			return chartProperties;
		},

		loadSettings() {
			let loadedData = JSON.parse(localStorage.getItem(`MWL_${this.watchListName}_P`));
			if (!!loadedData) {
				if (loadedData.type === this.CHART_TYPE) {
					for (let i = 0; i < loadedData.pointSettings.length; i++) {
						this.pointSettings[i].dataPoint = loadedData.pointSettings[i].dataPoint;
						this.pointSettings[i].startDate = new Date(
							loadedData.pointSettings[i].startDate,
						);
						this.pointSettings[i].startTime = new Date(
							loadedData.pointSettings[i].startTime,
						);
						this.pointSettings[i].endDate = new Date(loadedData.pointSettings[i].endDate);
						this.pointSettings[i].endTime = new Date(loadedData.pointSettings[i].endTime);
					}
				}
			}
		},

		saveSettings() {
			let saveData = {
				type: this.CHART_TYPE,
				pointSettings: [],
			};
			this.pointSettings.forEach((p) => {
				let point = {
					dataPoint: p.dataPoint,
					startDate: p.startDate,
					startTime: p.startTime,
					endDate: p.endDate,
					endTime: p.endTime,
				};
				saveData.pointSettings.push(point);
			});
			console.debug('TRYING TO SAVE', saveData);
			localStorage.setItem(`MWL_${this.watchListName}_P`, JSON.stringify(saveData));
		},

		async init() {
			await this.initDataPointsDetails();
		},

		initDataPointsDetails() {
			return new Promise((resolve) => {
				let pointPromises = [];
				let pointArray = this.pointId.split(',');
				pointArray.forEach((id) => {
					pointPromises.push(this.getDataPointDetails(id));
				});

				Promise.all(pointPromises).then(() => {
					resolve(true);
				});
			});
		},

		getDataPointDetails(pointId) {
			return new Promise((resolve) => {
				Axios.get(`./api/point_value/getValue/id/${pointId}`).then((r) => {
					let pointDetails = {
						id: pointId,
						xid: r.data.xid,
						name: r.data.name,
						type: r.data.type,
					};
					this.pointList.push(pointDetails);
					resolve(true);
				});
			});
		},

		convertDate(date, time) {
			if (!(time instanceof Date)) {
				if (time === undefined || time === null) {
					time = new Date();
				} else {
					time = new Date(time);
				}
			}
			let dateString = this.formatDate(new Date(date));
			let timeString = `${time.getHours()}:${time.getMinutes()}`;
			return `${dateString} ${timeString}`;
		},

		formatDate(date) {
			return (
				date.getUTCFullYear() +
				'/' +
				('0' + (date.getUTCMonth() + 1)).slice(-2) +
				'/' +
				('0' + date.getUTCDate()).slice(-2)
			);
		},
	},
};
</script>
<style></style>
