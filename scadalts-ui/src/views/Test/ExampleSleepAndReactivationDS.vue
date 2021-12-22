<template>
	<div>
		<h4>Example of the use of the SleepAndReactivationDS component</h4>

		<div class="container-fluid">
			<div class="row">
				<div class="col-xs-4">
					Describe: <br />A simple test to see if the value of the component is displayed.
				</div>
				<div class="col-xs-8">
					<SleepAndReactivationDS />
				</div>
			</div>
		</div>
	</div>
</template>

<script>
import SleepAndReactivationDS from '../../components/forms/SleepAndReactivationDS';

export default {
	name: 'example-sleep-and-reactivation-ds',
	components: {
		SleepAndReactivationDS,
	},
};
</script>

<style scoped></style>
