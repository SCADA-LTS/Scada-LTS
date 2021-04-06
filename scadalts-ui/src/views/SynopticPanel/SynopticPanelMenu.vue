<template>
	<div>
		<v-container fluid id="synoptic-panel-header">
			<v-row align="center">
				<v-col cols="12" md="8">
					<h1>Synoptic Panel</h1>
				</v-col>

				<v-col cols="12" md="2" class="row justify-end">
					<SynopticPanelCreator
						@created="createSynopticPanel($event)"
					></SynopticPanelCreator>
					<v-btn
						v-if="!!this.activePanel"
						elevation="0"
						fab
						@click="editSynopticPanel"
						class="small-margin"
					>
						<v-icon>mdi-pencil</v-icon>
					</v-btn>
					<v-btn
						v-if="!!this.activePanel"
						elevation="0"
						fab
						@click="deleteSynopticPanel"
						class="small-margin"
					>
						<v-icon>mdi-minus-circle</v-icon>
					</v-btn>
				</v-col>

				<v-col cols="12" md="2">
					<v-select
						label="Select panel"
						v-model="activePanel"
						@change="selectSynopticPanel"
						:items="synopticPanelList"
						item-value="id"
						item-text="name"
						dense
					></v-select>
				</v-col>
			</v-row>
		</v-container>

		<v-container fluid id="synoptic-panel-content">
			<router-view ref="panel" 
            @loaded="onLoadedSynopticPanel($event)"
            @updated="onUpdatedSynopticPanel($event)"
        ></router-view>
		</v-container>

		<ConfirmationDialog
			:btnvisible="false"
			:dialog="deletePanelDialog"
			@result="deleteSynopticPanelResult"
			title="Delete Synoptic panel?"
			message="This operation cannot be undone!"
		></ConfirmationDialog>

		<v-snackbar v-model="snackbar.visible" :color="snackbar.color">
			{{ snackbar.message }}
		</v-snackbar>
	</div>
</template>
<script>
import SynopticPanelCreator from './SynopticPanelCreator';
import ConfirmationDialog from '@/layout/dialogs/ConfirmationDialog';
import SnackbarMixin from '@/layout/snackbars/SnackbarMixin.js';

/**
 * Synoptic Panel component - Menu Page
 *
 * @author Radoslaw Jajko <rjajko@softq.pl>
 * @version 1.0.0
 */
export default {
	name: 'SynopticPanelMenu',

	components: {
		SynopticPanelCreator,
		ConfirmationDialog,
	},

	mixins: [SnackbarMixin],

	data() {
		return {
			activePanel: undefined,
			deletePanelDialog: false,
            synopticPanelList: [],
		};
	},

	mounted() {
		this.fetchSynopticPanelList();
	},

	methods: {
		async fetchSynopticPanelList() {
			this.synopticPanelList = await this.$store.dispatch('fetchSynopticPanelList');
		},

		createSynopticPanel(synopticPanel) {
			this.$store
				.dispatch('createSynopticPanel', synopticPanel)
				.then(() => {
					this.showCrudSnackbar('add');
					this.fetchSynopticPanelList();
				})
				.catch(() => {
					this.showCrudSnackbar('add', false);
				});
		},

		editSynopticPanel() {
			this.$refs.panel.edit();
		},

		deleteSynopticPanel() {
			this.deletePanelDialog = true;
		},

		deleteSynopticPanelResult(e) {
			this.deletePanelDialog = false;
			if (e) {
				this.$store
					.dispatch('deleteSynopticPanel', this.activePanel)
					.then(() => {
						this.showCrudSnackbar('delete');
						this.fetchSynopticPanelList();
                        this.$router.push({ path: `/synoptic-panel/` });
					})
					.catch(() => {
						this.showCrudSnackbar('delete', false);
					});
			}
		},

		onLoadedSynopticPanel(id) {
			this.activePanel = id;
		},

        onUpdatedSynopticPanel(status) {
            this.showCrudSnackbar('update', status);
        },

		selectSynopticPanel(id) {
			this.activePanel = id;
			this.$router.push({ path: `/synoptic-panel/${id}` });
		},
	},
};
</script>
<style scoped>
.small-margin {
	margin: 0px 5px 0px 5px;
}
</style>
