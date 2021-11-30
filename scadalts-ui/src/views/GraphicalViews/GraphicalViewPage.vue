<template>
	<div v-if="!!viewPage">
		<div
			class="canvas"
			:key="changes"
			v-bind:style="{
				width: viewSize.width + 'px',
				height: viewSize.height + 'px',
				backgroundImage: 'url(' + viewBackground + ')',
			}"
		>
			<component
				v-for="(cmp, index) in viewComponents"
				:key="index"
				:is="cmp.defName.toUpperCase()"
				:component="cmp"
				@update="updateComponents"
			>
			</component>
		</div>
	</div>
</template>
<script>
import ViewComponentDefinitions from '@/components/GraphicalView/ViewComponents/ViewComponentDefinitions.js';

export default {
	name: 'GraphicalViewPage',

	mixins: [ViewComponentDefinitions],

	data() {
		return {
			changes: 0,
		};
	},

	mounted() {
		this.fetchGraphicalView(this.$route.params.id);
		this.getImageSets();
	},

	computed: {
		viewPage() {
			return this.$store.state.graphicalViewModule.graphicalPage;
		},
		viewComponents() {
			if (!!this.$store.state.graphicalViewModule.graphicalPage) {
				return this.$store.getters.viewComponentsGetter;
			}
		},
		viewSize() {
			return this.$store.state.graphicalViewModule.resolution;
		},
		viewBackground() {
			console.log(this.$store.state.graphicalViewModule.graphicalPage.backgroundFilename);
			return this.$store.state.graphicalViewModule.graphicalPage.backgroundFilename;
		},
	},

	methods: {
		async fetchGraphicalView(graphicalViewId) {
			if (graphicalViewId > 0) {
				await this.$store.dispatch('getGraphicalViewById', graphicalViewId);
				this.$emit('routeChanged', Number(graphicalViewId));
			}
		},

		getImageSets() {
			this.$store.dispatch('getImageSets');
		},
		/**
		 * Update Component list
		 *
		 * Due to reactivity system in Vue.js we have
		 * to inform the DOM that the component list has changed
		 * to do that we can change the :key property of the "canvas"
		 * element to force the browser to re-render the component list
		 */
		updateComponents() {
			this.changes++;
		},
	},

	watch: {
		$route({params}) {
			this.fetchGraphicalView(params.id);
		},
	},
};
</script>
<style scoped>
.canvas {
	width: 1080px;
	height: 720px;
	background-color: azure;
	position: relative;
}
</style>
