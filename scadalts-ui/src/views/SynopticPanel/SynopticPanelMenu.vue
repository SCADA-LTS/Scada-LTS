<template>
<div>
    <v-container fluid>
        <v-row align="center">
            <v-col xs="8">
                <h1>Synoptic Panel</h1>
            </v-col>
            <v-col xs="2" class="row justify-end">
                <SynopticPanelCreator @created="createSynopticPanel($event)"></SynopticPanelCreator>
                <v-btn v-if="!!this.activePanel" elevation="0" fab @click="editSynopticPanel" class="small-margin">
                    <v-icon>mdi-pencil</v-icon>
                </v-btn>
                <v-btn v-if="!!this.activePanel" elevation="0" fab @click="deleteSynopticPanel" class="small-margin">
                    <v-icon>mdi-minus-circle</v-icon>
                </v-btn>
            </v-col>
            <v-col xs="2">
                <v-select
                    label="Select panel"
                    v-model="activePanel"
                    @change="selectSynopticPanel"
                    :items="synopticPanelList"
                    item-value="id"
                    item-text="name"
                    dense                    
                    ></v-select>
            </v-col>
        </v-row>
    </v-container>
    <v-container fluid>
        <router-view ref="panel" @loaded="loadedSynopticPanel($event)"></router-view>
    </v-container>
    <ConfirmationDialog
			:btnvisible="false"
			:dialog="deletePanelDialog"
			@result="deleteSynopticPanelResult"
			title="Delete Synoptic panel?"
			message="This operation cannot be undone!"
	></ConfirmationDialog>
</div>
    
</template>
<script>
import SynopticPanelCreator from './SynopticPanelCreator';
import ConfirmationDialog from '@/layout/dialogs/ConfirmationDialog';

export default {
    name: 'SynopticPanelMenu',

    components: {
        SynopticPanelCreator,
        ConfirmationDialog,
    },

    data() {
        return {
            activePanel: undefined,
            synopticPanelList: [],
            deletePanelDialog: false,
        }
    },

    mounted() {
        this.fetchSynopticPanelList();
    },

    methods: {

        async fetchSynopticPanelList() {
            this.synopticPanelList =  await this.$store.dispatch('fetchSynopticPanelList');
        },

        createSynopticPanel(synopticPanel) {
            console.log("Menu", synopticPanel);
            this.$store.dispatch('createSynopticPanel', synopticPanel);
        },

        editSynopticPanel() {
            this.$refs.panel.edit();
        },

        deleteSynopticPanel() {
            this.deletePanelDialog = true;
        },

        deleteSynopticPanelResult(e) {
            this.deletePanelDialog = false;
            if(e) {
                this.$store.dispatch('deleteSynopticPanel', this.activePanel);
            }
        },

        loadedSynopticPanel(id) {
            this.activePanel = id;
        },

        selectSynopticPanel(id) {
            this.activePanel = id;
            this.$router.push({path: `/synoptic-panel/${id}`});
        }
        
    },

    //TODO: Add Snackbar to show the operation status 
    
}
</script>
<style scoped>
.small-margin {
    margin: 0px 5px 0px 5px;
}
</style>