<template>
	<v-container
		fluid
		class="slts-page-content"
		v-bind:class="{ 'slts-fullscreen-container': fullscreenEnabled }"
	>
		<router-view @routeChanged="onRouteChanged"></router-view>
	</v-container>
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
			createMode: false,
			fullscreenEnabled: false,
		};
	},

	computed: {
		userAccess() {
			if (!!this.$store.state.graphicalViewModule.graphicalPage) {
				return this.$store.state.graphicalViewModule.graphicalPage.anonymousAccess;
			}
			return 0;
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
		this.addFullScreenKeyListener();
	},

	methods: {
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
    left: 47%;
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
