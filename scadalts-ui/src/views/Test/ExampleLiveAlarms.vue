<template>
	<div>
		<h4>Example of the use of the live alarms component</h4>

		<div class="container">
			<div class="row">
				<div class="col-xs-4">
					Describe: <br />A simple test to see if the value of the component is displayed.
				</div>
				<div class="col-xs-8">
					<LiveAlarms />
				</div>
			</div>
		</div>
	</div>
</template>

<script>
import LiveAlarms from '@/components/graphical_views/AlarmsComponent.vue';

export default {
	name: 'example-live-alarms',
	components: {
		LiveAlarms,
	},
};
</script>

<style scoped></style>
