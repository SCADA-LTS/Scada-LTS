<template>
    <v-dialog v-model="dialogVisible">
		
        <component :is="`${datasourceType}`" @canceled="onCanceled()" @saved="onSaved($event)">
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
		};
	},

	methods: {
		showDialog(item) {
			this.dialogVisible = true;
            this.datasourceType = `${item.type}pointeditor`;
		},

        onCanceled() {
            this.dialogVisible = false;
        },

        onSaved(event) {
            console.log(event);
        }
	},
    
}
</script>