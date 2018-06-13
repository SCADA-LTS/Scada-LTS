
Vue.component('export-import', {
    data() {
        return {
            export_import_json: {},
            refreshed: '',
            xidFolderExists:[],
            xidFolderNotExists:[],
            xidFolderBefore:[],
            xidFolderAfter:[],
            xidFolderToCheck:[],
            xidFolderToCreate: [],
            xidFolderToMoveTo: [],
            xidMapPointsExist: {},
            xidMapPointsAfter: {},
            xidPointsMoveFromTo: {},
            xidPointsNotMove: {},
            xidFolderToDelete: [],
            xidErrors: [],
            counter: 0
        }
    },
    methods: {
        doExport() {
            const apiExportXidFolder = `./api/pointHierarchy/export`;
            console.log( apiExportXidFolder );
            axios.get( apiExportXidFolder ).then(response => {
                console.log(response);
                this.export_import_json = response.data;
            }).catch(error => {
                console.log(error);
            });
        },
        doImport() {
            this.parse();
        },
        parse() {
            // check elements
            let exp_imp = JSON.parse(this.export_import_json);
            let folders = exp_imp.folders;
            this.xidFolderAfter = exp_imp.folders.slice();


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
                this.counter++;
            }
            this.prepareDataToMoveFolders();
        },
        prepareDataToMoveFolders() {
            // check in exist Folders parentId then different then to move
            console.log("run prepareDataToMoveFolders");

            for (i in this.xidFolderExists) {
                for (j in this.xidFolderBefore) {
                    if (this.xidFolderExists[i].xidFolder == this.xidFolderBefore[j].xid) {
                       this.counter++;
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

                    for (i in this.xidFolderBefore) {
                        for (j in this.xidFolderBefore[i].pointXids) {
                            this.xidMapPointsExist[this.xidFolderBefore[i].pointXids[j]] = {xidPoint:this.xidFolderBefore[i].pointXids[j], xidFolder: this.xidFolderBefore[i].xid }
                        }
                     }

                     for (a in this.xidFolderAfter) {
                        for (b in this.xidFolderAfter[a].points) {
                            this.xidMapPointsAfter[this.xidFolderAfter[a].points[b]] = {xidPoint:this.xidFolderAfter[a].points[b], xidFolder: this.xidFolderAfter[a].xidFolder, delete: this.xidFolderAfter[a].delete }
                        }
                     }
                     // move to root from folder delete
                    for(p in this.xidMapPointsExist ) {

                        this.counter++;

                        let xidPointToCheckAfter = this.xidMapPointsAfter[this.xidFolderBefore[i].pointXids[j]];
                        let xidPointToCheckExist = this.xidMapPointsExist[p];

                        // check is in After if not move to root
                        if (xidPointToCheckAfter == undefined) {
                            let pointMoveFromToRoot = { xidPoint: xidPointToCheckExist.xidPoint, oldParent: '?', newParent: ''}
                            this.xidPointsMoveFromTo[xidPointToCheckExist.xidPoint] = pointMoveFromToRoot;
                        // check is in After has another xidFolder then move point to new Folder
                        } else if (xidPointToCheckAfter.xidFolder !== xidPointToCheckExist.xidFolder) {
                            let pointMoveFromToAnother = {
                                xidPoint: xidPointToCheckExist.xidPoint,
                                oldParent: xidPointToCheckExist.xidFolder,
                                newParent: xidPointToCheckAfter.xidFolder}
                            this.xidPointsMoveFromTo[xidPointToCheckExist.xidPoint] = pointMoveFromToAnother;
                        // not move
                        } else if (xidPointToCheckAfter.xidFolder == xidPointToCheckExist.xidFolder) {
                            this.xidPointsNotMove[xidPointToCheckExist.xidPoint] == xidPointToCheckAfter;
                        }

                    }
                    this.runImportApi();
        },
        cacheRefresh() {
            const apiCacheRefresh = `./api/pointHierarchy/cacheRefresh`;
            axios.get(apiCacheRefresh).then(response => {
                console.log(response);
            }).catch(error => {
                this.errorMsg = 'No user or no location!';
                console.log(error)
            });
        },
        runImportApi(){
            alert('import');
        }
    },
    template: `
         <div>
            
            <textarea v-model="export_import_json" placeholder="JSON with definition hierarchy" rows="15" cols="200">
            
            </textarea></br>
            <button v-on:click="doExport()">Export</button>
            <button v-on:click="doImport()">Import</button>

            <p> Not Exist: {{xidFolderNotExists}} </p>
            <p> Is Exist: {{xidFolderExists}} </p>
            <p> Before: {{xidFolderBefore}} </p>
            <p> After: {{xidFolderAfter}} </p>
            <p> To check: {{xidFolderToCheck}} </p>
            <p> To create: {{xidFolderToCreate}}  </p>
            <p> To move folder: {{xidFolderToMoveTo}} </p>
            <p> Points exist: {{xidMapPointsExist}} </p>
            <p> Points after: {{xidMapPointsAfter}} </p>
            <p> To move points: {{xidPointsMoveFromTo}} </p>
            <p> Not to move points: {{xidPointsNotMove}} </p>
            <p> Errors: {{xidErrors}} </p>
            <p> counter: {{counter}} </p>


        </div>`
});

