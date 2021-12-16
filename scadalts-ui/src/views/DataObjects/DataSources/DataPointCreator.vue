<template>
	<v-dialog v-model="dialogVisible" max-width="800">
		<component
			v-if="dialogVisible"
			:is="`${datasourceType}`"
			:datapoint="datapoint"
			:createMode="!editMode"
			:visible="dialogVisible"
			@canceled="onCanceled()"
			@saved="onSaved($event)"
		>
			<template v-slot:title> Create Data Log</template>

			<template v-slot:action> Create </template>
		</component>
	</v-dialog>
</template>
<script>
import dataSourceMixin from '../../../components/datasources/DataSourcesMixin.js';

export default {
	mixins: [dataSourceMixin],

	data() {
		return {
			datasourceType: 'virtualdatasourcepointeditor',
			dialogVisible: false,
			editMode: false,
			datapoint: null,
			datasource: null,
		};
	},

	methods: {
		async showDialog(item, datapoint, datasourceType) {
			console.log(datapoint);
			if (!!datapoint) {
				this.datapoint = datapoint;
				this.editMode = true;
			} else {
				this.editMode = false;
				this.datapoint = this.createInitialDataPoint(datasourceType, item.id);
				try {
					this.datapoint.xid = await this.$store.dispatch('getUniqueDataPointXid');
				} catch (e) {
					console.error('Failed to auto-generate unique Export ID Number');
				}
			}
			this.datasource = item;
			this.datasourceType = `${datasourceType}pointeditor`;
			console.log(this.datapoint);
			this.dialogVisible = true;
		},

		onCanceled() {
			this.dialogVisible = false;
		},

		onSaved(event) {
			this.dialogVisible = false;
			if (this.editMode) {
				this.$emit('updated', { dp: this.datasource, e: event });
			} else {
				this.$emit('saved', { dp: this.datasource, e: event });
			}
		},
	},
};
</script>
