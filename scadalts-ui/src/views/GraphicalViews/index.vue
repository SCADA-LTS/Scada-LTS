<template>
	<div>
		<v-container fluid class="slts-page-header" v-if="!editMode">
			<v-row align="center">
				<v-col>
					<h1>Graphical Views</h1>
				</v-col>
				<v-col class="row justify-end">
					<v-btn icon @click="toggleFullScreen">
						<v-icon> mdi-arrow-expand-all </v-icon>
					</v-btn>
					<v-btn icon>
						<v-icon> mdi-plus </v-icon>
					</v-btn>
					<v-btn icon @click="changeToEditMode">
						<v-icon> mdi-pencil </v-icon>
					</v-btn>
					<v-btn icon @click="removeGraphicalView">
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
				<v-col>
					<v-btn>
						<v-icon> mdi-image </v-icon>
					</v-btn>
					<v-btn @click="toggleIconify">
						<v-icon> mdi-cube </v-icon>
					</v-btn>
					<v-btn>
						<v-icon> mdi-add </v-icon>
					</v-btn>
				</v-col>
			</v-row>
		</v-container>
		<v-container fluid class="slts-page-content" v-bind:class="{'slts-fullscreen-container' : fullscreenEnabled}">
			<router-view @routeChanged="onRouteChanged"></router-view>
		</v-container>
		<v-container fluid v-if="editMode" class="slts-page-actions--floating">
			<v-btn @click="editCancel"> Cancel </v-btn>
			<v-btn color="primary" @click="editAccept"> Save </v-btn>
		</v-container>
	</div>
</template>
<script>
import GraphicalViewPage from './GraphicalViewPage.vue';
export default {
	components: {
		GraphicalViewPage,
	},

	data() {
		return {
			activeGraphicalView: null,
			graphicalViewList: [],
			fullscreenEnabled: false,
			annonymousAccess: [
				{
					value: 'NONE',
					text: 'None',
				},
				{
					value: 'READ',
					text: 'Read',
				},
				{
					value: 'SET',
					text: 'Set',
				},
			],
			canvasResolutions: [
				{
					value: 'R640x480',
					text: '640 x 480',
				},
				{
					value: 'R800x600',
					text: '800 x 600',
				},
				{
					value: 'R1024x768',
					text: '1024 x 768',
				},
			],
		};
	},

	computed: {
		editMode() {
			return this.$store.state.graphicalViewModule.graphicalPageEdit;
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
				let res = await this.$store.dispatch('saveGraphicalView');
				console.log(res);
				if (res) {
					this.$store.commit('SET_GRAPHICAL_PAGE_EDIT', false);
				}
			} catch (e) {
				console.error(e);
			}
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
</style>
