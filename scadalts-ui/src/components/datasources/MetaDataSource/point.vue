<template>
	<div>
		<DataPointCreation
			title="Meta Data Point"
			:creator="createMode"
			:datapoint="datapoint"
			@cancel="cancel()"
			@accept="save()"
		>
			<template v-slot:selector>
				<v-select
					v-model="datapoint.pointLocator.dataTypeId"
					:items="datapointTypes"
				></v-select>
				
			</template>
			<!-- Binary -->
			<v-row v-if="datapoint.pointLocator.dataTypeId === DataTypes.BINARY">
				<v-col>
					<v-row
						v-if="
							datapoint.pointLocator.changeTypeId === DataChangeTypes.ALTERNATE_BOOLEAN
						"
					>
						<v-col>
							<v-select
								label="Start Value"
								v-model="datapoint.pointLocator.alternateBooleanChange.startValue"
								:items="booleanSelectBox"
							></v-select>
						</v-col>
					</v-row>
					<v-row
						v-if="datapoint.pointLocator.changeTypeId === DataChangeTypes.RANDOM_BOOLEAN"
					>
						<v-col>
							<v-select
								label="Start Value"
								v-model="datapoint.pointLocator.randomBooleanChange.startValue"
								:items="booleanSelectBox"
							></v-select>
						</v-col>
					</v-row>
					<v-row v-if="datapoint.pointLocator.changeTypeId === DataChangeTypes.NO_CHANGE">
						<v-col>
							<v-select
								label="Start Value"
								v-model="datapoint.pointLocator.noChange.startValue"
								:items="booleanSelectBox"
							></v-select>
						</v-col>
					</v-row>
				</v-col>
			</v-row>

			<!-- Multistate -->
			<v-row v-if="datapoint.pointLocator.dataTypeId === DataTypes.MULTISTATE">
				<v-col>
					<v-row
						v-if="
							datapoint.pointLocator.changeTypeId === DataChangeTypes.INCREMENT_MULTISTATE
						"
					>
						<v-col cols="5">
							<v-text-field type="Number" label="Values" v-model="multistateValue">
								<v-icon
									slot="append"
									@click="
										addMsValue(datapoint.pointLocator.incrementMultistateChange.values)
									"
									>mdi-plus</v-icon
								>
							</v-text-field>
						</v-col>

						<v-col cols="2">
							<v-checkbox
								label="Roll"
								v-model="datapoint.pointLocator.incrementMultistateChange.roll"
							></v-checkbox>
						</v-col>
						<v-col cols="5">
							<v-select
								label="Initail Value"
								v-model="datapoint.pointLocator.incrementMultistateChange.startValue"
								:items="datapoint.pointLocator.incrementMultistateChange.values"
							></v-select>
						</v-col>
						<v-col
							cols="2"
							v-for="(v, index) in datapoint.pointLocator.incrementMultistateChange
								.values"
							:key="index"
						>
							<v-chip
								close
								close-icon="mdi-delete"
								color="primary"
								label
								class="multistate-value--label"
								@click:close="removeMsValue(DataChangeTypes.INCREMENT_MULTISTATE, v)"
							>
								{{ v }}
							</v-chip>
						</v-col>
					</v-row>
					<v-row
						v-if="
							datapoint.pointLocator.changeTypeId === DataChangeTypes.RANDOM_MULTISTATE
						"
					>
						<v-col cols="5">
							<v-text-field type="Number" label="Values" v-model="multistateValue">
								<v-icon
									slot="append"
									@click="
										addMsValue(datapoint.pointLocator.randomMultistateChange.values)
									"
									>mdi-plus</v-icon
								>
							</v-text-field>
						</v-col>
						<v-col cols="2">
							<v-checkbox
								label="Roll"
								v-model="datapoint.pointLocator.randomMultistateChange.roll"
							></v-checkbox>
						</v-col>
						<v-col cols="5">
							<v-select
								label="Initail Value"
								v-model="datapoint.pointLocator.randomMultistateChange.startValue"
								:items="datapoint.pointLocator.randomMultistateChange.values"
							></v-select>
						</v-col>
						<v-col
							cols="2"
							v-for="(v, index) in datapoint.pointLocator.randomMultistateChange.values"
							:key="index"
						>
							<v-chip
								close
								close-icon="mdi-delete"
								color="primary"
								label
								class="multistate-value--label"
								@click:close="removeMsValue(DataChangeTypes.RANDOM_MULTISTATE, v)"
							>
								{{ v }}
							</v-chip>
						</v-col>
					</v-row>
					<v-row v-if="datapoint.pointLocator.changeTypeId === DataChangeTypes.NO_CHANGE">
						<v-col>
							<v-text-field
								label="Start Value"
								type="number"
								v-model="datapoint.pointLocator.noChange.startValue"
							></v-text-field>
						</v-col>
					</v-row>
				</v-col>
			</v-row>

			<!-- Numeric -->
			<v-row v-if="datapoint.pointLocator.dataTypeId === DataTypes.NUMERIC">
				<v-col>
					<v-row v-if="datapoint.pointLocator.changeTypeId === DataChangeTypes.BROWNIAN">
						<v-col>
							<v-text-field
								type="Number"
								label="Minimum"
								v-model="datapoint.pointLocator.brownianChange.min"
							></v-text-field>
						</v-col>
						<v-col>
							<v-text-field
								type="Number"
								label="Maximum"
								v-model="datapoint.pointLocator.brownianChange.max"
							></v-text-field>
						</v-col>
						<v-col>
							<v-text-field
								type="Number"
								label="Maximum Change"
								v-model="datapoint.pointLocator.brownianChange.maxChange"
							></v-text-field>
						</v-col>
						<v-col>
							<v-text-field
								type="Number"
								label="Start value"
								v-model="datapoint.pointLocator.brownianChange.startValue"
							></v-text-field>
						</v-col>
					</v-row>

					<v-row
						v-if="
							datapoint.pointLocator.changeTypeId === DataChangeTypes.INCREMENT_ANALOG
						"
					>
						<v-col>
							<v-text-field
								type="Number"
								label="Minimum"
								v-model="datapoint.pointLocator.incrementAnalogChange.min"
							></v-text-field>
						</v-col>
						<v-col>
							<v-text-field
								type="Number"
								label="Maximum"
								v-model="datapoint.pointLocator.incrementAnalogChange.max"
							></v-text-field>
						</v-col>
						<v-col>
							<v-text-field
								type="Number"
								label="Maximum Change"
								v-model="datapoint.pointLocator.incrementAnalogChange.change"
							></v-text-field>
						</v-col>
						<v-col>
							<v-checkbox
								label="Roll"
								v-model="datapoint.pointLocator.incrementAnalogChange.roll"
							></v-checkbox>
						</v-col>
						<v-col>
							<v-text-field
								type="Number"
								label="Start value"
								v-model="datapoint.pointLocator.incrementAnalogChange.startValue"
							></v-text-field>
						</v-col>
					</v-row>

					<v-row v-if="datapoint.pointLocator.changeTypeId === DataChangeTypes.NO_CHANGE">
						<v-col cols=6>
							<v-text-field
								type="Number"
								label="Start value"
								v-model="datapoint.pointLocator.noChange.startValue"
							></v-text-field>
						</v-col>
					</v-row>


					<v-row
						v-if="
							datapoint.pointLocator.changeTypeId === DataChangeTypes.ANALOG_ATTRACTOR
						"
					>
						<!-- TODO: Add Atttractor Point Logic -->
						<v-col>
							<v-text-field
								type="Number"
								label="attractionPointId"
								v-model="datapoint.pointLocator.analogAttractorChange.attractionPointId"
							></v-text-field>
						</v-col>
						<v-col>
							<v-text-field
								type="Number"
								label="volatility"
								v-model="datapoint.pointLocator.analogAttractorChange.volatility"
							></v-text-field>
						</v-col>
						<v-col>
							<v-text-field
								type="Number"
								label="maxChange"
								v-model="datapoint.pointLocator.analogAttractorChange.maxChange"
							></v-text-field>
						</v-col>
						<v-col>
							<v-text-field
								type="Number"
								label="Start value"
								v-model="datapoint.pointLocator.analogAttractorChange.startValue"
							></v-text-field>
						</v-col>
					</v-row>
				</v-col>
			</v-row>

			<!-- Alphanumeric -->
							<v-row>
								<v-col cols="6">
									<v-select
										item-value="id"
										:placeholder="$t('scriptList.selectDatapoint')"
										item-text="name"
										v-model="selectedDatapointId"
										@change="addDatapoint"
										:items="filteredDatapoints"
									></v-select>
								</v-col>
								<v-col cols="6">
									<table style="width: 100%">
										<tr v-for="p in datapoint.pointLocator.context">
											<td style="width: 20%">{{ p.dataPointXid }}</td>
											<td style="width: 70%">
												<v-text-field style="width: 100%" v-model="p.value" />
											</td>
											<td style="width: 10%">
												<v-icon
													color="red"
													style="cursor: pointer; border: 0"
													@click="removeDatapoint(p.dataPointXid)"
													>mdi-close</v-icon
												>
											</td>
										</tr>
									</table>
								</v-col>
							</v-row>
							<v-textarea
								style="width: 100%; font-family: monospace"
								:label="$t('scriptList.script')"
								v-model="datapoint.pointLocator.script"
								rows=3
                @input="validateScript"
                :rules="[ruleValidScript, ruleNotNull]"
								ref="scriptBodyTextarea"
							></v-textarea>
            <v-col>
              <v-btn block color="primary" @click="validateScript"
              >{{ $t('script.runScript') }}
              </v-btn>
            </v-col>
            <div v-if = "this.validScript">
              <v-alert title="Script is valid" type="success">
                Script results: {{this.resultMessage}}
              </v-alert>
            </div>
            <p v-else></p>
						<v-row>
						<v-col :cols="datapoint.pointLocator.updateEvent === 'START_OF_CRON' ? 3 : 6">
							<v-select
								label="Update event"
								v-model="datapoint.pointLocator.updateEvent"
								item-text="label"
								item-value="id"
								
								:items="eventTypes"
							>
							</v-select>
						</v-col>
						<v-col cols=3 v-if="datapoint.pointLocator.updateEvent === 'START_OF_CRON'">
							<v-text-field
								label="cron pattern"
								v-model="datapoint.pointLocator.updateCronPattern"
							></v-text-field>
						</v-col>
				<v-col>
					<v-text-field
						v-model="datapoint.pointLocator.executionDelaySeconds"
						label="Execution delay"
					></v-text-field>
				</v-col>
				<v-col>
					<v-select
						v-model="datapoint.pointLocator.executionDelayPeriodType"
						item-text="label"
						item-value="id"
						:items="[
							{id: 'MILLISECONDS', label:$t('executionDelayPeriodType.8')},
							{id: 'SECONDS', label:$t('executionDelayPeriodType.1')},
							{id: 'MINUTES', label:$t('executionDelayPeriodType.2')},
						]"
					></v-select>
				</v-col>
			</v-row>
		</DataPointCreation>
	</div>
