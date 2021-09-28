<template>
	<div>
		<v-container fluid class="slts-page-header">
            <v-row align="center">
                <v-col xs="12" md="6">
                    <h1>{{$t('watchlist.title')}}</h1>
                </v-col>

                <v-col xs="12" md="4" class="row justify-end">
                    <WatchListConfig 
                        :create="true"
                        @create="createWatchList"
                        class="header-settings--buttons"
                    ></WatchListConfig>
                    <WatchListConfig 
                        v-if="selectedWatchList"
                        :key="selectedWatchList.id"
                        @update="updateWatchList"
                        class="header-settings--buttons"
                    ></WatchListConfig>
                    <v-btn fab elevation="1"
                        v-if="selectedWatchList"
                        @click="deleteWatchList"
                        class="header-settings--buttons"
                        ><v-icon>mdi-minus-circle</v-icon>
                    </v-btn>
                </v-col>
                <v-col xs="12" md="2">
                    <v-select
                        :label="$t(`watchlist.header.select`)"
                        dense
                        :items="watchLists"
                        item-text="name"
                        item-value="id"
                        @change="fetchWatchListDetails"
                    ></v-select>
                </v-col>
            </v-row>
		</v-container>

		<v-container fluid class="slts-page-content" v-if="selectedWatchList && selectedWatchList.id !== -1" :key="selectedWatchList.id">
			<v-card class="slts-card">
				<v-row class="flex-jc-center">
                    <draggable
                        :list="watchListItems"
                        handle=".dragHandle"
                        @end="saveWatchListLayout"
                    >
                    <v-col v-for="cmp in watchListItems" :key="cmp.id" class="watchlist-component" :class="componentStyleClass(cmp)">
                        <v-icon class="dragHandle">mdi-drag-vertical</v-icon>
                        <span>{{$t(`watchlist.list.${cmp.component}`)}}</span>
                        
                            <div v-if="cmp.component === 'PointWatcher'">
                                <PointWatcher
                                    :dataPointList="selectedWatchList.pointList"
                                ></PointWatcher>
                            </div>
                            <div v-else>
                                <PointChart>
                                </PointChart>
                            </div>
                    </v-col>
                    </draggable>
				</v-row>
			</v-card>
		</v-container>
        
	</div>
</template>
<script>
import draggable from "vuedraggable";

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
        draggable
    },

	data() {
		return {
            // selectedWatchList: null,
            blankWatchList: {
                id: -1,
                name: '',
                xid: '',
                pointList: [],
            },
            watchLists: [],
            watchListItems: [{
                id: 0,
                component: 'PointWatcher',
            },
            {
                id: 1,
                component: 'PointChart',
            }],
			
		};
	},
    computed: {
        selectedWatchList: {
            get() {
                return this.$store.state.watchListModule2.activeWatchList;
            },
            set(newValue) {
                return this.$store.dispatch('updateActiveWatchList', newValue);
            }
        },

    },

	mounted() {
        this.fetchWatchLists();
    },

	methods: {
        async fetchWatchLists() {
            this.watchLists = await this.$store.dispatch('fetchWatchLists');
            this.loadWatchListLayout();
        },

        async fetchWatchListDetails(watchListId) {
            await this.$store.dispatch('getWatchListDetails', watchListId);
            this.loadWatchListLayout();
            // this.selectedWatchList = watchListId;
        },

        removePoint(point) {
            this.dataPointList = this.dataPointList.filter(p => p.id !== point.id);
        },

        createWatchList() {
            this.$store.dispatch('createWatchList').then(resp => {
                this.watchLists.push({
                    id: resp.id,
                    xid: resp.xid,
                    name: resp.name,
                });
            });
        },

        updateWatchList() {
            this.$store.dispatch('updateWatchList');
        },

        deleteWatchList() {
            this.$store.dispatch('deleteWatchList').then(() => {
                this.watchLists = this.watchLists.filter(wl => wl.id !== this.selectedWatchList.id);
                this.$store.commit('UPDATE_ACTIVE_WATCHLIST', null);
            });
            
            
        },

        componentStyleClass(item) {
            if(this.selectedWatchList.horizontal) {
                return 'watchlist-component--horizontal';
            } else {
                if(this.selectedWatchList.biggerChart) {
                    console.log('biggerChart');
                    if(item.component === 'PointChart') {
                        return 'watchlist-component--bigger-chart';
                    } else {
                        return 'watchlist-component--smaller-list';
                    }
                } else {
                    return 'watchlist-component--vertical';
                }

            }
        },

        saveWatchListLayout() {
            localStorage.setItem(`MWLDL_${this.selectedWatchList.id}`, JSON.stringify(this.watchListItems));
            
        }, 
        loadWatchListLayout() {
            let data = JSON.parse(localStorage.getItem(`MWLDL_${this.selectedWatchList.id}`));
            if(!!data) {
                this.watchListItems = data;
            }
        }


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
    width: 60%;
    float: left;
}
.watchlist-component--smaller-list {
    width: 40%;
    float: left;
}
.header-settings--buttons {
    margin: 0 5px;
}
</style>
