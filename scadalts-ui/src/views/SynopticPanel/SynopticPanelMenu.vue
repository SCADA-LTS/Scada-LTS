<template>
	<div>
		<v-container fluid class="slts-page-header" id="synoptic-panel-header">
			<v-row align="center">
				<v-col xs="12" sm="6" md="6" class="slts--title">
					<h1>{{$t('synopticpanels.titile')}}</h1>
				</v-col>

				<v-col xs="12" sm="6" md="4" class="slts--toolbar row justify-end">
					<SynopticPanelCreator
						ref="synopticPanelCreatorDialog"
						@created="createSynopticPanel($event)"
					></SynopticPanelCreator>
					<v-btn elevation="1" fab
						@click="showCreationDialog">
						<v-icon>mdi-plus</v-icon>
					</v-btn>
					<v-btn
						v-if="!!this.activePanel"
						elevation="1"
						fab
						@click="editSynopticPanel"
						class="small-margin"
					>
						<v-icon>mdi-pencil</v-icon>
					</v-btn>
					<v-btn
						v-if="!!this.activePanel"
						elevation="1"
						fab
						@click="deleteSynopticPanel"
						class="small-margin"
					>
						<v-icon>mdi-minus-circle</v-icon>
					</v-btn>
				</v-col>

				<v-col xs="12" sm="12" md="2" class="slts--selectbox">
					<v-select
						:label="$t('synopticpanels.panel.select')"
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

		<v-container fluid id="synoptic-panel-content" v-if="panelLoaded">
			<router-view ref="panel" 
            @loaded="onLoadedSynopticPanel($event)"
            @updated="onUpdatedSynopticPanel($event)"
        ></router-view>
		</v-container>

		<ConfirmationDialog
			:btnvisible="false"
			ref="deletionDialog"
			@result="deleteSynopticPanelResult"
			:title="$t('synopticpanels.dialog.delete.header')"
			:message="$t('synopticpanels.dialog.delete.content')"
		></ConfirmationDialog>

	</div>
</template>
<script>
import SynopticPanelCreator from './SynopticPanelCreator';
import ConfirmationDialog from '@/layout/dialogs/ConfirmationDialog';

/**
 * Synoptic Panel component - Menu Page
 *
 * @author Radoslaw Jajko <rjajko@softq.pl>
 * @version 1.1.0
 */
export default {
	name: 'SynopticPanelMenu',

	components: {
		SynopticPanelCreator,
		ConfirmationDialog,
	},

	data() {
		return {
			activePanel: undefined,
            synopticPanelList: [],
			panelLoaded: false,
		};
	},

	mounted() {
		this.fetchSynopticPanelList();
	},

	methods: {
		async fetchSynopticPanelList() {
			this.synopticPanelList = await this.$store.dispatch('fetchSynopticPanelList');
		},

		showCreationDialog() {
			this.$refs.synopticPanelCreatorDialog.showDialog();
		},

		createSynopticPanel(synopticPanel) {
			this.$store
				.dispatch('createSynopticPanel', synopticPanel)
				.then(() => {
					this.$store.dispatch('showSuccessNotification', this.$t(`common.snackbar.add.success`));
					this.fetchSynopticPanelList();
				})
				.catch(() => {
					this.$store.dispatch('showErrorNotification', this.$t(`common.snackbar.add.fail`));
				});
		},

		editSynopticPanel() {
			this.$refs.panel.edit();
		},

		deleteSynopticPanel() {
			this.$refs.deletionDialog.showDialog();
		},

		deleteSynopticPanelResult(e) {
			if (e) {
				this.$store
					.dispatch('deleteSynopticPanel', this.activePanel)
					.then(() => {
						this.$store.dispatch('showSuccessNotification', this.$t(`common.snackbar.delete.success`));
						this.fetchSynopticPanelList();
                        this.$router.push({ path: `/synoptic-panel/` });
					})
					.catch(() => {
						this.$store.dispatch('showErrorNotification', this.$t(`common.snackbar.delete.fail`));
					});
			}
		},

		onLoadedSynopticPanel(id) {
			this.activePanel = id;
		},

    onUpdatedSynopticPanel(status) {
			if(status) {
        this.updatePanelView()
				this.$store.dispatch('showSuccesNotification', this.$t(`common.snackbar.update.success`))
			} else {
				this.$store.dispatch('showErrorNotification', this.$t(`common.snackbar.update.fail`))
			}
    },

		updatePanelView() {
			this.panelLoaded = false;
			this.$nextTick().then(() => {
				this.panelLoaded = true;
			});
		},

		selectSynopticPanel(id) {
			this.activePanel = id;
			this.$router.push({ path: `/synoptic-panel/${id}` });
			this.updatePanelView();
		},
	},
};
</script>
<style scoped>
.small-margin {
	margin: 0px 5px 0px 5px;
}
</style>
