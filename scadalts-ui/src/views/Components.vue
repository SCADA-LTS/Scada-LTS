<template>
	<div>
		<h1>Components</h1>
		<div class="container">
			<div class="row">
				<ul class="list-group">
					<li class="list-group-item">
						<router-link to="/example-test">Tests of the test component </router-link>
					</li>
					<li class="list-group-item">
						<router-link to="/example-isalive"
							>Tests of the IsAlive component</router-link
						>
					</li>
					<li class="list-group-item">
						<router-link to="/example-simple-component-svg"
							>Tests of the SimpleComponentSVG component</router-link
						>
					</li>
					<li class="list-group-item">
						<router-link to="/example-sleep-and-reactivation-ds"
							>Tests of the SleepAndReactivationDS component</router-link
						>
					</li>
					<li class="list-group-item">
						<router-link to="/example-cmp">Tests of the CMP component</router-link>
					</li>
					<li class="list-group-item">
						<router-link to="/example-export-import-point-hierarchy"
							>Tests of the ExportImportPointHierarchy component</router-link
						>
					</li>
					<li class="list-group-item">
						<router-link to="/example-chart-cmp"
							>Tests of the AmCharts component</router-link
						>
					</li>
					<li class="list-group-item">
						<router-link to="/example-step-line-chart-cmp"
							>Tests of the StepLine Chart component</router-link
						>
					</li>
					<li class="list-group-item">
						<router-link to="/example-live-alarms"
							>Test of the Alarms component</router-link
						>
					</li>
				</ul>
			</div>
		</div>
	</div>
</template>

<script>
export default {
	name: 'components',
	components: {},
};
</script>

<style scoped></style>
