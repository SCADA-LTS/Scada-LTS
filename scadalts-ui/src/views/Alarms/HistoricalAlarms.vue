<template>
	<div class="historical-alarms">
		<HistoricalAlarmsComponent></HistoricalAlarmsComponent>
	</div>
</template>

<script>
import HistoricalAlarmsComponent from './HistoricalAlarmsComponent';

export default {
	el: '#alarms',
	name: 'alarms',
	components: {
		HistoricalAlarmsComponent,
	},
	methods: {},
};
</script>

<style scoped>
.historical-alarms {
	z-index: -1;
}
</style>
