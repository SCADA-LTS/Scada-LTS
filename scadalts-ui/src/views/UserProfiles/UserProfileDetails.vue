<template>
<div>
    <v-row v-if="userProfileLoadingProgress === 100">

		<v-col cols="12">
            <v-text-field 
                v-if="!userEdit"
                label="User Profile Name"
                type="text" v-model="userProfile.name">
            </v-text-field>
            <v-select v-else>
            </v-select>
        </v-col>
        <v-col cols="12">
            <v-row>
                <v-col xs="12" md="4" id="dataSourcesSection">
                    <v-treeview :items="dataSources" 
                        activatable 
                        :load-children="fetchDataPoints">
                    </v-treeview>
                </v-col>
                <v-col xs="12" md="4" id="graphicalViewSection">
                    <v-btn :disabled="!!graphicalViews && graphicalViews.length === 0">
                        Graphical Views
                    </v-btn>
                </v-col>
                <v-col xs="12" md="4" id="watchListSection">
                    <v-btn :disabled="!!watchLists && watchLists.length === 0">
                        Watch Lists
                    </v-btn>
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
        }
	},

	data() {
		return {
            userProfile: null,
            userProfileLoading: false,
            userProfileLoadingProgress: 0,
            dataSources: [],
            dataPoints: [],
            graphicalViews: [],
            watchLists: [],
		};
	},

	mounted() {
        this.fetchUserProfileDetails();
        
	},

	methods: {
        async fetchUserProfileDetails() {
            if(this.userProfileId !== -1) {
                this.userProfileLoadingProgress = 0;
                this.userProfile = await this.$store.dispatch('getUserProfile', this.userProfileId);
            }
            this.initialLoading();
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
                ds.forEach((ds) => {
                    this.dataSources.push({
                        id: ds.id, 
                        xid: ds.xid, 
                        name: ds.name, 
                        children: []
                    });
                    this.userProfileLoadingProgress += dataSourceProgress;
                })
            } else {
                this.userProfileLoadingProgress += 25;
            }
        },

        fetchDataPoints(item) {
            let dp = this.fetchDataPointForDataSource(item.id);
            console.log(dp);
        },

        async fetchDataPointForDataSource(datasourceId) {
            await this.$store.dispatch('fetchDataPointsFromDataSource', datasourceId);
        },

        async fetchGraphicalViews() {
            this.graphicalViews = await this.$store.dispatch('fetchGraphicalViewsList');
            this.userProfileLoadingProgress += 25;
        },

        async fetchWatchLists() {
            this.watchLists = await this.$store.dispatch('fetchWatchLists');
            this.userProfileLoadingProgress += 25;
        },

	},
};
</script>

<style scoped>

</style>
