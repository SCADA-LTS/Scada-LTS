<template>
	<div>
		<h4>Example of the use of the Test component</h4>
		<div class="container">
			<div class="row">
				<div class="col-xs-4">
					Describe: <br />A simple test to see if the value of the component is displayed.
				</div>
				<div class="col-xs-6">
					<Test />
				</div>
			</div>
		</div>
	</div>
</template>

<script>
import Test from '../../components/Test.vue';

export default {
	name: 'example-test',
	components: {
		Test,
	},
};
</script>

<style scoped></style>
