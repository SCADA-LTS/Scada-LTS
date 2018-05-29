
Vue.component('export-import', {
    data() {
        return {
            username: '',
            data: [],
            errorMsg: '',
            export_import_json: {},
            refreshed: '',
            xidFolder: '',
            xidFolderExists:[],
            xidFolderNotExists:[]
        }
    },
    mixins: [ExportImportHierarchiPoints],
    methods: {
        doExport() {ri
            this.export();
            //alert('export-import');
        },
        doImport() {
            this.import();
            //alert('export-import');
        },
        search() {
            const api = `https://api.github.com/users/${this.username}`
            axios.get(api).then(response => {
                this.data = response.data
            }).catch(error => {
                this.errorMsg = 'No user or no location!';
                this.data = [];
            });
        },
        parse() {
            // check elements
            let exp_imp = JSON.parse(this.export_import_json);
            let folders = exp_imp.folders;
            let promises = [];

            if (folders == undefined) {
              console.log('Folders is required');
            } else {
                // check exist folders and root folder 'may only one' etc.
                for (id in folders) {
                    console.log(folders[id]);
                    promises.push(this.check(folders[id].xidFolder));
                }
            }

            console.log("xidFolderExists");
            console.log(this.xidFolderExists);
            console.log("xidFolderNotExists");
            console.log(this.xidFolderNotExists);
        },

        check(xidFolder) {
            this.xidFolder = xidFolder;
            console.log(xidFolder);
            console.log(this.xidFolder);
            const apiCheckXidFolder = `./api/pointHierarchy/folderCheckExist/${this.xidFolder}`;

            axios.get( apiCheckXidFolder ).then(response => {
                console.log(response);
                if (response.data == true) {
                    this.xidFolderExists.push(this.xidFolder);
                } else if (this.xidFolder != undefined && response.data == false) {
                    this.xidFolderNotExists.push(this.xidFolder);
                }
            }).catch(error => {
                if (this.xidFolder != undefined && response.data == false) {
                   this.xidFolderNotExists.push(this.xidFolder);
                }
                console.log(error)
                return false;
            });
        },

        add() {
            const apiAdd = `../api/`;
        },
        remove() {

        },
        pointMove() {

        },
        folderMove() {

        },
        cacheRefresh() {
            const apiCacheRefresh = `./api/pointHierarchy/cacheRefresh`;
            axios.get(apiCacheRefresh).then(response => {
                console.log(response);
            }).catch(error => {
                this.errorMsg = 'No user or no location!';
                console.log(error)
            });
        }
    },
    template: `
         <div>
            
            <textarea v-model="export_import_json" placeholder="JSON with definition hierarchy" rows="15" cols="200">
            
            </textarea></br>
            <button v-on:click="doExport()">Export</button>
            <button v-on:click="doImport()">Import</button>
            <button v-on:click="check()">Check</button>
            <button v-on:click="cacheRefresh()">Refresh</button>
            <button v-on:click="parse()">Parse</button>


            <form @submit.prevent="search">
                <input v-model="username" placeholder="Enter a github username!">
            </form>
            <p v-if="data.name && data.location">
                {{ data.name }} ( {{ data.login }} )
                is from
                {{ data.location }}!
            </p>
            <p v-else>{{ errorMsg }}
            <p> Not Exist: {{xidFolderNotExists}} </p>
            <p> Is Exist: {{xidFolderExists}} </p>
        </div>`
});

