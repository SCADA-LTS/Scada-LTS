<template>
	<div>
		<div id="panel-canvas"></div>
		<div v-if="!panel">
			<p>{{$t('synopticpanels.panel.blank')}}</p>
		</div>
		<div>
			<SynopticPanelItemEditor
				:panel="panel"
				:graphic-items="graphicItems"
				@saved="savePanel"
				ref="editDialog"
			/>
		</div>
		<div v-for="item in panelItems" :key="item.name">
			<div v-if="customComponent.includes(item.name.split('_')[1])">
				<component
					v-bind:is="item.name.split('_')[1]"
					v-bind:component-data="item.data"
					v-bind:component-id="item.name"
					v-bind:component-editor="false"
				/>
			</div>
			<div v-else>
				<slts-default
					:component-data="item.data"
					:component-id="item.name"
					:component-editor="false"
				></slts-default>
			</div>
		</div>
	</div>
</template>
<script>
import customComponentMixin from '../../components/SynopticPanel/CustomComponentMixin.js';
import SynopticPanelItemEditor from './SynopticPanelItemEditor.vue';
import {unescapeHtml} from '../../utils/common';

/**
 * Synoptic Panel component - Item
 * 
 * Single Synoptic Panel Item object.
 * It handle business logic and provide methods to render
 * this components on the canvas. Depending on the
 * components in library there is a possibility to configure
 * synoptic panel behaviour.
 * 
 * @author Radoslaw Jajko <rjajko@softq.pl>
 * @version 1.0.0
 */
export default {
	name: 'SynopticPanelItem',

	mixins: [customComponentMixin],

	components: {
		SynopticPanelItemEditor,
	},

	data() {
		return {
			panel: undefined,
			graphicItems: undefined,
			panelItems: [],
		};
	},

	watch: {
		$route(to) {
			this.fetchSynopticPanel(to.params.id);
		},
	},

	mounted() {
		this.fetchSynopticPanel(this.$route.params.id);
	},

	methods: {
		async fetchSynopticPanel(id) {
			this.panel = await this.$store.dispatch('fetchSynopticPanel', id);
			this.renderPanel();
		},

		renderPanel() {
			let dom = document.getElementById('panel-canvas');
			if (!!dom && !!dom.firstChild) {
				dom.firstChild.remove();
			}
			let vectorImage = unescapeHtml(this.panel.vectorImage);
			this.$svg('panel-canvas')
				.size(window.innerWidth, window.innerHeight)
				.svg(vectorImage);
			this.$emit('loaded', this.panel.id);
			this.initSvgComponentData();
		},

		edit() {
			this.$refs.editDialog.openDialog();
		},

		parseSvgContent() {
			const regex = /SLTS_\w+_\d+\b/;
			let graphicItems = new Map();

			this.$svg.select('*[id^="SLTS_"]').members.forEach((member) => {
				if (member.node.id.match(regex)) {
					graphicItems.set(member.node.id, {
						data: {},
					});
				}
			});
			return graphicItems;
		},

		initEditItemsData() {
			this.panelItems = [];
			for (const [key, value] of this.graphicItems.entries()) {
				this.panelItems.push({ name: key, data: value });
			}
		},

		initSvgComponentData() {
			try {
				let graphicItems = this.parseSvgContent();
				let unescapeComponentData = unescapeHtml(this.panel.componentData);
				let componentData = new Map(JSON.parse(unescapeComponentData));
				if (componentData !== undefined) {
					for (const [key, value] of componentData.entries()) {
						graphicItems.set(key, value);
					}
				}
				this.graphicItems = graphicItems;
				this.initEditItemsData();
			} catch (e) {
				console.error(e);
			}
		},

		savePanel(childGraphicItems) {
			this.graphicItems = childGraphicItems;
			this.panel.componentData = JSON.stringify([...this.graphicItems]);
			this.panel.vectorImage = unescapeHtml(this.panel.vectorImage);

            this.$store.dispatch('updateSynopticPanel', this.panel).then(() => {
                this.$emit('updated', true);
            }).catch(() => {
                this.$emit('updated', false);
            });
		},

	},
};
</script>
<style scoped></style>
