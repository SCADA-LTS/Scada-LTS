/* eslint-disable no-console */
<template>

	<!--<v-app>-->
	<div>
		<div class="cmp-flex-container">
			<div id="cmp">
				<div class="cmp-flex-container">
					<div class="cmp-flex-label">
						<div>{{ label }}</div>
						<svg v-if="processOfCheckingTheStatus" width="20" height="10">
							<circle cx="10" cy="5" r="2" fill="red" />
						</svg>
					</div>
					<div class="cmp-flex-state">{{ insideState }}</div>
					<btn v-if="!disabledChange" class="cmp-flex-button" @click="show = !show">
						<svg
							xmlns="http://www.w3.org/2000/svg"
							xmlns:xlink="http://www.w3.org/1999/xlink"
							version="1.1"
							id="Capa_1"
							x="0px"
							y="0px"
							width="17px"
							height="17px"
							viewBox="0 0 900 900"
							style="enable-background: new 0 0 932.179 932.179"
							xml:space="preserve"
						>
							<g>
								<path
									d="M61.2,341.538c4.9,16.8,11.7,33,20.3,48.2l-24.5,30.9c-8,10.1-7.1,24.5,1.9,33.6l42.2,42.2c9.1,9.1,23.5,9.899,33.6,1.899   l30.7-24.3c15.8,9.101,32.6,16.2,50.1,21.2l4.6,39.5c1.5,12.8,12.3,22.4,25.1,22.4h59.7c12.8,0,23.6-9.601,25.1-22.4l4.4-38.1   c18.8-4.9,36.8-12.2,53.7-21.7l29.7,23.5c10.1,8,24.5,7.1,33.6-1.9l42.2-42.2c9.1-9.1,9.9-23.5,1.9-33.6l-23.1-29.3   c9.6-16.601,17.1-34.3,22.1-52.8l35.6-4.1c12.801-1.5,22.4-12.3,22.4-25.1v-59.7c0-12.8-9.6-23.6-22.4-25.1l-35.1-4.1   c-4.801-18.3-12-35.8-21.199-52.2l21.6-27.3c8-10.1,7.1-24.5-1.9-33.6l-42.1-42.1c-9.1-9.1-23.5-9.9-33.6-1.9l-26.5,21   c-17.2-10.1-35.601-17.8-54.9-23l-4-34.3c-1.5-12.8-12.3-22.4-25.1-22.4h-59.7c-12.8,0-23.6,9.6-25.1,22.4l-4,34.3   c-19.8,5.3-38.7,13.3-56.3,23.8l-27.5-21.8c-10.1-8-24.5-7.1-33.6,1.9l-42.2,42.2c-9.1,9.1-9.9,23.5-1.9,33.6l23,29.1   c-9.2,16.6-16.2,34.3-20.8,52.7l-36.8,4.2c-12.8,1.5-22.4,12.3-22.4,25.1v59.7c0,12.8,9.6,23.6,22.4,25.1L61.2,341.538z    M277.5,180.038c54.4,0,98.7,44.3,98.7,98.7s-44.3,98.7-98.7,98.7c-54.399,0-98.7-44.3-98.7-98.7S223.1,180.038,277.5,180.038z"
									fill="#FFFFFF"
								/>
								<path
									d="M867.699,356.238l-31.5-26.6c-9.699-8.2-24-7.8-33.199,0.9l-17.4,16.3c-14.699-7.1-30.299-12.1-46.4-15l-4.898-24   c-2.5-12.4-14-21-26.602-20l-41.1,3.5c-12.6,1.1-22.5,11.4-22.9,24.1l-0.799,24.4c-15.801,5.7-30.701,13.5-44.301,23.3   l-20.799-13.8c-10.602-7-24.701-5-32.9,4.7l-26.6,31.7c-8.201,9.7-7.801,24,0.898,33.2l18.201,19.399   c-6.301,14.2-10.801,29.101-13.4,44.4l-26,5.3c-12.4,2.5-21,14-20,26.601l3.5,41.1c1.1,12.6,11.4,22.5,24.1,22.9l28.1,0.899   c5.102,13.4,11.801,26.101,19.9,38l-15.699,23.7c-7,10.6-5,24.7,4.699,32.9l31.5,26.6c9.701,8.2,24,7.8,33.201-0.9l20.6-19.3   c13.5,6.3,27.699,11,42.299,13.8l5.701,28.2c2.5,12.4,14,21,26.6,20l41.1-3.5c12.6-1.1,22.5-11.399,22.9-24.1l0.9-27.601   c15-5.3,29.199-12.5,42.299-21.399l22.701,15c10.6,7,24.699,5,32.9-4.7l26.6-31.5c8.199-9.7,7.799-24-0.9-33.2l-18.301-19.399   c6.701-14.2,11.602-29.2,14.4-44.601l25-5.1c12.4-2.5,21-14,20-26.601l-3.5-41.1c-1.1-12.6-11.4-22.5-24.1-22.9l-25.1-0.8   c-5.201-14.6-12.201-28.399-20.9-41.2l13.699-20.6C879.4,378.638,877.4,364.438,867.699,356.238z M712.801,593.837   c-44.4,3.801-83.602-29.3-87.301-73.699c-3.801-44.4,29.301-83.601,73.699-87.301c44.4-3.8,83.602,29.301,87.301,73.7   C790.301,550.938,757.199,590.138,712.801,593.837z"
									fill="#FFFFFF"
								/>
								<path
									d="M205,704.438c-12.6,1.3-22.3,11.899-22.4,24.6l-0.3,25.3c-0.2,12.7,9.2,23.5,21.8,25.101l18.6,2.399   c3.1,11.301,7.5,22.101,13.2,32.301l-12,14.8c-8,9.899-7.4,24.1,1.5,33.2l17.7,18.1c8.9,9.1,23.1,10.1,33.2,2.3l14.899-11.5   c10.5,6.2,21.601,11.101,33.2,14.5l2,19.2c1.3,12.6,11.9,22.3,24.6,22.4l25.301,0.3c12.699,0.2,23.5-9.2,25.1-21.8l2.3-18.2   c12.601-3.101,24.601-7.8,36-14l14,11.3c9.9,8,24.101,7.4,33.201-1.5l18.1-17.7c9.1-8.899,10.1-23.1,2.301-33.2L496.6,818.438   c6.6-11,11.701-22.7,15.201-35l16.6-1.7c12.6-1.3,22.299-11.9,22.4-24.6l0.299-25.301c0.201-12.699-9.199-23.5-21.799-25.1   l-16.201-2.1c-3.1-12.2-7.699-24-13.699-35l10.1-12.4c8-9.9,7.4-24.1-1.5-33.2l-17.699-18.1c-8.9-9.101-23.102-10.101-33.201-2.3   l-12.101,9.3c-11.399-6.9-23.6-12.2-36.399-15.8l-1.601-15.7c-1.3-12.601-11.899-22.3-24.6-22.4l-25.3-0.3   c-12.7-0.2-23.5,9.2-25.101,21.8l-2,15.601c-13.199,3.399-25.899,8.6-37.699,15.399l-12.5-10.2c-9.9-8-24.101-7.399-33.201,1.5   l-18.2,17.801c-9.1,8.899-10.1,23.1-2.3,33.199l10.7,13.801c-6.2,11-11.1,22.699-14.3,35L205,704.438z M368.3,675.837   c36.3,0.4,65.399,30.301,65,66.601c-0.4,36.3-30.301,65.399-66.601,65c-36.3-0.4-65.399-30.3-65-66.601   C302.1,704.538,332,675.438,368.3,675.837z"
									fill="#FFFFFF"
								/>
							</g>
						</svg>
					</btn>
				</div>
			</div>
		</div>
		<div class="cmp-flex-container-columns">
			<collapse v-model="show">
				<div class="cmp-well">
					<btn-group>
						<btn v-on:click="close" class="close cmp_close" arial-label="Close">
							<span aria-hidden="true">&times;</span>
						</btn>
					</btn-group>
					<section>
						<btn-group>
							<div v-for="item in controlsLevel0" :key="item.xid">
								<btn
									block
									class="cmp-flex-container-columns"
									v-bind:class="[
										{
											cmp_selected:
												selectActionLevel0 == item.name &&
												!!item.runDirectlyBeforeShowSubMenu,
										},
										{
											cmp_selected_to_confirm:
												selectActionLevel0 == item.name &&
												!!!item.runDirectlyBeforeShowSubMenu,
										},
									]"
									v-on:click="setActionLeve0(item.name)"
								>
									{{ item.name }}
									<span v-if="item.runDirectlyBeforeShowSubMenu" class="cmp-small-info"
										>Don't need confirmation</span
									>
								</btn>
							</div>
						</btn-group>
						<btn-group v-if="controlsLevel1.length > 0">
							<div v-for="item in controlsLevel1" :key="item.xid">
								<btn
									block
									v-bind:class="{
										cmp_selected_to_confirm: selectActionLevel1 == item.name,
									}"
									v-on:click="setActionLevel1(item.name)"
									>{{ item.name }}
								</btn>
							</div>
						</btn-group>
						<btn-group>
							<btn
								v-if="checkIfThereAreSharesToConfirmValue"
								type="primary"
								v-on:click="tryChangeModePLC"
								>Confirm changes
							</btn>
						</btn-group>

						<hr />
						<!--<btn size="xs" type="primary" v-on:click="showFault" class="cmp-small-info" v-bind:class="{cmp_fault: showFaultV }">fault test</btn>-->
						<p class="cmp-small-info">v0.0.9</p>
						<btn size="xs" type="primary" v-on:click="refreshHistory()" class="cmp-small-info">refresh history</btn>
			
						<div>
							<HistoryCMP
								ref="refHistoryCMP"
								v-bind:pxIdViewAndIdCmp="xIdViewAndIdCmp"
							></HistoryCMP>
						</div>
					   
						
					</section>
				</div>
			</collapse>
			<collapse v-model="errorsNotification">
				<div class="cmp-well">
					<section>
						<div v-for="er in errors" :key="er.index">
							<alert type="danger"
								><b>{{ er }}</b></alert
							>
						</div>
					</section>
				</div>
			</collapse>
		</div>
	</div>
	<!--</v-app> -->
