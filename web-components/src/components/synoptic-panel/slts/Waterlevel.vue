<template>
    <div>
        <div v-if="componentEditor">
            <v-container fluid>
                <v-row>
                    <v-col cols="12" md="6">
                        <v-text-field v-model="componentData.data.pointXid" label="Point XID:"/>
                    </v-col>
                    <v-col cols="12" md="2">
                        <v-text-field v-model="componentData.data.label" label="Point Name:"/>
                    </v-col>
                    <v-col cols="12" md="2">
                        <v-text-field v-model="componentData.data.minval" label="Min value:"/>
                    </v-col>
                    <v-col cols="12" md="2">
                        <v-text-field v-model="componentData.data.maxval" label="Max value:"/>
                    </v-col>
                </v-row>
                <v-row>
                    <v-col cols="12" md="2">
                        <v-btn @click="saveData">Save</v-btn>
                    </v-col>
                </v-row>
            </v-container>
        </div>
    </div>
</template>
<script>
    import CustomComponentsBase from "../CustomComponentsBase";

    export default {
        extends: CustomComponentsBase,
        data() {
            return {
                refreshInterval: undefined,
            }
        },
        mounted() {
            if (!this.componentEditor) {
                this.startRefresh();
            }
        },
        beforeDestroy() {
            if (!this.componentEditor) {
                clearInterval(this.refreshInterval)
            }
        },
        methods: {
            updateComponent() {
                this.getDataPointValueXid(this.componentData.data.pointXid).then(data => {
                    this.changeWaterLevel(parseFloat(data.value));
                })
            },
            startRefresh() {
                this.refreshInterval = setInterval(() => {
                    this.updateComponent()
                }, 5000)
            },
            changeWaterLevel(value) {
                let maxHeight = this.$svg.get(this.componentId).attr('height');
                let minVal = this.componentData.data.minval;
                if (minVal === undefined || minVal === null) minVal = 0;
                let waterLevel = (value * maxHeight) / (this.componentData.data.maxval - minVal);
                this.$svg.get(`${this.componentId}_background`).animate().height(waterLevel)
            }
        }
    }
</script>