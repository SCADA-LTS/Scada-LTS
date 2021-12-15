<template>
	<div>
		<!-- DataTypes.NUMERIC -->
		<v-row :dense="true" v-if="datapoint.pointLocator.dataTypeId === DataTypes.NUMERIC">
			<v-col cols="3"> Numeric </v-col>
			<v-col cols="9">
				<v-row v-if="datapoint.pointLocator.changeTypeId === DataChangeTypes.BROWNIAN">
					<v-col cols="3"> Brownian </v-col>
					<v-col cols="3">
						<span>Min: {{ datapoint.pointLocator.brownianChange.min }}</span>
					</v-col>
					<v-col cols="3">
						<span>Max: {{ datapoint.pointLocator.brownianChange.max }}</span>
					</v-col>
					<v-col cols="3">
						<span>Change: {{ datapoint.pointLocator.brownianChange.maxChange }}</span>
					</v-col>
				</v-row>
				<v-row
					v-if="datapoint.pointLocator.changeTypeId === DataChangeTypes.INCREMENT_ANALOG"
				>
					<v-col cols="3"> Increment </v-col>
					<v-col cols="3">
						<span>Min: {{ datapoint.pointLocator.incrementAnalogChange.min }}</span>
					</v-col>
					<v-col cols="3">
						<span>Max: {{ datapoint.pointLocator.incrementAnalogChange.max }}</span>
					</v-col>
					<v-col cols="3">
						<span>Change: {{ datapoint.pointLocator.incrementAnalogChange.change }}</span>
					</v-col>
				</v-row>
				<v-row
					v-if="datapoint.pointLocator.changeTypeId === DataChangeTypes.RANDOM_ANALOG"
				>
					<v-col cols="3"> Random </v-col>
					<v-col cols="3">
						<span>Min: {{ datapoint.pointLocator.randomAnalogChange.min }}</span>
					</v-col>
					<v-col cols="3">
						<span>Max: {{ datapoint.pointLocator.randomAnalogChange.max }}</span>
					</v-col>
				</v-row>
				<v-row v-if="datapoint.pointLocator.changeTypeId === DataChangeTypes.NO_CHANGE">
					<v-col> No Change </v-col>
				</v-row>
			</v-col>
		</v-row>

		<!-- DataTypes.MULTISTATE -->
		<v-row
			:dense="true"
			v-if="datapoint.pointLocator.dataTypeId === DataTypes.MULTISTATE"
		>
			<v-col cols="3"> Multistate </v-col>
			<v-col cols="9">
				<v-row
					v-if="
						datapoint.pointLocator.changeTypeId === DataChangeTypes.INCREMENT_MULTISTATE
					"
				>
					<v-col>Increment</v-col>
				</v-row>
				<v-row
					v-if="datapoint.pointLocator.changeTypeId === DataChangeTypes.RANDOM_MULTISTATE"
				>
					<v-col>Random</v-col>
				</v-row>
				<v-row v-if="datapoint.pointLocator.changeTypeId === DataChangeTypes.NO_CHANGE">
					<v-col>No Change</v-col>
				</v-row>
			</v-col>
		</v-row>

		<!-- DataTypes.BINARY -->
		<v-row :dense="true" v-if="datapoint.pointLocator.dataTypeId === DataTypes.BINARY">
			<v-col cols="3"> Binary </v-col>
			<v-col cols="9">
				<v-row
					v-if="datapoint.pointLocator.changeTypeId === DataChangeTypes.ALTERNATE_BOOLEAN"
				>
					<v-col>Alternate</v-col>
				</v-row>
				<v-row
					v-if="datapoint.pointLocator.changeTypeId === DataChangeTypes.RANDOM_BOOLEAN"
				>
					<v-col>Random</v-col>
				</v-row>
				<v-row v-if="datapoint.pointLocator.changeTypeId === DataChangeTypes.NO_CHANGE">
					<v-col>No Change</v-col>
				</v-row>
			</v-col>
		</v-row>

		<!-- DataTypes.APLHANUMERIC -->
		<v-row
			:dense="true"
			v-if="datapoint.pointLocator.dataTypeId === DataTypes.APLHANUMERIC"
		>
			<v-col cols="12"> Alphanumeric </v-col>
		</v-row>
	</div>
</template>
<script>
import { DataTypes, DataChangeTypes } from '@/store/dataSource/constants';
export default {
	props: ['datapoint'],

	data() {
		return {
			DataTypes,
			DataChangeTypes,
		};
	},
};
</script>
<style scoped></style>
