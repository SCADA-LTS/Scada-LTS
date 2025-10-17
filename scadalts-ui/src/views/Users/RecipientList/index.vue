<template>
	<div>
		<v-container fluid>
			<h1>{{ $t('recipientlist.title') }}</h1>
		</v-container>
		<v-container fluid>
			<v-card class="slts-card">
				<v-row>
					<v-col md="3" sm="12" xs="12">
						<v-list v-if="recipeintListsLoaded" id="recipientListSection">
							<v-list-item
								v-for="item in recipeintLists"
								:key="item.id"
								@click="changeActiveRL(item)"
							>
								<v-list-item-content>
									<v-list-item-title>
										{{ item.name }}
									</v-list-item-title>
								</v-list-item-content>
								<v-list-item-action @click="deleteRecipientList">
									<v-icon> mdi-minus-circle </v-icon>
								</v-list-item-action>
							</v-list-item>
						</v-list>
						<v-skeleton-loader v-else type="list-item-two-line"></v-skeleton-loader>
						<v-list id="recipientListCreation">
							<v-list-item>
								<v-list-item-content>
									<v-list-item-title>
										<v-fab-transition>
											<v-btn
												color="primary"
												dark
												absolute
												bottom
												right
												x-small
												fab
												@click="createRecipientList()"
											>
												<v-icon> mdi-plus </v-icon>
											</v-btn>
										</v-fab-transition>
										{{ $t('recipientlist.list.create') }}
									</v-list-item-title>
								</v-list-item-content>
							</v-list-item>
						</v-list>
					</v-col>
					<v-divider vertical class="divider-horizontal-margin"></v-divider>
					<v-col md="8" sm="12" xs="12" id="recipientListDetails">
						<RecipientListDetails
							ref="recipientListDetails"
							:recipientList="activeRecipientList"
							v-if="activeRecipientList"
							:key="activeRecipientList.id"
							@saved="updateRecipientList"
						></RecipientListDetails>
						<v-row v-else>
							<v-col cols="12">
								<h3>{{ $t('recipientlist.list.blank') }}</h3>
							</v-col>
						</v-row>
					</v-col>
				</v-row>
			</v-card>
		</v-container>
		<v-dialog v-model="showRLCreationDialog" max-width="1200">
			<v-card>
				<v-card-title>{{ $t('recipientlist.dialog.create.title') }}</v-card-title>
				<v-card-text class="dialog-card-text">
					<RecipientListDetails
						ref="recipientListDialog"
						:recipientList="blankRecipientList"
						:edit="true"
					></RecipientListDetails>
				</v-card-text>
				<v-card-actions>
					<v-spacer></v-spacer>
					<v-btn text @click="showRLCreationDialog = false">{{
						$t('common.cancel')
					}}</v-btn>
					<v-btn text color="success" @click="addRecipientList()">{{
						$t('common.ok')
					}}</v-btn>
				</v-card-actions>
			</v-card>
		</v-dialog>

		<ConfirmationDialog
			:btnvisible="false"
			ref="deleteRecipientDialog"
			@result="deleteRecipientDialogResult"
			:title="$t('recipientlist.dialog.delete.title')"
			:message="$t('recipientlist.dialog.delete.text')"
		></ConfirmationDialog>

	</div>
</template>
<script>
import RecipientListDetails from './RecipientListDetails';
import ConfirmationDialog from '@/layout/dialogs/ConfirmationDialog';

/**
 * Recipient List component - View page.
 *
 * Recipient list display all recipient lists from Scada-LTS application.
 * Previously it was named as Mailing List but now it can contains also
 * a phone numbers. Users can be notified using also that channel.
 * User are able to create, delete or modify Recipient Lists from that View.
 *
 * Using REST API the Recipient Lists are provided to that component
 * but more detailed information about specific list is
 * passed to the "RecipientListDetails" component.
 *
 * @author Radoslaw Jajko <rjajko@softq.pl>
 * @version 1.0.1
 */
export default {
	name: 'RecipientList',

	components: {
		RecipientListDetails,
		ConfirmationDialog,
	},

	data() {
		return {
			showRLCreationDialog: false,
			recipeintListsLoaded: false,
			recipeintLists: undefined,
			activeRecipientList: undefined,
			blankRecipientList: undefined,
			deleteRecipientDialog: false,
		};
	},

	mounted() {
		this.fetchRecipeintLists();
	},

	methods: {
		async fetchRecipeintLists() {
			this.recipeintListsLoaded = false;
			this.recipeintLists = await this.$store.dispatch('getSimpleMailingLists');
			this.recipeintListsLoaded = true;
		},

		changeActiveRL(item) {
			this.$store.dispatch('getMailingList', item.id).then((resp) => {
				this.activeRecipientList = resp;
			});
		},

		deleteRecipientList() {
			this.$refs.deleteRecipientDialog.showDialog();
		},

		async deleteRecipientDialogResult(e) {
			if (e) {
				let resp = await this.$store.dispatch(
					'deleteMailingList',
					this.activeRecipientList.id,
				);
				let operationSuccess = !!resp.status && resp.status === 'deleted';
				this.$store.dispatch('showSuccessNotification', "Deleted successfully");
				if (operationSuccess) {
					this.activeRecipientList = null;
					this.fetchRecipeintLists();
				}
			}
		},

		async createRecipientList() {
			this.showRLCreationDialog = true;
			this.blankRecipientList = JSON.parse(
				JSON.stringify(this.$store.state.storeMailingList.mailingListTemplate),
			);
			this.blankRecipientList.xid = await this.$store.dispatch('getUniqueMailingListXid');
		},

		async addRecipientList() {
			this.$refs.recipientListDialog.preSave();
			let resp = await this.$store.dispatch('createMailingList', this.blankRecipientList);
			let operationSuccess = !!resp.status && resp.status === 'created';
			this.$store.dispatch('showSuccessNotification', "Created successfully");
			if (operationSuccess) {
				this.fetchRecipeintLists();
			}
			this.showRLCreationDialog = false;
		},

		updateRecipientList(resp) {
			if(resp.status === 'updated') {
				this.$store.dispatch('showSuccessNotification', "Updated successfully");
				this.fetchRecipeintLists();
			} else {
				this.$store.dispatch('showErrorNotification', "Error while updating");
			}
		},
	},
};
</script>
<style scoped>
.slts-card {
	min-height: 72vh;
}
.divider-horizontal-margin {
	margin: 0 3.5%;
	min-height: 72vh;
}
@media (max-width: 960px) {
	.divider-horizontal-margin {
		display: none;
	}
}
.dialog-card-text {
	max-height: 74vh;
	overflow-y: auto;
}
#recipientListSection {
	max-height: 61vh;
	overflow-y: auto;
}
</style>
