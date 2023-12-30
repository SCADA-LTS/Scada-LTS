<template>
	<div>
		<h4>Example of the use of the ExportImportPointHierarchy component</h4>

		<div class="container">
			<div class="row">
				<div class="col-xs-4">
					Describe: <br />A simple test to see if the value of the component is displayed.
				</div>
				<div class="col-xs-8">
					<ExportImportPointHierarchy />
				</div>
			</div>
		</div>
	</div>
</template>

<script>
import ExportImportPointHierarchy from '@/components/point_hierarchy/ExportImportPointHierarchy.vue';

export default {
	name: 'example-ph',
	components: {
		ExportImportPointHierarchy,
	},
};
</script>

<style scoped></style>
