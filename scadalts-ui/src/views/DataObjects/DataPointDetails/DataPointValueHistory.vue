<template>
	<v-card>
		<v-card-title v-if="hideSkeleton">
			{{ $t('datapointDetails.valueHistory.title') }}
			<v-spacer> </v-spacer>
			<v-dialog v-model="dialog" width="800">
				<template v-slot:activator="{ on, attrs }">
					<v-btn icon fab small v-bind="attrs" v-on="on">
						<v-icon>mdi-arrow-expand-all</v-icon>
					</v-btn>
				</template>

				<v-card>
					<v-card-title>
						{{ $t('datapointDetails.valueHistory.subtitle') }}
					</v-card-title>
					<v-card-text>
						<v-row>
							<v-col cols="6">
								<v-text-field
									v-model="timePeriod"
									:label="$t('common.timeperiod.title')"
									dense
								>
								</v-text-field>
							</v-col>
							<v-col cols="6">
								<v-select
									v-model="timePeriodType"
									:items="timePeriods"
									item-value="id"
									item-text="label"
									append-outer-icon="mdi-autorenew"
									@click:append-outer="fetchData"
									dense
								></v-select>
							</v-col>
						</v-row>
						<v-simple-table dense fixed-header height="500px">
							<template v-slot:default>
								<thead>
									<tr>
										<th>{{ $t('datapointDetails.valueHistory.table.value') }}</th>
										<th>{{ $t('datapointDetails.valueHistory.table.date') }}</th>
									</tr>
								</thead>
								<tbody>
									<tr v-for="e in valueList" :key="e">
										<td>{{ e.value }}</td>
										<td>{{ new Date(e.ts).toLocaleString() }}</td>
									</tr>
								</tbody>
							</template>
						</v-simple-table>
					</v-card-text>
				</v-card>
			</v-dialog>
		</v-card-title>

		<v-card-text v-if="hideSkeleton">
			<v-row align="center">
				<v-col cols="4" v-if="data.pointLocator.dataTypeId === 1">
					<v-btn 
						block
						@click="toggleValue()"
						:color="getBinaryRendererLabel(pointValue, 'color')">
						{{getBinaryRendererLabel(pointValue, 'label')}}
					</v-btn>
				</v-col>
				<v-col cols="4" v-else>
					<v-text-field
						v-model="pointValue"
						:label="$t('datapointDetails.valueHistory.stats.add')"
						append-icon="mdi-send"
						@click:append="sendValue"
						:disabled="!data.pointLocator.settable"
						dense
					></v-text-field>
				</v-col>
				<v-col cols="2"> </v-col>
				<v-col cols="3">
					<v-text-field
						v-model="timePeriod"
						:label="$t('datapointDetails.valueHistory.stats.timeperiod')"
						dense
					>
					</v-text-field>
				</v-col>
				<v-col cols="3">
					<v-select
						v-model="timePeriodType"
						:items="timePeriods"
						item-value="id"
						item-text="label"
						dense
					>
						<template v-slot:append-outer>
							<v-btn
								icon
								fab
								x-small
								@click="fetchData"
								:loading="fetchingData"
								:disabled="fetchingData"
							>
								<v-icon>mdi-autorenew</v-icon>
							</v-btn>
						</template>
					</v-select>
				</v-col>
			</v-row>
			<v-row>
				<v-col cols="5" v-if="data.pointLocator.dataTypeId === 3">
					<v-row>
						<v-col cols="6"> {{ $t('datapointDetails.valueHistory.stats.max') }} </v-col>
						<v-col cols="6">
							{{ maxValue.value }}
						</v-col>
						<v-col cols="6"> {{ $t('datapointDetails.valueHistory.stats.min') }} </v-col>
						<v-col cols="6">
							{{ minValue.value }}
						</v-col>
						<v-col cols="6"> {{ $t('datapointDetails.valueHistory.stats.avg') }} </v-col>
						<v-col cols="6">
							{{ avgValue }}
						</v-col>
						<v-col cols="6"> {{ $t('datapointDetails.valueHistory.stats.sum') }} </v-col>
						<v-col cols="6">
							{{ sumValue }}
						</v-col>
						<v-col cols="6">
							{{ $t('datapointDetails.valueHistory.stats.count') }}
						</v-col>
						<v-col cols="6">
							{{ countValue }}
						</v-col>
					</v-row>
				</v-col>
				<v-col cols="5" v-else>
					<v-row>
						<v-col cols="4">
							{{ $t('datapointDetails.valueHistory.table.value') }}
						</v-col>
						<v-col cols="4">
							{{ $t('datapointDetails.valueHistory.table.count') }}
						</v-col>
						<v-col cols="4">
							{{ $t('datapointDetails.valueHistory.table.runtime') }}
						</v-col>
					</v-row>
					<v-row v-for="el in statArray" :key="el">
						<v-col cols="4">
							{{ el.value }}
						</v-col>
						<v-col cols="4">
							{{ el.count }}
						</v-col>
						<v-col cols="4">
							{{ el.runtime }}
						</v-col>
					</v-row>
				</v-col>
				<v-col cols="7">
					<v-simple-table dense fixed-header height="150px">
						<template v-slot:default>
							<thead>
								<tr>
									<th>{{ $t('datapointDetails.valueHistory.table.value') }}</th>
									<th>{{ $t('datapointDetails.valueHistory.table.date') }}</th>
								</tr>
							</thead>
							<tbody>
								<tr v-for="e in valueList" :key="e">
									<td>{{ e.value }}</td>
									<td>{{ new Date(e.ts).toLocaleString() }}</td>
								</tr>
							</tbody>
						</template>
					</v-simple-table>
				</v-col>
			</v-row>
		</v-card-text>

		<v-skeleton-loader v-else type="article"></v-skeleton-loader>
	</v-card>
