<template>
	<div>
		<DataPointCreation
			v-if="!pointLocatorTest"
			title="ModBus IP Data Point"
			:creator="createMode"
			:datapoint="datapoint"
			:settableDisabled="settingsInputDisabled"
			@cancel="cancel()"
			@accept="save()"
		>
			<template v-slot:selector>
				<v-select
					v-model="datapoint.pointLocator.dataTypeId"
					:items="datapointTypes"
					disabled
				></v-select>
			</template>

			<v-row>
				<v-col cols="2">
					<v-text-field
						label="Slave id"
						v-model="datapoint.pointLocator.slaveId"
					></v-text-field>
				</v-col>
				<v-col cols="4">
					<v-select
						label="Register range"
						v-model="datapoint.pointLocator.range"
						:items="registerRanges"
						@change="changeRegisterRange"
					></v-select>
				</v-col>
				<v-col cols="6">
					<v-select
						label="Modbus data type"
						v-model="datapoint.pointLocator.modbusDataType"
						:items="modbusDataTypes"
						:disabled="modbusDataTypeDiabled"
						@change="changeModbusDataType"
					></v-select>
				</v-col>

				<v-col cols="2">
					<v-text-field
						label="Offset"
						v-model="datapoint.pointLocator.offset"
					></v-text-field>
				</v-col>

				<v-col v-if="settingsCharVisible">
					<v-text-field
						label="Number of registers"
						v-model="datapoint.pointLocator.registerCount"
					></v-text-field>
				</v-col>
				<v-col v-if="settingsCharVisible">
					<v-text-field
						label="Character encoding"
						v-model="datapoint.pointLocator.charset"
					></v-text-field>
				</v-col>

				<v-col v-if="settingsBinaryVisible">
					<v-text-field label="Bit" v-model="datapoint.pointLocator.bit"></v-text-field>
				</v-col>

				<v-col v-if="settingsNumericVisible">
					<v-text-field
						label="Multiplier"
						v-model="datapoint.pointLocator.multiplier"
					></v-text-field>
				</v-col>
				<v-col v-if="settingsNumericVisible">
					<v-text-field
						label="Additive"
						v-model="datapoint.pointLocator.additive"
					></v-text-field>
				</v-col>
			</v-row>
		</DataPointCreation>
		<div v-else>
			<v-row>
				<v-col cols="2">
					<v-text-field
						label="Slave id"
						v-model="datapoint.pointLocator.slaveId"
					></v-text-field>
				</v-col>
				<v-col cols="4">
					<v-select
						label="Register range"
						v-model="datapoint.pointLocator.range"
						:items="registerRanges"
						@change="changeRegisterRange"
					></v-select>
				</v-col>
				<v-col cols="6">
					<v-select
						label="Modbus data type"
						v-model="datapoint.pointLocator.modbusDataType"
						:items="modbusDataTypes"
						:disabled="modbusDataTypeDiabled"
						@change="changeModbusDataType"
					></v-select>
				</v-col>

				<v-col cols="2">
					<v-text-field
						label="Offset"
						v-model="datapoint.pointLocator.offset"
					></v-text-field>
				</v-col>

				<v-col v-if="settingsCharVisible">
					<v-text-field
						label="Number of registers"
						v-model="datapoint.pointLocator.registerCount"
					></v-text-field>
				</v-col>
				<v-col v-if="settingsCharVisible">
					<v-text-field
						label="Character encoding"
						v-model="datapoint.pointLocator.charset"
					></v-text-field>
				</v-col>

				<v-col v-if="settingsBinaryVisible">
					<v-text-field label="Bit" v-model="datapoint.pointLocator.bit"></v-text-field>
				</v-col>

				<v-col v-if="settingsNumericVisible">
					<v-text-field
						label="Multiplier"
						v-model="datapoint.pointLocator.multiplier"
					></v-text-field>
				</v-col>
				<v-col v-if="settingsNumericVisible">
					<v-text-field
						label="Additive"
						v-model="datapoint.pointLocator.additive"
					></v-text-field>
				</v-col>
			</v-row>
		</div>
	</div>
