<template>
	<div>
		<v-container fluid>
			<h1>{{ $t('userList.title') }}</h1>
		</v-container>
		<v-container fluid>
			<v-card class="slts-card">
				<v-row>
					<v-col md="2" sm="12" xs="12">
						<v-list v-if="userListLoaded" id="recipientListSection">
							<v-list-item
								v-for="item in recipeintLists"
								:key="item.id"
								@click="changeActiveRL(item)"
							>
								<v-list-item-content>
									<v-list-item-title>
										{{ item.username }}
									</v-list-item-title>
								</v-list-item-content>
								<v-list-item-action @click="deleteUser">
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
												@click="createUser()"
											>
												<v-icon> mdi-plus </v-icon>
											</v-btn>
										</v-fab-transition>
										{{ $t('userDetails.list.create') }}
									</v-list-item-title>
								</v-list-item-content>
							</v-list-item>
						</v-list>
					</v-col>
					<v-divider vertical class="divider-horizontal-margin"></v-divider>
					<v-col md="9" sm="12" xs="12" id="recipientListDetails">
						<UserDetails
							ref="recipientListDetails"
							:recipientList="selectedUser"
							v-if="selectedUser"
							:key="selectedUser.id"
							@saved="updateUserList"
						></UserDetails>
						<v-row v-else>
							<v-col cols="12">
								<h3>{{ $t('userDetails.list.blank') }}</h3>
							</v-col>
						</v-row>
					</v-col>
				</v-row>
			</v-card>
		</v-container>
		<v-dialog v-model="showRLCreationDialog" max-width="1200" >
			<v-card>
				<v-card-title>{{ $t('userDetails.dialog.create.title') }}</v-card-title>
				<v-card-text class="dialog-card-text">
					<UserDetails style="padding:1rem"
						ref="recipientListDialog"
						:recipientList="blankUserList"
						:edit="true"
					></UserDetails>
				</v-card-text>
				<v-card-actions>
					<v-spacer></v-spacer>
					<v-btn text @click="showRLCreationDialog = false">{{
						$t('common.cancel')
					}}</v-btn>
					<v-btn text color="success" @click="addUser()">{{
						$t('common.ok')
					}}</v-btn>
				</v-card-actions>
			</v-card>
		</v-dialog>
		<ConfirmationDialog
			:btnvisible="false"
			:dialog="deleteUserDialog"
			@result="deleteUserDialogResult"
			:title="$t('recipientlist.dialog.delete.title')"
			:message="$t('recipientlist.dialog.delete.text')"
		></ConfirmationDialog>

		<v-snackbar v-model="snackbar.visible" :color="snackbar.color">
			{{ snackbar.message }}
		</v-snackbar>
	</div>
</template>
<script>
import UserDetails from './UserDetails';
import ConfirmationDialog from '@/layout/dialogs/ConfirmationDialog';
import SnackbarMixin from '@/layout/snackbars/SnackbarMixin.js';

/**
 * User List component - View page.
 *
 * User list display all recipient lists from Scada-LTS application.
 *
 * Using REST API the User Lists are provided to that component
 * but more detailed information about specific list is
 * passed to the "UserDetails" component.
 *
 * @author Sergio Selvaggi <sselvaggi@softq.pl>
 * @version 1.0.1
 */
export default {
	name: 'UserList',

	components: {
		UserDetails,
		ConfirmationDialog,
	},

	mixins: [SnackbarMixin],

	data() {
		return {
			showRLCreationDialog: false,
			userListLoaded: false,
			recipeintLists: undefined,
			selectedUser: undefined,
			blankUserList: undefined,
			deleteUserDialog: false,
		};
	},

	mounted() {
		this.fetchUsers();
	},

	methods: {
		async fetchUsers() {
			this.userListLoaded = false;
			this.recipeintLists = await this.$store.dispatch('getAllUsers');
			this.userListLoaded = true;
		},

		changeActiveRL(item) {
			this.$store.dispatch('getMailingList', item.id).then((resp) => {
				this.selectedUser = resp;
			});
		},

		deleteUser() {
			this.deleteUserDialog = true;
		},

		async deleteUserDialogResult(e) {
			this.deleteUserDialog = false;
			if (e) {
				let resp = await this.$store.dispatch(
					'deleteMailingList',
					this.selectedUser.id,
				);
				let operationSuccess = !!resp.status && resp.status === 'deleted';
				this.showCrudSnackbar('delete', operationSuccess);
				if (operationSuccess) {
					this.selectedUser = null;
					this.fetchUsers();
				}
			}
		},

		async createUser() {
			this.showRLCreationDialog = true;
			this.blankUserList = JSON.parse(
				JSON.stringify(this.$store.state.storeMailingList.mailingListTemplate),
			);
			this.blankUserList.xid = await this.$store.dispatch('getUniqueMailingListXid');
		},

		async addUser() {
			this.$refs.recipientListDialog.preSave();
			let resp = await this.$store.dispatch('createUser', this.blankUserList);
			let operationSuccess = !!resp.status && resp.status === 'created';
			this.showCrudSnackbar('add', operationSuccess);
			if (operationSuccess) {
				this.fetchUsers();
			}
			this.showRLCreationDialog = false;
		},

		updateUserList(resp) {
			this.showCrudSnackbar('update', !!resp.status && resp.status === 'updated');
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
	max-height: 67vh;
	overflow-y: auto;
}
</style>
