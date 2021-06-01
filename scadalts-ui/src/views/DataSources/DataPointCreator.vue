<template>
    <v-dialog v-model="dialogVisible">
		
        <component :is="`${datasourceType}`" :datapoint="datapoint" @canceled="onCanceled()" @saved="onSaved($event)">
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
			datapoint: null,
		};
	},

	methods: {
		showDialog(item, datapoint) {
			this.dialogVisible = true;
			if(!!datapoint) {
				this.datapoint = datapoint;
			} else {
				this.datapoint = {
					name: '',
					xid: 'DP_VDS_',
					settable: false,
					type: 'Binary',
				}
			}
			console.log(this.datapoint)
            this.datasourceType = `${item.type}pointeditor`;
		},

        onCanceled() {
            this.dialogVisible = false;
        },

        onSaved(event) {
			this.dialogVisible = false;
			this.$emit('onSaved', event);
        }
	},
    
}
</script>