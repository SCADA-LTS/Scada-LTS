<template>
	<v-dialog max-width="800px" persistent v-model="dialogVisible">
		<v-card>
			<v-card-title class="headline"> Edit Synoptic Panel </v-card-title>

			<v-card-text>
				<v-row>
					<v-col cols="6">
						<v-text-field label="Name" v-model="panel.name" dense></v-text-field>
					</v-col>
					<v-col cols="6">
						<v-text-field
							label="Export ID"
							v-model="panel.xid"
							dense
							disabled
						></v-text-field>
					</v-col>
				</v-row>

				<v-expansion-panels accordion focusable multiple flat hover>
					<v-expansion-panel v-for="graphicItem in panelItems" :key="graphicItem.name">
						<v-expansion-panel-header>
							{{ graphicItem.name }}
						</v-expansion-panel-header>
						<v-expansion-panel-content>
							<div v-if="customComponent.includes(graphicItem.name.split('_')[1])">
								<component
									v-bind:is="graphicItem.name.split('_')[1]"
									v-bind:component-data="graphicItem.data"
									v-bind:component-id="graphicItem.name"
									v-bind:component-editor="true"	
								/>
							</div>

							<div v-else>
								<slts-default
									:component-data="item.data"
									:component-id="item.name"
									:component-editor="true"
								></slts-default>
							</div>
						</v-expansion-panel-content>
					</v-expansion-panel>
				</v-expansion-panels>
			</v-card-text>

			<v-card-actions>
				<v-spacer></v-spacer>
				<v-btn text @click="closeDialog()">Close</v-btn>
				<v-btn color="success" text @click="saveData()">Save</v-btn>
			</v-card-actions>
		</v-card>
	</v-dialog>
</template>
<script>
import customComponentsMixin from '../../components/SynopticPanel/CustomComponentMixin.js';
/**
 * Synoptic Panel component - Item Editor
 * 
 * Dialog component that handle the editor components logic
 * Specific components are dynamicly rendered inside
 * Expansion Panel based on the copmponent type.
 * If there proviced component does not exist in 
 * this libraty it will be interpreted as generic 
 * slst-default component.
 * 
 * To add a new custom component add a new enty inside
 * CustomComponentMixin.js file. 
 * src/components/SynopticPanel/CustomComponentMixin.js
 * 
 * @author Radoslaw Jajko <rjajko@softq.pl>
 * @version 1.0.0
 */
export default {
	props: ['graphicItems', 'panel'],

	mixins: [customComponentsMixin],

	data() {
		return {
			dialogVisible: false,
			panelItems: [],
		};
	},

	methods: {
		openDialog() {
			this.panelItems = [];
			for (const [key, value] of this.graphicItems.entries()) {
				this.panelItems.push({ name: key, data: value });
			}
			this.dialogVisible = true;
		},

		closeDialog() {
			this.dialogVisible = false;
		},

		saveData() {
			this.$emit('saved', this.graphicItems);
			this.dialogVisible = false;
		},
	},
};
</script>
