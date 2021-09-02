<template>
<div>
    <v-row v-if="userProfileLoadingProgress >= 100">
        <v-col cols="12" class="heading-action-buttons">
            <h2>{{ $t('userprofileDetails.title') }}</h2>
            <v-spacer></v-spacer>
            <v-btn fab elevation="2" 
                color="primary" small 
                v-if="saveButtonVisible" 
                @click="updateUserProfile">
                <v-icon>mdi-content-save</v-icon>
            </v-btn>
        </v-col>

		<v-col cols="12">
            <v-row v-if="!userEdit">
                <v-col cols="10">
                    <v-text-field 
                        :label="$t('userprofileDetails.form.name')"
                        type="text" 
                        v-model="userProfile.name"
                    ></v-text-field>
                </v-col>
                <v-col cols="2">
                    <v-text-field 
                        :label="$t('userprofileDetails.form.xid')"
                        :disabled="true"
                        type="text" 
                        v-model="userProfile.xid"
                    ></v-text-field>
                </v-col>
            </v-row>
            <v-row v-else>
                <v-col cols="12">
                    <v-select
                        :label="$t('userprofileDetails.form.selectedProfile')"
                        v-model="selectedProfile"
                        :items="userProfileList"
                        item-text="name"
                        item-value="id"
                        @change="selectedUserProfileChanged"
                    ></v-select>
                </v-col>
            </v-row>
        </v-col>
        <v-col cols="2">
            
        </v-col>
        <v-col cols="12">
            <v-row v-if="!userEdit || (userEdit && selectedProfile === -1)">
                <v-col cols="12" id="dataSourcesSection">
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
                    
                    <v-treeview :items="dataSources" 
                        activatable 
                        :load-children="fetchDataPoints">
                        <template v-slot:append="{ item }">
                            <v-row v-if="!item.children">
                                <v-col cols="12">
                                    <v-radio-group row v-model="item.permission"
                                        @change="changePermissionsDataPoint(item)">
                                        <v-radio :value="0">
                                        </v-radio>
                                        <v-radio :value="1">
                                        </v-radio>
                                        <v-radio :value="2">
                                        </v-radio>
                                </v-radio-group>
                                </v-col>
                            </v-row>
                            <v-row v-else>
                                <v-col cols="12">
                                    <v-tooltip bottom>
							            <template v-slot:activator="{ on, attrs }">
                                            <div v-bind="attrs" v-on="on">
                                                <v-checkbox v-model="item.permission" 
                                                    @change="changePermissionsDataSource(item)"
                                                ></v-checkbox>
                                            </div>
                                            
							            </template>
							            <span>{{
								            $t('userprofileDetails.help.datasource')
							            }}</span>
						            </v-tooltip>
                                </v-col>
                            </v-row>
                        </template>
                    </v-treeview>
                </v-col>

                <v-col cols="12" id="graphicalViewSection">
                    <v-row>
                        <v-col>
                            <h4>{{ $t('userprofileDetails.gv.title') }}</h4>
                        </v-col>
                        <v-col cols="4" class="flex jc--space-evenly radio-label--header">
                            <span>{{$t('userprofileDetails.common.none')}}</span>
                            <span>{{$t('userprofileDetails.common.read')}}</span>
                            <span>{{$t('userprofileDetails.common.set')}}</span>
                        </v-col>
                    </v-row>
                    
                    <v-list>
                        <v-list-item v-for="view in graphicalViews" :key="view.id">
                            <v-list-item-content>
                                <v-list-item-title v-text="view.name">
                                </v-list-item-title>
                            </v-list-item-content>
                            <v-list-item-action>
                                <v-row>
                                    <v-col cols="12">
                                        <v-radio-group row 
                                            v-model="view.permission"
                                            @change="changePermissionsGraphicalView(view)">
                                            <v-radio :value="0">
                                            </v-radio>
                                            <v-radio :value="1">
                                            </v-radio>
                                            <v-radio :value="2">
                                            </v-radio>
                                        </v-radio-group>
                                    </v-col>
                                </v-row>
                            </v-list-item-action>
                        </v-list-item>
                    </v-list>
                </v-col>

                <v-col cols="12" id="watchListSection">
                    <v-row>
                        <v-col>
                            <h4>{{ $t('userprofileDetails.wl.title') }}</h4>
                        </v-col>
                        <v-col cols="4" class="flex jc--space-evenly radio-label--header">
                            <span>{{$t('userprofileDetails.common.none')}}</span>
                            <span>{{$t('userprofileDetails.common.read')}}</span>
                            <span>{{$t('userprofileDetails.common.set')}}</span>
                        </v-col>
                    </v-row>

                    <v-list>
                        <v-list-item v-for="watchList in watchLists" :key="watchList.id">
                            <v-list-item-content>
                                <v-list-item-title v-text="watchList.name">
                                </v-list-item-title>
                            </v-list-item-content>
                            <v-list-item-action>
                                <v-row>
                                    <v-col cols="12">
                                        <v-radio-group row 
                                            v-model="watchList.permission"
                                            @change="changePermissionsWatchLists(watchList)"
                                        >
                                            <v-radio :value="0">
                                            </v-radio>
                                            <v-radio :value="1">
                                            </v-radio>
                                            <v-radio :value="2">
                                            </v-radio>
                                        </v-radio-group>
                                    </v-col>
                                </v-row>
                            </v-list-item-action>
                        </v-list-item>
                    </v-list>
                </v-col>
            </v-row>
        </v-col>
	</v-row>
    <v-progress-circular v-else :value="userProfileLoadingProgress" color="primary">
    </v-progress-circular>
