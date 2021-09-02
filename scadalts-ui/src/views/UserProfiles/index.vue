<template>
	<div>
		<v-container fluid>
			<h1>{{ $t('userprofiles.title') }}</h1>
		</v-container>
		<v-container fluid>
			<v-card class="slts-card">
				<v-row>
					<v-col md="3" sm="12" xs="12">
						<v-list v-if="userProfileListsLoaded" id="userProfileListSection">
							<v-list-item
								v-for="item in profileLists"
								:key="item.id"
								@click="changeActiveUP(item)"
							>
								<v-list-item-content>
									<v-list-item-title>
										{{ item.name }}
									</v-list-item-title>
								</v-list-item-content>
								<v-list-item-action @click="deleteUserProfile(item)">
									<v-icon> mdi-minus-circle </v-icon>
								</v-list-item-action>
							</v-list-item>
						</v-list>
						<v-skeleton-loader v-else type="list-item-two-line"></v-skeleton-loader>
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
												@click="createUserProfile()"
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
		<v-dialog v-model="showUPCreationDialog" max-width="1200">
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
					<v-btn text @click="showUPCreationDialog = false">{{
						$t('common.cancel')
					}}</v-btn>
					<v-btn text color="success" @click="addUserProfile()">{{
						$t('common.ok')
					}}</v-btn>
				</v-card-actions>
			</v-card>
		</v-dialog>
		<ConfirmationDialog
			:btnvisible="false"
			:dialog="deleteUserProfileDialog"
			@result="deleteUserProfileDialogResult"
			title="Delete User Profile"
			message="Are you sssure?"
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
            userProfileListsLoaded: true,
            showUPCreationDialog: false,
            profileLists: [],
            activeUserProfile: -1,
			blankUserProfile: null,
			deleteUserProfileDialog: false,
			operationQueue: null,
		};
	},

	mounted() {
        this.fetchUserProfileList();
	},

	methods: {
        async fetchUserProfileList() {
            this.userProfileListsLoaded = false;
            this.profileLists = await this.$store.dispatch('getUserProfilesList');
            this.userProfileListsLoaded = true;
        },

        async changeActiveUP(userProfile) {
            this.activeUserProfile = userProfile.id;
        },

        createUserProfile() {
			this.showUPCreationDialog = true;
			this.$refs.userProfileDialog.fetchUserProfileDetails();
        },

        addUserProfile() {
			this.showUPCreationDialog = false;
			this.$refs.userProfileDialog.createUserProfile();
        },

		deleteUserProfile(userProfile) {
			this.deleteUserProfileDialog = true;
			this.operationQueue = userProfile.id;
        },

		async deleteUserProfileDialogResult(result) {
			this.deleteUserProfileDialog = false;
			if (result) {
				try {
					await this.$store.dispatch('deleteUserProfile', this.operationQueue);
					this.showCrudSnackbar('delete')
					this.profileLists = this.profileLists.filter(item => item.id !== this.operationQueue);
					if(this.activeUserProfile === this.operationQueue) {
						this.activeUserProfile = -1;
					}
				} catch (e) {
					this.showCrudSmackbar('delete', false)
					console.error(e);
				}
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
</style>
