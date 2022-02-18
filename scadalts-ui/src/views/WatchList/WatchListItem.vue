<template>
	<v-container
		fluid
		class="slts-page-content"
		v-if="activeWatchList && activeWatchList.id !== -1"
		:key="activeWatchList.id"
	>
		<v-card class="slts-card">
			<v-row class="flex-jc-center">
				<draggable
					:list="watchListComponents"
					handle=".dragHandle"
					@end="saveWatchListLayout"
					class="full-width"
				>
					<v-col
						v-for="cmp in watchListComponents"
						:key="cmp.id"
						class="watchlist-component"
						:class="componentStyleClass(cmp)"
					>
						<div class="jc-space-between">
							<div>
								<v-icon class="dragHandle" v-if="userRights === 2"
									>mdi-drag-vertical</v-icon
								>
								<span>{{ $t(`watchlist.list.${cmp.component}`) }}</span>
							</div>

							<v-btn
								v-if="cmp.component === 'PointWatcher'"
								class="small-margin--right"
								x-small
								icon
								elevation="0"
								@click="hideDataSourceName = !hideDataSourceName"
							>
								<v-icon v-show="!hideDataSourceName"> mdi-database </v-icon>
								<v-icon v-show="hideDataSourceName"> mdi-database-outline </v-icon>
							</v-btn>
						</div>

						<div v-if="cmp.component === 'PointWatcher'">
							<PointWatcher
								:key="activeWatchList.id"
								:dataPointList="activeWatchList.pointList"
								:permissions="userRights"
								:hideDataSourceName="hideDataSourceName"
							></PointWatcher>
						</div>
						<div v-else>
							<PointChart></PointChart>
						</div>
					</v-col>
				</draggable>
			</v-row>
		</v-card>
	</v-container>
</template>
<script>
import draggable from 'vuedraggable';

import PointWatcher from './PointWatcher';
import PointChart from './PointChart';

export default {
	name: 'WatchListItem',

	components: {
		PointWatcher,
		PointChart,
		draggable,
	},

	data() {
		return {
			hideDataSourceName: false,
			watchListComponents: [
				{
					id: 0,
					component: 'PointWatcher',
				},
				{
					id: 1,
					component: 'PointChart',
				},
			],
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
		this.fetchWatchListDetails(this.$route.params.id);
	},

	methods: {
		async fetchWatchListDetails(watchListId) {
			await this.$store.dispatch('getWatchListDetails', watchListId);
			this.loadWatchListLayout();
			this.$emit('routeChanged', Number(watchListId));
		},

		saveWatchListLayout() {
			localStorage.setItem(
				`MWLDL_${this.activeWatchList.id}`,
				JSON.stringify(this.watchListComponents)
			);
		},

		loadWatchListLayout() {
			if (!!this.activeWatchList) {
				let data = JSON.parse(localStorage.getItem(`MWLDL_${this.activeWatchList.id}`));
				if (!!data) {
					this.watchListComponents = data;
				}
			}
		},

		componentStyleClass(item) {
			if (this.activeWatchList.horizontal) {
				return 'watchlist-component--horizontal';
			} else {
				if (this.activeWatchList.biggerChart) {
					if (item.component === 'PointChart') {
						return 'watchlist-component--bigger-chart';
					} else {
						return 'watchlist-component--smaller-list';
					}
				} else {
					return 'watchlist-component--vertical';
				}
			}
		},
	},

	watch: {
		$route(to) {
			this.fetchWatchListDetails(to.params.id);
		},
	},
};
</script>
<style scoped>
.jc-space-between {
	display: flex;
	justify-content: space-between;
}
.small-margin--right {
	margin-right: 10px;
}
.full-width {
	width: 100%;
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
@media screen and (max-width: 768px) {
	.watchlist-component--vertical {
		width: 100%;
		float: none;
	}
	.watchlist-component--bigger-chart {
		width: 100%;
		float: none;
	}
	.watchlist-component--smaller-list {
		width: 100%;
		float: none;
	}
}
</style>
