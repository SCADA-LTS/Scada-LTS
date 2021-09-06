<template>
<v-row>
    <v-col id="dataSourcesSection" v-if="!dataSourceListLoading">

        <v-row>
            <v-col>
                <h4>{{ $t('userprofileDetails.ds.title') }}</h4>
            </v-col>
            <v-col cols="4" class="flex jc--space-evenly radio-label--header">
                <span>{{$t('userprofileDetails.common.none')}}</span>
                <span>{{$t('userprofileDetails.common.read')}}</span>
                <span>{{$t('userprofileDetails.common.set')}}</span>
            </v-col>
        </v-row>

        <v-treeview
            :items="dataSourceList"
            :load-children="fetchDataPointList"
            activatable
        >
            <template v-slot:append="{ item }">
                <div v-if="!!item.children">
                    <v-tooltip bottom>
						<template v-slot:activator="{ on, attrs }">
                            <div v-bind="attrs" v-on="on">
                                <v-checkbox 
                                    v-model="item.permission" 
                                    @change="onDataSourcePermissionChange(item)"
                                ></v-checkbox>
                            </div>
                                            
						</template>
						<span>
                            {{$t('userprofileDetails.help.datasource')}}
                        </span>
					</v-tooltip>
                </div>
                <div v-else>
                    <v-radio-group 
                        row v-model="item.permission"
                        @change="onDataPointPermissionChange(item)"
                    >
                        <v-radio :value="0"></v-radio>
                        <v-radio :value="1"></v-radio>
                        <v-radio :value="2" :disabled="!!item.settable"></v-radio>
                    </v-radio-group>
                </div>
            </template>
        </v-treeview>

    </v-col>
    <v-col v-else>
        <v-skeleton-loader type="list-item-two-line"></v-skeleton-loader>
    </v-col>
</v-row>
</template>
<script>
/**
 * Permissions DataSource
 * 
 * @author Radoslaw Jajko <rjajko@softq.pl>
 * @version 1.0.0
 */
export default {
    name: 'PermissionsDataSource',

    data() {
        return {
            dataSourceList: [],
            dataSourceListLoading: false,
        }
    },

    computed: {
        permissionsDS() {
            return this.$store.state.userProfileModule.activeUserProfile.dataSourcePermissions;
        },

        permissionsDP() {
            return this.$store.state.userProfileModule.activeUserProfile.dataPointPermissions;
        }
    },

    mounted() {
        this.fetchData();
    },

    methods: {

        async fetchData() {
            this.dataSourceListLoading = true;
            try {
                let dataSources = await this.$store.dispatch('fetchDataSourcesList');
                dataSources.forEach((ds) => {
                    this.dataSourceList.push({
                        id: ds.id,
                        xid: ds.xid,
                        name: ds.name,
                        permission: false,
                        children: []
                    });
                })
                this.initPermissionsDataSource();
            } catch (e) {
                console.error(e);
            } finally {
                this.dataSourceListLoading = false;
            }
        },

        initPermissionsDataSource() {
            this.permissionsDS.forEach(entry => {
                this.dataSourceList.find((item) => item.id === entry).permission = true;
            });
        },

        onDataSourcePermissionChange(item) {
            this.$store.commit('UPDATE_PERMISSION_UP_DS', item);
            this.$emit('change');
        },


        /* --- DATAPOINTS --- */


        async fetchDataPointList(item) {
            try {
                let dataPoints = await this.$store.dispatch('fetchDataPointsFromDataSource', item.id);
                let dataSource = this.dataSourceList.find((ds) => ds.id === item.id);
                dataPoints.forEach(dp => {
                    dataSource.children.push({
                        id: dp.id,
                        name: dp.name,
                        permission: 0,
                        settable: dp.settable
                    });
                });
                this.initPermissionsDataPoint(dataSource.children);
            } catch (error) {
                console.error(error);
            }
        },

        initPermissionsDataPoint(datapointList) {
            this.permissionsDP.forEach(entry => {
                datapointList.find((item) => item.dataPointId === entry.id).permission = entry.permission;
            });
        },

        onDataPointPermissionChange(item) {
            this.$store.commit('UPDATE_PERMISSION_UP_DP', item);
            this.$emit('change');
        },

    },


    
}
</script>
<style scoped>

</style>