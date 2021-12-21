<template>
	<div>
		<v-row>
			<v-col> OID: {{ datapoint.pointLocator.oid }} </v-col>
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
