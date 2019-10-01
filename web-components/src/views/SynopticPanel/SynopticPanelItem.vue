<template>
    <div>
        <div>
            <SynopticPanelItemEditor :graphic-items="graphicItems" @saved="savePanel"/>
        </div>
        <p>{{synopticPanel.name}}</p>
        <div id="panel-canvas">
        </div>
        <div v-for="graphicItem in panelItems">
            <div v-if="customComponent.includes(graphicItem.name.split('_')[1])">
                <component v-bind:is="graphicItem.name.split('_')[1]"
                           v-bind:component-data="graphicItem.data"
                           v-bind:component-id="graphicItem.name"
                           v-bind:component-editor="false"/>
            </div>
            <div v-else>
                <component is="slts-default"
                           :component-data="graphicItem.data"
                           :component-id="graphicItem.name"
                           :component-editor="false"/>
            </div>
        </div>
    </div>
</template>
<script>
    import SynopticPanelItemEditor from "./SynopticPanelItemEditor";
    import CustomComponentsMixin from "../../components/synoptic-panel/CustomComponentsMixin";

    export default {
        props: ['synopticPanel'],
        mixins: [CustomComponentsMixin],
        components: {
            SynopticPanelItemEditor
        },
        data() {
            return {
                graphicItems: undefined,
                panelItems: []
            }
        },
        methods: {
            renderGraphic() {
                // this.graphicItems = null;
                if (this.synopticPanel !== undefined) {
                    try {
                        document.getElementById("panel-canvas").firstChild.remove();
                    } catch (e) {
                        console.log(e);
                    }
                    this.$svg("panel-canvas").size(1920, 1080).svg(this.synopticPanel.vectorImage);
                    this.loadGraphicItems();
                    this.initComponents();
                }
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
                    let componentData = new Map(JSON.parse(this.synopticPanel.componentData))
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
                this.synopticPanel.componentData = JSON.stringify([...this.graphicItems]);
                console.log(this.synopticPanel.componentData);
                this.$emit('panelupdated', this.synopticPanel)
            }
        }
    }

</script>