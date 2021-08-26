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
							ref="userProfileDetails"
							:userProfileId="activeUserProfile"
							v-if="activeUserProfile != -1"
                            :key="activeUserProfile"
							@saved="updateUserProfile"
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
					<!-- <RecipientListDetails
						ref="userProfileDialog"
						:userProfile="blankUserProfile"
						:edit="true"
					></RecipientListDetails> -->
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
		<!-- <ConfirmationDialog
			:btnvisible="false"
			:dialog="deleteRecipientDialog"
			@result="deleteRecipientDialogResult"
			:title="$t('recipientlist.dialog.delete.title')"
			:message="$t('recipientlist.dialog.delete.text')"
		></ConfirmationDialog> -->

		<!-- <v-snackbar v-model="snackbar.visible" :color="snackbar.color">
			{{ snackbar.message }}
		</v-snackbar> -->
	</div>
</template>
<script>
import UserProfileDetails from './UserProfileDetails.vue';
export default {
	name: 'UserProfilesPage',

    components: {
        UserProfileDetails
    },

	data() {
		return {
            userProfileListsLoaded: true,
            showUPCreationDialog: false,
            profileLists: [],
            activeUserProfile: null,
			blankUserProfile: null,
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

        },

        updateUserProfile() {

        },

        addUserProfile() {

        },

        deleteUserProfile(userProfile) {

        }
		
	},
};
</script>
<style scoped>
</style>