</template>
<script>
import DataPointCreation from '../DataPointCreation';
import { DataTypes, DataChangeTypes } from '@/store/dataSource/constants';
export default {
	components: {
		DataPointCreation,
	},
	async mounted() {
		this.fetchScriptList();
		this.datapoints = await this.$store.dispatch('getAllDatapoints');
	},

	props: {
		createMode: {
			type: Boolean,
			default: true,
		},
		datapoint: {
			required: true,
			type: Object,
		},
		visible: {
			default: false,
			type: Boolean,
		},
	},

	data() {
		return {
			eventTypes: [
				{id: 'CONTEXT_UPDATE',label: this.$t("updateEventTypes.CONTEXT_UPDATE")},
				{id: 'CONTEXT_CHANGE',label: this.$t("updateEventTypes.CONTEXT_CHANGE")},
				{id: 'START_OF_MINUTE',label: this.$t("updateEventTypes.START_OF_MINUTE")},
				{id: 'START_OF_HOUR',label: this.$t("updateEventTypes.START_OF_HOUR")},
				{id: 'START_OF_DAY',label: this.$t("updateEventTypes.START_OF_DAY")},
				{id: 'START_OF_WEEK',label: this.$t("updateEventTypes.START_OF_WEEK")},
				{id: 'START_OF_MONTH',label: this.$t("updateEventTypes.START_OF_MONTH")},
				{id: 'START_OF_YEAR',label: this.$t("updateEventTypes.START_OF_YEAR")},
				{id: 'START_OF_CRON',label: this.$t("updateEventTypes.START_OF_CRON")},
			],
			selectedDatapointId: null,
			datapoints: [],
			msValues: ['1', '2'],
			DataTypes: DataTypes,
			DataChangeTypes: DataChangeTypes,
			multistateValue: 0,
			booleanSelectBox: [
				{
					text: 'False',
					value: false,
				},
				{
					text: 'True',
					value: true,
				},
			],
			datapoint: {
				runDelayMinutes: 0,
				xid: "DP_49512",
				name: "dp_123",
				description: "abc",
				enabled: false,
				dataSourceTypeId: 9,
				dataSourceId: 49,
				deviceName: "meta_ds",
				pointLocator: {
					dataSourceTypeId: 9,
					dataTypeId: 1,
					settable: false,
					context: [
					],
					script: "",
					updateEvent: "START_OF_WEEK",
					updateCronPattern: "",
					executionDelaySeconds: 10,
					executionDelayPeriodType: "SECONDS"
				}
			},
      validScript: false,
      resultMessage: "",
			ruleNotNull: (v) => !!v || this.$t('validation.rule.notNull'),
      ruleValidScript: () => this.validScript || this.resultMessage,
		};
	},

	computed: {
		filteredDatapoints() {
			const p = this.datapoints.find((x) => x.id === this.selectedDatapointId);
			return this.datapoints
			.filter(dp => this.datapoint.pointLocator.context
				.filter(sp => dp.xid === sp.dataPointXid)
			.length === 0)
		},
		changeTypes() {
			if (!!this.datapoint) {
				return this.$store.getters.getVirtualDatapointChangeType(
					this.datapoint.pointLocator.dataTypeId,
				);
			} else {
				return [
					{
						value: DataChangeTypes.NO_CHANGE,
						text: 'No Change',
					},
				];
			}
		},

		datapointTypes() {
			return this.$store.state.dataSourceState.datapointTypes;
		},
	},

	methods: {
		removeDatapoint(dataPointXid) {
			this.datapoint.pointLocator.context = this.datapoint.pointLocator.context.filter(
				(p) => p.dataPointXid != dataPointXid,
			);
		},
		addDatapoint() {
			const p = this.datapoints.find((x) => x.id === this.selectedDatapointId);
			if (!this.datapoint.pointLocator.context.find((x) => p.xid === x.dataPointXid)) {
				this.datapoint.pointLocator.context.push({
					key: p.id,
					value: `p${p.id}`,
				});
				this.selectedDatapointId = null;
			}
		},
		async fetchScriptList() {
			this.loading = true;
			this.scriptList = await this.$store.dispatch('searchScripts', this.searchFilters);
			if (!this.search) {
				this.scriptListFiltered = this.scriptList;
			} else {
				const keywords = this.search.split(' ');
				this.scriptListFiltered = this.scriptList.filter((x) =>
					`${x.id} ${x.xid} ${x.name} ${x.script}`
						.toLowerCase()
						.includes(keywords[0].toLowerCase()),
				);
			}
			this.totalScripts = this.scriptList.total;
		},
		cancel() {
			console.debug('VirtualDataSource.point.vue::cancel()');
			this.$emit('canceled');
		},

		save() {
			console.debug('VirtualDataSource.point.vue::save()');
			this.$emit('saved', this.datapoint);
		},

		addMsValue(array) {
			array.push(this.multistateValue);
			this.multistateValue = Number(this.multistateValue) + 1;
		},

		removeMsValue(type, index) {
			if (type === DataChangeTypes.RANDOM_MULTISTATE) {
				this.datapoint.pointLocator.randomMultistateChange.values = this.datapoint.pointLocator.randomMultistateChange.values.filter(
					(t) => t !== index,
				);
			} else if (type === DataChangeTypes.INCREMENT_MULTISTATE) {
				this.datapoint.pointLocator.incrementMultistateChange.values = this.datapoint.pointLocator.incrementMultistateChange.values.filter(
					(t) => t !== index,
				);
			} else {
				console.log('Remove Multistate Value Failed!');
				return;
			}
		},

    async validateScript() {
      try {
        let resp = await this.$store.dispatch('requestPost', {
          url:  `/datapoint/meta/test`,
          data: this.datapoint.pointLocator
        });
        this.validScript = resp.success;
        this.resultMessage = resp.message;
        this.$refs.scriptBodyTextarea.validate();
        this.$t(resp);
      } catch (e) {
        console.log('error:' + e);
      }
    },
	},
};
</script>
<style>
.multistate-value--label {
	width: 100%;
}
.multistate-value--label .v-chip__content {
	width: 100%;
	display: flex;
	justify-content: space-between;
}
</style>
