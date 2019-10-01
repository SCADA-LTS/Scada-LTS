<template>
    <v-row justify="center">
        <v-dialog v-model="dialog" persistent max-width="400">
            <template v-slot:activator="{on}">
                <v-btn fab small dark color="secondary" v-on="on">
                    <v-icon dark>mdi-plus-circle</v-icon>
                </v-btn>
            </template>
            <v-card>
                <v-card-title class="headline">
                    Create Synoptic Panel
                </v-card-title>
                <v-card-text>
                    <v-container>
                        <v-col cols="12">
                            <v-text-field label="Panel name*" required v-model="name"/>
                        </v-col>
                        <v-col cols="12">
                            <v-text-field label="Panel export ID*" required v-model="xid"/>
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
        data() {
            return {
                dialog: false,
                name: '',
                xid: 'SP_',
                vectorImage: '',
                synopticPanel: undefined
            }
        },
        methods: {
            handleFileUpload(file) {
                this.readFileContent(file).then(content => {
                    let synopticPanel = {};
                    synopticPanel.id = -1;
                    synopticPanel.name = this.name;
                    synopticPanel.xid = this.xid;
                    synopticPanel.vectorImage = content;
                    synopticPanel.componentData = "[]";
                    this.synopticPanel = synopticPanel;
                });
            },
            closeDialog() {
                this.dialog = false;
                this.name = '';
                this.xid = 'SP_';
                this.vectorImage = '';
            },
            submitFile() {
                this.$emit("created", this.synopticPanel)
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
