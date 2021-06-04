<template>
	<v-dialog v-model="dialogVisible">
		<component
			:is="`${datasourceType}`"
			:datapoint="datapoint"
			:createMode="!editMode"
			@canceled="onCanceled()"
			@saved="onSaved($event)"
		>
			<template v-slot:title> Create Data Log</template>

			<template v-slot:action> Create </template>
		</component>
	</v-dialog>
</template>
<script>
import dataSourceMixin from '../../components/datasources/DataSourcesMixin.js';

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
		showDialog(item, datapoint) {
			this.dialogVisible = true;
			if (!!datapoint) {
				this.datapoint = datapoint;
				this.editMode = true;
			} else {
				this.editMode = false;
				this.datapoint = {
					name: '',
					xid: 'DP_VDS_',
					settable: false,
					type: 'Binary',
				};
			}
			this.datasource = item;
			console.log(this.datapoint);
			this.datasourceType = `${item.type}pointeditor`;
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