</template>
<script>
import webSocketMixin from '@/utils/web-socket-utils';
import internetMixin from '@/utils/connection-status-utils';

/**
 * Value History List for Data Point
 * 
 * Display history values from specific data point.
 * Present the statiscics from given time period and allow user 
 * to set a new value for that Data Point.
 * Using Web-Sockets user is informed about all changes without polling.
 *
 * @param {number} data - Point Details object with data.
 *
 * @author Radoslaw Jajko <rjajko@softq.pl>
 * @version 1.2
 */
export default {
	name: 'DataPointValueHistory',

	props: ['data'],

	mixins: [webSocketMixin, internetMixin],

	data() {
		return {
			hideSkeleton: false,
			pointValue: 0.0,
			timePeriod: 1,
			timePeriodType: 3,
			fetchingData: false,
			valueList: null,
			maxValue: { value: -Infinity, ts: null },
			minValue: { value: Infinity, ts: null },
			countValue: 0,
			sumValue: 0,
			avgValue: 0,
			statArray: [],
			temp: {
				ts: null,
				value: null,
			},

			wsCallback: () => {				
				this.wsSubscribeTopic(`datapoint/${this.data.id}/value`, this.updatePointWs);		
			},

			onAppOnline: () => {
				console.log("Application status: online");
				this.fetchData();
			},
		};
	},

	computed: {
		timePeriods() {
			return this.$store.state.timePeriods.filter((e) => {
				return !(e.id === 0 || e.id === 8 || e.id === 7);
			});
		},
	},

	mounted() {
		this.fetchData();
		this.hideSkeleton = true;
	},

	methods: {

		updatePointWs(data) {
			this.pointValue = JSON.parse(data.body).value;
			this.fetchData();
		},

		wsBeforeDisconnect() {
			this.ws.send(`/ws-scada/datapoint/${this.data.id}/value/unsub`)
		},

		async fetchData() {
			this.fetchingData = true;
			let from = await this.$store.dispatch('convertSinceTimePeriodToTimestamp', {
				period: this.timePeriod,
				type: this.timePeriodType,
			});

			let response = await this.$store.dispatch('getDataPointValueFromTimeperiod', {
				datapointId: this.data.id,
				startTs: from.getTime(),
				endTs: new Date().getTime(),
			});
			this.valueList = response.values;
			if (this.valueList.length > 0) {
				this.calculateStatistics();
				this.valueList.reverse();
			}
			this.fetchingData = false;
		},

		sendValue() {
			let request = {
				xid: this.data.xid,
				type: this.data.pointLocator.dataTypeId,
				value: this.pointValue,
			};

			if (request.type === 1) {
				if (request.value === true || request.value === 'true') {
					request.value = 1;
				} else if (request.value === false || request.value === 'false') {
					request.value = 0;
				}
			}

			this.$store.dispatch('setDataPointValue', request).then((resp) => {
				this.fetchData();
			});
		},

		toggleValue() {
			this.pointValue = this.pointValue === 'true' ? true : (this.pointValue === 'false' ? false : this.pointValue);
			this.pointValue = !this.pointValue;
			this.sendValue();
		},

		calculateStatistics(precision = 1000) {
			this.restoreStatistics();
			if (this.data.pointLocator.dataTypeId === 3) {
				this.valueList.forEach((v) => {
					this.countValue = 1 + this.countValue;
					this.sumValue = Number(this.sumValue) + Number(v.value);
					this.avgValue = this.sumValue / this.countValue;
					if (v.value > this.maxValue.value) {
						this.maxValue = v;
					}
					if (v.value < this.minValue.value) {
						this.minValue = v;
					}
				});
				this.avgValue = Math.round(this.avgValue * precision) / precision;
				this.sumValue = Math.round(this.sumValue * precision) / precision;
			} else {
				this.valueList.forEach((v) => {
					let index = this.statArray.findIndex((x) => x.value === v.value);
					if (index < 0) {
						this.statArray.push({
							value: v.value,
							ts: v.ts,
							count: 1,
							duration: 0,
						});
						if (this.temp.value === null) {
							this.temp.ts = v.ts;
						} else {
							let index2 = this.statArray.findIndex((x2) => x2.value === this.temp.value);
							this.statArray[index2].duration += v.ts - this.statArray[index2].ts;
						}
					} else {
						let index2 = this.statArray.findIndex((x2) => x2.value === this.temp.value);
						this.statArray[index].count += 1;
						this.statArray[index].ts = v.ts;
						this.statArray[index2].duration += v.ts - this.statArray[index2].ts;
					}
					this.temp.value = v.value;
				});
				let time = new Date().getTime();
				let since = time - this.temp.ts;
				let index = this.statArray.findIndex((x) => x.value === this.temp.value);
				this.statArray[index].duration =
					this.statArray[index].duration + (time - this.statArray[index].ts);
				this.statArray.forEach((v) => {
					v.runtime = `${Math.round((v.duration / since) * 100)}%`;
				});
			}
		},

		restoreStatistics() {
			this.countValue = 0;
			this.sumValue = 0;
			this.avgValue = 0;
			this.maxValue.value = -Infinity;
			this.minValue.value = Infinity;
			this.statArray = [];
			this.temp.ts = null;
			this.temp.value = null;
		},

		isInArray(element) {
			this.statArray.forEach((e) => {
				return element === e.value;
			});
			return -1;
		},

		getBinaryRendererLabel(value, type) {
			if(!!this.data.textRenderer && this.data.textRenderer.typeName === "textRendererBinary") {
				if(value === "false") {
					if(type === "label") {
						return this.data.textRenderer.zeroLabel;
					} else {
						return this.data.textRenderer.zeroColour;
					}
				} else {
					if(type === "label") {
						return this.data.textRenderer.oneLabel;
					} else {
						return this.data.textRenderer.oneColour;
					}
				}
			}
			if(type === "color") {
				return '#f5f5f5';
			}
			return value;
			
		}
	},
};
</script>
<style scoped></style>
