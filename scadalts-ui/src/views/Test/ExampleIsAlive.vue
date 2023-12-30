<template>
	<div>
		<h4>Example of the use of the IsAlive component</h4>

		<div class="container">
			<div class="row">
				<div class="col-xs-4">
					Describe: <br />A simple test to see if the value of the component is displayed.
				</div>
				<div class="col-xs-8">
					<IsAlive />
				</div>
			</div>
		</div>
	</div>
</template>

<script>
import IsAlive from '@/components/graphical_views/IsAlive.vue';

export default {
	name: 'example-isalive',
	components: {
		IsAlive,
	},
};
</script>

<style scoped></style>
