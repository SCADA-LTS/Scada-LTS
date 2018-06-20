<template>
  <div>

    <v-jsoneditor v-model="json" style="width: 800px; height: 300px"
                  @input="jsonChanged">
    </v-jsoneditor>
    <div>
      <button v-on:click="doExport()">Export</button>
      <button v-on:click="doImport">Import</button>
    </div>
    <p>{{status}}</p>
    <p>To import: {{counterToParse}}</p>

  </div>
</template>

<script>
  import VJsoneditor from 'vue-jsoneditor';
  import axios from 'axios';

  export default {
    name: "export-import-point-hierarchy",
    components: {
      VJsoneditor,
    },
    data() {
      return {
        json: {},
        timer: {
          startImport: 0,
          endImport: 0,
          timeImport: 0
        },
        status:"",
        counterToParse:0,
        xidFolderAfter:[]
      };
    },
    methods: {
      jsonChanged(value) {
        console.log(JSON.stringify(value));
      },
      doExport() {
        const apiExportXidFolder = `./api/pointHierarchy/export`;
        console.log(apiExportXidFolder);
        axios.get(apiExportXidFolder).then(response => {
          console.log(response);
          this.json = response.data;
        }).catch(error => {
          console.log(error);
        });
      },
      doImport() {
        this.timer.startImport = performance.now();
        this.parse();
      },
      showStatus(str) {
        this.status = str;
      },
      parse() {
        this.showStatus("Parse input string");

        let folders = this.json.folders;
        this.xidFolderAfter = folders.slice();
        this.counterToParse = this.xidFolderAfter.length;

        if (folders == undefined) {

        } else {
          this.xidFolderToCheck = folders;
          this.check();
        }
      }

    }
  }
</script>

<style>

</style>
