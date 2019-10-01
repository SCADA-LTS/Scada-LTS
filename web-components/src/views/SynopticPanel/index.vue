<template>
    <v-app>
        <v-app-bar color="primary" app class="mainToolbar">
            <v-img src="assets/logo.png" max-height="60" contain position="left center"></v-img>
            <v-toolbar-title class="col-6">Synoptic Panel</v-toolbar-title>
            <div class="flex-grow-1"></div>
            <div v-if="isAdmin" class="panel-controls">
                <SynopticPanelCreator @created="addSynopticPanel"/>
                <SynopticPanelEditor v-if="synopticPanelSelected" @edited="editSynopticPanel"/>
                <SynopticPanelDelete v-if="synopticPanelSelected" @accepted="deleteSynopticPanel"/>
            </div>
            <div>
                <v-select
                        v-on:change="onSynopticPanelChange"
                        :items="synopticPanelList"
                        item-text="name"
                        item-value="id"
                        label="Select Panel">
                </v-select>
            </div>

        </v-app-bar>
        <v-navigation-drawer expand-on-hover permanent app>
            <Navigation></Navigation>
        </v-navigation-drawer>

        <v-content app class="mainPanelView">
            <v-container fluid>
                <div>
                    <SynopticPanelItem :synoptic-panel="synopticPanelSelected" ref="panelItem"
                                       @panelupdated="updateSynopticPanel"/>
                </div>
                <v-snackbar v-model="snackbar.visible" :timeout="snackbar.timeout" :color="snackbar.color">
                    {{snackbar.text}}
                </v-snackbar>
            </v-container>
        </v-content>

        <v-footer app>
        </v-footer>
    </v-app>
</template>
<script>
    import SynopticPanelCreator from "./SynopticPanelCreator";
    import SynopticPanelItem from "./SynopticPanelItem";
    import SynopticPanelDelete from "./SynopticPanelDelete";
    import SynopticPanelEditor from "./SynopticPanelEditor";
    import Navigation from "../Navigation";

    export default {
        components: {
            SynopticPanelCreator,
            SynopticPanelEditor,
            SynopticPanelDelete,
            SynopticPanelItem,
            Navigation
        },
        data: () => ({
            snackbar: {
                visible: false,
                timeout: 5000,
                text: '',
                color: 'cyan'
            },
            loading: false,
        }),
        computed: {
            synopticPanelList() {
                return this.$store.state.synopticPanel.synopticPanelList
            },
            synopticPanelSelected() {
                return this.$store.state.synopticPanel.synopticPanelSelected
            },
            isAdmin() {
                return this.$store.state.adminPrivilege
            }
        },
        created() {
            this.loading = true;
            this.$store.dispatch('isAdminPrivilege')
            this.$store.dispatch('fetchSynopticPanelList').then(() => {
                this.loading = false;
            });
        },
        methods: {
            /* Creating, updating and deleting synoptic panels */
            addSynopticPanel(synopticPanel) {
                this.$store.dispatch('createSynopticPanel', synopticPanel).then(() => {
                    this.showSnackbar(`Created successfully!`, "OK")
                }).catch(() => {
                    this.showSnackbar(`Creation failed!`, "Error")
                });
            },
            updateSynopticPanel(panel) {
                this.$store.dispatch('updateSynopticPanel', panel).then(() => {
                    this.showSnackbar(`Updated successfully!`, "OK")
                }).catch(() => {
                    this.showSnackbar(`Deleted failed!`, "Error")
                });
            },
            editSynopticPanel(panel) {
                this.$store.dispatch('updateSynopticPanel', panel).then(() => {
                    this.showSnackbar(`Updated successfully!`, "OK")
                }).catch(() => {
                    this.showSnackbar(`Deleted failed!`, "Error")
                });
            },
            deleteSynopticPanel() {
                this.$store.dispatch('deleteSynopticPanel', this.$store.getters.selectedPanelId).then(() => {
                    this.showSnackbar(`Deleted successfully!`, "OK")
                    document.getElementById("panel-canvas").firstChild.remove();
                }).catch(() => {
                    this.showSnackbar(`Deleted failed!`, "Error")
                });
            },
            /* EventHandlers */
            onSynopticPanelChange(event) {
                this.$store.dispatch('fetchSynopticPanelId', event).then(() => {
                    setTimeout(this.renderPanelGraphic, 500)
                });
            },
            /* Utils */
            renderPanelGraphic() {
                this.$refs.panelItem.renderGraphic();
            },
            showSnackbar(text, status) {
                this.snackbar.text = text;
                this.snackbar.visible = true;
                if (status === "OK") {
                    this.snackbar.color = "green";
                } else if (status === "Warning") {
                    this.snackbar.color = "orange";
                } else {
                    this.snackbar.color = "red";
                }
            }
        }
    }
</script>


<style>
    .panel-controls {
        display: flex;
        width: 7%;
        padding-right: 10px;
    }

    .mainToolbar {
        z-index: 10 !important;
        right: 0 !important;
        left: 0 !important;
    }
    .mainPanelView {
        padding: 74px 0px 12px 80px !important;
    }
</style>