/* eslint-disable */
<template>
	<div>
		<div style="background-color: white; padding: 2em">
			<v-jsoneditor v-model="json" :options="options" :plus="true" style="height: 200px">
			</v-jsoneditor>
			<br />
			<button v-on:click="doExport()" type="button" class="btn btn-primary">
				Export
			</button>
			<button
				v-on:click="doImport()"
				color="warning"
				type="button"
				class="btn btn-warning"
			>
				Import
			</button>

			<h4 class="progress-label">
				{{ base.status }}
				<span v-if="base.canceled" class="badge badge-danger">CANCELED</span>
			</h4>
			<h5 class="progress-label">&nbsp;{{ base.statusGroup }}</h5>
			<div class="progress">
				<div
					id="group-progress"
					class="progress-bar progress-bar-striped"
					v-bind:class="{ active: base.progressImport.isActive }"
					role="progressbar"
					v-bind:style="{ width: base.progressImport.group.percent + '%' }"
				>
					<span v-if="base.canceled">canceled</span>
					<span v-else>{{ base.progressImport.group.percent }}%</span>
				</div>
			</div>
			<h5 class="progress-label">{{ base.statusDetail }}</h5>
			<!--
      <div class="progress">
        <div id="detail-progress" class="progress-bar progress-bar-striped"
             v-bind:class="{ active: base.progressImport.isActive }"
             role="progressbar"
             v-bind:style="{ width: base.progressImport.detail.percent + '%'}">
          <span v-if="base.canceled">canceled</span>
          <span v-else>{{base.progressImport.detail.percent}}%</span>
        </div>
      </div>
      -->

			<div class="table-responsive">
				<table class="table table-striped table-bordered">
					<tr>
						<td colspan="2">
							<span v-html="base.status" />
						</td>
					</tr>
					<tr>
						<td colspan="2">Import time: {{ timer.timeImport | moment }}</td>
					</tr>
					<tr>
						<td colspan="2">To be imported: {{ base.counterToParse }}</td>
					</tr>
					<tr>
						<td>To be created: {{ toCreate.xidFolderToCreate.length }}</td>
						<td>Created: {{ toCreate.xidFolderCreated.length }}</td>
					</tr>
					<tr>
						<td>Folders to be moved: {{ toMoveFolder.xidFolderToMoveTo.length }}</td>
						<td>Moved folders: {{ toMoveFolder.xidFolderMoved.length }}</td>
					</tr>
					<tr>
						<td>
							Points to be moved:
							{{ Object.keys(toMovePoints.xidPointsMoveFromTo).length }}
						</td>
						<td>Moved points: {{ toMovePoints.xidPointMoved.length }}</td>
					</tr>
					<tr>
						<td>
							Folder to be deleted:
							{{ toDeleteFolder.xidFolderToDelete.length }}
						</td>
						<td>Deleted: {{ toDeleteFolder.xidFolderDeleted.length }}</td>
					</tr>
					<tr>
						<td>
							Folder names to be changed:
							{{ toChangeNameFolder.xidFolderToNameChange.length }}
						</td>
						<td>
							Names changed:
							{{ toChangeNameFolder.xidFolderNameChanged.length }}
						</td>
					</tr>
					<tr>
						<td colspan="2">
							Errors: {{ base.xidErrors.length }}
							<span v-if="base.xidErrors.length > 0" class="badge badge-danger"
								>Check console</span
							>
						</td>
					</tr>
				</table>
			</div>
			<button
				v-on:click="doCancel()"
				color="danger"
				type="button"
				class="btn btn-danger btn-sm"
			>
				Cancel
			</button>
		</div>
	</div>
	<!-- debug


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

  <p>Created {{toCreate.xidFolderCreated}}</p>
  <p>Moved folder {{toMoveFolder.xidFolderMoved}}</p>
  <p>Moved point {{toMovePoints.xidPointMoved}}</p>

  <p>Point in folder create {{toMovePoints.xidMapPointsInCreateFolder}}</p>
  -->
</template>

<script>
import VJsoneditor from 'vue-jsoneditor';
import axios from 'axios';
import moment from 'moment';
import Ajv from 'ajv';

