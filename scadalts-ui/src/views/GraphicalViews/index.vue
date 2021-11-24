<template>
	<div>
		<v-container fluid class="slts-page-header" v-if="!editMode">
			<v-row align="center">
				<v-col>
					<h1>Graphical Views</h1>
				</v-col>
				<v-col class="row justify-end">
					<v-btn icon @click="toggleFullScreen" v-if="!!activeGraphicalView">
						<v-icon> mdi-arrow-expand-all </v-icon>
					</v-btn>
					<v-btn icon @click="changeToCreateMode">
						<v-icon> mdi-plus </v-icon>
					</v-btn>
					<v-btn icon @click="changeToEditMode" v-if="!!activeGraphicalView">
						<v-icon> mdi-pencil </v-icon>
					</v-btn>
					<v-btn icon @click="removeGraphicalView" v-if="!!activeGraphicalView">
						<v-icon> mdi-delete </v-icon>
					</v-btn>
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
			<v-row>
				<v-col>
					<v-text-field label="Name" v-model="activePage.name"></v-text-field>
				</v-col>
				<v-col>
					<v-text-field label="Export ID" v-model="activePage.xid"></v-text-field>
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
					></v-select>
				</v-col>
				<v-col class="flex-jc-space-around">
					<v-btn>
						<v-icon> mdi-image </v-icon>
					</v-btn>
					<v-btn @click="toggleIconify">
						<v-icon> mdi-cube </v-icon>
					</v-btn>
					<v-btn color="primary" @click="showComponentsDialog">
						<v-icon> mdi-plus </v-icon>
					</v-btn>
				</v-col>
			</v-row>
		</v-container>
		<v-container fluid class="slts-page-content" v-bind:class="{'slts-fullscreen-container' : fullscreenEnabled}">
			<router-view @routeChanged="onRouteChanged"></router-view>
		</v-container>
		<v-container fluid v-if="editMode" class="slts-page-actions--floating">
			<v-btn @click="editCancel"> Cancel </v-btn>
			<v-btn color="primary" @click="editAccept"> {{createMode ? 'Create' : 'Update' }} </v-btn>
		</v-container>
		<ComponentCreationDialog
			ref="creationDialog"	
		></ComponentCreationDialog>
	</div>
</template>
<script>
import GraphicalViewPage from './GraphicalViewPage.vue';
import GraphicalViewItem from '../../models/GraphicalViewItem';
import ComponentCreationDialog from './ComponentCreationDialog';

export default {
	components: {
		GraphicalViewPage,
		ComponentCreationDialog
	},

	data() {
		return {
			activeGraphicalView: null,
			graphicalViewList: [],
			createMode: false,
			fullscreenEnabled: false,
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
				console.log(this.graphicalViewList);
			} catch (e) {
				console.log(e);
			}
		},
		changeToEditMode() {
			this.$store.commit('SET_GRAPHICAL_PAGE_EDIT', true);
		},
		changeToCreateMode() {
			this.changeToEditMode();
			this.createMode = true;
			const view = new GraphicalViewItem(this.$store.state.loggedUser.username)
			this.$store.commit("SET_GRAPHICAL_PAGE", view);
		},
		async removeGraphicalView() {
			try {
				await this.$store.dispatch('deleteGraphicalView');
			} catch (e) {
				console.log(e);
			}
		},
		addFullScreenKeyListener() {
			window.addEventListener("keypress", e=> {
				if(e.keyCode === 6) {
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
		},
		async editAccept() {
			try {
				let res;
				if(this.createMode) {
					res = await this.$store.dispatch('createGraphicalView')
					if(res) {
						this.createMode = false;						
					}
				} else {
					res = await this.$store.dispatch('saveGraphicalView');
				}	
				if (res) {
					this.$store.commit('SET_GRAPHICAL_PAGE_EDIT', false);
				}			
			} catch (e) {
				console.error(e);
			}
		},

		showComponentsDialog() {
			this.$refs.creationDialog.openDialog();
		},
		moveToGraphicalView() {
			console.log(this.activeGraphicalView);
			this.$router.push({ path: `/graphical-view/${this.activeGraphicalView}` });
		},
		onRouteChanged(id) {
			this.activeGraphicalView = id;
		},
	},
};
</script>
<style scoped>
.slts-page-content {
	height: 77vh;
	background-color: bisque;
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
