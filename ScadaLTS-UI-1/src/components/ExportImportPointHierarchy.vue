<template>
  <div>

    <v-jsoneditor v-model="json" style="width: 800px; height: 300px">
    </v-jsoneditor>
    <div>
      <button v-on:click="doExport()">Export</button>
      <button v-on:click="doImport">Import</button>
    </div>
    <div class="exph-report">
      <table border="1">
        <tr>
          <td colspan="2">
          <span v-html="status">
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
            To moved folder:
          </td>
        </tr>
        <tr>
          <td>
            To move points: {{Object.keys(toMovePoints.xidPointsMoveFromTo).length}}
          </td>
          <td>
            To moved points:
          </td>
        </tr>
        <tr>
          <td>
            To delete folder: {{toDeleteFolder.xidFolderToDelete.length}}
          </td>
          <td>
            To delete
          </td>
        </tr>
      </table>
    </div>


    <!-- TODO format moment -->

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



      <p>To delete folder: {{toDeleteFolder.xidFolderToDelete}}</p>
      <p>Timer: {{timer}}</p>



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
        constants:{
          // if new parentId is # then move to root
          ROOT:"_"
        },
        json: {},
        timer: {
          startImport: 0,
          endImport: 0,
          timeImport: 0
        },
        base: {
          status: "",
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
        toDeleteFolder: {
          xidFolderToDelete: []
        },
        toMovePoints: {
          xidMapPointsExist: {},
          xidMapPointsAfter: {},
          xidPointsMoveFromTo: {},
          xidPointsNotMove: {},
          xidPointMoved: []
        }
      };
    },
    methods: {
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
        this.base.xidFolderAfter = folders.slice();
        this.base.counterToParse = this.base.xidFolderAfter.length;

        if (folders == undefined) {

        } else {
          this.base.xidFolderToCheck = folders;
          this.check();
        }
      },
      check() {
        if (this.base.xidFolderToCheck.length == 0) {
          alert('this situation should not occur');
        } else {
          this.showStatus("Check folder:" + this.base.xidFolderToCheck[0].xidFolder);
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
            this.showStatus("Error:" + error);
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
        if (this.toCreate.xidFolderNotExists.length > 0) {
          this.showStatus("Check folder to create");
          for (var id in this.toCreate.xidFolderNotExists) {
            if (this.toCreate.xidFolderNotExists[id] != null) {
              this.showStatus("Validate to create:" + this.toCreate.xidFolderNotExists[id].xidFolder);
              if (this.toCreate.xidFolderNotExists[id].xidFolder == undefined || this.toCreate.xidFolderNotExists[id].name == undefined) {
                this.base.xidErrors.push(this.toCreate.xidFolderToCreate[id])
              } else {
                // is ok
                this.toCreate.xidFolderToCreate.push(this.toCreate.xidFolderNotExists[id]);
              }
            }
          }
        }
        this.prepareDataToMoveFolders();
      },
      prepareDataToMoveFolders() {

        this.showStatus("Check in exist Folders parentId then different then to move");
        for (var i in this.base.xidFolderExists) {
          for (var j in this.base.xidFolderBefore) {
            if (this.base.xidFolderExists[i].xidFolder == this.base.xidFolderBefore[j].xid) {
              this.showStatus("The same xid:" + this.base.xidFolderExists[i].xidFolder);

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
        this.showStatus("Check points to move");

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
            let pointMoveFromToFolder = {xidPoint: xidPointToCheckAfter.xidPoint, oldParent: '?', newParent: xidPointToCheckAfter.xidFolder}
            this.toMovePoints.xidPointsMoveFromTo[xidPointToCheckAfter.xidPoint] = pointMoveFromToFolder;
          }
        }

        for (var p in this.toMovePoints.xidMapPointsExist) {

          let xidPointToCheckAfter = this.toMovePoints.xidMapPointsAfter[p];
          let xidPointToCheckExist = this.toMovePoints.xidMapPointsExist[p];
          // check is in After if not move to root
          if (xidPointToCheckAfter == undefined) {
            let pointMoveFromToRoot = {xidPoint: xidPointToCheckExist.xidPoint, oldParent: '?', newParent: this.constants.ROOT}
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
        this.showStatus("Check folder to delete");
        for (let z in this.base.xidFolderAfter) {
          if (this.base.xidFolderAfter[z].toDelete != undefined && this.base.xidFolderAfter[z].toDelete == "true") {
            let xidFolderToDelete = this.base.xidFolderAfter[z];
            this.toDeleteFolder.xidFolderToDelete.push(xidFolderToDelete);
          }
        }
        this.execute();
      },
      execute() {
        this.createFolders();
      },
      createFolders() {
        this.showStatus("Create folders");
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
              let newCreated = {};
              newCreated = this.toCreate.xidFolderToCreate[0];
              this.xidFolderCreated.push(newCreated);
              this.toCreate.xidFolderToCreate.splice(0, 1);
              if (this.toCreate.xidFolderToCreate.length > 0) {
                this.createFolders();
              } else {
                this.moveFolders();
              }
            })
            .catch(error => {
              console.log(error);
              //this.base.xidErrors.push(this.toCreate.xidFolderToCreate[0]);
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
        this.showStatus("Move folders");
        if (this.toMoveFolder.xidFolderToMoveTo.length > 0) {
          const apiMoveFolder = `./api/pointHierarchy/folderMoveTo/${this.toMoveFolder.xidFolderToMoveTo[0].xidFolder}/${this.toMoveFolder.xidFolderToMoveTo[0].newParentXid}`;
          axios.put(apiMoveFolder)
            .then(response => {
              console.log(response);
              this.toMoveFolder.xidFolderMoved.push(this.toMoveFolder.xidFolderToMoveTo[0])
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
        setTimeout(()=>{
          this.showStatus("Move points");
          let arrPointToMove = Object.keys(this.toMovePoints.xidPointsMoveFromTo).map(key => this.toMovePoints.xidPointsMoveFromTo[key]);
          if (arrPointToMove.length > 0) {
            let newParent = this.constants.ROOT;
            if (!arrPointToMove[0].newParent.trim() == "") {
              newParent = arrPointToMove[0].newParent;
            }
            const apiMovePoints = `./api/pointHierarchy/pointMoveTo/${arrPointToMove[0].xidPoint}/${newParent}`;
            console.log(apiMovePoints);
            axios.put(apiMovePoints)
              .then(response => {
                console.log(response);
                this.toMovePoints.xidPointMoved.push(arrPointToMove[0]);
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
        },1000);
      },
      deleteFolders() {
        this.showStatus("Change name");
        this.timer.stopImport = performance.now();
        this.timer.timeImport = this.timer.stopImport - this.timer.startImport;
        this.refreshCache();
      },
      refreshCache() {
        const apiRefreshCache = `./api/pointHierarchy/cacheRefresh/`;

        axios.post(apiRefreshCache)
          .then(response => {
            console.log(response);
            this.showStatus("<b>End Import</b>");
          })
          .catch(error => {
            console.log(error);
            this.showStatus("<b>End Import</b>");
          });
        this.showStatus("<b>End Import</b>");
      }
    },
  }
</script>

<style>

  exph-report {
    margin: 10px;
  }

</style>
