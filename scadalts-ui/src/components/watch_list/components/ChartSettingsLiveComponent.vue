<template>
	<div class="col-xs-12">
		<div class="col-xs-3">
			<input
				type="number"
				id="live-sd"
				v-model="startTime"
				:placeholder="$t('modernwatchlist.settings.live.time.label')"
				class="form-control"
				min="1"
				max="31"
			/>
		</div>
		<div class="col-xs-3">
			<select v-model="startTimeMultiplier" class="form-control" id="live-rrs">
				<option
					v-for="option in timeOptions"
					v-bind:value="option.value"
					v-bind:key="option.id"
				>
					{{ option.text }}
				</option>
			</select>
		</div>
		<div class="col-xs-6">
			<select id="live-rr" v-model="refreshRate" class="form-control">
				<option
					v-for="option in performanceOptions"
					v-bind:value="option.value"
					v-bind:key="option.id"
				>
					{{ option.text }}
				</option>
			</select>
		</div>
	</div>
</template>
<script>
export default {
	name: 'ChartSettingsLiveComponent',

	props: ['watchListName'],

	data() {
		return {
			CHART_TYPE: 'live',
			startTime: 1,
			startTimeMultiplier: 'hour',
			timeOptions: [
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

	mounted() {
		this.loadSettings();
	},

	computed: {},

	methods: {
		applySettings() {
			let chartProperties = {
				type: this.CHART_TYPE,
				refreshRate: this.refreshRate,
				startDate: `${this.startTime}-${this.startTimeMultiplier}`,
				endDate: null,
			};
			//TODO to może iść do Vuex
			this.saveSettings();
			return chartProperties;
		},

		loadSettings() {
			let loadedData = JSON.parse(localStorage.getItem(`MWL_${this.watchListName}_P`));
			if (!!loadedData) {
				if (loadedData.type === this.CHART_TYPE) {
					this.startTime = loadedData.startTime;
					this.startTimeMultiplier = loadedData.startTimeMultiplier;
					this.refreshRate = loadedData.refreshRate;
				}
			}
		},

		saveSettings() {
			let saveData = {
				type: this.CHART_TYPE,
				startTime: this.startTime,
				startTimeMultiplier: this.startTimeMultiplier,
				refreshRate: this.refreshRate,
			};
			localStorage.setItem(`MWL_${this.watchListName}_P`, JSON.stringify(saveData));
		},
	},
};
</script>
<style scoped></style>