</template>

<script>
/* eslint-disable */

import moment from 'moment';
import httpClient from 'axios';
import { _ } from 'vue-underscore';
import BtnGroup from 'uiv/src/components/button/BtnGroup';
import HistoryCMP from './HistoryCMP';

/**
 *
 */
class ChangeDataDTO {
	constructor(xid, value, resultOperationSave, error) {
		this.xid = xid;
		this.value = value;
		this.resultOperationSave = resultOperationSave;
		this.error = error;
	}
}

/**
 * @author grzegorz.bylica@gmail.com
 */
class ApiCMP {
	get(xIds) {
		return new Promise((resolve, reject) => {
			try {
				const apiCMPCheck = `./api/cmp/get/${xIds}`;
				if (xIds.length > 0) {
					httpClient
						.get(apiCMPCheck, { timeout: 5000 })
						.then((response) => {
							resolve(response);
						})
						.catch((error) => {
							reject(error);
						});
				} else {
					const reason = new Error('Probably not have data');
					reject(reason);
				}
			} catch (e) {
				reject(e);
			}
		});
	}

	set(newData, xidViewAndIdCmp, interpretedState) {
		// console.log("interpetedState:"+JSON.stringify(interpretedState))
		// console.log("interpetedState:"+interpretedState)
		return new Promise((resolve, reject) => {
			try {
				if (newData.length > 0) {
					httpClient({
						method: 'post',
						url: `./api/cmp/set/${xidViewAndIdCmp}/${interpretedState}`,
						headers: {},
						timeout: 5000,
						data: newData,
					})
						.then((response) => {
							resolve(response);
						})
						.catch((error) => {
							reject(error);
						});
				} else {
					const reason = new Error('Probably not have data');
					reject(reason);
				}
			} catch (e) {
				reject(e);
			}
		});
	}
}