export default {
	name: 'export-import-point-hierarchy',
	components: {
		VJsoneditor,
	},
	filters: {
		moment: function (date) {
			return moment(date).format(' mm:ss');
		},
	},
	data() {
		return {
			options: {
				mode: 'text',
				modes: [],
				/*templates: [
            {
              text: 'Folder',
              title: 'Insert a Folder Node',
              className: 'jsoneditor-type-object',
              field: 'FolderTemplate',
              value: {
                'parentXid': 'null',
                'name': 'new name folder',
                'xidFolder': 'new xid folder',
                'pointXids': [],
                'toDelete': "false"
              }
            }]*/
			},
			constants: {
				SCHEMA: {
					folders: {
						type: 'array',
						items: {
							type: 'object',
							properties: {
								parentXid: { type: 'string' },
								name: { type: 'string' },
								xidFolder: { type: 'string' },
								pointXids: [{ type: 'string' }],
								toDelete: { enum: ['true', 'false'] },
							},
							required: ['parentXid', 'name', 'xidFolder', 'pointXids'],
						},
					},
					required: ['folders'],
				},
				// if new parentId is # then move to root
				ROOT: '_',
				STATUS: 0, // run - green, warning - yellow, error - red
				STATUS_INFO: {
					RUN: 0,
					WARNING: 1,
					ERROR: 2,
					END: 3,
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
					REFRESH_CACHE: 13,
				},
				STATUS_DETAIL: 2,
				STATUS_DETAIL_INFO: {
					//
				},
			},
			json: {},
			timer: {
				startImport: 0,
				endImport: 0,
				timeImport: 0,
			},
			base: {
				progressImport: {
					group: {
						percent: 0,
						elementToDo: 13,
						elementDoing: 1,
					},
					detail: {
						percent: 0,
						elementToDo: 0,
						elementDoing: 1,
					},
					isActive: false,
				},
				canceled: false,
				statusGroup: '',
				statusDetail: '',
				counterToParse: 0,
				xidFolderAfter: [],
				xidFolderBefore: [],
				xidFolderExists: [],
				xidErrors: [],
				xidFolderToCheck: [],
			},
			toCreate: {
				xidFolderNotExists: [],
				xidFolderToCreate: [],
				xidFolderCreated: [],
			},
			toMoveFolder: {
				xidFolderToMoveTo: [],
				xidFolderMoved: [],
			},
			toMovePoints: {
				xidMapPointsExist: {},
				xidMapPointsAfter: {},
				xidMapPointsInCreateFolder: {},
				xidPointsMoveFromTo: {},
				xidPointsNotMove: {},
				xidPointMoved: [],
			},
			toDeleteFolder: {
				xidFolderToDelete: [],
				xidFolderDeleted: [],
			},
			toChangeNameFolder: {
				xidFolderToNameChange: [],
				xidFolderNameChanged: [],
			},
		};
	},
	methods: {
		doExport() {
			const apiExportXidFolder = `./api/pointHierarchy/export`;
			axios
				.get(apiExportXidFolder)
				.then((response) => {
					this.json = response.data;
				})
				.catch((error) => {
					console.log(error);
				});
		},
		doImport() {
			this.clearData();
			this.timer.startImport = performance.now();
			this.parse();
		},
		doCancel() {
			this.base.canceled = true;
		},
		clearData() {
			//Cleansing of data from previous imports
			this.timer.startImport = 0;
			this.timer.endImport = 0;
			this.timer.timeImport = 0;

			this.base.statusGroup = '';
			this.base.statusDetail = '';
			this.base.counterToParse = 0;
			this.base.xidFolderAfter = [];
			this.base.xidFolderBefore = [];
			this.base.xidFolderExists = [];
			this.base.xidErrors = [];
			this.base.canceled = false;
			this.base.xidFolderToCheck = [];

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
		showStatus(status, infoStatus, str, detailsElementsDoing, detailsElementsToDo) {
			if (status == this.constants.STATUS) {
				this.base.status = str;
				if (
					infoStatus == this.constants.STATUS_INFO.WARNING ||
					infoStatus == this.constants.STATUS_INFO.ERROR ||
					infoStatus == this.constants.STATUS_INFO.END
				) {
					console.log('STATUS INFO:' + infoStatus);

					this.base.progressImport.isActive = false;
					this.base.progressImport.detail.percent = 0;
					this.base.statusDetail = '';
					this.base.statusGroup = '';
				} else if (infoStatus == this.constants.STATUS_INFO.RUN) {
					this.base.progressImport.isActive = true;
					this.base.progressImport.detail.percent = 0;
				}
			} else if (status == this.constants.STATUS_GROUP) {
				this.base.statusGroup = str;
				this.calculateProgressGroup(infoStatus);
				this.base.progressImport.detail.percent = 0;
			} else if (status == this.constants.STATUS_DETAIL) {
				this.calculateProgressDetail(detailsElementsDoing, detailsElementsToDo);
				this.base.statusDetail = str;
			}
			this.timer.stopImport = performance.now();
			this.timer.timeImport = this.timer.stopImport - this.timer.startImport;
		},
		parse() {
			this.showStatus(this.constants.STATUS, this.constants.STATUS_INFO.RUN, 'Start');
			this.showStatus(
				this.constants.STATUS_GROUP,
				this.constants.STATUS_GROUP_INFO.PARSE,
				'Validate',
			);

			let ajv = new Ajv();
			let validate = ajv.compile(this.constants.SCHEMA);
			let valid = validate(this.json);
			if (!valid) {
				console.log(validate.errors);
				this.showStatus(
					this.constants.STATUS,
					this.constants.STATUS_INFO.ERROR,
					validate.errors,
				);
			} else {
				try {
					let folders = this.json.folders;
					this.base.xidFolderAfter = folders.slice();
					this.base.counterToParse = this.base.xidFolderAfter.length;
					if (folders == undefined) {
						this.showStatus(
							this.constants.STATUS,
							this.constants.STATUS_INFO.WARNING,
							'Folders information required',
						);
					} else {
						this.base.xidFolderToCheck = folders;
						this.check();
					}
				} catch (err) {
					this.showStatus(
						this.constants.STATUS,
						this.constants.STATUS_INFO.ERROR,
						'Incorrect date format:' + err,
					);
				}
			}
		},
		check() {
			if (this.canceled()) return;

			this.showStatus(
				this.constants.STATUS_GROUP,
				this.constants.STATUS_GROUP_INFO.CHECK,
				'Check folders',
			);
			if (this.base.xidFolderToCheck.length == 0) {
				this.showStatus(
					this.constants.STATUS,
					this.constants.STATUS_INFO.WARNING,
					'Nothing to import',
				);
			} else {
				this.showStatus(
					this.constants.STATUS_DETAIL,
					0,
					'Check folder:' + this.base.xidFolderToCheck[0].xidFolder,
					this.base.xidFolderExists.length + this.toCreate.xidFolderNotExists.length,
					this.base.xidFolderToCheck.length +
						this.base.xidFolderExists.length +
						this.toCreate.xidFolderNotExists.length,
				);
				const apiCheckXidFolder = `./api/pointHierarchy/folderCheckExist/${this.base.xidFolderToCheck[0].xidFolder}`;
				axios
					.get(apiCheckXidFolder)
					.then((response) => {
						if (response.data !== undefined) {
							console.log('FOLDER exist:' + this.base.xidFolderToCheck[0]);
							this.base.xidFolderExists.push(this.base.xidFolderToCheck[0]);
							this.base.xidFolderBefore.push(response.data);
						} else if (
							this.base.xidFolderToCheck[0] != undefined &&
							response.data == false
						) {
							console.log('FOLDER not exist:' + this.base.xidFolderToCheck[0]);
							this.toCreate.xidFolderNotExists.push(this.base.xidFolderToCheck[0]);
						}
						this.base.xidFolderToCheck.splice(0, 1);
						if (this.base.xidFolderToCheck.length > 0) {
							this.check();
						} else {
							this.validToCreate();
						}
					})
					.catch((error) => {
						console.log('FOLDER not exist:' + this.base.xidFolderToCheck[0]);
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
			if (this.canceled()) return;
			this.showStatus(
				this.constants.STATUS_GROUP,
				this.constants.STATUS_GROUP_INFO.CHECK_TO_CREATE,
				'Check folders to be created',
			);
			//console.log("SIZE FOLDERS NOT EXISTS:"+this.toCreate.xidFolderNotExists.length);
			if (this.toCreate.xidFolderNotExists.length > 0) {
				for (var id in this.toCreate.xidFolderNotExists) {
					if (this.toCreate.xidFolderNotExists[id] != null) {
						this.showStatus(
							this.constants.STATUS_DETAIL,
							0,
							'Validation folder to be created:' +
								this.toCreate.xidFolderNotExists[id].xidFolder,
							this.toCreate.xidFolderToCreate.length,
							this.toCreate.xidFolderNotExists.length,
						);

						const validate =
							this.toCreate.xidFolderNotExists[id].xidFolder == undefined ||
							this.toCreate.xidFolderNotExists[id].name == undefined ||
							this.toCreate.xidFolderNotExists[id].toDelete == true ||
							this.toCreate.xidFolderNotExists[id].toDelete == 'true';

						if (validate) {
							this.base.xidErrors.push(this.toCreate.xidFolderNotExists[id]);
						} else {
							this.toCreate.xidFolderToCreate.push(this.toCreate.xidFolderNotExists[id]);
						}
					}
				}
			}
			this.prepareDataToMoveFolders();
		},
		prepareDataToMoveFolders() {
			if (this.canceled()) return;
			this.showStatus(
				this.constants.STATUS_GROUP,
				this.constants.STATUS_GROUP_INFO.CHECK_TO_MOVE_FOLDER,
				'Check to-be-moved folders',
			);
			for (var i in this.base.xidFolderExists) {
				for (var j in this.base.xidFolderBefore) {
					if (
						this.base.xidFolderExists[i].xidFolder == this.base.xidFolderBefore[j].xid
					) {
						this.showStatus(
							this.constants.STATUS_DETAIL,
							0,
							'xid:' + this.base.xidFolderExists[i].xidFolder,
							i + j,
							this.base.xidFolderExists.length + this.base.xidFolderBefore.length,
						);
						if (
							this.base.xidFolderExists[i].parentXid !==
							this.base.xidFolderBefore[j].parentXid
						) {
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
			if (this.canceled()) return;
			this.showStatus(
				this.constants.STATUS_GROUP,
				this.constants.STATUS_GROUP_INFO.CHECK_TO_MOVE_POINTS,
				'Check points to be moved',
			);

			for (var v in this.toCreate.xidFolderToCreate) {
				for (var w in this.toCreate.xidFolderToCreate[v].pointXids) {
					this.toMovePoints.xidMapPointsInCreateFolder[
						this.toCreate.xidFolderToCreate[v].pointXids[w]
					] = {
						xidPoint: this.toCreate.xidFolderToCreate[v].pointXids[w],
						xidFolder: this.toCreate.xidFolderToCreate[v].xidFolder,
					};
				}
			}

			for (var i in this.base.xidFolderBefore) {
				for (var j in this.base.xidFolderBefore[i].pointXids) {
					this.toMovePoints.xidMapPointsExist[
						this.base.xidFolderBefore[i].pointXids[j]
					] = {
						xidPoint: this.base.xidFolderBefore[i].pointXids[j],
						xidFolder: this.base.xidFolderBefore[i].xid,
					};
				}
			}

			for (var a in this.base.xidFolderAfter) {
				for (var b in this.base.xidFolderAfter[a].pointXids) {
					this.toMovePoints.xidMapPointsAfter[
						this.base.xidFolderAfter[a].pointXids[b]
					] = {
						xidPoint: this.base.xidFolderAfter[a].pointXids[b],
						xidFolder: this.base.xidFolderAfter[a].xidFolder,
						delete: this.base.xidFolderAfter[a].delete,
					};
				}
			}

			for (var p in this.toMovePoints.xidMapPointsAfter) {
				let xidPointToCheckAfter = this.toMovePoints.xidMapPointsAfter[p];
				let xidPointToCheckExist = this.toMovePoints.xidMapPointsExist[p];
				let xidPointInCreateFolder = this.toMovePoints.xidMapPointsInCreateFolder[p];

				if (xidPointInCreateFolder == undefined) {
					if (xidPointToCheckExist == undefined) {
						let pointMoveFromToFolder = {
							xidPoint: xidPointToCheckAfter.xidPoint,
							oldParent: '?',
							newParent: xidPointToCheckAfter.xidFolder,
						};
						this.toMovePoints.xidPointsMoveFromTo[
							xidPointToCheckAfter.xidPoint
						] = pointMoveFromToFolder;
					}
				}
			}

			for (var p in this.toMovePoints.xidMapPointsExist) {
				let xidPointToCheckAfter = this.toMovePoints.xidMapPointsAfter[p];
				let xidPointToCheckExist = this.toMovePoints.xidMapPointsExist[p];
				let xidPointInCreateFolder = this.toMovePoints.xidMapPointsInCreateFolder[p];

				if (xidPointInCreateFolder == undefined) {
					if (xidPointToCheckAfter == undefined) {
						let pointMoveFromToRoot = {
							xidPoint: xidPointToCheckExist.xidPoint,
							oldParent: '?',
							newParent: this.constants.ROOT,
						};
						this.toMovePoints.xidPointsMoveFromTo[
							xidPointToCheckExist.xidPoint
						] = pointMoveFromToRoot;
						// check is in After has another xidFolder then move point to new Folder
					} else if (xidPointToCheckAfter.xidFolder !== xidPointToCheckExist.xidFolder) {
						let pointMoveFromToAnother = {
							xidPoint: xidPointToCheckExist.xidPoint,
							oldParent: xidPointToCheckExist.xidFolder,
							newParent: xidPointToCheckAfter.xidFolder,
						};
						this.toMovePoints.xidPointsMoveFromTo[
							xidPointToCheckExist.xidPoint
						] = pointMoveFromToAnother;
						// not move
					} else if (xidPointToCheckAfter.xidFolder == xidPointToCheckExist.xidFolder) {
						this.toMovePoints.xidPointsNotMove[xidPointToCheckExist.xidPoint] ==
							xidPointToCheckAfter;
					}
				}
			}
			this.prepareDataToDeleteFolders();
		},
		prepareDataToDeleteFolders() {
			if (this.canceled()) return;
			this.showStatus(
				this.constants.STATUS_GROUP,
				this.constants.STATUS_GROUP_INFO.CHECK_FOLDERS_TO_DELETE,
				'Check folders to be deleted',
			);

			for (let z in this.base.xidFolderAfter) {
				if (
					this.base.xidFolderAfter[z].toDelete != undefined &&
					this.base.xidFolderAfter[z].toDelete == 'true'
				) {
					let xidFolderToDelete = this.base.xidFolderAfter[z];
					let isNotInError = false;
					for (let g in this.base.xidErrors) {
						isNotInError = !(this.base.xidErrors[g].xidFolder == xidFolderToDelete);
						if (isNotInError) break;
					}
					if (!isNotInError) {
						this.toDeleteFolder.xidFolderToDelete.push(xidFolderToDelete);
					}
				}
			}
			this.prepareFolderToNameChange();
		},
		prepareFolderToNameChange() {
			if (this.canceled()) return;
			this.showStatus(
				this.constants.STATUS_GROUP,
				this.constants.STATUS_GROUP_INFO.CHECK_TO_CHANGE_FOLDER_NAME,
				'Check folders to be updated',
			);
			for (var i in this.base.xidFolderAfter) {
				for (var j in this.base.xidFolderBefore) {
					if (this.base.xidFolderAfter[i].xidFolder == this.base.xidFolderBefore[j].xid) {
						if (this.base.xidFolderAfter[i].name != this.base.xidFolderBefore[j].name) {
							this.toChangeNameFolder.xidFolderToNameChange.push(
								this.base.xidFolderAfter[i],
							);
						}
					}
				}
			}
			this.createFolders();
		},
		createFolders() {
			if (this.canceled()) return;
			this.showStatus(
				this.constants.STATUS_GROUP,
				this.constants.STATUS_GROUP_INFO.CREATE_FOLDERS,
				'Create folders',
			);
			if (this.toCreate.xidFolderToCreate.length > 0) {
				const data = {};
				data.name = this.toCreate.xidFolderToCreate[0].name;
				data.xid = this.toCreate.xidFolderToCreate[0].xidFolder;
				data.parentXid = this.toCreate.xidFolderToCreate[0].parentXid;
				data.pointXids = this.toCreate.xidFolderToCreate[0].pointXids;

				this.showStatus(
					this.constants.STATUS_DETAIL,
					0,
					'xid:' + this.toCreate.xidFolderToCreate[0].xidFolder,
					this.toCreate.xidFolderCreated.length,
					this.toCreate.xidFolderToCreate.length + this.toCreate.xidFolderCreated.length,
				);

				const apiAddFolder = `./api/pointHierarchy/folderAdd/`;

				axios
					.post(apiAddFolder, data)
					.then((response) => {
						let newCreated = this.toCreate.xidFolderToCreate[0];
						this.toCreate.xidFolderCreated.push(newCreated);
						this.toCreate.xidFolderToCreate.splice(0, 1);
						if (this.toCreate.xidFolderToCreate.length > 0) {
							this.createFolders();
						} else {
							this.moveFolders();
						}
					})
					.catch((error) => {
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
			if (this.canceled()) return;
			this.showStatus(
				this.constants.STATUS_GROUP,
				this.constants.STATUS_GROUP_INFO.MOVE_FOLDERS,
				'Move folders',
			);
			if (this.toMoveFolder.xidFolderToMoveTo.length > 0) {
				this.showStatus(
					this.constants.STATUS_DETAIL,
					0,
					'xid:' + this.toMoveFolder.xidFolderToMoveTo[0].xidFolder,
					this.toMoveFolder.xidFolderMoved.length,
					this.toMoveFolder.xidFolderToMoveTo.length +
						this.toMoveFolder.xidFolderMoved.length,
				);

				const apiMoveFolder = `./api/pointHierarchy/folderMoveTo/${this.toMoveFolder.xidFolderToMoveTo[0].xidFolder}/${this.toMoveFolder.xidFolderToMoveTo[0].newParentXid}`;
				axios
					.put(apiMoveFolder)
					.then((response) => {
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
					.catch((error) => {
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
			if (this.canceled()) return;
			// setTimeout(() => {
			this.showStatus(
				this.constants.STATUS_GROUP,
				this.constants.STATUS_GROUP_INFO.MOVE_POINTS,
				'Move points',
			);
			let arrPointToMove = Object.keys(this.toMovePoints.xidPointsMoveFromTo).map(
				(key) => this.toMovePoints.xidPointsMoveFromTo[key],
			);
			if (arrPointToMove.length > 0) {
				let newParent = this.constants.ROOT;
				if (!arrPointToMove[0].newParent.trim() == '') {
					newParent = arrPointToMove[0].newParent;
				}

				this.showStatus(
					this.constants.STATUS_DETAIL,
					0,
					'xid:' + arrPointToMove[0].xidPoint,
					this.toMovePoints.xidPointMoved.length,
					this.toMovePoints.xidPointsMoveFromTo.length +
						this.toMovePoints.xidPointMoved.length,
				);

				const apiMovePoints = `./api/pointHierarchy/pointMoveTo/${arrPointToMove[0].xidPoint}/${newParent}`;
				axios
					.put(apiMovePoints)
					.then((response) => {
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
					.catch((error) => {
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
			if (this.canceled()) return;
			this.showStatus(
				this.constants.STATUS_GROUP,
				this.constants.STATUS_GROUP_INFO.DELETE_FOLDERS,
				'Delete folders',
			);
			if (this.toDeleteFolder.xidFolderToDelete.length > 0) {
				this.showStatus(
					this.constants.STATUS_DETAIL,
					0,
					'xid:' + this.toDeleteFolder.xidFolderToDelete[0].xidFolder,
					this.toDeleteFolder.xidFolderDeleted.length,
					this.toDeleteFolder.xidFolderToDelete.length +
						this.toDeleteFolder.xidFolderDeleted.length,
				);

				const apiDeleteFolders = `./api/pointHierarchy/deleteFolder/${this.toDeleteFolder.xidFolderToDelete[0].xidFolder}`;
				axios
					.post(apiDeleteFolders)
					.then((response) => {
						let deleted = this.toDeleteFolder.xidFolderToDelete[0];
						this.toDeleteFolder.xidFolderDeleted.push(deleted);
						this.toDeleteFolder.xidFolderToDelete.splice(0, 1);
						if (this.toDeleteFolder.xidFolderToDelete.length > 0) {
							this.deleteFolders();
						} else {
							this.changeNameFolders();
						}
					})
					.catch((error) => {
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
			if (this.canceled()) return;
			this.showStatus(
				this.constants.STATUS_GROUP,
				this.constants.STATUS_GROUP_INFO.CHANGE_NAME_FOLDERS,
				"Change folders' names",
			);

			if (this.toChangeNameFolder.xidFolderToNameChange.length > 0) {
				this.showStatus(
					this.constants.STATUS_DETAIL,
					0,
					'xid:' + this.toChangeNameFolder.xidFolderToNameChange[0].xidFolder,
					this.toChangeNameFolder.xidFolderNameChanged.length,
					this.toChangeNameFolder.xidFolderToNameChange.length +
						this.toChangeNameFolder.xidFolderNameChanged.length,
				);

				const apiUpdateFolder = `./api/pointHierarchy/changeName/${this.toChangeNameFolder.xidFolderToNameChange[0].xidFolder}/${this.toChangeNameFolder.xidFolderToNameChange[0].name}`;
				axios
					.put(apiUpdateFolder)
					.then((response) => {
						let change = this.toChangeNameFolder.xidFolderToNameChange[0];
						this.toChangeNameFolder.xidFolderNameChanged.push(change);
						this.toChangeNameFolder.xidFolderToNameChange.splice(0, 1);
						if (this.toChangeNameFolder.xidFolderToNameChange.length > 0) {
							this.changeNameFolders();
						} else {
							this.createFolderAndMovePoints();
						}
					})
					.catch((error) => {
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
				'Refresh cache',
			);

			const apiRefreshCache = `./api/pointHierarchy/cacheRefresh/`;
			axios
				.post(apiRefreshCache)
				.then((response) => {
					this.timer.stopImport = performance.now();
					this.timer.timeImport = this.timer.stopImport - this.timer.startImport;
					reload();
				})
				.catch((error) => {
					console.log(error);
					this.showStatus(
						this.constants.STATUS,
						this.constants.STATUS_INFO.ERROR,
						'End import',
					);
					reload();
				});

			this.showStatus(
				this.constants.STATUS,
				this.constants.STATUS_INFO.END,
				'End import',
			);
		},
		calculateProgressGroup(elementsDoing) {
			this.base.progressImport.group.elementDoing = elementsDoing;
			this.base.progressImport.group.percent = (
				(this.base.progressImport.group.elementDoing /
					this.base.progressImport.group.elementToDo) *
				100
			).toFixed(0);
		},
		calculateProgressDetail(elementsDoing, elementsToDo) {
			this.base.progressImport.detail.elementDoing = elementsDoing;
			this.base.progressImport.detail.elementToDo = elementsToDo;
			if (elementsDoing == undefined || elementsToDo == undefined) {
				this.base.progressImport.detail.percent = 100;
			} else if (elementsToDo == 0) {
				this.base.progressImport.detail.percent = 100;
			}
			if (elementsDoing == 0) {
				this.base.progressImport.detail.percent = 0;
			} else {
				this.base.progressImport.detail.percent = (
					(this.base.progressImport.detail.elementDoing /
						this.base.progressImport.detail.elementToDo) *
					100
				).toFixed(0);
			}
			if (isNaN(this.base.progressImport.detail.percent)) {
				this.base.progressImport.detail.percent = 100;
			}
		},
		canceled() {
			if (this.base.canceled) {
				this.refreshCache();
			}
			return this.base.canceled;
			reload();
		},
	},
};
</script>

<style></style>
