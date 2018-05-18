
Vue.component('export-import', {
    data: function () {
        return {
            json: ''
        }
    },
    mixins: [ExportImportHierarchiPoints],
    methods: {
        doExport() {
            this.export();
            //alert('export-import');
        },
        doImport() {
            this.import();
            //alert('export-import');
        }


    },
    template: `
         <div>
            
            <textarea v-model="json" placeholder="JSON with definition hierarchy" rows="15" cols="200">
            
            </textarea></br>
            <button v-on:click="doExport()">Export</button>
            <button v-on:click="doImport()">Import</button>
            <!--
                <p>{{json}}</p>
            -->
        </div>`
});