</div>
	
</template>
<script>

export default {
	name: 'UserProfilesDetails',

	props: {
		userProfileId: {
			type: Number,
            default: -1,
		},
		edit: {
			type: Boolean,
			default: false,
		},
        userEdit: {
            type: Boolean,
            default: false,
        },
	},

	data() {
		return {
            saveButtonVisible: false,
            selectedProfile: -1,
            userProfile: null,
            userProfileJson: null,
            userProfileLoading: false,
            userProfileLoadingProgress: 0,
            userProfileList: [],
            dataSources: [],
            dataPoints: [],
            graphicalViews: [],
            watchLists: [],
		};
	},

	mounted() {
        // Using that component inside User Details Section
        if(this.userEdit) {
            this.fetchUserProfileList();
        } else {
            this.fetchUserProfileDetails();
        }
        
        
        
	},

	methods: {
        async fetchUserProfileDetails() {
            if(this.userProfileId !== -1) {
                this.userProfileLoadingProgress = 0;
                this.userProfile = await this.$store.dispatch('getUserProfile', this.userProfileId);
                this.userProfileJson = JSON.parse(JSON.stringify(this.userProfile));
            } else {
                this.initUserProfile();
            }
            this.initialLoading();
        },

        async fetchUserProfileList() {
            this.userProfileList = await this.$store.dispatch('getUserProfilesList');
            this.userProfileList.push({
                id: -1,
                name: "None",
            });
            this.userProfileLoadingProgress = 100;
        },

        initialLoading() {
            if(this.dataSources.length === 0 && this.graphicalViews.length === 0 && this.watchLists.length === 0) {
                //If there is no data load this 4 sections//
                this.userProfileLoadingProgress += 25;
                this.fetchDataSources();
                this.fetchGraphicalViews();
                this.fetchWatchLists();
            } else {
                this.userProfileLoadingProgress = 100;
            }
        },

        async fetchDataSources() {
            let ds = await this.$store.dispatch('fetchDataSourcesList');
            if(ds.length !== 0) {
                const dataSourceProgress = 25 / ds.length;
                ds.forEach((dse) => {
                    this.dataSources.push({
                        id: dse.id, 
                        xid: dse.xid, 
                        name: dse.name, 
                        permission: false,
                        children: []
                    });
                    this.userProfileLoadingProgress += dataSourceProgress;
                })
            } else {
                this.userProfileLoadingProgress += 25;
            }
            this.initDataSourcePermissions();
        },

        async fetchDataPoints(item) {
            try {
                let datapoints = await this.fetchDataPointForDataSource(item.id);
                let dataSource = this.dataSources.find((ds) => ds.id === item.id);
                datapoints.forEach(dp => {
                    dataSource.children.push({
                        id: dp.id,
                        name: dp.name,
                        permission: 0,
                    });
                })
                this.initPermissions(dataSource.children, this.userProfile.dataPointPermissions, 1);
            } catch (error) {
                console.log(error);
            }
        },

        fetchDataPointForDataSource(datasourceId) {
            return this.$store.dispatch('fetchDataPointsFromDataSource', datasourceId);
        },

        async fetchGraphicalViews() {
            try {
                let gv =  await this.$store.dispatch('fetchGraphicalViewsList');
                gv.forEach((g) => {
                    this.graphicalViews.push({
                        id: g.id,
                        name: g.name,
                        permission: 0,
                    });
                });
                this.initGraphicalViewPermissions();
            } catch (error) {
                console.log(error);
            } finally {
                this.userProfileLoadingProgress += 25;
            }
            
            
        },

        async fetchWatchLists() {
            try {
                let wl = await this.$store.dispatch('fetchWatchLists');
                wl.forEach((w) => {
                    this.watchLists.push({
                        id: w.id,
                        name: w.name,
                        permission: 0,
                    });
                });
                this.initWatchListPermissions();
            } catch (error) {
                console.log(error);
            } finally {
                this.userProfileLoadingProgress += 25;
            }
        },

        initDataSourcePermissions() {
            const DATASOURCE_PERMISSIONS = 2
            this.initPermissions(this.dataSources, this.userProfile.dataSourcePermissions, DATASOURCE_PERMISSIONS);
            
        },

        initGraphicalViewPermissions() {
            this.initPermissions(this.graphicalViews, this.userProfile.viewPermissions);
        },

        initWatchListPermissions() {
            this.initPermissions(this.watchLists, this.userProfile.watchlistPermissions);
        },

        /**
         * Initialize Permissions
         * 
         * Load the permissions base on the UserProfile properties.
         * Depending on the permission type the different approach is used.
         * 
         * @param {Array} destination - Destination array to be filled with the permissions
         * @param {Array} source - Source array with the permissions definitions
         * @param {Number} permissionType - Type of the permission object initialization.
         */
        initPermissions(destination, source, permissionType = 0) {
            source.forEach(entry => {
                switch(permissionType) {
                    case 0:
                        destination.find((item) => item.id === entry.id).permission = entry.permission;
                        break;
                    case 1:
                        destination.find((item) => item.dataPointId === entry.id).permission = entry.permission;
                        break;
                    case 2:
                        destination.find((item) => item.id === entry).permission = true;
                        break;
                    default:
                        console.error('Invalid permission type');
                }
            });
        },

        selectedUserProfileChanged() {
            this.$emit('profileIdChanged', this.selectedProfile);
            if(this.selectedProfile === -1) {
                this.userProfileLoadingProgress = 0;
                this.initUserProfile();
                this.initialLoading();
            }
            
        },

        /* --- Permission change Handlers --- */
        changePermissionsDataSource(item) {
            if(item.permission === true) {
                this.userProfile.dataSourcePermissions.push(item.id);
            } else {
                this.userProfile.dataSourcePermissions = this.userProfile.dataSourcePermissions
                    .filter((id) => id !== item.id);
            }
            this.saveButtonVisible = this.isDataChanged();
        },

        changePermissionsDataPoint(item) {
            if(item.permission === 0) {
                this.userProfile.dataPointPermissions = this.userProfile.dataPointPermissions.filter(dp => dp.dataPointId !== item.id);
            } else {
                let point = this.userProfile.dataPointPermissions.find(dp => dp.dataPointId === item.id);
                if(!!point) {
                    point.permission = item.permission;
                } else {
                    this.userProfile.dataPointPermissions.push({
                        dataPointId: item.id,
                        permission: item.permission
                    });
                }
            }
            this.saveButtonVisible = this.isDataChanged();
        },

        changePermissionsGraphicalView(item) {
            if(item.permission === 0) {
                this.userProfile.viewPermissions = this.userProfile.viewPermissions.filter(gv => gv.id !== item.id);
            } else {
                let view = this.userProfile.viewPermissions.find(gv => gv.id === item.id);
                if(!!view) {
                    view.permission = item.permission;
                } else {
                    this.userProfile.viewPermissions.push({
                        id: item.id,
                        permission: item.permission
                    });                
                }
            }
            this.saveButtonVisible = this.isDataChanged();
        },

        changePermissionsWatchLists(item) {
            if(item.permission === 0) {
                this.userProfile.watchlistPermissions = this.userProfile.watchlistPermissions.filter(wl => wl.id !== item.id);
            } else {
                let watchList = this.userProfile.watchlistPermissions.find(wl => wl.id === item.id);
                if(!!watchList) {
                    watchList.permission = item.permission;
                } else {
                    this.userProfile.watchlistPermissions.push({
                        id: item.id,
                        permission: item.permission
                    });                
                }
            }
            this.saveButtonVisible = this.isDataChanged();
        },

        isDataChanged() {
            if(this.edit) {
                return JSON.stringify(this.userProfile) !== JSON.stringify(this.userProfileJson);
            }
            
        },

        async initUserProfile() {
            this.userProfile = {
                id: -1,
                xid: "",
                name: "",
                dataSourcePermissions: [],
                dataPointPermissions: [],
                viewPermissions: [],
                watchlistPermissions: []
            }
            this.userProfile.xid = await this.$store.dispatch('getUserProfileUniqueXid');
        },

        async updateUserProfile() {
            try {
                await this.$store.dispatch('updateUserProfile', this.userProfile);
                this.userProfileJson = JSON.parse(JSON.stringify(this.userProfile));
                this.saveButtonVisible = false;
                this.$emit('update', true);
            } catch (error) {
                this.$emit('update', false);
                console.log(error);
            }
        },

        async createUserProfile() {
            try {
                await this.$store.dispatch('createUserProfile', this.userProfile);
                this.$emit('create', true);
            } catch (error) {
                this.$emit('create', false);
                console.log(error);
            }
        }

	},
};
</script>

<style scoped>
.heading-action-buttons {
	display: flex;
	align-items: center;
}
.flex {
    display: flex;
}
.jc--space-evenly {
    justify-content: space-evenly;
}
.radio-label--header {
    max-width: 220px;
    padding: 16px;
}
</style>
