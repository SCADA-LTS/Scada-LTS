
Vue.component('export-import', {
    data() {
        return {
            username: '',
            data: [],
            errorMsg: '',
            export_import_json: {},
            refreshed: '',
            xidFolderExists:[],
            xidFolderNotExists:[],
            xidFolderBefore:[],
            xidFolderToCheck:[],
            xidFolderToCreate: [],
            xidFolderToMoveTo: [],
            xidPointsToMove: [],
            xidPointsExist: [],
            xidPointsMoveFromTo: [],
            xidFolderToDelete: [],
            xidErrors: []
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

            if (folders == undefined) {
              console.log('Folders is required');
            } else {
                // check exist folders and root folder 'may only one' etc.
                for (id in folders) {
                    console.log(folders[id]);
                }
                //this.checks(folders[id].xidFolder);
                this.xidFolderToCheck = folders;
                this.check();

            }

            console.log("xidFolderExists");
            console.log(this.xidFolderExists);
            console.log("xidFolderNotExists");
            console.log(this.xidFolderNotExists);
            console.log("xidFolderToCheck");
            console.log(this.xidFolderToCheck);

        },
        check() {
                    //TODO check responce becouse server is down then error.
                    console.log( this.xidFolderToCheck[0].xidFolder );
                    const apiCheckXidFolder = `./api/pointHierarchy/folderCheckExist/${this.xidFolderToCheck[0].xidFolder}`;
                    console.log( apiCheckXidFolder );
                    axios.get( apiCheckXidFolder ).then(response => {
                        console.log(response);
                        if (response.data !== undefined) {
                            this.xidFolderExists.push(this.xidFolderToCheck[0]);
                            this.xidFolderBefore.push(response.data);
                        } else if (this.xidFolderToCheck[0] != undefined && response.data == false) {
                            this.xidFolderNotExists.push(this.xidFolderToCheck[0]);
                        }
                        this.xidFolderToCheck.splice(0, 1);
                        if (this.xidFolderToCheck.length > 0) {
                            this.check();
                        } else {
                            this.validToCreate();
                        }
                    }).catch(error => {
                        console.log(error);
                        this.xidFolderNotExists.push(this.xidFolderToCheck[0]);
                        this.xidFolderToCheck.splice(0, 1);
                        if (this.xidFolderToCheck.length > 0) {
                            this.check();
                        } else {
                            this.validToCreate();
                        }
                    });
        },
        validToCreate() {
            console.log("run validToCreate");
            for (id in this.xidFolderNotExists) {
                if (this.xidFolderNotExists[id].xidFolder == undefined || this.xidFolderNotExists[id].name == undefined ) {
                    this.xidErrors.push(this.xidFolderToCreate[id])
                } else {
                    // is ok
                    this.xidFolderToCreate.push( this.xidFolderNotExists[id]);
                }
            }
            this.prepareDataToMoveFolders();
        },
        prepareDataToMoveFolders() {
            // check in exist Folders parentId then different then to move
            console.log("run prepareDataToMoveFolders");

            var animal = {};

            for (i in this.xidFolderExists) {
                for (j in this.xidFolderBefore) {
                    if (this.xidFolderExists[i].xidFolder == this.xidFolderBefore[j].xid) {
                        console.log("the same xid:"+ this.xidFolderExists[i].xidFolder);
                        if (this.xidFolderExists[i].parentXid !== this.xidFolderBefore[j].parentXid) {
                            let newMoveFolder = {};
                            newMoveFolder.newParentXid = this.xidFolderExists[i].parentXid;
                            newMoveFolder.xidFolder = this.xidFolderExists[i].xidFolder;
                            this.xidFolderToMoveTo.push(newMoveFolder);
                        }
                    }
                }
            }

            this.prepareDataToMovePoints();
        },
        prepareDataToMovePoints() {
                    // check in exist Folders parentId then different then to move
                    console.log("run prepareDataToMovePoints");

                    for (i in this.xidFolderExists) {
                        console.log(this.xidFolderExists[i]);
                        for (j in this.xidFolderNotExists[i].points) {
                            this.xidPointsExist[xidFolderNotExists[i].points[j]] = {xidPoint:xidFolderNotExists[i].points[j], xidFolder: this.xidFolderNotExists[i].xidFolder }
                        }
                    }
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
            <p> Before: {{xidFolderBefore}} </p>
            <p> To check: {{xidFolderToCheck}} </p>
            <p> To create: {{xidFolderToCreate}}  </p>
            <p> To move folder: {{xidFolderToMoveTo}} </p>
            <p> Points exist: {{xidPointsExist}} </p>
            <p> Points to move: {{xidPointsToMove}} </p>
            <p> To move points: {{xidPointsMoveFromTo}} </p>
            <p> Errors: {{xidErrors}} </p>


        </div>`
});

