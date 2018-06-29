<template>
  <div class="container">
    <div class="row">
      <div class="col-md-12" style="margin: 10px;">
        <v-jsoneditor v-model="json" style="width: 800px; height: 300px">
        </v-jsoneditor>
      </div>
    </div>
    <div class="row">
      <div class="col-md-12">
        <button v-on:click="doExport()" type="button" class="btn btn-primary">Export</button>
        <button v-on:click="doImport()" color="warning" type="button" class="btn btn-warning">Import</button>
      </div>
    </div>

    <div class="row">
      <div class="col-md-12">
        <div class="panel panel-default">
          <div class="container">
            <h4 class="progress-label">{{base.status}}</h4>
          </div>
          <div class="container">
            <h5 class="progress-label">{{base.statusGroup}}</h5>
            <div class="progress">
              <div class="progress-bar progress-bar-striped active" role="progressbar" aria-valuenow="20"
                   aria-valuemin="0" aria-valuemax="100" style="min-width: 2em;">
                20%
              </div>
            </div>
          </div>
          <div class="container">
            <h5 class="progress-label">{{base.statusDetail}}</h5>
            <div class="progress">
              <div class="progress-bar progress-bar-striped active" role="progressbar" aria-valuenow="30"
                   aria-valuemin="0" aria-valuemax="100" style="min-width: 2em;">
                30%
              </div>
            </div>
          </div>
          <div class="table-responsive">
            <table class="table table-striped table-bordered">
              <tr>
                <td colspan="2">
                  <span v-html="base.status"/>
                </td>
              </tr>
              <tr>
                <td colspan="2">
                  Time import: {{timer.timeImport}}
                </td>
              </tr>
              <tr>
                <td colspan="2">
                  To import: {{base.counterToParse}}
                </td>
              </tr>
              <tr>
                <td>
                  To create: {{toCreate.xidFolderToCreate.length}}
                </td>
                <td>
                  To created: {{toCreate.xidFolderCreated.length}}
                </td>
              </tr>
              <tr>
                <td>
                  To move folder: {{toMoveFolder.xidFolderToMoveTo.length}}
                </td>
                <td>
                  To moved folder: {{toMoveFolder.xidFolderMoved.length}}
                </td>
              </tr>
              <tr>
                <td>
                  To move points: {{Object.keys(toMovePoints.xidPointsMoveFromTo).length}}
                </td>
                <td>
                  To moved points: {{toMovePoints.xidPointMoved.length}}
                </td>
              </tr>
              <tr>
                <td>
                  To delete folder: {{toDeleteFolder.xidFolderToDelete.length}}
                </td>
                <td>
                  To deleted: {{toDeleteFolder.xidFolderDeleted.length}}
                </td>
              </tr>
              <tr>
                <td>
                  Folder names to change: {{toChangeNameFolder.xidFolderToNameChange.length}}
                </td>
                <td>
                  Names changed: {{toChangeNameFolder.xidFolderNameChanged.length}}
                </td>
              </tr>
            </table>
          </div>
        </div>
      </div>
    </div>


    <!-- debug -->
    <!--
   <p>check: {{base.xidFolderToCheck}}</p>
   <p>Exist: {{base.xidFolderExists}}</p>
   <p>Before: {{base.xidFolderBefore}}</p>

   <p>Not exist: {{toCreate.xidFolderNotExists}}</p>

   <p>To create: {{toCreate.xidFolderToCreate}}</p>
   <p>Errors: {{base.xidErrors}}</p>

   <p>After: {{base.xidFolderAfter}}}</p>
   <p>Points after: {{toMovePoints.xidMapPointsAfter}}</p>

   <p>Move folder: {{toMoveFolder.xidFolderToMoveTo}} </p>
   <p>Points move from to: {{toMovePoints.xidPointsMoveFromTo}} </p>

   <p>Points exists: {{toMovePoints.xidMapPointsExist}}</p>
   <p>Points not move: {{toMovePoints.xidPointsNotMove}} </p>

   <p>Name folder to change: {{toChangeNameFolder.xidFolderToNameChange}}</p>
   <p>Name folder changed: {{toChangeNameFolder.xidFolderNameChanged}}</p>

   <p>Folder to delete: {{toDeleteFolder.xidFolderToDelete}}</p>
   <p>Folder deleted: {{toDeleteFolder.xidFolderDeleted}}</p>

   <p>To delete folder: {{toDeleteFolder.xidFolderToDelete}}</p>
   <p>Timer: {{timer}}</p>
   -->


  </div>
