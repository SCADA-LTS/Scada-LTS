<template>
	<v-row justify="center">
		<v-dialog v-model="editDialog" persistent>
			<v-card>
				<v-card-title class="headline"> Edit Synoptic Panel </v-card-title>
				<v-card-text>
					{{ panelExpanded }}
					<v-expansion-panels focusable multiple>
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
										@saved="componentSaved"
									/>
								</div>

								<div v-else>
									<slts-default
										:component-data="item.data"
										:component-id="item.name"
										:component-editor="true"
									></slts-default>
									<!-- <component is="slts-default"
                                               v-bind:component-data="graphicItem.data"
                                               v-bind:component-id="graphicItem.name"
                                               v-bind:component-editor="true"
                                               @saved="componentSaved"/> -->
								</div>
							</v-expansion-panel-content>
						</v-expansion-panel>
					</v-expansion-panels>
				</v-card-text>
				<v-card-actions>
					<div class="flex-grow-1"></div>
					<v-btn color="green darken-1" text @click="closeDialog()">Close</v-btn>
					<v-btn color="green darken-1" text @click="saveData()">Save</v-btn>
				</v-card-actions>
			</v-card>
		</v-dialog>
	</v-row>
</template>
<script>
// import customComponentsMixin from "../../components/SynopticPanel/CustomComponentsMixin";
import customComponentsMixin from '../../components/SynopticPanel/CustomComponentMixin.js';

export default {
	props: ['graphicItems'],

	mixins: [customComponentsMixin],

	data() {
		return {
			editDialog: false,
			panelItems: [],
		};
	},
	computed: {
		// isAdmin() {
		//     return this.$store.state.adminPrivilege
		// }
	},
	mounted() {
		console.debug(this.customComponent);
	},
	methods: {
		openDialog() {
			this.panelItems = [];
			for (const [key, value] of this.graphicItems.entries()) {
				this.panelItems.push({ name: key, data: value });
			}
			this.editDialog = true;
			console.debug(this.customComponent);
		},
		closeDialog() {
			this.editDialog = false;
		},
		componentSaved(itemId, itemData) {
			this.graphicItems.set(itemId, itemData);
		},
		saveData() {
			this.$emit('saved', this.graphicItems);
			this.editDialog = false;
		},
	},
};
</script>
