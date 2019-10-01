<template>
    <v-row justify="center">
        <v-dialog v-model="dialog" persistent max-width="400">
            <template v-slot:activator="{on}">
                <v-btn fab small dark color="secondary" v-on="on">
                    <v-icon dark>mdi-rename-box</v-icon>
                </v-btn>
            </template>
            <v-card>
                <v-card-title class="headline">
                    Edit Synoptic Panel
                </v-card-title>
                <v-card-text>
                    <v-container>
                        <v-col cols="12">
                            <v-text-field label="Panel name*" required v-model="synopticPanelData.name"/>
                        </v-col>
                        <v-col cols="12">
                            <v-text-field label="Panel export ID*" disabled required v-model="synopticPanelData.xid"/>
                        </v-col>
                        <v-col cols="12">
                            <v-file-input label="SVG graphic file*" required
                                          v-on:change="handleFileUpload($event)"
                                          accept="image/svg+xml"
                            />
                        </v-col>
                    </v-container>
                </v-card-text>
                <v-card-actions>
                    <div class="flex-grow-1"></div>
                    <v-btn color="green darken-1" text @click="closeDialog">Close</v-btn>
                    <v-btn color="green darken-1" text @click="submitFile">Save</v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
    </v-row>
</template>
<script>
    export default {
        computed : {
            synopticPanelData() {
                return this.$store.state.synopticPanel.synopticPanelSelected
            }
        },
        data() {
            return {
                dialog: false,
                synopticPanel: undefined
            }
        },
        methods: {
            handleFileUpload(file) {
                this.readFileContent(file).then(content => {
                    let synopticPanel = {};
                    synopticPanel.id = this.synopticPanelData.id;
                    synopticPanel.name = this.synopticPanelData.name;
                    synopticPanel.xid = this.synopticPanelData.xid;
                    synopticPanel.vectorImage = content;
                    synopticPanel.componentData = this.synopticPanelData.componentData;
                    this.synopticPanel = synopticPanel;
                });
            },
            closeDialog() {
                this.dialog = false;
            },
            submitFile() {
                this.$emit("edited", this.synopticPanel)
                this.closeDialog();
            },
            readFileContent(file) {
                const reader = new FileReader();
                return new Promise((resolve, reject) => {
                    reader.onload = event => resolve(event.target.result);
                    reader.onerror = error => reject(error);
                    reader.readAsText(file, "UTF-8");
                })
            },
        }
    }

</script>
