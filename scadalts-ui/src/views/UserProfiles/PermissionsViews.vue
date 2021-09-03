<template>
<v-row>
    <v-col id="graphicalViewSection" v-if="!viewListLoading">

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
            <v-list-item v-for="item in viewList" :key="item.id">
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
 * Permissions Views
 * 
 * @author Radoslaw Jajko <rjajko@softq.pl>
 * @version 1.0.0
 */
export default {
    name: 'PermissionsViews',

    data() {
        return {
            viewList: [],
            viewListLoading: false,
        }
    },

    computed: {
        permissions() {
            return this.$store.state.userProfileModule.activeUserProfile.viewPermissions;
        }
    },

    mounted() {
        this.fetchData();
    },

    methods: {

        async fetchData() {
            this.viewListLoading = true;
            try {
                let views = await this.$store.dispatch('fetchGraphicalViewsList');
                views.forEach(v => {
                    this.viewList.push({
                        id: v.id,
                        name: v.name,
                        permission: 0
                    });
                });
                this.initPermissions();
            } catch (e) {
                console.error(e);
            } finally {
                this.viewListLoading = false;
            }
        },

        initPermissions() {
            this.permissions.forEach(entry => {
                this.viewList.find((item) => item.id === entry.id).permission = entry.permission;
            });
        },

        onPermissionChange(item) {
            this.$store.commit('UPDATE_PERMISSION_UP_GV', item);
            this.$emit('change');
        },

    },


    
}
</script>
<style scoped>

</style>