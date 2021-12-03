<template>
	<div v-if="!!viewPage">
		<div class="canvas-toolbar" v-if="editMode">
			<div class="canvas-toolbar--actions">
				<v-btn icon :disabled="activeComponentsSize < 1" @click="layoutMoveLeft">
					<v-icon>mdi-align-horizontal-left</v-icon>
				</v-btn>

				<v-btn icon :disabled="activeComponentsSize < 1" @click="layoutHorizontalCenter">
					<v-icon>mdi-align-horizontal-center</v-icon>
				</v-btn>

				<v-btn icon :disabled="activeComponentsSize < 1" @click="layoutMoveRight">
					<v-icon>mdi-align-horizontal-right</v-icon>
				</v-btn>

				<v-btn icon :disabled="activeComponentsSize < 1" @click="layoutMoveTop">
					<v-icon>mdi-align-vertical-top</v-icon>
				</v-btn>

				<v-btn icon :disabled="activeComponentsSize < 1" @click="layoutVerticalCenter">
					<v-icon>mdi-align-vertical-center</v-icon>
				</v-btn>

				<v-btn icon :disabled="activeComponentsSize < 1" @click="layoutMoveBottom">
					<v-icon>mdi-align-vertical-bottom</v-icon>
				</v-btn>

				<v-btn
					icon
					:disabled="activeComponentsSize < 3"
					@click="layoutHorizontalDistribute"
				>
					<v-icon>mdi-align-horizontal-distribute</v-icon>
				</v-btn>
				<v-btn
					icon
					:disabled="activeComponentsSize < 3"
					@click="layoutVerticalDistribute"
				>
					<v-icon>mdi-align-vertical-distribute</v-icon>
				</v-btn>

				<v-divider vertical class="small-divider"></v-divider>

				<v-btn-toggle v-model="layoutContext" borderless>
					<v-btn icon :disabled="activeComponentsSize < 1">
						<v-icon>mdi-application</v-icon>
					</v-btn>

					<v-btn icon :disabled="activeComponentsSize < 2">
						<v-icon>mdi-border-none-variant</v-icon>
					</v-btn>
				</v-btn-toggle>

				<v-divider vertical class="small-divider"></v-divider>

				<v-btn icon :disabled="hiddenComponents.length < 1" @click="showAllComponents">
					<v-icon>mdi-eye-refresh-outline</v-icon>
				</v-btn>
			</div>

			<div class="canvas-toolbar--actions" v-if="selectedComponents.length === 1">
				<v-btn icon @click="moveComponentDown">
					<v-icon>mdi-chevron-down</v-icon>
				</v-btn>
				<v-btn icon @click="moveComponentToBottom">
					<v-icon>mdi-chevron-triple-down</v-icon>
				</v-btn>
				<v-btn icon @click="moveComponentToTop">
					<v-icon>mdi-chevron-triple-up</v-icon>
				</v-btn>
				<v-btn icon @click="moveComponentUp">
					<v-icon>mdi-chevron-up</v-icon>
				</v-btn>

				<v-divider vertical class="small-divider"></v-divider>

				<v-btn icon @click="toggleComponentVisibility">
					<v-icon v-if="selectedComponents[0].visible">mdi-eye</v-icon>
					<v-icon v-else>mdi-eye-off</v-icon>
				</v-btn>

				<v-btn icon @click="editComponent">
					<v-icon>mdi-pencil</v-icon>
				</v-btn>
				<v-btn icon @click="deleteComponent">
					<v-icon>mdi-delete</v-icon>
				</v-btn>
			</div>
		</div>

		<div
			class="canvas"
			:key="changes"
			@contextmenu="deselectComponents"
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
				@click="componentSelected($event)"
				@mousedown="draggingStart"
			>
			</component>
		</div>
	</div>
</template>
<script>
import ViewComponentDefinitions from '@/components/GraphicalView/ViewComponents/ViewComponentDefinitions.js';
import layoutMixin from './mixins/layout.js';
import draggableMixin from './mixins/drag.js';

export default {
	name: 'GraphicalViewPage',

	mixins: [ViewComponentDefinitions, layoutMixin, draggableMixin],

	data() {
		return {
			changes: 0,
			hiddenComponents: [],
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
			const bg = this.$store.state.graphicalViewModule.graphicalPage.backgroundFilename;
			if(!!bg) {
				let url = this.$store.state.graphicalViewModule.graphicalPage.backgroundFilename.split(" ").join("%20");
				return `${url}`;
			} else {
				return null;
			}
		},
		editMode() {
			return this.$store.state.graphicalViewModule.graphicalPageEdit;
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

		showAllComponents() {
			this.hiddenComponents.forEach((c) => {
				c.visible = true;
			});
			this.hiddenComponents = [];
		},

		moveComponentDown() {
			this.activeComponent.moveComponentDown();
		},

		moveComponentToBottom() {
			this.activeComponent.moveComponentToBottom();
		},

		moveComponentUp() {
			this.activeComponent.moveComponentUp();
		},

		moveComponentToTop() {
			this.activeComponent.moveComponentToTop();
		},

		toggleComponentVisibility() {
			const cmp = this.activeComponent;
			cmp.visible = !cmp.visible;
			if (!cmp.visible) {
				this.hiddenComponents.push(cmp);
			} else {
				this.hiddenComponents.splice(this.hiddenComponents.indexOf(cmp), 1);
			}
		},

		editComponent() {
			this.activeComponent.showMenuEdit();
		},
		deleteComponent() {
			this.activeComponent.deleteComponent();
		},
	},

	watch: {
		$route({ params }) {
			this.fetchGraphicalView(params.id);
		},
	},
};
</script>
<style scoped>
.canvas {
	width: 1080px;
	height: 720px;
	position: relative;
	background-color: white;
	border-radius: 10px;
	border: solid 1px #0202021a;
	box-shadow: 0px 0px 3px #00000047;
}
.canvas-toolbar {
	display: flex;
	justify-content: space-between;
}
.canvas-toolbar--actions {
	display: flex;
	align-items: center;
}
.canvas-toolbar--actions > * {
	margin-right: 10px;
}
.canvas-toolbar--actions > *:last-of-type {
	margin-right: 0px;
}
.small-divider {
	height: 24px;
	min-height: unset;
	margin-right: 15px !important;
	margin: 10px 0px;
}
</style>
