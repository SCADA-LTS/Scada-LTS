
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
            counter: 0,
            mytimer: {
                startImport:0,
                endImport:0,
                timeImport:0
            },
            status:{},
            ref_status: {}
        }
    },
    methods: {
        /*jsonChanged(value) {
          console.log(JSON.stringify(value));
        },*/
        doExport() {
            const apiExportXidFolder = `./api/pointHierarchy/export`;
            console.log( apiExportXidFolder );
            axios.get( apiExportXidFolder ).then(response => {
                console.log(response);
                this.export_import_json = JSON.stringify(response.data);
            }).catch(error => {
                console.log(error);
            });
        },
        doImport() {
            BootstrapDialog.show({
                        title: 'Manipulating Buttons',
                        message: function(dialog) {
                            var $content = $(

                            '<ul class="nav nav-tabs"> ' +
                                 '<li class="active"><a data-toggle="tab" href="#tab_prepare_import">'+
                                 '<span id="asterisk_work_prepare_import" class="bootstrap-dialog-button-icon glyphicon glyphicon-asterisk icon-spin"></span>' +
                                 'Prepare import</a></li>'+
                                 '<li><a data-toggle="tab" href="#tab_import">Import</a></li>'+
                               '</ul>'+

                               '<div class="tab-content">'+
                                 '<div id="tab_prepare_import" class="tab-pane fade in active">'+
                                   //'<h3>HOME</h3>'+
                                   '<p id="status_prepare_import" ref="ref_status_prepare_import">Start</p>'+
                                 '</div>' +
                                 '<div id="tab_import" class="tab-pane fade">'+
                                   '<h3>Menu 1</h3>'+
                                   '<p>Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.</p>'+
                                 '</div>'+
                            '<div><button class="btn btn-success">Revert button status right now.</button></div>' +
                            '<div class="progress">  <div class="progress-bar" role="progressbar" aria-valuenow="70" aria-valuemin="0" aria-valuemax="100" style="width:70%"> <span class="sr-only">70% Complete</span> </div></div>');
                            var $footerButton = dialog.getButton('btn-1');
                            $content.find('button').click({$footerButton: $footerButton}, function(event) {
                                event.data.$footerButton.enable();
                                event.data.$footerButton.stopSpin();
                                dialog.setClosable(true);
                            });

                            return $content;
                        },
                        buttons: [{
                            id: 'btn-1',
                            label: 'Click to disable and spin.',
                            action: function(dialog) {
                                var $button = this; // 'this' here is a jQuery object that wrapping the <button> DOM element.
                                $button.disable();
                                $button.spin();
                                dialog.setClosable(false);
                            }
                        }]
                    });

            this.mytimer.startImport = performance.now();
            this.parse();
        },
        parse() {
            // check elements
            this.status = document.getElementById("status_prepare_import");
            this.ref_status = this.$refs[“ref_status_prepare_import”]
            if (this.status == undefined) {
                alert("ERROR");
            } else {
                this.status.innerHtml = "Parse input string";
            }
            if (this.ref_status = undefined) {
                alert("ERROR1");
            } else {
                this.ref_status.innerHtml = "Parse input string";
            }

            let exp_imp = JSON.parse(this.export_import_json);
            let folders = exp_imp.folders;
            this.xidFolderAfter = exp_imp.folders.slice();


            if (folders == undefined) {
              //document.getElementById("status_prepare_import").value = "Folders is required";
            } else {

                this.xidFolderToCheck = folders;
                this.check();

            }

        },
        check() {
            //document.getElementById("status_prepare_import"). = "Check folder:" + this.xidFolderToCheck[0].xidFolder;
            const apiCheckXidFolder = `./api/pointHierarchy/folderCheckExist/${this.xidFolderToCheck[0].xidFolder}`;
            axios.get( apiCheckXidFolder ).then(response => {
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
               //document.getElementById("status_prepare_import").value = "Error:" + error;
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
            this.mytimer.stopImport = performance.now();
            this.mytimer.timeImport = this.mytimer.stopImport - this.mytimer.startImport;
            alert(this.mytimer.timeImport);
            alert('import');
        }
    },
    template: `
         <div>
            
            <textarea v-model="export_import_json" placeholder="JSON with definition hierarchy" rows="15" cols="200">
            <!--
            <v-jsoneditor v-model="export_import_json"
                              @input="jsonChanged">
            </v-jsoneditor>
            -->
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
            <p> time import: {{mytimer.timeImport}}


        </div>`
});

