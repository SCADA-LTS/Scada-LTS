<template>
	<div>
		<v-row>
			<v-col cols="2"> Slave: {{ datapoint.pointLocator.slaveId }} </v-col>
			<v-col cols="2"> Offset: {{ datapoint.pointLocator.offset }} </v-col>
			<v-col>
				Range: {{ $t(`datasource.modbus.range.${datapoint.pointLocator.range}`) }}
			</v-col>
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