/**
 * @author grzegorz.bylica@gmail.com
 */
export default {
	components: {
		BtnGroup,
		HistoryCMP,
	},
	props: ['pConfig', 'pLabel', 'pTimeRefresh', 'pxIdViewAndIdCmp'],
	data() {
		return {
			open: false,
			show: false,
			errors: [],
			newErrors: [],
			errorResultingFromOperationControl: '',
			errorsNotification: false,
			strConfig: this.pConfig,
			config: {},
			insideState: 'NOT-CHECKED',
			label: this.pLabel,
			timeRefresh: this.pTimeRefresh,
			controlsLevel0: [],
			controlsLevel1: [],
			selectActionLevel0: '',
			selectActionLevel1: '',
			disabledChange: false,
			processOfCheckingTheStatus: false,
			counterForAnaliseInOrder: -1,
			showFaultV: false,
			checkIfThereAreSharesToConfirmValue: true,
			xIdViewAndIdCmp: this.pxIdViewAndIdCmp,
		};
	},
	methods: {
		refreshHistory() {
			this.$refs.refHistoryCMP.loadData()
		},
		endChecking() {
			this.insideState = this.config.state.analiseInOrder[
				this.counterForAnaliseInOrder
			].name;
			this.disabledChange = !!this.config.state.analiseInOrder[
				this.counterForAnaliseInOrder
			].disable;
			if (this.disabledChange) {
				this.show = false;
				if (this.showFaultV == true) {
					this.showFaultV = false;
					this.newErrors = [];
				}
			}
			this.processOfCheckingTheStatus = false;
			this.counterForAnaliseInOrder = -1;
			this.checkToNotificationError();
		},
		checkToNotificationError() {
			if (
				this.errorsNotification != this.newErrors.length > 0 ||
				this.errorsNotification != this.errorResultingFromOperationControl.length > 0
			) {
				this.errorsNotification =
					this.newErrors.length > 0 || this.errorResultingFromOperationControl.length > 0;
			}
			this.errors = this.newErrors;
			if (this.errorResultingFromOperationControl.length > 0) {
				this.errors.push(this.errorResultingFromOperationControl);
			}
			this.newErrors = [];
			this.errorResultingFromOperationControl = '';
		},
		setErrorAndStopAnaliseInOrder(msg) {
			this.newErrors.push(msg);
			this.insideState = 'Error';
			this.processOfCheckingTheStatus = false;
			this.counterForAnaliseInOrder = -1;
		},
		setErrorAndNotification(msg) {
			this.errorResultingFromOperationControl = msg;
			this.insideState = 'Error';
			this.checkToNotificationError();
		},
		showFault() {
			this.showFaultV = !this.showFaultV;
			this.checkStatus();
		},
		checkStatus() {
			this.newErros = [];
			this.counterForAnaliseInOrder = 0;
			this.processOfCheckingTheStatus = false;
		},
		setActionLeve0(action) {
			// get action by name action
			
			let foundLevel = _.findWhere(this.controlsLevel0, { name: action });
			let runDirectlyBeforeShowSubMenu = !!foundLevel.runDirectlyBeforeShowSubMenu;

			// check the action have runDirectlyBeforeShowSubMenu (and currently status is different then action)
			if (runDirectlyBeforeShowSubMenu == true) {
				let newData = [];
				// if action have runDirectlyBeforeShowSubMenu true then first run command then ok check next level
				let toSave = foundLevel.save;
				for (let i = 0; i < toSave.length; i++) {
					let xid = _.findWhere(this.config.control.definitionPointToSaveValue, {
						def: toSave[i].refDefPoint,
					});
					let change = new ChangeDataDTO(xid.xid, toSave[i].value, '', '');
					newData.push(change);
				}
				if (newData.length > 0) {
					new ApiCMP()
						.set(newData, this.xIdViewAndIdCmp, action)
						.then((response) => {
							//refreshHistory()
							// rxjs
							let found = _.findWhere(this.controlsLevel0, { name: action });
							if (found.toChange != undefined) {
								if (this.controlsLevel1 != found.toChange)
									this.controlsLevel1 = found.toChange;
							} else {
								this.controlsLevel1 = [];
							}
							this.selectActionLevel1 = '';
							//
						})
						.catch((er) => {
							this.setErrorAndNotification(er.message);
							//refreshHistory()
						});
				}
			} else {
				if (this.selectActionLevel0 == action) {
					this.selectActionLevel0 = '';
				} else {
					this.selectActionLevel0 = action;
					let found = _.findWhere(this.controlsLevel0, { name: action });
					if (found.toChange != undefined) {
						this.controlsLevel1 = found.toChange;
					} else {
						this.controlsLevel1 = [];
					}
					this.selectActionLevel1 = '';
				}
			}
		},
		setActionLevel1(action) {
			//refreshHistory()
			if (this.selectActionLevel1 == action) {
				this.selectActionLevel1 = '';
			} else {
				this.selectActionLevel1 = action;
			}
		},
		tryChangeModePLC() {
			let newData = [];
			let action = null;
			let control = null;
			if (this.selectActionLevel1 != '') {
				action = this.selectActionLevel1;
				control = this.controlsLevel1;
			} else if (this.selectActionLevel0 != '') {
				action = this.selectActionLevel0;
				control = this.controlsLevel0;
			} else {
				// Nothing to do;
			}
			if (action != null) {
				let foundLevel = _.findWhere(control, { name: action });
				let toSave = foundLevel.save;
				for (let i = 0; i < toSave.length; i++) {
					let xid = _.findWhere(this.config.control.definitionPointToSaveValue, {
						def: toSave[i].refDefPoint,
					});
					let change = new ChangeDataDTO(xid.xid, toSave[i].value, '', '');
					newData.push(change);
				}
				if (newData.length > 0) {
					new ApiCMP()
						.set(newData, this.xIdViewAndIdCmp, action)
						.then((response) => {
							this.show = false;
						})
						.catch((er) => {
							this.setErrorAndNotification(er.message);
						});
				}
			}
		},
		checkIfThereAreSharesToConfirm() {
			let result = false;

			if (typeof this.controlsLevel1 === 'object' && this.controlsLevel1.length > 0) {
				result = true;
			} else {
				for (let i = 0; i < this.controlsLevel0.length; i++) {
					let runDirectlyBeforeShowSubMenu = !!this.controlsLevel0[i]
						.runDirectlyBeforeShowSubMenu;
					if (runDirectlyBeforeShowSubMenu == false) {
						result = true;
						break;
					}
				}
			}
			return result;
		},
		close() {
			this.show = false;
		},
		callback (msg) {
        	this.$notify(`Modal dismissed with msg '${msg}'.`)
      	},
	},
	created() {
		try {
			this.config = JSON.parse(this.strConfig);
			this.controlsLevel0 = this.config.control.toChange;
		} catch (e) {
			this.setErrorAndNotification(e.message);
		}
		if (this.timeRefresh) {
			setInterval(
				function () {
					try {
						this.checkStatus();
					} catch (e) {
						console.log(e);
					}
				}.bind(this),
				this.timeRefresh,
			);
		}
	},
	mounted() {
		console.log(`CMP pxIdViewAndIdCmp:${this.xIdViewAndIdCmp}`);
		this.checkStatus();
	},
	filters: {
		moment: function (date) {
			return moment(date).format(' hh:mm:ss');
		},
	},
	watch: {
		show: function (val, oldVal) {
			if (val == true) {
				let found = _.findWhere(this.controlsLevel0, {
					name: this.insideState,
				});
				if (found != undefined) {
					if (found.toChange != undefined) {
						this.controlsLevel1 = found.toChange;
					} else {
						this.controlsLevel1 = [];
					}
					this.selectActionLevel0 = found.name;
					this.selectActionLevel1 = '';
				}
			}
		},
		insideState: function (val, oldVal) {
			if (val != oldVal) {
				let found = _.findWhere(this.controlsLevel0, {
					name: this.insideState,
				});
				if (found != undefined) {
					if (found.toChange != undefined) {
						this.controlsLevel1 = found.toChange;
					} else {
						this.controlsLevel1 = [];
					}
					this.selectActionLevel0 = found.name;
					this.selectActionLevel1 = '';
				}
			}
		},
		counterForAnaliseInOrder: function (val, oldVal) {
			if (this.counterForAnaliseInOrder >= 0) {
				this.processOfCheckingTheStatus = true;

				let points = this.config.state.analiseInOrder[this.counterForAnaliseInOrder]
					.toChecked;
				let name = this.config.state.analiseInOrder[this.counterForAnaliseInOrder].name;
				if (points != undefined && points.length > 0) {
					let xIDs = [];
					for (let j = 0; j < points.length; j++) {
						if (points[j].last == 'true') {
							if (this.insideState != name) {
								this.insideState = name;
							}
							this.endChecking();
							break;
						} else {
							xIDs.push(points[j].xid);
						}
					}

					if (xIDs.length > 0) {
						new ApiCMP()
							.get(xIDs)
							.then((response) => {
								if (response.data.length > 0) {
									let toCheck = [];
									for (let k = 0; k < points.length; k++) {
										let entry = points[k];
										try {
											for (let z = 0; z < response.data.length; z++) {
												if (
													response.data[z].xid.toUpperCase().trim() ==
													entry.xid.toUpperCase().trim()
												) {
													entry.value = response.data[z].value;
													toCheck.push(entry);
													break;
												}
											}
											if (entry.value == undefined) {
												this.setErrorAndStopAnaliseInOrder(
													'The device did not retain data',
												);
												return;
											}
										} catch (e) {
											this.setErrorAndStopAnaliseInOrder(e.message);
											return;
										}
									}

									let condition = '';
									for (let e = 0; e < toCheck.length; e++) {
										if (!!toCheck[e].toNoteError == true) {
											if (this.showFaultV) {
												this.newErrors.push(toCheck[e].describe);
											} else {
												let check = eval(
													'(' + toCheck[e].value + toCheck[e].equals + ')',
												);
												if (!!check == true) {
													this.newErrors.push(toCheck[e].describe);
												}
											}
										}

										let bitOperator =
											toCheck[e].bitOperatorToThePreviousCondition != undefined
												? toCheck[e].bitOperatorToThePreviousCondition
												: '';
										condition +=
											bitOperator + '(' + toCheck[e].value + toCheck[e].equals + ')';
									}

									let resultCondition = eval(condition);

									if (!!resultCondition == true) {
										this.endChecking();
									} else {
										if (
											this.config.state.analiseInOrder.length >
											this.counterForAnaliseInOrder - 1
										) {
											this.counterForAnaliseInOrder++;
										}
									}
								}
							})
							.catch((er) => {
								this.setErrorAndNotification(er.message);
							});
					}
				}
			}
		},
		controlsLevel0: function (val, oldVal) {
			this.checkIfThereAreSharesToConfirmValue = this.checkIfThereAreSharesToConfirm();
		},
		controlsLevel1: function (val, oldV) {
			this.checkIfThereAreSharesToConfirmValue = this.checkIfThereAreSharesToConfirm();
		},
	},
};
</script>

