<template>
<div>
    <v-row v-if="!userProfileDetailsLoading">
        <v-col cols="12" class="heading-action-buttons" v-if="edit">
            <h2>{{ $t('userprofileDetails.title') }}</h2>

            <v-spacer></v-spacer>

            <v-btn fab elevation="2" 
                color="primary" small 
                class="mar-right-small"
                v-if="saveButtonVisible" 
                @click="updateUserProfile">
                <v-icon>mdi-content-save</v-icon>
            </v-btn>
            <v-tooltip bottom>
                <template v-slot:activator="{on, attrs}"> 
                    <v-btn fab elevation="2" 
                        v-bind="attrs" 
                        v-on="on"
                        small 
                        @click="copyUserProfile">
                        <v-icon>mdi-content-copy</v-icon>
                    </v-btn>
                </template>
                <span>
                    {{$t('common.createcopy')}}
                </span>
            </v-tooltip>
            
        </v-col>

		<v-col cols="8">
            <v-text-field 
                :label="$t('userprofileDetails.form.name')"
                type="text" 
                v-model="userProfileName"
            ></v-text-field>
        </v-col>
        <v-col cols="2">
            <v-text-field 
                :label="$t('userprofileDetails.form.xid')"
                :disabled="edit"
                type="text" 
                v-model="userProfileXid"
            ></v-text-field>
        </v-col>

        <v-col cols="2" class="flex justify-content-space-evenly">
            <v-tooltip bottom>
                <template v-slot:activator="{on, attrs}"> 
                    <v-btn fab elevation="2"
                        v-bind="attrs"
						v-on="on"
                        small
                        @click="scrollTo('dataSourcesSection')"            
                        ><v-icon>mdi-database</v-icon>
                    </v-btn>
                </template>
                <span>
                    {{$t('userprofileDetails.help.datasources')}}
                </span>
            </v-tooltip>
            
            

            <v-tooltip bottom>
                <template v-slot:activator="{on, attrs}"> 
                    <v-btn fab elevation="2"
                        v-bind="attrs"
						v-on="on"
                        small
                        @click="scrollTo('graphicalViewSection')"            
                        ><v-icon>mdi-image</v-icon>
                    </v-btn>
                </template>
                <span>
                    {{$t('userprofileDetails.help.views')}}
                </span>
            </v-tooltip>

            <v-tooltip bottom>
                <template v-slot:activator="{on, attrs}"> 
                    <v-btn fab elevation="2"
                        v-bind="attrs"
						v-on="on"
                        small
                        @click="scrollTo('watchListSection')"            
                        ><v-icon>mdi-chart-line</v-icon>
                    </v-btn>
                </template>
                <span>
                    {{$t('userprofileDetails.help.watchlists')}}
                </span>
            </v-tooltip>
                </v-col>


        <v-col cols="12">
            <v-row class="permission-content">
                <v-col cols="12">
                    <PermissionsDataSource
                        @change="onPermissionChanged"
                    ></PermissionsDataSource>
                </v-col>
                <v-col cols="12">
                    <PermissionsViews
                        @change="onPermissionChanged"
                    ></PermissionsViews>
                </v-col>
                <v-col cols="12">
                    <PermissionsWatchLists
                        @change="onPermissionChanged"
                    ></PermissionsWatchLists>
                </v-col>
            </v-row>
        </v-col>
	</v-row>

    <v-progress-circular v-else 
        indeterminate
        color="primary"
    ></v-progress-circular>
</div>
	
</template>
<script>
import PermissionsDataSource from './PermissionsDataSource.vue';
import PermissionsViews from './PermissionsViews.vue';
import PermissionsWatchLists from './PermissionsWatchLists.vue';

