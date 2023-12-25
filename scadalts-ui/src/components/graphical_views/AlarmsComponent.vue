<template>
	<div class="historical-alarms-components">
		<SimplePanel v-if="showMainToolbar == 'true'" class="panel_top">
			To refresh {{ toRefresh }}
			<div class="action">
				<input
					v-if="showSelectToAcknowledge == 'true'"
					type="checkbox"
					id="select_all"
					v-on:click="toggleSelectAll()"
					name="Select_All"
					value="0"
					ref="selectAll"
					v-model="sellAll"
				/>
				&nbsp;<label class="selall" for="select_all" v-on:click="toggleSelectAll()"
					>Select All</label
				><br />
			</div>
		</SimplePanel>
		<table>
			<tr>
				<th v-if="showSelectToAcknowledge == 'true'"></th>
				<th>Activation Timestamp</th>
				<th>Inactivation Timestamp</th>
				<th>Event message</th>
				<th>Variable name and description</th>
			</tr>
			<tr
				v-for="(item, index) in alarms"
				:key="index"
				v-bind:class="{
					activation: isActivation(
						item['activation-time'],
						item['inactivation-time'],
						item.level,
					),
					activation_alarm: isActivationAlarm(
						item['activation-time'],
						item['inactivation-time'],
						item.level,
					),
					inactivation: isInactivation(
						item['activation-time'],
						item['inactivation-time'],
						item.level,
					),
				}"
			>
				<td v-if="showSelectToAcknowledge == 'true'">
					<input
						v-if="
							item != undefined &&
							item['inactivation-time'] != undefined &&
							item['inactivation-time'].trim().length > 0
						"
						type="checkbox"
						name="ActivationAction"
						:value="item.id"
						v-model="to_acknowledges"
					/>
				</td>
				<td>{{ item['activation-time'] }}</td>
				<td>{{ item['inactivation-time'] }}</td>
				<td>{{ item.eventTextRender }}</td>
				<td>{{ item.name }}&nbsp;{{ item.description }}</td>
			</tr>
		</table>

		<SimplePanel v-if="showPagination == 'true'">
			<div class="pagination">
				<SimplePagination
					:current-page="currentPage"
					:page-count="pageCount"
					:visible-pages-count="8"
					@nextPage="pageChangeHandle('next')"
					@previousPage="pageChangeHandle('pref')"
					@loadPage="pageChangeHandle"
				></SimplePagination>
			</div>
		</SimplePanel>

		<div v-if="showAcknowledgeBtn == 'true'" class="action_bottom">
			<v-btn v-on:click="acknowledge()">Acknowledge Störung/Alarms</v-btn>
		</div>
	</div>
</template>

<script>
import store from '../../store';

