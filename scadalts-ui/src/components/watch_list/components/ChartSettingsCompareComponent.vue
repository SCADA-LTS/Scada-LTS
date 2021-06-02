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

			<v-col md="4" xs="4">
				<v-menu offset-y :close-on-content-click="false" class="wl-menu-time-picker">
					<template v-slot:activator="{ on }">
						<v-text-field
							v-on="on"
							:label="$t('modernwatchlist.settings.date.start')"
							:value="concatenateDateTime(point.startDate, point.startTime)"
							dense
						></v-text-field>
					</template>
					<v-date-picker first-day-of-week="1" v-model="point.startDate"></v-date-picker>
					<v-time-picker format="24hr" v-model="point.startTime"></v-time-picker>
				</v-menu>
			</v-col>

			<v-col md="4" xs="4">
				<v-menu offset-y :close-on-content-click="false" class="wl-menu-time-picker">
					<template v-slot:activator="{ on }">
						<v-text-field
							v-on="on"
							:label="$t('modernwatchlist.settings.date.end')"
							:value="concatenateDateTime(point.endDate, point.endTime)"
							dense
						></v-text-field>
					</template>
					<v-date-picker first-day-of-week="1" v-model="point.endDate"></v-date-picker>
					<v-time-picker format="24hr" v-model="point.endTime"></v-time-picker>
				</v-menu>
			</v-col>
		</v-row>
	</div>
</template>
<script>
export default {
	name: 'ChartSettingsCompareComonent',

	props: ['watchListName', 'pointArray'],

	data() {
		return {
			CHART_TYPE: 'compare',
			pointList: [],
			pointSettings: [
				{
					dataPoint: undefined,
					startDate: '',
					startTime: '',
					endDate: '',
					endTime: '',
				},
				{
					dataPoint: undefined,
					startDate: '',
					startTime: '',
					endDate: '',
					endTime: '',
				},
			],
		};
	},

	mounted() {
		this.loadSettings();
		this.init();
	},

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
			this.saveSettings();
			return chartProperties;
		},

		loadSettings() {
			let loadedData = JSON.parse(localStorage.getItem(`MWL_${this.watchListName}_P`));
			if (!!loadedData) {
				if (loadedData.type === this.CHART_TYPE) {
					for (let i = 0; i < loadedData.pointSettings.length; i++) {
						this.pointSettings[i].dataPoint = loadedData.pointSettings[i].dataPoint;
						this.pointSettings[i].startDate = loadedData.pointSettings[i].startDate
						this.pointSettings[i].startTime = loadedData.pointSettings[i].startTime
						this.pointSettings[i].endDate = loadedData.pointSettings[i].endDate;
						this.pointSettings[i].endTime = loadedData.pointSettings[i].endTime;
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
