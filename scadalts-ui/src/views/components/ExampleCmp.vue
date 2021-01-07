<template>
	<div>
		<h4>Example of the use of the CMP component</h4>

		<div class="container">
			<div class="row">
				<div class="col-xs-4">
					Describe: <br />A simple test to see if the value of the component is displayed.
				</div>
				<div class="col-xs-8">
					<CMP />
				</div>
			</div>
		</div>
	</div>
</template>

<script>
import CMP from '../../components/graphical_views/cmp/CMP';

export default {
	name: 'example-cmp',
	components: {
		CMP,
	},
};
</script>

<style scoped></style>
