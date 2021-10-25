<template>
<v-row>
    <v-col id="watchListSection" v-if="!watchListsLoading">

        <v-row>
            <v-col cols="4">
                <h4>
                    <v-icon>mdi-chart-line</v-icon> 
                    {{ $t('userprofileDetails.wl.title') }}
                </h4>
            </v-col>
            <v-col cols="4">
                <v-text-field
                    :placeholder="$t('common.search')"
                    v-model="name"
                    prepend-icon="mdi-magnify"
                    dense
                    clearable
                ></v-text-field>
            </v-col>
            <v-col cols="4">
                <div class="radio-label--header">
                    <span>{{$t('userprofileDetails.common.none')}}</span>
                    <span>{{$t('userprofileDetails.common.read')}}</span>
                    <span>{{$t('userprofileDetails.common.set')}}</span>
                </div>
            </v-col>
        </v-row>

        <v-list>
            <v-list-item v-for="item in filterItemsByName" :key="item.id">
                <v-list-item-content>
                    <v-list-item-title v-text="item.name"></v-list-item-title>
                </v-list-item-content>
                <v-list-item-action>
                    <v-row>
                        <v-col cols="12">
                            <v-radio-group row 
                                v-model="item.permission"
                                @change="onPermissionChange(item)">
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
    <v-col v-else>
        <v-skeleton-loader type="list-item-two-line"></v-skeleton-loader>
    </v-col>
</v-row>
</template>
<script>
/**
 * Permissions WatchLists
 * 
 * @author Radoslaw Jajko <rjajko@softq.pl>
 * @version 1.0.0
 */
export default {
    name: 'PermissionsWatchLists',

    data() {
        return {
            name: '',
            watchLists: [],
            watchListsLoading: false,
        }
    },

    computed: {
        permissions() {
            return this.$store.state.userProfileModule.activeUserProfile.watchlistPermissions;
        },

        filterItemsByName() {
            let result = this.watchLists;
            result = result.filter(list => list.name !== '(unnamed)');
            if(!!this.name) {
                result = result.filter(list => list.name.toLowerCase().includes(this.name.toLowerCase()));
            } 
            return result;   
        }
    },

    mounted() {
        this.fetchData();
    },

    methods: {

        async fetchData() {
            this.watchListsLoading = true;
            try {
                let watchListList = await this.$store.dispatch('getAllWatchLists');
                watchListList.forEach(wl => {
                    this.watchLists.push({
                        id: wl.id,
                        name: wl.name,
                        permission: 0
                    });
                });
                this.initPermissions();
            } catch (e) {
                console.error(e);
            } finally {
                this.watchListsLoading = false;
            }
        },

        initPermissions() {
            this.permissions.forEach(entry => {
                this.watchLists.find((item) => item.id === entry.id).permission = entry.permission;
            });
        },

        onPermissionChange(item) {
            this.$store.commit('UPDATE_PERMISSION_UP_WL', item);
            this.$emit('change');
        },

    },


    
}
</script>
<style scoped>

</style>