<template>
	<div>
		<div v-if="loaded">
			<slot></slot>
		</div>
		<v-skeleton-loader v-else type="article"></v-skeleton-loader>
		<v-dialog v-model="editorVisible" width="900">
			<div v-if="loaded">
				<slot name="config"></slot>
			</div>
			<v-skeleton-loader v-else type="article"></v-skeleton-loader>
		</v-dialog>
	</div>
</template>
<script>
/**
 * Data Source Loader Component
 *
 * This component is responsible for providing a skeleton loaders
 * for DataSources. They are displayed until "loaded" property is false.
 * When data from API has been downloaded the Deticated Point "Index" page and
 * "config" component is ready to be rendered.
 *
 */
export default {
	props: {
		loaded: {
			type: Boolean,
			default: false,
		},
	},

	data() {
		return {
			editorVisible: false,
		};
	},

	methods: {
		openEditor() {
			this.editorVisible = true;
		},
	},
};
</script>
