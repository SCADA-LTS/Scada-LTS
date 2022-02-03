<template>
<v-row>
	<v-col xs="3">
		<v-text-field
			v-model="startTime"
			:label="$t('modernwatchlist.settings.live.time.label')"
			type="number"
		></v-text-field>
	</v-col>
	
	<v-col xs="3">
		<v-select
			v-model="startTimeMultiplier"
			:items="timeOptions"
			item-value="value"
			item-text="text"
		></v-select>
	</v-col>

	<v-col xs="6">
		<v-select
			v-model="refreshRate"
			:items="performanceOptions"
			item-value="value"
			item-text="text"
		></v-select>
	</v-col>
</v-row>
</template>
<script>
export default {
	name: 'ChartSettingsLiveComponent',

	data() {
		return {
			CHART_TYPE: 'live',
			startTime: 1,
			startTimeMultiplier: 'hour',
			timeOptions: [
				{
					id: 4,
					text: this.$t('modernwatchlist.settings.live.time.minute'),
					value: 'minute',
				},
				{
					id: 0,
					text: this.$t('modernwatchlist.settings.live.time.hour'),
					value: 'hour',
				},
				{
					id: 1,
					text: this.$t('modernwatchlist.settings.live.time.day'),
					value: 'day',
				},
				{
					id: 2,
					text: this.$t('modernwatchlist.settings.live.time.week'),
					value: 'week',
				},
				{
					id: 3,
					text: this.$t('modernwatchlist.settings.live.time.month'),
					value: 'month',
				},
			],
			refreshRate: 10000,
			performanceOptions: [
				{
					id: 0,
					text: this.$t('modernwatchlist.settings.live.performance.1'),
					value: 1000,
				},
				{
					id: 1,
					text: this.$t('modernwatchlist.settings.live.performance.2'),
					value: 2000,
				},
				{
					id: 2,
					text: this.$t('modernwatchlist.settings.live.performance.5'),
					value: 5000,
				},
				{
					id: 3,
					text: this.$t('modernwatchlist.settings.live.performance.10'),
					value: 10000,
				},
				{
					id: 4,
					text: this.$t('modernwatchlist.settings.live.performance.30'),
					value: 30000,
				},
				{
					id: 5,
					text: this.$t('modernwatchlist.settings.live.performance.60'),
					value: 60000,
				},
			],
		};
	},

	methods: {
		loadSettings(watchListId) {
			let loadedData = JSON.parse(localStorage.getItem(`MWL_${watchListId}_P`));
			if (!!loadedData) {
				if (loadedData.type === this.CHART_TYPE) {
					this.startTime = loadedData.startTime;
					this.startTimeMultiplier = loadedData.startTimeMultiplier;
					this.refreshRate = loadedData.refreshRate;
				}
			}
			return {
				type: this.CHART_TYPE,
				refreshRate: this.refreshRate,
				startDate: `${this.startTime}-${this.startTimeMultiplier}`,
				endDate: null,
			}
		},

		saveSettings(watchListId) {
			let saveData = {
				type: this.CHART_TYPE,
				startTime: this.startTime,
				startTimeMultiplier: this.startTimeMultiplier,
				refreshRate: this.refreshRate,
			};
			localStorage.setItem(`MWL_${watchListId}_P`, JSON.stringify(saveData));
		},
	},
};
</script>
<style scoped></style>