</template>

<script>
  import VJsoneditor from 'vue-jsoneditor';
  import axios from 'axios';

  export default {
    name: "export-import-point-hierarchy",
    components: {
      VJsoneditor
    },
    data() {
      return {
        constants: {
          // if new parentId is # then move to root
          ROOT: "_",
          STATUS: 0,  // run - green, warning - yellow, error - red
          STATUS_INFO: {
            run:0,
            warning: 1,
            error: 2,
            end: 3
          },
          STATUS_GROUP: 1,
          STATUS_GROUP_INFO: {
            PARSE: 1,
            CHECK: 2,
            CHECK_TO_CREATE: 3,
            CHECK_TO_MOVE_FOLDER: 4,
            CHECK_TO_MOVE_POINTS: 5,
            CHECK_FOLDERS_TO_DELETE: 6,
            CHECK_TO_CHANGE_FOLDER_NAME: 7,
            CREATE_FOLDERS: 8,
            MOVE_FOLDERS: 9,
            MOVE_POINTS: 10,
            DELETE_FOLDERS: 11,
            CHANGE_NAME_FOLDERS: 12,
            REFRESH_CACHE: 13
          },
          STATUS_DETAIL: 2,
          STATUS_DETAIL_INFO: {
            //
          }
        },
        json: {},
        timer: {
          startImport: 0,
          endImport: 0,
          timeImport: 0
        },
        base: {
          progressImport: {
            group: {
              label: "",
              percent: 0,
              status: "active",
              elementToDo: 13,
              elementDoing:1
            },
            detail: {
              label: "",
              percent: 0,
              status: "active"
            }
          },
          status: "",
          statusGroup: "",
          statusDetail: "",
          counterToParse: 0,
          xidFolderAfter: [],
          xidFolderBefore: [],
          xidFolderExists: [],
          xidErrors: [],
        },
        toCreate: {
          xidFolderNotExists: [],
          xidFolderToCreate: [],
          xidFolderCreated: [],
        },
        toMoveFolder: {
          xidFolderToMoveTo: [],
          xidFolderMoved: []
        },
        toMovePoints: {
          xidMapPointsExist: {},
          xidMapPointsAfter: {},
          xidPointsMoveFromTo: {},
          xidPointsNotMove: {},
          xidPointMoved: []
        },
        toDeleteFolder: {
          xidFolderToDelete: [],
          xidFolderDeleted: []
        },
        toChangeNameFolder: {
          xidFolderToNameChange: [],
          xidFolderNameChanged: []
        }
      };
    },
    methods: {
      doExport() {
        const apiExportXidFolder = `./api/pointHierarchy/export`;
        axios.get(apiExportXidFolder).then(response => {
          this.json = response.data;
        }).catch(error => {
          console.log(error);
        });
      },
      doImport() {
        this.clearData();
        this.timer.startImport = performance.now();
        this.parse();
      },
      clearData() {
        //Cleansing of data from previous imports

        this.timer.startImport = 0;
        this.timer.endImport = 0;
        this.timer.timeImport = 0;

        this.base.status = "";
        this.base.counterToParse = 0;
        this.base.xidFolderAfter = [];
        this.base.xidFolderBefore = [];
        this.base.xidFolderExists = [];
        this.base.xidErrors = [];

        this.toCreate.xidFolderNotExists = [];
        this.toCreate.xidFolderToCreate = [];
        this.toCreate.xidFolderCreated = [];

        this.toMoveFolder.xidFolderToMoveTo = [];
        this.toMoveFolder.xidFolderMoved = [];

        this.toMovePoints.xidMapPointsExist = {};
        this.toMovePoints.xidMapPointsAfter = {};
        this.toMovePoints.xidPointsMoveFromTo = {};
        this.toMovePoints.xidPointsNotMove = {};
        this.toMovePoints.xidPointMoved = [];

        this.toDeleteFolder.xidFolderToDelete = [];
        this.toDeleteFolder.xidFolderDeleted = [];

        this.toChangeNameFolder.xidFolderToNameChange = [];
        this.toChangeNameFolder.xidFolderNameChanged = [];
      },
      showStatus(status, infoStatus, str) {
        //TODO err, info
        if (status == this.constants.STATUS) {
          this.base.status = str;
          if (
            infoStatus == this.constants.STATUS_INFO.warning ||
            infoStatus == this.constants.STATUS_INFO.error ||
            infoStatus == this.constants.STATUS_INFO.end
          ) {
            this.timer.stopImport = performance.now();
            this.timer.timeImport = this.timer.stopImport - this.timer.startImport;
          }
        } else if (status == this.constants.STATUS_GROUP) {
          this.base.statusGroup = str;
          this.calculateProgressGroup(infoStatus);
        } else if (status == this.constants.STATUS_DETAIL) {
          this.base.statusDetail = str;
        }
      },
      parse() {
        this.showStatus(
          this.constants.STATUS,
          this.constants.STATUS_INFO.run,
          "Start");
        this.showStatus(
          this.constants.STATUS_GROUP,
          this.constants.STATUS_GROUP_INFO.PARSE,
          "Parse input string");
        try {
          let folders = this.json.folders;
          this.base.xidFolderAfter = folders.slice();
          this.base.counterToParse = this.base.xidFolderAfter.length;
          if (folders == undefined) {
            this.showStatus(
              this.constants.STATUS,
              this.constants.STATUS_INFO.warning,
              "In import don't have folders");
          } else {
            this.base.xidFolderToCheck = folders;
            this.check();
          }
        } catch (err) {
          this.showStatus(
            this.constants.STATUS,
            this.constants.STATUS_INFO.error,
            "Poor data format:"+err);
        }
      },
      check() {
        this.showStatus(
          this.constants.STATUS_GROUP,
          this.constants.STATUS_GROUP_INFO.CHECK,
          "Check folders");
        if (this.base.xidFolderToCheck.length == 0) {
          this.showStatus(
            this.constants.STATUS,
            this.constants.STATUS_INFO.warning,
            "there seems to be nothing to import");
        } else {
          this.showStatus(
            this.constants.STATUS_DETAIL,
            0,
            "Check folder:" + this.base.xidFolderToCheck[0].xidFolder);
          const apiCheckXidFolder = `./api/pointHierarchy/folderCheckExist/${this.base.xidFolderToCheck[0].xidFolder}`;
          axios.get(apiCheckXidFolder).then(response => {
            if (response.data !== undefined) {
              this.base.xidFolderExists.push(this.base.xidFolderToCheck[0]);
              this.base.xidFolderBefore.push(response.data);
            } else if (this.base.xidFolderToCheck[0] != undefined && response.data == false) {
              this.toCreate.xidFolderNotExists.push(this.base.xidFolderToCheck[0]);
            }
            this.base.xidFolderToCheck.splice(0, 1);
            if (this.base.xidFolderToCheck.length > 0) {
              this.check();
            } else {
              this.validToCreate();
            }
          }).catch(error => {
            this.showStatus(
              this.constants.STATUS,
              this.constants.STATUS_INFO.error,
              "Error:"+err);
            console.log(error);
            this.toCreate.xidFolderNotExists.push(this.base.xidFolderToCheck[0]);
            this.base.xidFolderToCheck.splice(0, 1);
            if (this.base.xidFolderToCheck.length > 0) {
              this.check();
            } else {
              this.validToCreate();
            }
          });
        }
      },
      validToCreate() {
        this.showStatus(
          this.constants.STATUS_GROUP,
          this.constants.STATUS_GROUP_INFO.CHECK_TO_CREATE,
          "Check folders to create");
        if (this.toCreate.xidFolderNotExists.length > 0) {
          for (var id in this.toCreate.xidFolderNotExists) {
            if (this.toCreate.xidFolderNotExists[id] != null) {
              this.showStatus(
                this.constants.STATUS_DETAIL,
                0,
                "Validate to create:" + this.toCreate.xidFolderNotExists[id].xidFolder);
              if (this.toCreate.xidFolderNotExists[id].xidFolder == undefined || this.toCreate.xidFolderNotExists[id].name == undefined) {
                this.base.xidErrors.push(this.toCreate.xidFolderToCreate[id])
              } else {
                this.toCreate.xidFolderToCreate.push(this.toCreate.xidFolderNotExists[id]);
              }
            }
          }
        }
        this.prepareDataToMoveFolders();
      },
      prepareDataToMoveFolders() {
        this.showStatus(
          this.constants.STATUS_GROUP,
          this.constants.STATUS_GROUP_INFO.CHECK_TO_MOVE_FOLDER,
          "Check in exist Folders parentId then different then to move");
        for (var i in this.base.xidFolderExists) {
          for (var j in this.base.xidFolderBefore) {
            if (this.base.xidFolderExists[i].xidFolder == this.base.xidFolderBefore[j].xid) {
              this.showStatus(
                this.constants.STATUS_DETAIL, 0,
                "The same xid:" + this.base.xidFolderExists[i].xidFolder);
              if (this.base.xidFolderExists[i].parentXid !== this.base.xidFolderBefore[j].parentXid) {
                let newMoveFolder = {};
                newMoveFolder.newParentXid = this.base.xidFolderExists[i].parentXid;
                newMoveFolder.xidFolder = this.base.xidFolderExists[i].xidFolder;
                this.toMoveFolder.xidFolderToMoveTo.push(newMoveFolder);
              }
            }
          }
        }

        this.prepareDataToMovePoints();

      },
      prepareDataToMovePoints() {
        this.showStatus(
          this.constants.STATUS_GROUP,
          this.constants.STATUS_GROUP_INFO.CHECK_TO_MOVE_POINTS,
          "Check points to move");

        for (var i in this.base.xidFolderBefore) {
          for (var j in this.base.xidFolderBefore[i].pointXids) {
            this.toMovePoints.xidMapPointsExist[this.base.xidFolderBefore[i].pointXids[j]] = {
              xidPoint: this.base.xidFolderBefore[i].pointXids[j],
              xidFolder: this.base.xidFolderBefore[i].xid
            }
          }
        }

        for (var a in this.base.xidFolderAfter) {
          for (var b in this.base.xidFolderAfter[a].pointXids) {
            this.toMovePoints.xidMapPointsAfter[this.base.xidFolderAfter[a].pointXids[b]] = {
              xidPoint: this.base.xidFolderAfter[a].pointXids[b],
              xidFolder: this.base.xidFolderAfter[a].xidFolder,
              delete: this.base.xidFolderAfter[a].delete
            }
          }
        }

        for (var p in this.toMovePoints.xidMapPointsAfter) {
          let xidPointToCheckAfter = this.toMovePoints.xidMapPointsAfter[p];
          let xidPointToCheckExist = this.toMovePoints.xidMapPointsExist[p];

          if (xidPointToCheckExist == undefined) {
            let pointMoveFromToFolder = {
              xidPoint: xidPointToCheckAfter.xidPoint,
              oldParent: '?',
              newParent: xidPointToCheckAfter.xidFolder
            }
            this.toMovePoints.xidPointsMoveFromTo[xidPointToCheckAfter.xidPoint] = pointMoveFromToFolder;
          }
        }

        for (var p in this.toMovePoints.xidMapPointsExist) {

          let xidPointToCheckAfter = this.toMovePoints.xidMapPointsAfter[p];
          let xidPointToCheckExist = this.toMovePoints.xidMapPointsExist[p];
          // check is in After if not move to root
          if (xidPointToCheckAfter == undefined) {
            let pointMoveFromToRoot = {
              xidPoint: xidPointToCheckExist.xidPoint,
              oldParent: '?',
              newParent: this.constants.ROOT
            }
            this.toMovePoints.xidPointsMoveFromTo[xidPointToCheckExist.xidPoint] = pointMoveFromToRoot;
            // check is in After has another xidFolder then move point to new Folder
          } else if (xidPointToCheckAfter.xidFolder !== xidPointToCheckExist.xidFolder) {
            let pointMoveFromToAnother = {
              xidPoint: xidPointToCheckExist.xidPoint,
              oldParent: xidPointToCheckExist.xidFolder,
              newParent: xidPointToCheckAfter.xidFolder
            }
            this.toMovePoints.xidPointsMoveFromTo[xidPointToCheckExist.xidPoint] = pointMoveFromToAnother;
            // not move
          } else if (xidPointToCheckAfter.xidFolder == xidPointToCheckExist.xidFolder) {
            this.toMovePoints.xidPointsNotMove[xidPointToCheckExist.xidPoint] == xidPointToCheckAfter;
          }
        }
        this.prepareDataToDeleteFolders();
      },
      prepareDataToDeleteFolders() {
        this.showStatus(
          this.constants.STATUS_GROUP,
          this.constants.STATUS_GROUP_INFO.CHECK_FOLDERS_TO_DELETE,
          "Check folder to delete");

        for (let z in this.base.xidFolderAfter) {
          if (this.base.xidFolderAfter[z].toDelete != undefined && this.base.xidFolderAfter[z].toDelete == "true") {
            let xidFolderToDelete = this.base.xidFolderAfter[z];
            this.toDeleteFolder.xidFolderToDelete.push(xidFolderToDelete);
          }
        }
        this.prepareFolderToNameChange();
      },
      prepareFolderToNameChange() {
        this.showStatus(
          this.constants.STATUS_GROUP,
          this.constants.STATUS_GROUP_INFO.CHECK_TO_CHANGE_FOLDER_NAME,
          "Check folder to update");
        for (var i in this.base.xidFolderAfter) {
          for (var j in this.base.xidFolderBefore) {
            if (this.base.xidFolderAfter[i].xidFolder == this.base.xidFolderBefore[j].xid) {
              if (this.base.xidFolderAfter[i].name != this.base.xidFolderBefore[j].name) {
                this.toChangeNameFolder.xidFolderToNameChange.push(this.base.xidFolderAfter[i]);
              }
            }
          }
        }
        this.createFolders();
      },
      createFolders() {
        this.showStatus(
          this.constants.STATUS_GROUP,
          this.constants.STATUS_GROUP_INFO.CREATE_FOLDERS,
          "Create folders");
        if (this.toCreate.xidFolderToCreate.length > 0) {
          const data = {};
          data.name = this.toCreate.xidFolderToCreate[0].name;
          data.xid = this.toCreate.xidFolderToCreate[0].xidFolder;
          data.parentXid = this.toCreate.xidFolderToCreate[0].parentXid;
          data.pointXids = this.toCreate.xidFolderToCreate[0].points;

          const apiAddFolder = `.//api/pointHierarchy/folderAdd/`;

          axios.post(apiAddFolder, data)
            .then(response => {
              console.log(response);
              let newCreated = this.toCreate.xidFolderToCreate[0];
              this.toCreate.xidFolderCreated.push(newCreated);
              this.toCreate.xidFolderToCreate.splice(0, 1);
              if (this.toCreate.xidFolderToCreate.length > 0) {
                this.createFolders();
              } else {
                this.moveFolders();
              }
            })
            .catch(error => {
              console.log(error);
              this.toCreate.xidFolderToCreate.splice(0, 1);
              if (this.toCreate.xidFolderToCreate.length > 0) {
                this.createFolders();
              } else {
                this.moveFolders();
              }
            });
        } else {
          this.moveFolders();
        }
      },
      moveFolders() {
        this.showStatus(
          this.constants.STATUS_GROUP,
          this.constants.STATUS_GROUP_INFO.MOVE_FOLDERS,
          "Move folders");
        if (this.toMoveFolder.xidFolderToMoveTo.length > 0) {
          const apiMoveFolder = `./api/pointHierarchy/folderMoveTo/${this.toMoveFolder.xidFolderToMoveTo[0].xidFolder}/${this.toMoveFolder.xidFolderToMoveTo[0].newParentXid}`;
          axios.put(apiMoveFolder)
            .then(response => {
              console.log(response);
              let moved = this.toMoveFolder.xidFolderToMoveTo[0];
              this.toMoveFolder.xidFolderMoved.push(moved);
              this.toMoveFolder.xidFolderToMoveTo.splice(0, 1);
              if (this.toMoveFolder.xidFolderToMoveTo.length > 0) {
                this.moveFolders();
              } else {
                this.movePoints();
              }
            })
            .catch(error => {
              console.log(error);
              this.toMoveFolder.xidFolderToMoveTo.splice(0, 1);
              if (this.toMoveFolder.xidFolderToMoveTo.length > 0) {
                this.moveFolders();
              } else {
                this.movePoints();
              }
            });
        } else {
          this.movePoints();
        }
      },
      movePoints() {
        // setTimeout(() => {
        this.showStatus(
          this.constants.STATUS_GROUP,
          this.constants.STATUS_GROUP_INFO.MOVE_POINTS,
          "Move points");
        let arrPointToMove = Object.keys(this.toMovePoints.xidPointsMoveFromTo).map(key => this.toMovePoints.xidPointsMoveFromTo[key]);
        if (arrPointToMove.length > 0) {
          let newParent = this.constants.ROOT;
          if (!arrPointToMove[0].newParent.trim() == "") {
            newParent = arrPointToMove[0].newParent;
          }
          const apiMovePoints = `./api/pointHierarchy/pointMoveTo/${arrPointToMove[0].xidPoint}/${newParent}`;
          axios.put(apiMovePoints)
            .then(response => {
              console.log(response);
              let moved = arrPointToMove[0];
              this.toMovePoints.xidPointMoved.push(moved);
              delete this.toMovePoints.xidPointsMoveFromTo[arrPointToMove[0].xidPoint];
              arrPointToMove.splice(0, 1);
              if (arrPointToMove.length > 0) {
                this.movePoints();
              } else {
                this.deleteFolders();
              }
            })
            .catch(error => {
              console.log(error);
              delete this.toMovePoints.xidPointsMoveFromTo[arrPointToMove[0].xidPoint];
              arrPointToMove.splice(0, 1);
              if (arrPointToMove.length > 0) {
                this.movePoints();
              } else {
                this.deleteFolders();
              }
            });
        } else {
          this.deleteFolders();
        }
        //}, 1000);
      },
      deleteFolders() {
        this.showStatus(
          this.constants.STATUS_GROUP,
          this.constants.STATUS_GROUP_INFO.DELETE_FOLDERS,
          "Delete folders");
        if (this.toDeleteFolder.xidFolderToDelete.length > 0) {
          const apiDeleteFolders = `./api/pointHierarchy/deleteFolder/${this.toDeleteFolder.xidFolderToDelete[0].xidFolder}`;
          axios.post(apiDeleteFolders)
            .then(response => {
              let deleted = this.toDeleteFolder.xidFolderToDelete[0];
              this.toDeleteFolder.xidFolderDeleted.push(deleted);
              this.toDeleteFolder.xidFolderToDelete.splice(0, 1);
              if (this.toDeleteFolder.xidFolderToDelete.length > 0) {
                this.deleteFolders();
              } else {
                this.changeNameFolders();
              }
            })
            .catch(error => {
              console.log(error);
              this.toDeleteFolder.xidFolderToDelete.splice(0, 1);
              if (this.toDeleteFolder.xidFolderToDelete.length > 0) {
                this.deleteFolders();
              } else {
                this.changeNameFolders();
              }
            });
        } else {
          this.changeNameFolders();
        }
      },
      changeNameFolders() {
        this.showStatus(
          this.constants.STATUS_GROUP,
          this.constants.STATUS_GROUP_INFO.CHANGE_NAME_FOLDERS,
          "Change name folders");

        if (this.toChangeNameFolder.xidFolderToNameChange.length > 0) {
          const apiUpdateFolder = `./api/pointHierarchy/changeName/${this.toChangeNameFolder.xidFolderToNameChange[0].xidFolder}/${this.toChangeNameFolder.xidFolderToNameChange[0].name}`;
          axios.put(apiUpdateFolder)
            .then(response => {
              let change = this.toChangeNameFolder.xidFolderToNameChange[0];
              this.toChangeNameFolder.xidFolderNameChanged.push(change);
              this.toChangeNameFolder.xidFolderToNameChange.splice(0, 1);
              if (this.toChangeNameFolder.xidFolderToNameChange.length > 0) {
                this.changeNameFolders();
              } else {
                this.refreshCache();
              }
            })
            .catch(error => {
              console.log(error);
              this.toChangeNameFolder.xidFolderToNameChange.splice(0, 1);
              if (this.toChangeNameFolder.xidFolderToNameChange.length > 0) {
                this.changeNameFolders();
              } else {
                this.refreshCache();
              }
            });
        } else {
          this.refreshCache();
        }
      },
      refreshCache() {
        this.showStatus(
          this.constants.STATUS_GROUP,
          this.constants.STATUS_GROUP_INFO.REFRESH_CACHE,
          "Refresh cache");

        const apiRefreshCache = `./api/pointHierarchy/cacheRefresh/`;
        axios.post(apiRefreshCache)
          .then(response => {
            this.timer.stopImport = performance.now();
            this.timer.timeImport = this.timer.stopImport - this.timer.startImport;
            this.showStatus(this.constants.STATUS_GROUP, "<b>End Import</b>");
          })
          .catch(error => {
            console.log(error);
            this.showStatus(this.constants.STATUS_GROUP, "<b>End Import</b>");
          });
        this.showStatus(this.constants.STATUS_GROUP, "<b>End Import</b>");
      },
      calculateProgressGroup(elementDoing) {
        this.base.progressImport.group.elementDoing = elementDoing;
        this.base.progressImport.group.percent = (this.base.progressImport.group.elementDoing / this.base.progressImport.group.elementToDo) * 100;
      }
    },
  }
</script>

<style>
</style>