export default {
	el: '#alarms_component',
	name: 'alarmsComponent',
	props: [
		'pShowAcknowledgeBtn',
		'pShowMainToolbar',
		'pShowSelectToAcknowledge',
		'pShowPagination',
		'pMaximumNumbersOfRows',
	],
	data() {
		return {
			newAlarms: null,
			LEVEL_FAULT: 1,
			LEVEL_ALARM: 2,
			alarms: [],
			currentPage: 1,
			pageCount: 10,
			toRefresh: 5,
			to_acknowledges: [],
			sellAll: false,
			showAcknowledgeBtn: this.pShowAcknowledgeBtn,
			showMainToolbar: this.pShowMainToolbar,
			showSelectToAcknowledge: this.pShowSelectToAcknowledge,
			showPagination: this.pShowPagination,
			maximumNumbersOfRows: this.pMaximumNumbersOfRows,
			hidePagination: false,
		};
	},
	methods: {
		getAlarms(page) {
			let recordsCount = this.maximumNumbersOfRows;
			let loffset = String(recordsCount * (page - 1));
			let llimit = String(recordsCount);

			store.dispatch('getLiveAlarms', { offset: loffset, limit: llimit }).then((ret) => {
				if (ret.length) this.newAlarms = true
				if (this.alarms.length >= this.maximumNumbersOfRows || page > 1) {
					if (this.showPagination == 'false') {
						this.hidePagination = true;
					}
					this.showPagination = 'true';
				} else {
					if (this.hidePagination == true) {
						this.showPagination = 'false';
					}
				}

				this.alarms = ret;
			});
		},
		acknowledge() {
			for (let i = 0; i < this.to_acknowledges.length; i++) {
				const id = this.to_acknowledges[i];
				store
					.dispatch('setAcknowledge', { id })
					.then((ret) => {
						console.log(`ackn:${ret}`);
						this.sellAll = false;
					})
					.catch((err) => {
						console.log(`ackn-err:${err}`);
					});
			}
			this.getAlarms(this.currentPage);
		},
		toggleSelectAll() {
			const checkboxes = document.getElementsByName('ActivationAction');

			if (this.sellAll) {
				this.to_acknowledges = [];
			} else if (checkboxes != null && checkboxes != undefined) {
				for (let i = 0; i < checkboxes.length; i++) {
					this.to_acknowledges.push(checkboxes[i]._value);
				}
			}
		},
		checkPrm(activationTime, inactivationTime, level) {
			if (
				activationTime === undefined ||
				inactivationTime === undefined ||
				level === undefined
			)
				return false;
			if (activationTime === null || inactivationTime === null || level === null)
				return false;
			return true;
		},
		isActivation(activationTime, inactivationTime, level) {
			if (!this.checkPrm(activationTime, inactivationTime, level)) return false;

			if (
				activationTime.trim().length > 0 &&
				inactivationTime.trim().length == 0 &&
				level == this.LEVEL_FAULT
			) {
				return true;
			} else {
				return false;
			}
		},
		isActivationAlarm(activationTime, inactivationTime, level) {
			if (!this.checkPrm(activationTime, inactivationTime, level)) return false;

			if (
				activationTime.trim().length > 0 &&
				inactivationTime.trim().length == 0 &&
				level == this.LEVEL_ALARM
			) {
				return true;
			} else {
				return false;
			}
		},
		isInactivation(activationTime, inactivationTime, level) {
			if (!this.checkPrm(activationTime, inactivationTime, level)) return false;

			if (activationTime.trim().length > 0 && inactivationTime.trim().length > 0) {
				return true;
			} else {
				return false;
			}
		},
		pageChangeHandle(pr) {
			this.to_acknowledges = [];
			this.sellAll = false;
			try {
				if (pr === 'next') {
					if (this.currentPage < 9) {
						this.currentPage = Number(this.currentPage + 1);
						this.getAlarms(this.currentPage);
					}
					return;
				} else if (pr === 'pref') {
					if (this.currentPage > 1) {
						this.currentPage = Number(this.currentPage - 1);
						this.getAlarms(this.currentPage);
					}
					return;
				}
				this.currentPage = Number(pr);
				this.getAlarms(this.currentPage);
			} catch (e) {
				console.log(`pageCH:${e}`);
			}
		},
	},
	created() {
		if (this.showAcknowledgeBtn === undefined) {
			this.showAcknowledgeBtn = 'true';
		}

		if (this.showMainToolbar === undefined) {
			this.showMainToolbar = 'true';
		}
		if (this.showSelectToAcknowledge === undefined) {
			this.showSelectToAcknowledge = 'true';
		}
		if (this.showPagination === undefined) {
			this.showPagination = 'true';
		}
		if (this.maximumNumbersOfRows == undefined) {
			this.maximumNumbersOfRows = 20;
		}
		this.getAlarms(1);
	},
	mounted() {
		setInterval(() => {
			if (this.toRefresh == 0) {
				this.getAlarms(this.currentPage);
				this.toRefresh = 5;
				console.log('getAlarms');
			} else {
				this.toRefresh = this.toRefresh - 1;
			}
		}, 1000);
	},
};
</script>

<style lang="scss" scoped>
data {
	margin: 20px;
}

table {
	margin: 20px;
	font-family: arial, sans-serif;
	border-collapse: collapse;
	width: 95%;
}
td {
	padding: 2px;
	border: 1px solid #ddd;
}
th {
	border: 1px solid #dddddd;
	text-align: left;
	padding: 8px;
}

.activation_alarm {
	color: red;
	background: yellow;
}

.activation {
	color: red;
	background: white;
}

.inactivation {
	color: green;
	background: white;
}

.pagination {
	margin: 0px 0px 0px 10px;
}

.action_bottom {
	padding-top: 10px;
	margin-left: 20px;
}

.panel_top {
	margin-top: 45px;
}
</style>
