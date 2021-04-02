<template>
<v-dialog v-model="dialog" max-width="500px">
    <template v-slot:activator="{on, attrs}">
        <v-btn elevation="0" fab v-bind="attrs" v-on="on">
            <v-icon>mdi-plus</v-icon>
        </v-btn>
    </template>

    <v-card id="dialog-synoptic-panel-creation">
        <v-card-title>Create Synoptic Panel</v-card-title>
        <v-card-text>
            <v-row>
                <v-col cols="12">
                    <v-text-field
                        label="Name"
                        v-model="name"
                        dense
                    ></v-text-field>                    
                </v-col>
                <v-col cols="12">
                    <v-text-field
                        label="Export ID"
                        v-model="xid"
                        dense
                    ></v-text-field>                    
                </v-col>
                <v-col cols="12">
                    <v-file-input
                        label="SVG graphic file"
                        @change="handleFileUpload($event)"
                        accept="image/svg+xml"
                    ></v-file-input>
                </v-col>
            </v-row>
        </v-card-text>
        <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn text @click="closeDialog()">Cancel</v-btn>
			<v-btn color="primary" text @click="save()">Create!</v-btn>
        </v-card-actions>
    </v-card>
</v-dialog>
</template>
<script>
    export default {
        name: 'SynopticPanelCreator',

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
                    console.log("Creator", this.synopticPanel);
                    this.synopticPanel = synopticPanel;
                });
            },
            closeDialog() {
                this.dialog = false;
                this.name = '';
                this.xid = 'SP_';
                this.vectorImage = '';
            },
            save() {
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