<style scoped lang="scss">
$grey: rgba(0, 0, 0, 0.1);
$green: #6b0;
$black: rgba(0, 0, 0, 0.5);
$next_grey: hsla(0, 0%, 100%, 0.2);
$radius: 10px;

.cmp-well {
	display: flex;
	padding: 10px;
	background-color: #f5f5f5;
	border: 1px solid #e3e3e3;
	border-radius: 4px;
	z-index: 999999;
	position: relative;
}

#cmp {
	border-radius: $radius;
	border: 3px solid $grey;
}

.cmp-flex-container {
	display: flex;
}

.cmp-flex-container-columns {
	display: flex;
	flex-direction: column;
}

.cmp-flex-label {
	color: #2b2b2b;
	font-weight: bold;
	flex-direction: column;
}

.cmp-flex-state {
	padding: 5px;
	font-size: 14px;
	font-weight: 300;
	color: #2b2b2b;
}

.cmp-flex-button {
	background: $green linear-gradient($next_grey, transparent);
	border-radius: 7px;
}

.cmp_selected {
	background-color: orangered;
	color: #d4d4d4;
}

.cmp_selected_to_confirm {
	background-color: #2e6da4;
	color: #d4d4d4;
}

.cmp-small-info {
	font-size: 8px;
}

.cmp_fault {
	color: red;
}

.cmp_close {
	display: flex;
	justify-content: flex-end;
	align-items: flex-start;
}
</style>
