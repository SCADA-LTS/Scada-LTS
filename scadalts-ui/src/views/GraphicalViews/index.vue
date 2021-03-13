<template>
	<div>
		<v-container fluid class="slts-page-header" v-if="!editMode">
			<v-row align="center">
				<v-col>
					<h1>Graphical Views</h1>
				</v-col>
				<v-col class="row justify-end toolbar--action-butons">
					<v-tooltip bottom v-if="!!activeGraphicalView">
						<template v-slot:activator="{ on, attrs }">
							<v-btn icon @click="toggleFullScreen" v-on="on" v-bind="attrs">
								<v-icon> mdi-arrow-expand-all </v-icon>
							</v-btn>
						</template>
						<span>
							Toggle Full Screen<br />
							Press "Ctrl + Shift + F"
						</span>
					</v-tooltip>

					<v-tooltip bottom>
						<template v-slot:activator="{ on, attrs }">
							<v-btn icon @click="changeToCreateMode" v-on="on" v-bind="attrs">
								<v-icon> mdi-plus </v-icon>
							</v-btn>
						</template>
						<span> Create a new view </span>
					</v-tooltip>

					<v-tooltip bottom v-if="!!activeGraphicalView && userAccess === 2">
						<template v-slot:activator="{ on, attrs }">
							<v-btn icon @click="changeToEditMode" v-on="on" v-bind="attrs">
								<v-icon> mdi-pencil </v-icon>
							</v-btn>
						</template>
						<span> Edit view </span>
					</v-tooltip>

					<v-tooltip bottom v-if="!!activeGraphicalView && userAccess === 2">
						<template v-slot:activator="{ on, attrs }">
							<v-btn icon @click="removeGraphicalView" v-on="on" v-bind="attrs">
								<v-icon> mdi-delete </v-icon>
							</v-btn>
						</template>
						<span> Delete view </span>
					</v-tooltip>
				</v-col>
				<v-col>
					<v-select
						label="Selected Graphical View"
						@change="moveToGraphicalView"
						v-model="activeGraphicalView"
						:items="graphicalViewList"
						item-value="id"
						item-text="name"
					></v-select>
				</v-col>
			</v-row>
		</v-container>
		<v-container fluid v-else>
			<v-form v-model="valid" ref="form">
				<v-row class="toolbar--edit-form">
					<v-col>
						<v-text-field
							label="Name"
							v-model="activePage.name"
							:rules="[ruleRequired]"
						></v-text-field>
					</v-col>
					<v-col>
						<v-text-field
							label="Export ID"
							v-model="activePage.xid"
							@input="checkXidUnique"
							:rules="[ruleRequired, ruleXidUnique]"
						></v-text-field>
					</v-col>
					<v-col>
						<v-select
							label="Annonymous Access"
							v-model="activePage.anonymousAccess"
							:items="annonymousAccess"
						></v-select>
					</v-col>
					<v-col>
						<v-select
							label="Canvas size"
							v-model="activePage.resolution"
							:items="canvasResolutions"
							@change="changeCanvasResolution"
						></v-select>
					</v-col>
					<v-col class="flex-jc-space-around">
						<v-tooltip bottom>
							<template v-slot:activator="{ on, attrs }">
								<v-btn @click="showBackgroundDialog" v-on="on" v-bind="attrs">
									<v-icon> mdi-image </v-icon>
								</v-btn>
							</template>
							<span> Open background settings </span>
						</v-tooltip>

						<v-tooltip bottom>
							<template v-slot:activator="{ on, attrs }">
								<v-btn @click="toggleIconify" v-on="on" v-bind="attrs">
									<v-icon> mdi-cube </v-icon>
								</v-btn>
							</template>
							<span> Iconify components </span>
						</v-tooltip>

						<v-tooltip bottom>
							<template v-slot:activator="{ on, attrs }">
								<v-btn color="primary" @click="showComponentsDialog" v-on="on" v-bind="attrs">
									<v-icon> mdi-plus </v-icon>
								</v-btn>
							</template>
							<span> Show component library </span>
						</v-tooltip>

					</v-col>
				</v-row>
			</v-form>
		</v-container>
		<v-container
			fluid
			class="slts-page-content"
			v-bind:class="{ 'slts-fullscreen-container': fullscreenEnabled }"
		>
			<router-view @routeChanged="onRouteChanged"></router-view>
		</v-container>
		<v-container fluid v-if="editMode" class="slts-page-actions--floating">
			<v-btn @click="editCancel"> Cancel </v-btn>
			<v-btn color="primary" @click="editAccept" :disabled="!valid">
				{{ createMode ? 'Create' : 'Update' }}
			</v-btn>
		</v-container>
		<BackgroundSettingsDialog
			ref="backgroundDialog"
			@image-update="onBackgroundImageChanged"
			@reset="onBackgroundReset"
		></BackgroundSettingsDialog>
		<ComponentCreationDialog ref="creationDialog"></ComponentCreationDialog>
		<ConfirmationDialog
			ref="confirmationDialog"
			@result="onRemoveGraphicalView"
			title="Remove Graphical View"
			message="Are you sure you want to remove this graphical view?"
		></ConfirmationDialog>

		<v-snackbar v-model="snackbar.visible" :color="snackbar.color">
			{{ snackbar.message }}
		</v-snackbar>
	</div>
