<template>
	<div>
		<DataPointCreation
			title="SNPM Data Point"
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
					<v-text-field
						label="OID"
						v-model="datapoint.pointLocator.oid"
						:rules="[ruleNotNull]"
						required
					></v-text-field>
				</v-col>
				<!-- Binary -->
				<v-col v-if="datapoint.pointLocator.dataTypeId === DataTypes.BINARY">
					<v-text-field
						type="Number"
						label="Binary 0 value"
						v-model="datapoint.pointLocator.binary0Value"
					></v-text-field>
				</v-col>
				<v-col>
					<v-select
						label="Set type"
						v-model="datapoint.pointLocator.setType"
						:items="snmpSetTypes"
					>
					</v-select>
				</v-col>
				<v-col>
					<v-select
						label="Polling"
						v-model="datapoint.pointLocator.trapOnly"
						:items="snmpPollingTypes"
					>
					</v-select>
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
	},

	computed: {
		datapointTypes() {
			return this.$store.state.dataSourceState.datapointTypes;
		},
	},

	data() {
		return {
			DataTypes: DataTypes,
			DataChangeTypes: DataChangeTypes,
			ruleNotNull: (v) => !!v || this.$t('validation.rule.notNull'),
			snmpPollingTypes: [
				{
					text: 'Trap only',
					value: true,
				},
				{
					text: 'Poll and Trap',
					value: false,
				},
			],
			snmpSetTypes: [
				{
					text: 'Not settable',
					value: 0,
				},
				{
					text: 'Integer 32',
					value: 1,
				},
				{
					text: 'Octet String',
					value: 2,
				},
				{
					text: 'Object Identifier',
					value: 3,
				},
				{
					text: 'IP Address',
					value: 4,
				},
				{
					text: 'Counter 32',
					value: 5,
				},
				{
					text: 'Gauge 32',
					value: 6,
				},
				{
					text: 'Time ticks',
					value: 7,
				},
				{
					text: 'Opaque',
					value: 8,
				},
				{
					text: 'Counter 64',
					value: 9,
				},
			],
		};
	},

	methods: {
		cancel() {
			this.$emit('canceled');
		},

		save() {
			this.$emit('saved', this.datapoint);
		},
	},
};
</script>
<style></style>
