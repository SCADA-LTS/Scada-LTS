<template>
<div class="datapointList">
    <v-dialog max-width="500" v-model="dialog">
        <template v-slot:activator="{on, attrs}">
             <v-btn fab elevation="1" v-bind="attrs" v-on="on" @click="showDialog">
                <v-icon v-if="create">mdi-plus</v-icon>
                <v-icon v-else>mdi-pencil</v-icon>
            </v-btn>
        </template>
        <v-card v-if="!!watchListDetails">
            <v-card-title>
                <h2>{{$t(`watchlist.settings.title`)}}</h2>
            </v-card-title>
            <v-card-text>
                <section>
                    <v-row>
                        <v-col cols="8">
                            <v-text-field
                                :label="$t(`watchlist.settings.name`)"
                                v-model="watchListDetails.name">
                            </v-text-field>
                        </v-col>
                        <v-col cols="4">
                            <v-text-field
                                :label="$t(`common.xid`)"
                                :disabled="!create"
                                v-model="watchListDetails.xid">
                            </v-text-field>
                        </v-col>
                    </v-row>
                </section>
                
                <section>
                    
                    <v-row dense>
                        
                    <v-col cols="12" class="watchlist-settings--layout">
                        <p>{{$t(`watchlist.settings.layout`)}}</p>
                        <v-btn-toggle v-model="watchListDetails.horizontal" group>
                            <v-btn :value="false">
                                {{$t(`watchlist.settings.layout.vertical`)}}
                                <v-icon>
                                    mdi-view-parallel
                                </v-icon>
                            </v-btn>
                            <v-btn :value="true">
                                {{$t(`watchlist.settings.layout.horizontal`)}}
                                <v-icon>
                                    mdi-view-sequential
                                </v-icon>
                            </v-btn>
                        </v-btn-toggle>
                    </v-col>
                    <v-col cols="12" v-if="!watchListDetails.horizontal">
                        <v-checkbox 
                            :label="$t(`watchlist.settings.chartBigger`)"
                            v-model="watchListDetails.biggerChart">
                        </v-checkbox>
                    </v-col>
                </v-row>

                </section>
                <section>
                    <v-autocomplete
                        v-model="searchDataPoint"
                        :label="$t(`common.search`)"
                        auto-select-first
                        clearable
                        :loading="isDPListLoading"
                        :items="datapointsList"
                        prepend-icon="mdi-magnify"
                        item-text="name"
                        return-object>
                        <template v-slot:append-outer>
                            <v-icon @click="addDataPointSearch">mdi-check-outline</v-icon>
                        </template>

                    </v-autocomplete>
                    <v-treeview
                        v-if="datapointHierarchy"
                        :items="datapointHierarchy"
                        :load-children="fetchDataPointList"
                        dense
                        activatable
                    >
                        <template v-slot:prepend="{item, open}">
                            <v-icon v-if="item.folder">
                                {{ open ? 'mdi-folder-open' : 'mdi-folder' }}
                            </v-icon>
                            <v-icon v-else>mdi-cube-outline</v-icon>
                        </template>
                        <template v-slot:append="{item}">
                            <v-checkbox v-if="!item.folder" dense
                                v-model="item.selected"
                                @change="datapointSelected(item)">
                            </v-checkbox>
                        </template>
                    </v-treeview>    
                </section>
            </v-card-text>
            <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn text @click="closeDialog">
                    {{$t(`common.cancel`)}}
                </v-btn>
                <v-btn text v-if="!create" @click="revert">
                    {{$t(`common.reset`)}}
                </v-btn>              
                <v-btn text v-if="create" color="primary" @click="createWatchList">
                    {{$t(`common.create`)}}
                </v-btn>
                <v-btn text v-else color="primary" @click="updateWatchList">
                    {{$t(`common.update`)}}
                </v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</div>
</template>
<script>

/**
 * 
 * 
 * @author Radoslaw Jajko <rjajko@softq.pl>
 * @version 1.0.0
 */
export default {
    name: 'WatchListConfig',

    props: {
        // watchListDetails: {
        //     type: Object,
        //     required: true
        // },

        create: {
            type: Boolean,
            default: false
        }

    },



    data() {
        return {
            dialog: false,
            // watchList: null,
            isDPListLoading: false,
            searchDataPoint:null,
            datapointsList: [],
        }
    },

    mounted() {
        // if(this.create) {

            
        // } else {
        //     this.watchList = this.watchListDetails;
        // }
        
        this.loadWatchListData();
    },

    computed: {
        datapointHierarchy() {
            return this.$store.state.watchListModule2.datapointHierarchy;
        },
        
        watchListDetails: {
            get() {
                return this.$store.state.watchListModule2.activeWatchList;
            },
            set(newValue) {
                return this.$store.dispatch('updateActiveWatchList', newValue);
            }
        },
    },

    methods: {

        async loadWatchListData() {
            this.isDPListLoading = true;
            this.$store.dispatch('loadWatchListPointHierarchyNode', {id: 0})
            this.datapointsList = await this.$store.dispatch('getAllDatapoints');
            this.isDPListLoading = false;
        },

        async fetchDataPointList(item) {
            await this.$store.dispatch('loadWatchListPointHierarchyNode', {id: item.id.slice(1), parentNode: item.children});
        },

        datapointSelected(item) {
            let data = { 
                id: Number(item.id.slice(1)),
                xid: item.xid,
                name: item.name,
            }
            
            if(item.selected) {
                this.$store.commit('ADD_POINT_TO_WATCHLIST', data);
                if(!this.create) {
                    this.$store.dispatch('loadWatchListDataPointDetails', data.id);
                }
            } else {
                this.$store.commit('REMOVE_POINT_FROM_WATCHLIST', data);
            }
        },

        addDataPointSearch() {
            console.log(this.searchDataPoint);
            if(!!this.searchDataPoint) {
                let data = {
                    id: this.searchDataPoint.id,
                    xid: this.searchDataPoint.xid,
                    name: this.searchDataPoint.name,
                }
                this.$store.commit('ADD_POINT_TO_WATCHLIST', data);
                this.$store.commit('CHECK_POINT_IN_PH', data.id);
                this.searchDataPoint = null;
            }
            
            

        },

        showDialog() {
            if(this.create) {
                this.$store.commit('SET_BLANK_ACTIVE_WATCHLIST');
            }
            this.dialog = true;
        },

        closeDialog() {
            this.dialog = false;
        },

        revert() {

        },

        createWatchList() {
            this.$emit('create');
            this.closeDialog();
        },

        updateWatchList() {
            this.$emit('update');
            this.closeDialog();
        }
    },


    
}
</script>
<style scoped>
.watchlist-settings--layout{
    display: flex;
    justify-content: space-between;
    align-items: baseline;
}
</style>