</template>
<script>
import DataPointCreation from '../DataPointCreation.vue';
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
		pointLocatorTest: {
			default: false,
			type: Boolean,
		},
	},

	data() {
		return {
			settingsInputDisabled: false,
			settingsNumericVisible: false,
			settingsCharVisible: false,
			settingsBinaryVisible: false,
			DataTypes: DataTypes,
			DataChangeTypes: DataChangeTypes,
			registerRanges: [
				{
					text: 'Coil Status',
					value: 1,
				},
				{
					text: 'Input Status',
					value: 2,
				},
				{
					text: 'Holding register',
					value: 3,
				},
				{
					text: 'Input register',
					value: 4,
				},
			],
			modbusDataTypeDiabled: true,
			modbusDataTypes: [
				{
					text: 'Binary',
					value: 1,
				},
				{
					text: '2 byte unsigned integer',
					value: 2,
				},
				{
					text: '2 byte signed integer',
					value: 3,
				},
				{
					text: '2 byte BCD',
					value: 16,
				},
				{
					text: '4 byte unsigned integer',
					value: 4,
				},
				{
					text: '4 byte signed integer',
					value: 5,
				},
				{
					text: '4 byte unsigned integer swapped',
					value: 6,
				},
				{
					text: '4 byte signed integer swapped',
					value: 7,
				},
				{
					text: '4 byte float',
					value: 8,
				},
				{
					text: '4 byte float swapped',
					value: 9,
				},
				{
					text: '4 byte float swapped inverted',
					value: 21,
				},
				{
					text: '4 byte BCD',
					value: 17,
				},
				{
					text: '8 byte unsigned integer',
					value: 10,
				},
				{
					text: '8 byte signed integer',
					value: 11,
				},
				{
					text: '8 byte unsigned integer swapped',
					value: 12,
				},
				{
					text: '8 byte signed integer swapped',
					value: 13,
				},
				{
					text: '8 byte float',
					value: 14,
				},
				{
					text: '8 byte float swapped',
					value: 15,
				},
				{
					text: 'Fixed length string',
					value: 18,
				},
				{
					text: 'Variable length string',
					value: 19,
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

	mounted() {
		this.changeRegisterRange();
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

		changeRegisterRange() {
			if (
				this.datapoint.pointLocator.range === 1 ||
				this.datapoint.pointLocator.range === 2
			) {
				this.datapoint.pointLocator.dataTypeId = 1;
				this.modbusDataTypeDiabled = true;
				this.settingsNumericVisible = false;
				this.settingsCharVisible = false;
				this.settingsBinaryVisible = false;
			} else {
				this.modbusDataTypeDiabled = false;
				this.changeModbusDataType();
			}
			if (
				this.datapoint.pointLocator.range === 1 ||
				this.datapoint.pointLocator.range === 3
			) {
				this.settingsInputDisabled = false;
			} else {
				this.datapoint.settable = false;
				this.datapoint.pointLocator.settable = false;
				this.settingsInputDisabled = true;
			}
		},

		changeModbusDataType() {
			switch (this.datapoint.pointLocator.modbusDataType) {
				case 1:
					this.datapoint.pointLocator.dataTypeId = 1;
					this.settingsNumericVisible = false;
					this.settingsCharVisible = false;
					this.settingsBinaryVisible = true;
					break;
				case 2:
				case 3:
				case 4:
				case 5:
				case 6:
				case 7:
				case 17:
				case 10:
				case 11:
				case 12:
				case 13:
				case 16:
					this.datapoint.pointLocator.dataTypeId = 2;
					this.settingsNumericVisible = true;
					this.settingsCharVisible = false;
					this.settingsBinaryVisible = false;
					break;
				case 8:
				case 9:
				case 21:
				case 14:
				case 15:
					this.datapoint.pointLocator.dataTypeId = 3;
					this.settingsNumericVisible = true;
					this.settingsCharVisible = false;
					this.settingsBinaryVisible = false;
					break;
				default:
					this.datapoint.pointLocator.dataTypeId = 4;
					this.settingsNumericVisible = false;
					this.settingsCharVisible = true;
					this.settingsBinaryVisible = false;
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
