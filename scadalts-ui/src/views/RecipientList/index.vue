<template>
	<div>
		<v-container fluid>
			<h1>Recipient List</h1>
		</v-container>
		<v-container fluid>
			<v-card class="slts-card">
				<v-row>
					<v-col cols="3" xs="12">
						<v-list v-if="recipeintListsLoaded">
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
								<v-list-item-action @click="deleteRecipientList(item)">
									<v-icon> mdi-minus-circle </v-icon>
								</v-list-item-action>
							</v-list-item>
							<v-list-item>
								<v-list-item-content>
									<v-list-item-title>
										<v-fab-transition>
											<v-btn color="primary" dark absolute bottom right x-small fab @click="createRecipientList()">
												<v-icon> mdi-plus </v-icon>
											</v-btn>
										</v-fab-transition>
										Create new recipient list
									</v-list-item-title>
								</v-list-item-content>
							</v-list-item>
						</v-list>
						<v-skeleton-loader v-else type="list-item-two-line"></v-skeleton-loader>
					</v-col>
					<v-divider vertical class="divider-horizontal-margin"></v-divider>
					<v-col cols="8" xs="12">
						<RecipientListDetails
							ref="recipientListDetails"
							:recipientList="activeRecipientList"
							v-if="activeRecipientList"
						></RecipientListDetails>
						<v-row v-else>
							<v-col cols="12">
								<h3>Select recipient list to see the preview.</h3>
							</v-col>
						</v-row>
					</v-col>
				</v-row>
			</v-card>
		</v-container>
		<v-dialog v-model="showRLCreationDialog" max-width="1000">
			<v-card>
				<v-card-title>
					Create Mailing List
				</v-card-title>
				<v-card-text class="dialog-card-text">
					<RecipientListDetails
						ref="recipientListDialog"
						:recipientList="blankRecipientList"
						:edit="true"
					></RecipientListDetails>
				</v-card-text>
				<v-card-actions>
					<v-spacer></v-spacer>
					<v-btn text @click="showRLCreationDialog = false">{{$t('common.cancel')}}</v-btn>
					<v-btn text color="success" @click="addRecipientList()">{{$t('common.ok')}}</v-btn>
				</v-card-actions>
			</v-card>
		</v-dialog>
	</div>
</template>
<script>
import RecipientListDetails from './RecipientListDetails';

export default {
	name: 'RecipientList',

	components: {
		RecipientListDetails,
	},

	data() {
		return {
			showRLCreationDialog: false,
			recipeintListsLoaded: false,
			recipeintLists: undefined,
			activeRecipientList: undefined,
			blankRecipientList: undefined,
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

		async changeActiveRL(item) {
			this.activeRecipientList = await this.$store.dispatch('getMailingList', item.id);
			this.$refs.recipientListDetails.changeActiveRL();
		},

		async deleteRecipientList(item) {
			await this.$store.dispatch('deleteMailingList', item.id);
			this.fetchRecipeintLists();
			//TODO: Add Confirmation dialog
		},

		createRecipientList() {
			//TODO: Add Recipient list dialog
			this.showRLCreationDialog = true;
			this.blankRecipientList = JSON.parse(JSON.stringify(this.$store.state.storeMailingList.mailingListTemplate));
		},

		addRecipientList() {			
			this.$refs.recipientListDialog.preSave();
			this.$store.dispatch('createMailingList', this.blankRecipientList);
			this.recipeintLists.push(this.blankRecipientList);
			this.showRLCreationDialog = false;
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
