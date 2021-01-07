<template>
	<div>
		<h4>Example of the use of the SimpleComponentSvg component</h4>

		<div class="container">
			<div class="row">
				<div class="col-xs-4">
					Describe: <br />A simple test to see if the value of the component is displayed.
				</div>
				<div class="col-xs-8">
					<SimpleComponentSvg />
				</div>
			</div>
		</div>
	</div>
</template>

<script>
import SimpleComponentSvg from '../../components/graphical_views/SimpleComponentSVG';

export default {
	name: 'example-simple-component-svg',
	components: {
		SimpleComponentSvg,
	},
};
</script>

<style scoped></style>
