<template>
	<div v-if="!!viewPage">
		<div
			class="canvas"
			:key="changes"
			v-bind:style="{
				width: viewPage.resolution.split('x')[0].substring(1) + 'px',
				height: viewPage.resolution.split('x')[1] + 'px',
			}"
		>
			<component
				v-for="(cmp, index) in viewComponents"
				:key="index"
				:is="cmp.type"
				:component="cmp"
				@update="updateComponents"
			>
			</component>
		</div>
	</div>
</template>
<script>
import ViewComponentDefinitions from './ViewComponents/ViewComponentDefinitions.js';

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
	},

	methods: {
		async fetchGraphicalView(graphicalViewId) {
			await this.$store.dispatch('getGraphicalViewById', graphicalViewId);
			this.$emit('routeChanged', Number(graphicalViewId));
			console.log(this.graphicalViewPage);
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
			// console.log('updateComponents');
			// this.$forceUpdate();
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
