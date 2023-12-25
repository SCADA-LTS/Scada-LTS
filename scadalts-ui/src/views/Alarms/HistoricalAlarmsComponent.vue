<template>
	<div class="historical-alarms-components">
		<SimplePanel>
			<datepicker
				v-model="fdate"
				:format="formatter"
				ref="datapicker"
				style="
					margin-top: 5px;
					margin-left: 5px;
					position: relative;
					width: 120px;
					float: left;
					line-height: 30px;
				"
				:disabled="filterOn == true"
				v-bind:class="{ input_disabled: filterOn == true }"
			></datepicker>

			<input
				v-model="frlike"
				class="min-gb-input"
				placeholder="rLike filter"
				style="margin-left: 45px; font-family: arial, sans-serif"
				:readonly="filterOn == true"
				v-bind:class="{ input_disabled: filterOn == true }"
			/>

			<button
				class="min-gb-button"
				style="margin-top: 4px"
				v-bind:class="{ filter_on: filterOn == true }"
				v-on:click="onFilter"
			>
				Filter
			</button>
		</SimplePanel>
		<SimpleTable v-bind:data="historicalAlarms" v-bind:columns="columns"></SimpleTable>

		<SimplePanel>
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
	</div>
</template>

<script>
import store from '../../store';
import moment from 'moment';
import i18n from '../../i18n';

export default {
	el: '#historicalAlarms_component',
	name: 'historicalAlarmsComponent',
	data() {
		return {
			filterOn: false,
			fdate: new Date(),
			frlike: '',
			columns: [{ name: 'time' }, { name: 'name' }, { name: 'description' }],
			historicalAlarms: [],
			currentPage: 1,
			pageCount: 10,
			FORMAT_DT: 'YYYY-MM-DD',
			RLIKE_NULL: 'EMPTY',
		};
	},
	methods: {
		formatter(date) {
			return moment(date).format(this.FORMAT_DT);
		},
		getHistoricalAlarms(adate, arlike, page, afilterOn) {
			try {
				let ldate = '';
				let recordsCount = 20;
				let loffset = String(recordsCount * (page - 1));
				let llimit = String(recordsCount * page);
				let lrlike = this.RLIKE_NULL;

				console.log(`filterOn: ${this.filterOn} typeof:${typeof this.filterOn}`);
				console.log(`afilterOn: ${afilterOn} typeof:${typeof afilterOn}`);

				if (afilterOn == false) {
					ldate = moment(new Date()).format(this.FORMAT_DT);
				} else if (adate === undefined || adate === null || adate == 0) {
					ldate = moment(this.fdate).format(this.FORMAT_DT);
				} else {
					ldate = moment(adate).format(this.FORMAT_DT);
				}

				if (afilterOn === false) {
					lrlike = String(this.RLIKE_NULL);
				} else if (
					arlike === undefined ||
					arlike === null ||
					arlike.trim().length === 0
				) {
					lrlike = String(this.RLIKE_NULL);
				} else {
					lrlike = String(arlike);
				}

				store
					.dispatch('getHistoryAlarms', {
						dateDay: ldate,
						filterRLike: lrlike,
						offset: loffset,
						limit: llimit,
					})
					.then((ret) => {
						//{"activeTime":"2020-08-21 17:38:17","inactiveTime":" ","acknowledgeTime":" ","name":"test AL test","level":2}
						//transform to {time: "",utc:"", type:"aT", name:"",description:""}
						console.log(ret);
						let toRet = [];
						ret.find((r) => {
							if (
								r.acknowledgeTime != undefined &&
								r.acknowledgeTime.trim() != '' &&
								r.acknowledgeTime.length > 0
							) {
								let toAdd = {};
								toAdd.time = r.acknowledgeTime;
								toAdd.utc = new Date(r.time);
								if (r.level === 2) {
									toAdd.description = i18n.t('plcalarms.alarm.acknowledge');
								} else {
									toAdd.description = i18n.t('plcalarms.fault.acknowledge');
								}
								toRet.push(toAdd);
							}
							if (
								r.inactiveTime != undefined &&
								r.inactiveTime.trim() != '' &&
								r.inactiveTime.length > 0
							) {
								let toAdd = {};
								toAdd.time = r.inactiveTime;
								toAdd.utc = new Date(r.time);
								if (r.level === 2) {
									toAdd.description = i18n.t('plcalarms.alarm.inactive');
								} else {
									toAdd.description = i18n.t('plcalarms.fault.inactive');
								}
								toRet.push(toAdd);
							}
							if (
								r.activeTime != undefined &&
								r.activeTime.trim() != '' &&
								r.activeTime.length > 0
							) {
								let toAdd = {};
								toAdd.time = r.activeTime;
								toAdd.utc = new Date(r.time);
								if (r.level === 2) {
									toAdd.description = i18n.t('plcalarms.alarm.active');
								} else {
									toAdd.description = i18n.t('plcalarms.fault.active');
								}
								toRet.push(toAdd);
							}
						});

						console.log(toRet);

						//sorting time

						toRet.sort((a, b) => a.utc - b.utc);

						console.log(ret);
						this.historicalAlarms = toRet;
					})
					.catch((err) => {
						this.historicalAlarms = [];
						console.log(`getHA:${err}`);
					});
			} catch (e) {
				this.historicalAlarms = [];
				console.log(`getHA1:${e}`);
			}
		},
		pageChangeHandle(pr) {
			try {
				if (pr === 'next') {
					console.log('pageChangeHandle next');
					return;
				} else if (pr === 'pref') {
					console.log('pageChangeHandle pref');
					return;
				}
				this.currentPage = Number(pr);
				this.getHistoricalAlarms(this.fdate, this.rlikeFilter, this.currentPage);
			} catch (e) {
				console.log(`pageCH:${e}`);
			}
		},
		onFilter() {
			this.filterOn = !this.filterOn;
			try {
				this.getHistoricalAlarms(
					this.fdate,
					this.frlike,
					this.currentPage,
					this.filterOn,
				);
			} catch (e) {
				console.log(`created:${e}`);
			}
		},
	},
	created() {
		try {
			this.getHistoricalAlarms(this.fdate, this.frlike, this.currentPage);
		} catch (e) {
			console.log(`created:${e}`);
		}
	},
	mounted() {
		//this.$refs.datapicker.style = "position: relative; width: 120px; float:left; line-height: 34px;"
	},
};
</script>

<style scoped>
.historical-alarms-components {
	margin-top: 40px;
	margin-left: 20px;
}

input:-moz-read-only {
	/* For Firefox */
	padding: 0;
	border: 0;
	width: 120px;
	line-height: 32px;
}

input {
	padding: 0;
	margin-top: 5px;
	border: 0;
	line-height: 32px;
	width: 120px;
}

input[type='text'] {
	width: 120px;
}

button:-moz-any {
	padding: 0;
}

.filter_on {
	background-color: yellow;
}

.input_disabled {
	background-color: #f4f4f4 !important;
}

.pagination {
	margin: 0px 0px 0px 10px;
}
</style>
