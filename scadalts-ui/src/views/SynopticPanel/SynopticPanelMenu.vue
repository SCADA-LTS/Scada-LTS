<template>
	<div>
		<v-container fluid id="synoptic-panel-header">
			<v-row align="center">
				<v-col cols="12" xs="12" md="6" >
					<h1>{{$t('synopticpanels.titile')}}</h1>
				</v-col>

				<v-col cols="12" xs="12" md="4" class="row justify-end">
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

				<v-col cols="12" xs="12" md="2">
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
 * @version 1.0.0
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
			this.deletePanelDialog = true;
		},

		deleteSynopticPanelResult(e) {
			this.deletePanelDialog = false;
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
				this.$store.dispatch('showSuccesNotification', this.$t(`common.snackbar.update.success`))
			} else {
				this.$store.dispatch('showErrorNotification', this.$t(`common.snackbar.update.fail`))
			}
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
