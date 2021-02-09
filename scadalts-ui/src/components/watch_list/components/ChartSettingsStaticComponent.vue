<template>
	<div class="col-xs-12">
		<div class="flex-row flex-align-center col-xs-6">
			<label for="static-sd" class="small-padd">{{
				$t('modernwatchlist.settings.date.start')
			}}</label>
			<dropdown class="form-group">
				<div class="input-group">
					<input class="form-control" type="text" v-model="startDate" />
					<div class="input-group-btn">
						<btn class="dropdown-toggle">
							<i class="glyphicon glyphicon-calendar"></i>
						</btn>
					</div>
				</div>
				<template slot="dropdown">
					<li>
						<datepicker
							v-model="startDate"
							format="yyyy/MM/dd"
							:inline="true"
							:monday-first="true"
						/>
					</li>
				</template>
			</dropdown>
			<dropdown class="form-group">
				<div class="input-group">
					<div class="input-group-btn">
						<btn class="dropdown-toggle">
							<i class="glyphicon glyphicon-time"></i>
						</btn>
					</div>
				</div>
				<template slot="dropdown">
					<li style="padding: 10px">
						<time-picker v-model="startTime" :show-meridian="false" />
					</li>
				</template>
			</dropdown>
		</div>
		<div class="flex-row flex-align-center col-xs-6">
			<label for="static-ed" class="small-padd">{{
				$t('modernwatchlist.settings.date.end')
			}}</label>
			<dropdown class="form-group">
				<div class="input-group">
					<input class="form-control" type="text" v-model="endDate" />
					<div class="input-group-btn">
						<btn class="dropdown-toggle">
							<i class="glyphicon glyphicon-calendar"></i>
						</btn>
					</div>
				</div>
				<template slot="dropdown">
					<li>
						<datepicker
							v-model="endDate"
							format="yyyy MM dd"
							:inline="true"
							:monday-first="true"
						/>
					</li>
				</template>
			</dropdown>
			<dropdown class="form-group">
				<div class="input-group">
					<div class="input-group-btn">
						<btn class="dropdown-toggle">
							<i class="glyphicon glyphicon-time"></i>
						</btn>
					</div>
				</div>
				<template slot="dropdown">
					<li style="padding: 10px">
						<time-picker v-model="endTime" :show-meridian="false" />
					</li>
				</template>
			</dropdown>
		</div>
	</div>
</template>
<script>
import Datepicker from 'vuejs-datepicker';

export default {
	name: 'ChartSettingsStaticComponent',

	components: {
		Datepicker,
	},

	props: ['watchListName'],

	data() {
		return {
			CHART_TYPE: 'static',
			startDate: new Date(),
			startTime: new Date(),
			endDate: new Date(),
			endTime: new Date(),
		};
	},

	mounted() {
		this.loadSettings();
	},

	computed: {},

	methods: {
		applySettings() {
			let chartProperties = {
				type: this.CHART_TYPE,
				refreshRate: null,
				startDate: this.convertDate(this.startDate, this.startTime),
				endDate: this.convertDate(this.endDate, this.endTime),
			};
			this.saveSettings();
			return chartProperties;
		},

		loadSettings() {
			let loadedData = JSON.parse(localStorage.getItem(`MWL_${this.watchListName}_P`));
			if (!!loadedData) {
				if (loadedData.type === this.CHART_TYPE) {
					this.startDate = new Date(loadedData.startDate);
					this.startTime = new Date(loadedData.startTime);
					this.endDate = new Date(loadedData.endDate);
					this.endTime = new Date(loadedData.endTime);
				}
			}
		},

		saveSettings() {
			let saveData = {
				type: this.CHART_TYPE,
				startDate: this.startDate.toString(),
				startTime: this.startTime.toString(),
				endDate: this.endDate.toString(),
				endTime: this.endTime.toString(),
			};
			localStorage.setItem(`MWL_${this.watchListName}_P`, JSON.stringify(saveData));
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
<style scoped></style>
