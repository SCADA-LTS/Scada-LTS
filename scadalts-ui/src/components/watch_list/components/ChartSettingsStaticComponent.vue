<template>
	<v-row>
		<v-col md="6" xs="6">
			<v-menu offset-y :close-on-content-click="false" class="wl-menu-time-picker">
				<template v-slot:activator="{ on }">
					<v-text-field
						v-on="on"
						:label="$t('modernwatchlist.settings.date.start')"
						:value="concatenateDateTime(startDate, startTime)"
					></v-text-field>
				</template>
				<v-date-picker first-day-of-week="1" v-model="startDate"></v-date-picker>
				<v-time-picker format="24hr" v-model="startTime"></v-time-picker>
			</v-menu>
		</v-col>

		<v-col md="6" xs="6">
			<v-menu offset-y :close-on-content-click="false" class="wl-menu-time-picker">
				<template v-slot:activator="{ on }">
					<v-text-field 
						v-on="on" 
						:label="$t('modernwatchlist.settings.date.end')"
						:value="concatenateDateTime(endDate, endTime)"
					></v-text-field>
				</template>
				<v-date-picker first-day-of-week="1" v-model="endDate"></v-date-picker>
				<v-time-picker format="24hr" v-model="endTime"></v-time-picker>
			</v-menu>
		</v-col>
	</v-row>
</template>
<script>
export default {
	name: 'ChartSettingsStaticComponent',

	props: ['watchListName'],

	data() {
		return {
			CHART_TYPE: 'static',
			startDate: '',
			startTime: '',
			endTime: '',
			endDate: '',
		};
	},

	mounted() {
		this.loadSettings();
	},

	methods: {
		applySettings() {
			let chartProperties = {
				type: this.CHART_TYPE,
				refreshRate: null,
				startDate: this.concatenateDateTime(this.startDate, this.startTime),
				endDate: this.concatenateDateTime(this.endDate, this.endTime),
			};
			this.saveSettings();
			return chartProperties;
		},

		loadSettings() {
			let loadedData = JSON.parse(localStorage.getItem(`MWL_${this.watchListName}_P`));
			if (!!loadedData) {
				if (loadedData.type === this.CHART_TYPE) {
					this.startDate = loadedData.startDate;
					this.startTime = loadedData.startTime;
					this.endDate = loadedData.endDate;
					this.endTime = loadedData.endTime;
				}
			} else {
				let time = this.$date();
				this.endDate = time.format('YYYY-MM-DD');
				this.endTime = time.format('HH:mm');
				time = time.subtract(1, 'hour');
				this.startDate = time.format('YYYY-MM-DD');
				this.startTime = time.format('HH:mm');
			}
		},

		saveSettings() {
			let saveData = {
				type: this.CHART_TYPE,
				startDate: this.startDate,
				startTime: this.startTime,
				endDate: this.endDate,
				endTime: this.endTime,
			};
			localStorage.setItem(`MWL_${this.watchListName}_P`, JSON.stringify(saveData));
		},

		concatenateDateTime(date, time) {
			return `${date} ${time}`;
		}

	},
};
</script>
<style scoped>
.menuable__content__active .v-picker .v-picker__title {
	height: 100px;
}

.row+.row {
	margin-top: -12px;
}
</style>
