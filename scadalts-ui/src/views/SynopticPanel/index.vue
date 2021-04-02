<template>
<div>
    <div id="panel-canvas"></div>
    <div v-if="!panel">
        <p>Nothing to show! Create your first Synoptic Panel</p>
    </div>
    <div>
        <SynopticPanelItemEditor :graphic-items="graphicItems" @saved="savePanel" ref="editDialog"/>
    </div>
    <div v-for="item in panelItems" :key="item.name">
        <div v-if="customComponent.includes(item.name.split('_')[1])">
            <component 
                v-bind:is="item.name.split('_')[1]"
                v-bind:component-data="item.data"
                v-bind:component-id="item.name"
                v-bind:component-editor="false" />
        </div>
        <div v-else>
            <slts-default 
                :component-data="item.data" 
                :component-id="item.name" 
                :component-editor="false"
            ></slts-default>
        </div>
    </div>
</div>
</template>
<script>
import customComponentMixin from '../../components/SynopticPanel/CustomComponentMixin.js';
import SynopticPanelItemEditor from "./SynopticPanelItemEditor.vue";

    export default {

        mixins: [customComponentMixin],

        components: {
            SynopticPanelItemEditor
        },

        data() {
            return {
                panel: undefined,
                graphicItems: undefined,
                panelItems: []
            }
        },
        
        watch: {
            $route(to, from) {
                this.fetchSynopticPanel(to.params.id);
            }
        },

        mounted() {
            this.fetchSynopticPanel(this.$route.params.id);
        },

        methods: {

            async fetchSynopticPanel(id) {
                this.panel = await this.$store.dispatch('fetchSynopticPanel', id);
                this.renderPanel();
            },

            renderPanel() {
                let dom = document.getElementById("panel-canvas");
                if(!!dom && !!dom.firstChild) {
                    dom.firstChild.remove();
                }
                this.$svg("panel-canvas").size(window.innerWidth, window.innerHeight).svg(this.panel.vectorImage);
                this.$emit('loaded', this.panel.id);
                this.loadGraphicItems();
                this.initComponents();
            },

            edit() {
                this.$refs.editDialog.openDialog();
            },

            initComponents() {
                this.panelItems = [];
                for (const [key, value] of this.graphicItems.entries()) {
                    this.panelItems.push({name: key, data: value})
                }
            },
            loadGraphicItems() {
                let regex = /SLTS_\w+_\d+\b/;
                let loadedGraphicItems = new Map();
                this.$svg.select('*[id^="SLTS_"]').members.forEach(member => {
                    if (member.node.id.match(regex)) {
                        loadedGraphicItems.set(
                            member.node.id, {
                                data: {}
                            })
                    }
                });

                //Load data from database: add saved data//
                try {
                    let componentData = new Map(JSON.parse(this.panel.componentData))
                    if(componentData !== undefined) {
                        for (const [key, value] of componentData.entries()) {
                            loadedGraphicItems.set(key, value);
                        }
                    }
                } catch (e) {
                    console.error(e);
                }
                this.graphicItems = loadedGraphicItems;

            },

            savePanel(childGraphicItems) {
                this.graphicItems = childGraphicItems;
                console.log(this.graphicItems);
                this.panel.componentData = JSON.stringify([...this.graphicItems]);
                console.log(this.panel);
                this.$store.dispatch('updateSynopticPanel', this.panel); 

                
            }
            
            //TODO: Add Snackbar to show the operation status 
        }
    }
</script>
<style scoped></style>