</template>
<script>
import GraphicalViewPage from './GraphicalViewPage.vue';
import GraphicalViewItem from '@/models/GraphicalViewItem';
import ComponentCreationDialog from './dialogs/ComponentCreationDialog';
import BackgroundSettingsDialog from './dialogs/BackgroundSettingsDialog';
import ConfirmationDialog from '@/layout/dialogs/ConfirmationDialogV2';
import SnackbarMixin from '@/layout/snackbars/SnackbarMixin.js';

export default {
	components: {
		GraphicalViewPage,
		ComponentCreationDialog,
		BackgroundSettingsDialog,
		ConfirmationDialog,
	},

	mixins: [SnackbarMixin],

	data() {
		return {
			activeGraphicalView: null,
			graphicalViewList: [],
			createMode: false,
			fullscreenEnabled: false,
			xidUnique: true,
			valid: false,
			ruleRequired: (v) => !!v || this.$t('form.validation.required'),
			ruleXidUnique: () => this.xidUnique || this.$t('common.snackbar.xid.not.unique'),
		};
	},

	computed: {
		editMode() {
			return this.$store.state.graphicalViewModule.graphicalPageEdit;
		},
		canvasResolutions() {
			return this.$store.state.graphicalViewModule.canvasResolutions;
		},
		annonymousAccess() {
			return this.$store.state.graphicalViewModule.annonymousAccess;
		},
		userAccess() {
			return this.$store.getters.userGraphicViewAccess;
		},
		activePage: {
			get() {
				return this.$store.state.graphicalViewModule.graphicalPage;
			},
			set(page) {
				this.$store.commit('SET_GRAPHICAL_PAGE', page);
			},
		},
	},

	mounted() {
		this.fetchGraphicalViewList();
		this.addFullScreenKeyListener();
	},

	methods: {
		async fetchGraphicalViewList() {
			try {
				this.graphicalViewList = await this.$store.dispatch('fetchGraphicalViewsList');
			} catch (e) {
				console.log(e);
			}
		},
		changeToEditMode() {
			this.$store.commit('SET_GRAPHICAL_PAGE_EDIT', true);
		},
		async changeToCreateMode() {
			this.changeToEditMode();
			this.createMode = true;
			const view = new GraphicalViewItem(this.$store.state.loggedUser.id);
			try {
				view.xid = await this.$store.dispatch('getUniqeGraphicalViewXid');
			} catch (e) {
				console.log(e);
			}
			this.$store.commit('SET_GRAPHICAL_PAGE', view);
			this.$router.push({ path: `/graphical-view/-1` });
		},

		removeGraphicalView() {
			this.$refs.confirmationDialog.openDialog();
		},

		async onRemoveGraphicalView(event) {
			if (event.result) {
				try {
					await this.$store.dispatch('deleteGraphicalView');
					this.$router.push({ path: '/graphical-view' });
					this.fetchGraphicalViewList();
					this.activeGraphicalView = null;
					this.showCrudSnackbar('delete');
				} catch (e) {
					this.showCrudSnackbar('delete', false);
					console.log(e);
				}
			}
		},
		addFullScreenKeyListener() {
			window.addEventListener('keypress', (e) => {
				if (e.keyCode === 6) {
					this.toggleFullScreen();
				}
			});
		},
		toggleFullScreen() {
			this.fullscreenEnabled = !this.fullscreenEnabled;
		},

		toggleIconify() {
			this.$store.commit('TOGGLE_GRAPHICAL_PAGE_ICONIFY');
		},
		editCancel() {
			this.$store.commit('REVERT_GRAPHICAL_PAGE');
			this.$store.commit('SET_GRAPHICAL_PAGE_EDIT', false);
			this.createMode = false;
		},
		async editAccept() {
			try {
				let res;
				if (this.createMode) {
					res = await this.$store.dispatch('createGraphicalView');
					if (res) {
						this.fetchGraphicalViewList();
						this.activeGraphicalView = res.id;
						this.moveToGraphicalView();
						this.showCrudSnackbar('add');
						this.moveToGraphicalView();
					}
				} else {
					res = await this.$store.dispatch('saveGraphicalView');
					this.showCrudSnackbar('update');
				}
				this.createMode = false;

				if (res) {
					this.$store.commit('SET_GRAPHICAL_PAGE_EDIT', false);
				}
			} catch (e) {
				if (e.data.errors) {
					this.showSnackbar(e.data.errors, 'error');
				} else {
					this.showCrudSnackbar('add', false);
				}

				console.error(e);
			}
		},

		showComponentsDialog() {
			this.$refs.creationDialog.openDialog();
		},
		moveToGraphicalView() {
			this.$router.push({ path: `/graphical-view/${this.activeGraphicalView}` });
		},
		onRouteChanged(id) {
			this.activeGraphicalView = id;
		},

		showBackgroundDialog() {
			this.$refs.backgroundDialog.openDialog();
		},

		onBackgroundImageChanged(image) {
			this.$store.commit('SET_GRAPHICAL_PAGE_BACKGROUND', image);
		},

		onBackgroundReset() {
			this.$store.commit('RESET_GRAPHICAL_PAGE_BACKGROUND');
		},

		changeCanvasResolution() {
			this.$store.commit('UPDATE_GRAPHICAL_PAGE_RESOLUTION');
		},

		isFormValid() {
			return this.$refs.form.validate();
		},

		async checkXidUnique() {
			try {
				if (this.editMode) {
					const res = await this.$store.dispatch('isGraphicalViewXidUnique', {
						xid: this.activePage.xid,
						id: this.activePage.id,
					});
					this.xidUnique = res.unique;
					this.$refs.form.validate();
				}
			} catch (e) {
				console.error('Failed to check unique of xid!');
			}
		},
	},
};
</script>
<style scoped>
.slts-page-content {
	height: 77vh;
	/* background-color: bisque; */
	background-color: white;
	overflow: auto;
}
.slts-page-actions--floating {
	position: fixed;
	bottom: 10px;
	width: 100%;
	display: flex;
	justify-content: center;
	align-items: center;
}
.slts-fullscreen-container {
	position: fixed;
	top: 0;
	left: 0;
	z-index: 10;
	height: 100vh;
	width: 100vw;
	overflow: auto;
	display: flex;
	align-items: center;
	justify-content: center;
}
.flex-jc-space-around {
	display: flex;
	justify-content: space-around;
}
</style>
