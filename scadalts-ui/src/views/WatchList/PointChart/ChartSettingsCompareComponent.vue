<template>
	<div>
		<v-row v-for="(point, index) in pointSettings" :key="index">
			<v-col md="4" xs="4">
				<v-select
					v-model="point.dataPoint"
					:items="pointList"
					item-text="xid"
					:return-object="true"
					dense
				>
					<template slot="selection" slot-scope="data">
						{{ data.item.xid }} - {{ data.item.name }}
					</template>
					<template slot="item" slot-scope="data">
						{{ data.item.xid }} - {{ data.item.name }}
					</template>
				</v-select>
			</v-col>

			<v-col cols="2">
				<v-menu
					ref="start-date-menu"
					:close-on-content-click="false"
					:nudge-right="40"
					transition="scale-transition"
					offset-y
					min-width="auto"
					attach
				>
					<template v-slot:activator="{ on, attrs }">
						<v-text-field
							v-model="point.startDate"
							:label="$t('modernwatchlist.settings.date.start')"
							prepend-icon="mdi-calendar"
							v-bind="attrs"
							v-on="on"
							dense
						></v-text-field>
					</template>
					<v-date-picker
						v-model="point.startDate"
						first-day-of-week="1"
						no-title
						scrollable
					></v-date-picker>
				</v-menu>
			</v-col>
			<v-col cols="2">
				<v-menu
					ref="start-time-menu"
					:close-on-content-click="false"
					:nudge-right="40"
					transition="scale-transition"
					offset-y
					max-width="290px"
					min-width="290px"
					attach
				>
					<template v-slot:activator="{ on, attrs }">
						<v-text-field
							v-model="point.startTime"
							:label="$t('modernwatchlist.settings.time.start')"
							prepend-icon="mdi-clock-time-four-outline"
							v-bind="attrs"
							v-on="on"
							dense
						></v-text-field>
					</template>
					<v-time-picker
						v-model="point.startTime"
						format="24hr"
						scrollable
					></v-time-picker>
				</v-menu>
			</v-col>

			<v-col cols="2">
				<v-menu
					ref="end-date-menu"
					:close-on-content-click="false"
					:nudge-right="40"
					transition="scale-transition"
					offset-y
					min-width="auto"
					attach
				>
					<template v-slot:activator="{ on, attrs }">
						<v-text-field
							v-model="point.endDate"
							:label="$t('modernwatchlist.settings.date.end')"
							prepend-icon="mdi-calendar"
							v-bind="attrs"
							v-on="on"
							dense
						></v-text-field>
					</template>
					<v-date-picker
						v-model="point.endDate"
						first-day-of-week="1"
						no-title
						scrollable
					></v-date-picker>
				</v-menu>
			</v-col>
			<v-col cols="2">
				<v-menu
					ref="end-time-menu"
					:close-on-content-click="false"
					:nudge-right="40"
					transition="scale-transition"
					offset-y
					max-width="290px"
					min-width="290px"
					attach
				>
					<template v-slot:activator="{ on, attrs }">
						<v-text-field
							v-model="point.endTime"
							:label="$t('modernwatchlist.settings.time.end')"
							prepend-icon="mdi-clock-time-four-outline"
							v-bind="attrs"
							v-on="on"
							dense
						></v-text-field>
					</template>
					<v-time-picker v-model="point.endTime" format="24hr" scrollable></v-time-picker>
				</v-menu>
			</v-col>
		</v-row>
	</div>
</template>
<script>
export default {
	name: 'ChartSettingsCompareComonent',

	props: ['pointArray'],

	data() {
		return {
			CHART_TYPE: 'compare',
			pointList: [],
			pointSettings: [
				{
					dataPoint: null,
					startDate: '',
					startTime: '',
					endDate: '',
					endTime: '',
				},
				{
					dataPoint: null,
					startDate: '',
					startTime: '',
					endDate: '',
					endTime: '',
				},
			],
		};
	},

	mounted() {
		this.init();
	},

	methods: {
		loadSettings(watchListId) {
			let loadedData = JSON.parse(localStorage.getItem(`MWL_${watchListId}_P`));
			if (!!loadedData) {
				if (loadedData.type === this.CHART_TYPE) {
					for (let i = 0; i < loadedData.pointSettings.length; i++) {
						this.pointSettings[i].dataPoint = loadedData.pointSettings[i].dataPoint;
						this.pointSettings[i].startDate = loadedData.pointSettings[i].startDate;
						this.pointSettings[i].startTime = loadedData.pointSettings[i].startTime;
						this.pointSettings[i].endDate = loadedData.pointSettings[i].endDate;
						this.pointSettings[i].endTime = loadedData.pointSettings[i].endTime;
					}
				}
			}
			return {
				type: this.CHART_TYPE,
				refreshRate: null,
				startDate: null,
				endDate: null,
				comparePoints: [
					{
						pointId: this.pointSettings[0].dataPoint.id,
						startDate: this.concatenateDateTime(
							this.pointSettings[0].startDate,
							this.pointSettings[0].startTime
						),
						endDate: this.concatenateDateTime(
							this.pointSettings[0].endDate,
							this.pointSettings[0].endTime
						),
					},
					{
						pointId: this.pointSettings[1].dataPoint.id,
						startDate: this.concatenateDateTime(
							this.pointSettings[1].startDate,
							this.pointSettings[1].startTime
						),
						endDate: this.concatenateDateTime(
							this.pointSettings[1].endDate,
							this.pointSettings[1].endTime
						),
					},
				],
			};
		},

		saveSettings(watchListId) {
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
			localStorage.setItem(`MWL_${watchListId}_P`, JSON.stringify(saveData));
		},

		async init() {
			await this.initDataPointsDetails();
		},

		initDataPointsDetails() {
			return new Promise((resolve) => {
				let pointPromises = [];
				this.pointArray.forEach((id) => {
					pointPromises.push(this.getDataPointDetails(id.id));
				});

				Promise.all(pointPromises).then(() => {
					resolve(true);
				});
			});
		},

		getDataPointDetails(pointId) {
			return new Promise((resolve) => {
				this.$store.dispatch('getDataPointValue', pointId).then((r) => {
					let pointDetails = {
						id: pointId,
						xid: r.xid,
						name: r.name,
						type: r.type,
					};
					this.pointList.push(pointDetails);
					resolve(true);
				}).catch(() => {
					resolve(false);
				});
			});
		},

		concatenateDateTime(date, time) {
			return `${date} ${time}`;
		},
	},
};
</script>
<style></style>