/**
 * User Profiles Details component
 * 
 * This component is responsible for rendering 
 * a details of specific User Profile. It can be used 
 * to creation or edition of that profile. Most of the 
 * permission logic is handled by Vuex Store.
 * 
 * @author Radoslaw Jajko <rjajko@softq.pl>
 * @version 1.0.0
 */
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
	},

    components: {
        PermissionsDataSource,
        PermissionsViews,
        PermissionsWatchLists,
    },

	data() {
		return {
            selectedProfile: -1,
            userProfileName: '',
            userProfileXid: '',
            userProfileList: [],
            saveButtonVisible: false,
            userProfileDetailsLoading: false,
		};
	},

	mounted() {
        if(this.edit) {
            this.fetchUserProfileDetails();
        } else {
            this.createBlankUserProfile();
        }        
	},

    computed: {
        userProfile() {
            return this.$store.state.userProfileModule.activeUserProfile;
        },
        userProfileRevert() {
            return this.$store.state.userProfileModule.activeUserProfileRevert;
        },
    },

	methods: {
        // Initialization options

        /**
         * Fetch User Profile Details
         * 
         * Load User Profile settings from REST API and set
         * Vuex Variable for active user profile. Initialize
         * Name and Xid fields with values from store.
         */
        async fetchUserProfileDetails() {
            this.userProfileDetailsLoading = true;
            await this.$store.dispatch('getUserProfile', this.userProfileId);
            this.userProfileName = this.userProfile.name;
            this.userProfileXid = this.userProfile.xid;
            this.userProfileDetailsLoading = false;
        },

        /**
         * Create Blank User Profile
         * 
         * Create a new User Profile withot any data.
         * Get from the REST API the unique Export ID number.
         * userProfileId in that case should be equal to -1;
         */
        async createBlankUserProfile() {
            this.userProfileDetailsLoading = true;
            await this.$store.commit('SET_CLEAR_UP');
            this.userProfileName = '';
            this.userProfileXid = await this.$store.dispatch('getUserProfileUniqueXid');
            this.userProfileDetailsLoading = false;
        },

        /**
         * Create a Copy of User Profile
         * 
         * Based on the provided profile initialize the
         * component with specific data but instead of
         * updating use 'create' method. This initialization
         * is accessible from parent component, where user must 
         * provide the initial values.
         * 
         * @param {Number} profileId - User Profile ID to be copied.
         */
        async createCopyOfUserProfile(profileId) {
            this.userProfileDetailsLoading = true;
            await this.$store.dispatch('getUserProfile', profileId);
            this.userProfileXid = await this.$store.dispatch('getUserProfileUniqueXid');
            this.userProfileName = `${this.userProfile.name} - copy`;
            this.userProfileDetailsLoading = false;
        },

        /**
         * Copy user profile
         * 
         * Inform parent component that should create
         * a creation dialog with UserProfileID settings.
         */
        copyUserProfile() {
            this.$emit('copy', this.userProfile.id);
        },

        /**
         * Update User Profile
         * 
         * Most of the configuration is stored 
         * inside Vuex for ActiveUserProfile.
         * So to update we should only provide
         * a name if it has changed
         */
        async updateUserProfile() {
            try {
                await this.$store.dispatch('updateUserProfile', this.userProfileName);
                this.saveButtonVisible = false;
                this.$emit('update', true);
            } catch (error) {
                this.$emit('update', false);
                console.log(error);
            }
        },

        /**
         * Create User Profile
         * 
         * Configuration is saved inside Vuex store.
         * To create a new user profile we must provide
         * a profile name and profile xid.
         */
        async createUserProfile() {
            try {
                await this.$store.dispatch('createUserProfile', {
                    name: this.userProfileName,
                    xid: this.userProfileXid
                });
                this.$emit('create', true);
            } catch (error) {
                this.$emit('create', false);
                console.log(error);
            }
        },

        /**
         * On Permission Changed Event
         * 
         * Handle the permission change event.
         * Validate configuration to show or hide
         * a save button.
         */
        onPermissionChanged(){
            this.saveButtonVisible = this.isDataChanged();
        },

        /**
         * Is Data Changed
         * 
         * Help method to check if configuarations are equal.
         * @private
         */
        isDataChanged() {
            if(this.edit) {
                return JSON.stringify(this.userProfile) !== JSON.stringify(this.userProfileRevert);
            }
        },

        scrollTo(section) {
			document.getElementById(section).scrollIntoView({behavior: "smooth"});
		}
	},
};
</script>
<style>
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
    float: right;
}

.permission-content {
    max-height: 60vh;
    overflow: auto;
}

.radio-label--header > span {
    margin: 5px;
}
.radio-label--header > span:last-of-type {
    margin-right: 2vw;
}


</style>
<style scoped>
.mar-right-small {
    margin-right: 10px;
}
.flex {
    display: flex;
}
.justify-content-space-evenly {
    justify-content: space-evenly;
}
</style>
