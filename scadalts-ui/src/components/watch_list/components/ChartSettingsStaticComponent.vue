<template>
	<v-row>
		<v-col cols="3">
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
						v-model="startDate"
						:label="$t('modernwatchlist.settings.date.start')"
						prepend-icon="mdi-calendar"
						v-bind="attrs"
						v-on="on"
					></v-text-field>
				</template>
				<v-date-picker
					v-model="startDate"
					first-day-of-week="1"
					no-title
					scrollable
				></v-date-picker>
			</v-menu>
		</v-col>
		<v-col cols="3">
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
						v-model="startTime"
						:label="$t('modernwatchlist.settings.time.start')"
						prepend-icon="mdi-clock-time-four-outline"
						v-bind="attrs"
						v-on="on"
					></v-text-field>
				</template>
				<v-time-picker v-model="startTime" format="24hr" scrollable></v-time-picker>
			</v-menu>
		</v-col>

		<v-col cols="3">
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
						v-model="endDate"
						:label="$t('modernwatchlist.settings.date.end')"
						prepend-icon="mdi-calendar"
						v-bind="attrs"
						v-on="on"
					></v-text-field>
				</template>
				<v-date-picker
					v-model="endDate"
					first-day-of-week="1"
					no-title
					scrollable
				></v-date-picker>
			</v-menu>
		</v-col>
		<v-col cols="3">
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
						v-model="endTime"
						:label="$t('modernwatchlist.settings.time.end')"
						prepend-icon="mdi-clock-time-four-outline"
						v-bind="attrs"
						v-on="on"
					></v-text-field>
				</template>
				<v-time-picker v-model="endTime" format="24hr" scrollable></v-time-picker>
			</v-menu>
		</v-col>
	</v-row>
</template>
<script>
export default {
	name: 'ChartSettingsStaticComponent',

	data() {
		return {
			CHART_TYPE: 'static',
			startDate: '',
			startTime: '',
			endTime: '',
			endDate: '',
		};
	},

	methods: {
		loadSettings(watchListId) {
			let loadedData = JSON.parse(localStorage.getItem(`MWL_${watchListId}_P`));
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
			return {
				type: this.CHART_TYPE,
				refreshRate: null,
				startDate: this.concatenateDateTime(this.startDate, this.startTime),
				endDate: this.concatenateDateTime(this.endDate, this.endTime),
			};
		},

		saveSettings(watchListId) {
			let saveData = {
				type: this.CHART_TYPE,
				startDate: this.startDate,
				startTime: this.startTime,
				endDate: this.endDate,
				endTime: this.endTime,
			};
			localStorage.setItem(`MWL_${watchListId}_P`, JSON.stringify(saveData));
		},

		concatenateDateTime(date, time) {
			return `${date} ${time}`;
		},
	},
};
</script>
<style scoped>
.menuable__content__active .v-picker .v-picker__title {
	height: 100px;
}

.row + .row {
	margin-top: -12px;
}
</style>
