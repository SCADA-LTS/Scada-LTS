<template>
	<div>
		<v-container fluid class="slts-page-header">
			<v-row align="center">
				<v-col xs="12" sm="6" md="6" class="slts--title">
					<h1>
						<span>
							{{ $t('watchlist.title') }}
						</span>
						<v-tooltip bottom v-if="activeWatchList && activeWatchList.id !== -1">
							<template v-slot:activator="{ on, attrs }">
								<v-icon v-bind="attrs" v-on="on">mdi-account</v-icon>
							</template>
							<div>
								<span class="tooltip--username"
									>Owner: {{ activeWatchList.user.username }}</span
								><br />
								<span
									>{{ activeWatchList.user.firstName }}
									{{ activeWatchList.user.lastName }}</span
								>
							</div>
						</v-tooltip>
					</h1>
				</v-col>

				<v-col xs="12" sm="6" md="4" class="slts--toolbar row justify-end">
					<WatchListConfig
						:create="true"
						@createStarted="onCreateStarted"
						@create="createWatchList"
						class="header-settings--buttons"
					></WatchListConfig>
					<WatchListConfig
						v-if="activeWatchList && activeWatchList.id !== -1 && userRights === 2"
						:key="activeWatchList.id"
						@update="updateWatchList"
						class="header-settings--buttons"
					></WatchListConfig>
					<v-btn
						fab
						elevation="1"
						v-if="activeWatchList && activeWatchList.id !== -1 && userRights === 2"
						@click="openDeletionDialog"
						class="header-settings--buttons"
						><v-icon>mdi-minus-circle</v-icon>
					</v-btn>
				</v-col>
				<v-col xs="12" sm="12" md="2" class="slts--selectbox">
					<v-select
						v-model="watchListSelectBox"
						:label="$t(`watchlist.header.select`)"
						dense
						:items="watchListsArray"
						item-text="name"
						item-value="id"
						@change="navToWatchListDetails"
					></v-select>
				</v-col>
			</v-row>
		</v-container>

		<router-view @routeChanged="onRouteChanged"></router-view>

		<ConfirmationDialog
			:btnvisible="false"
			ref="deletionDialog"
			@result="onDeleteDialogClose"
			:title="$t('watchlist.dialog.delete.title')"
			:message="$t('watchlist.dialog.delete.text')"
		></ConfirmationDialog>
	</div>
</template>
<script>
import draggable from 'vuedraggable';

import ConfirmationDialog from '@/layout/dialogs/ConfirmationDialog';

import PointWatcher from './PointWatcher';
import PointChart from './PointChart/index.vue';
import WatchListConfig from './WatchListConfig';
/**
 * @author Radoslaw Jajko <rjajko@softq.pl>
 * @version 1.0.0
 */
export default {
	name: 'WatchList',

	components: {
		PointWatcher,
		PointChart,
		WatchListConfig,
		ConfirmationDialog,
		draggable,
	},

	data() {
		return {
			watchListSelectBox: -1,
			watchListsArray: [],
		};
	},
	computed: {
		activeWatchList: {
			get() {
				return this.$store.state.watchListModule.activeWatchList;
			},
			set(newValue) {
				return this.$store.dispatch('updateActiveWatchList', newValue);
			},
		},

		userRights() {
			const user = this.$store.state.loggedUser;
			if (!!user && !!this.activeWatchList) {
				if (!!user.admin || user.id === this.activeWatchList.user.id) {
					return 2;
				} else {
					return this.activeWatchList.accessType === 3 
						? 2 : this.activeWatchList.accessType;
				}
			}
			return 0;
		},
	},

	mounted() {
		this.fetchWatchLists();
	},

	beforeDestroy() {
		this.$store.commit('UPDATE_ACTIVE_WATCHLIST', null);
	},

	methods: {
		async fetchWatchLists() {
			this.watchListsArray = await this.$store.dispatch('getAllWatchLists');
		},

		async navToWatchListDetails(watchListId) {
			this.$router.push({ path: `/watch-list/${watchListId}` });
		},

		createWatchList() {
			this.$store.dispatch('createWatchList').then((resp) => {
				this.watchListsArray.push({
					id: resp.id,
					xid: resp.xid,
					name: resp.name,
				});
				this.watchListSelectBox = resp.id;
				this.navToWatchListDetails(resp.id);
			});
		},

		updateWatchList() {
			this.$store.dispatch('updateWatchList');
			this.$router.go();
		},

		deleteWatchList() {
			this.$store.dispatch('deleteWatchList').then(() => {
				this.watchListsArray = this.watchListsArray.filter(
					(wl) => wl.id !== this.activeWatchList.id
				);
				this.$store.commit('UPDATE_ACTIVE_WATCHLIST', null);
			});
		},

		openDeletionDialog() {
			this.$refs.deletionDialog.showDialog();
		},

		onDeleteDialogClose(result) {
			if (result) {
				this.deleteWatchList();
			}
		},

		onCreateStarted() {
			this.watchListSelectBox = -1;
			this.$router.push({ path: `/watch-list/` });
		},

		onRouteChanged(id) {
			this.watchListSelectBox = id;
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
.flex-jc-center {
	justify-content: center;
}
.watchlist-component--vertical {
	width: 50%;
	float: left;
}
.watchlist-component--bigger-chart {
	width: 65%;
	float: left;
}
.watchlist-component--smaller-list {
	width: 35%;
	float: left;
}
.header-settings--buttons {
	margin: 0 5px;
}

.tooltip--username {
	font-size: 12px;
	font-style: italic;
}
</style>
