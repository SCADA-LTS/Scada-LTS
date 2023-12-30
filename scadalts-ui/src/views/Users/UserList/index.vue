<template>
	<div>
		<v-container fluid v-if="loggedUser.admin">
			<h1>{{ $t('userList.title') }}</h1>
		</v-container>
		<v-container fluid>
			<v-card class="slts-card">
				<v-row class="flex-jc-center">
					<v-col md="3" sm="12" xs="12" v-if="loggedUser.admin">
						<v-list v-if="userListLoaded" id="usersList">
							<v-list-item
								v-for="item in userList"
								:key="item.id"
								@click="showUserDetails(item)"
							>
								<v-list-item-icon>
									<v-icon v-show="item.admin && !item.disabled">mdi-account-tie</v-icon>
									<v-icon v-show="item.admin && item.disabled">mdi-account-tie-outline</v-icon>
									<v-icon v-show="!item.admin && !item.disabled">mdi-account</v-icon>
									<v-icon v-show="!item.admin && item.disabled">mdi-account-outline</v-icon>
          						</v-list-item-icon>
								<v-list-item-content>
									<v-list-item-title>
										{{ item.username }}
									</v-list-item-title>
								</v-list-item-content>
								<v-list-item-action @click="openDeletionDialog(item.id)">
									<v-icon> mdi-minus-circle </v-icon>
								</v-list-item-action>
							</v-list-item>
						</v-list>
						
						<v-skeleton-loader v-else type="list-item-two-line"></v-skeleton-loader>
						
						<v-list id="userCreation">
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
												@click="openCreationDialog()"
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

					<v-divider vertical class="divider-horizontal-margin" v-if="loggedUser.admin"></v-divider>
					
					<v-col md="8" sm="12" xs="12" id="userDetails">
						<UserDetails
							ref="userDetailsComponent"
							:userDetails="selectedUser"
							:userProfiles="userProfiles"
							v-if="selectedUser"
							:key="selectedUser.id"
							@saved="onUpdateUserDetails"
							@passwordChanged="onPasswordChanged"
							@userProfileCreated="onUserProfileCreated"
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
		
		<v-dialog v-model="dialogCreationVisible" max-width="1200" >
			<v-card>
				<v-card-title>{{ $t('userDetails.dialog.create.title') }}</v-card-title>
				<v-card-text class="dialog-card-text">
					<UserDetails style="padding:1rem"
						ref="userCreationDialog"
						:userDetails="createdUser"
						:userProfiles="userProfiles"
						:edit="true"
						@userProfileCreated="onUserProfileCreated"
						@passwordInput="onPasswordInput"
					></UserDetails>
				</v-card-text>
				<v-card-actions>
					<v-spacer></v-spacer>
					<v-btn text @click="dialogCreationVisible = false">{{
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
			ref="deletionDialog"
			@result="onDeleteDialogClose"
			:title="$t('userDetails.dialog.delete.title')"
			:message="$t('userDetails.dialog.delete.text')"
		></ConfirmationDialog>

	</div>
</template>
<script>
import UserDetails from './UserDetails.vue';
import ConfirmationDialog from '@/layout/dialogs/ConfirmationDialog.vue';

/**
 * User List component - View page.
 *
 * This page is used to display the list of users and 
 * manage the user details. You can create, edit and delete users
 * from that page. Communication is based on the HTTP requests.
 *
 * @author Sergio Selvaggi <sselvaggi@softq.pl>
 * @author Radoslaw Jajko <rjajko@softq.pl>
 * @version 1.0.2
 */
export default {
	name: 'UserList',

	components: {
		UserDetails,
		ConfirmationDialog,
	},

	data() {
		return {
			userListLoaded: false,
			userList: [],
			userProfiles: [],
			selectedUser: null,
			createdUser: null,
			userPassword: '',
			dialogCreationVisible: false,
			operationQueue: null,
			retriesCounter: 0,
		};
	},

	computed: {
		loggedUser() {
			return this.$store.state.loggedUser;
		}
	},

	mounted() {
		this.initializeUserList();
	},

	methods: {

		initializeUserList() {
			if(!!this.loggedUser && this.loggedUser.admin) {
				this.fetchUserList();
				this.fetchUserProfiles();
			} else if(!!this.loggedUser) {
				this.showUserDetails(this.loggedUser);
			} else {
				if(this.retriesCounter < 3) {
					this.retriesCounter++;
					setTimeout(() => {
						this.initializeUserList();
					}, 1000);
				} else {
					throw new Error('Unable to initialize user list. User data are unavailable.');
				}
			}
		},


		async fetchUserList() {
			this.userListLoaded = false;
			this.userList = await this.$store.dispatch('getAllUsers');
			this.userListLoaded = true;
		},
		fetchUserProfiles() {
			this.$store.dispatch('getUserProfilesList').then((r) => {
				this.userProfiles = r;
				this.userProfiles.push({
					"id": -1,
					"name": this.$t('common.none'),
				});
			});
		},

		showUserDetails(item) {
			this.$store.dispatch('getUserDetails', item.id).then((resp) => {
				this.selectedUser = resp;
			}).catch(() => {
				this.selectedUser = null;
			});
		},

		openDeletionDialog(itemId) {
			this.operationQueue = itemId;
			this.$refs.deletionDialog.showDialog();
		},

		onDeleteDialogClose(result) {
			if (result) {
				this.deleteUser(this.operationQueue);
			}
		},

		async deleteUser(userId) {
			try {
				await this.$store.dispatch('deleteUser', userId);
			
				this.userList = this.userList.filter(item => item.id !== userId);
				if(this.selectedUser.id === userId) {
					this.selectedUser = null;
				}
				this.$store.dispatch('showSuccessNotification', this.$t(`common.snackbar.delete.success`));
			} catch (e) {
				this.$store.dispatch('showErrorNotification', this.$t(`common.snackbar.delete.fail`));
				console.error(e);
			}			
		},

		openCreationDialog() {
			this.selectedUser = null;
			this.dialogCreationVisible = true;
			this.createdUser = JSON.parse(JSON.stringify(
				this.$store.state.storeUsers.userTemplate));
        },

		async addUser() {
			if(this.$refs.userCreationDialog.isFormValid()) {
				let requestData = this.createdUser;
				this.dialogCreationVisible = false;
				requestData.password = this.userPassword;
				try {
					await this.$store.dispatch('createUser', requestData);
					this.$store.dispatch('showSuccessNotification', this.$t(`common.snackbar.add.success`));
					this.fetchUserList();
				} catch (e) {
					this.$store.dispatch('showErrorNotification', this.$t(`common.snackbar.add.fail`));
				}
			}
        },

		onPasswordInput(password) {
			this.userPassword = password;
		},

		async onUpdateUserDetails() {
			try {
				await this.$store.dispatch('updateUser', this.selectedUser);
				this.$store.dispatch('showSuccessNotification', this.$t(`common.snackbar.update.success`));
			} catch (e) {
				this.$store.dispatch('showErrorNotification', this.$t(`common.snackbar.update.fail`));
			}
		},

		onPasswordChanged(result) {
			if(result) {
				this.$store.dispatch('showSuccessNotification', this.$t(`common.snackbar.update.success`));
			} else {
				this.$store.dispatch('showErrorNotification', this.$t(`common.snackbar.update.fail`));
			}
		},

		onUserProfileCreated(result) {
			if(result) {
				this.$store.dispatch('showSuccessNotification', this.$t(`common.snackbar.add.success`));
				this.fetchUserProfiles();
			} else {
				this.$store.dispatch('showErrorNotification', this.$t(`common.snackbar.add.fail`));
			}
		}


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
#usersList {
	max-height: 61vh;
	overflow-y: auto;
}
.flex-jc-center {
	justify-content: center;
}
</style>
