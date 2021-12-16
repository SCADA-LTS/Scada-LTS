<template>
	<div>
		<v-container fluid>
			<h1>{{ $t('userprofiles.title') }}</h1>
		</v-container>
		<v-container fluid>
			<v-card class="slts-card">
				<v-row>

					<v-col md="3" sm="12" xs="12">
						<v-list v-if="itemsListLoaded" id="userProfileListSection">
							<v-list-item
								v-for="item in profileLists"
								:key="item.id"
								@click="changeActiveItem(item)"
							>
								<v-list-item-content>
									<v-list-item-title>
										{{ item.name }}
									</v-list-item-title>
								</v-list-item-content>
								<v-list-item-action @click="openDeletionDialog(item)">
									<v-icon> mdi-minus-circle </v-icon>
								</v-list-item-action>
							</v-list-item>
						</v-list>
						<v-skeleton-loader v-else type="list-item-two-line"></v-skeleton-loader>

						<!-- Add new item -->
						<v-list id="userProfileCreation">
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
										{{ $t('userprofile.list.create') }}
									</v-list-item-title>
								</v-list-item-content>
							</v-list-item>
						</v-list>

					</v-col>

					<v-divider vertical class="divider-horizontal-margin"></v-divider>

					<v-col md="8" sm="12" xs="12" id="userProfileDetails">
                        <UserProfileDetails
							:userProfileId="activeUserProfile"
							:edit="true"
							v-if="activeUserProfile != -1"
                            :key="activeUserProfile"
							@update="onUserProfileUpdate"
							@copy="onCopyUserProfile"
						></UserProfileDetails>
						<v-row v-else>
							<v-col cols="12">
								<h3>{{ $t('userprofile.list.blank') }}</h3>
							</v-col>
						</v-row>
					</v-col>

				</v-row>
			</v-card>
		</v-container>

		<!-- Creation Dialog -->
		<v-dialog v-model="dialogCreationVisible" max-width="1200">
			<v-card>
				<v-card-title>{{ $t('userprofile.dialog.create.title') }}</v-card-title>
				<v-card-text class="dialog-card-text">
					<UserProfileDetails
						ref="userProfileDialog"
						@create="onUserProfileCreation"
					></UserProfileDetails>
				</v-card-text>
				<v-card-actions>
					<v-spacer></v-spacer>
					<v-btn text @click="dialogCreationVisible = false">{{
						$t('common.cancel')
					}}</v-btn>
					<v-btn text color="success" @click="addUserProfile()">{{
						$t('common.ok')
					}}</v-btn>
				</v-card-actions>
			</v-card>
		</v-dialog>

		<!-- Deletion Dialog -->
		<ConfirmationDialog
			:btnvisible="false"
			:dialog="dialogDeletionVisible"
			@result="onDeleteDialogClose"
			:title="$t('userprofile.dialog.delete.title')"
			:message="$t('userprofile.dialog.delete.text')"
		></ConfirmationDialog>


		<v-snackbar v-model="snackbar.visible" :color="snackbar.color">
			{{ snackbar.message }}
		</v-snackbar>
	</div>
</template>
<script>
import UserProfileDetails from './UserProfileDetails.vue';
import ConfirmationDialog from '@/layout/dialogs/ConfirmationDialog';
import SnackbarMixin from '@/layout/snackbars/SnackbarMixin.js';

/**
 * User Profile List component - View Page
 * 
 * This page is used to view and manage the user profiles. User
 * profile is a set of permissions that can be assigned to a multiple users.
 * 
 * @author Radoslaw Jajko <rjajko@softq.pl>
 * @version 1.0.0
 */
export default {
	name: 'UserProfilesPage',

    components: {
        UserProfileDetails,
		ConfirmationDialog
    },

	mixins: [SnackbarMixin],

	data() {
		return {
            itemsListLoaded: true,
            profileLists: [],
            activeUserProfile: -1,
			operationQueue: null,
			dialogCreationVisible: false,
			dialogDeletionVisible: false,
		};
	},

	mounted() {
        this.fetchUserProfileList();
	},

	methods: {
        async fetchUserProfileList() {
            this.itemsListLoaded = false;
            this.profileLists = await this.$store.dispatch('getUserProfilesList');
            this.itemsListLoaded = true;
        },

        async changeActiveItem(item) {
            this.activeUserProfile = item.id;
        },

        addUserProfile() {
			this.dialogCreationVisible = false;
			this.$refs.userProfileDialog.createUserProfile();
        },

		openCreationDialog() {
			this.activeUserProfile = -1;
			this.dialogCreationVisible = true;
			this.$refs.userProfileDialog.createBlankUserProfile();
        },

		onCopyUserProfile(profileId) {
			this.activeUserProfile = -1;
			this.dialogCreationVisible = true;
			this.$refs.userProfileDialog.createCopyOfUserProfile(profileId);
		},

		openDeletionDialog(userProfile) {
			this.dialogDeletionVisible = true;
			this.operationQueue = userProfile.id;
        },

		onDeleteDialogClose(result) {
			this.dialogDeletionVisible = false;
			if (result) {
				try {
					this.deleteUserProfile(this.operationQueue);
					this.showCrudSnackbar('delete')
				} catch (e) {
					this.showCrudSnackbar('delete', false)
					console.error(e);
				}
			}
		},

		async deleteUserProfile(userProfileId) {
			await this.$store.dispatch('deleteUserProfile', userProfileId);
			
			this.profileLists = this.profileLists.filter(item => item.id !== userProfileId);
			if(this.activeUserProfile === userProfileId) {
				this.activeUserProfile = -1;
			}
		},

		/* EVENTS FROM CHILD COMPONENTS */
		onUserProfileUpdate(result) {
			if (result) {
				this.showCrudSnackbar('update')
			} else {
				this.showCrudSnackbar('update', false)
			}
        },

		onUserProfileCreation(result) {
			if (result) {
				this.showCrudSnackbar('add')
				this.fetchUserProfileList();
			} else {
				this.showCrudSnackbar('add', false)
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
.dialog-card-text {
	max-height: 74vh;
	overflow-y: auto;
}
#userProfileListSection {
	max-height: 61vh;
	overflow-y: auto;
}
</style>
