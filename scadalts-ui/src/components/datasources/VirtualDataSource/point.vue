<template>
	<div>
		<DataPointCreation
			title="Virtual Data Point"
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

			<v-row>
				<v-col>
					<v-select
						label="Change Type"
						v-model="datapoint.pointLocator.changeTypeId"
						:items="changeTypes"
					></v-select>
				</v-col>
			</v-row>

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
									@click="addMsValue(datapoint.pointLocator.incrementMultistateChange.values)"
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
                :rules="[ruleNotNull]"
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
                :rules="[ruleNotNull]"
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
                :rules="[ruleNotNull]"
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
                :rules="[ruleNotNull]"
							></v-text-field>
						</v-col>
						<v-col>
							<v-text-field
								type="Number"
								label="Maximum Change"
								v-model="datapoint.pointLocator.brownianChange.maxChange"
                :rules="[ruleNotNull]"
							></v-text-field>
						</v-col>
						<v-col>
							<v-text-field
								type="Number"
								label="Start value"
								v-model="datapoint.pointLocator.brownianChange.startValue"
                :rules="[ruleNotNull]"
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
                :rules="[ruleNotNull]"
							></v-text-field>
						</v-col>
						<v-col>
							<v-text-field
								type="Number"
								label="Maximum Change"
								v-model="datapoint.pointLocator.incrementAnalogChange.change"
                :rules="[ruleNotNull]"
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
                :rules="[ruleNotNull]"
							></v-text-field>
						</v-col>
					</v-row>

					<v-row v-if="datapoint.pointLocator.changeTypeId === DataChangeTypes.NO_CHANGE">
						<v-col>
							<v-text-field
								type="Number"
								label="Start value"
								v-model="datapoint.pointLocator.noChange.startValue"
                :rules="[ruleNotNull]"
							></v-text-field>
						</v-col>
					</v-row>

					<v-row
						v-if="datapoint.pointLocator.changeTypeId === DataChangeTypes.RANDOM_ANALOG"
					>
						<v-col>
							<v-text-field
								type="Number"
								label="Minimum"
								v-model="datapoint.pointLocator.randomAnalogChange.min"
							></v-text-field>
						</v-col>
						<v-col>
							<v-text-field
								type="Number"
								label="Maximum"
								v-model="datapoint.pointLocator.randomAnalogChange.max"
							></v-text-field>
						</v-col>
						<v-col>
							<v-text-field
								type="Number"
								label="Start value"
								v-model="datapoint.pointLocator.randomAnalogChange.startValue"
								:rules="[ruleNotNull]"
							></v-text-field>
							<!-- TODO: Add rule for not null -->
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
								label="Attraction Point ID"
								v-model="datapoint.pointLocator.analogAttractorChange.attractionPointId"
                :rules="[ruleNotNull]"
							></v-text-field>
						</v-col>
						<v-col>
							<v-text-field
								type="Number"
								label="volatility"
								v-model="datapoint.pointLocator.analogAttractorChange.volatility"
                :rules="[ruleNotNull]"
							></v-text-field>
						</v-col>
						<v-col>
							<v-text-field
								type="Number"
								label="maxChange"
								v-model="datapoint.pointLocator.analogAttractorChange.maxChange"
                :rules="[ruleNotNull]"
							></v-text-field>
						</v-col>
						<v-col>
							<v-text-field
								type="Number"
								label="Start value"
								v-model="datapoint.pointLocator.analogAttractorChange.startValue"
                :rules="[ruleNotNull]"
							></v-text-field>
						</v-col>
					</v-row>
				</v-col>
			</v-row>

			<!-- Alphanumeric -->
			<v-row v-if="datapoint.pointLocator.dataTypeId === DataTypes.APLHANUMERIC">
				<v-col>
					<v-text-field
						label="Initial Value"
						v-model="datapoint.pointLocator.noChange.startValue"
            :rules="[ruleNotNull]"
					></v-text-field>
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
			ruleNotNull: (v) => !!v || this.$t('validation.rule.notNull'),
		};
	},

	computed: {
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
	},

  watch: {
    'datapoint.pointLocator.dataTypeId': {
      handler(newValue) {
        if (newValue === DataTypes.BINARY)
          this.datapoint.pointLocator.noChange.startValue = true;
        else
          this.datapoint.pointLocator.noChange.startValue = "";
        this.datapoint.pointLocator.changeTypeId = 5;
      },
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
