<template>
	<v-dialog v-model="dialogVisible">
		<component :is="`${selectedType}`" @canceled="onCanceled()" @saved="onSaved($event)">
			<template v-slot:title> Create Data Log</template>
			<template v-slot:selector>
                <v-select
                    v-model="selectedType"
                    :items="dataSourceList"
                ></v-select>
            </template>
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
			dialogVisible: false,
			selectedType: 'virtualdatasourceeditor',
		};
	},

	methods: {
		showDialog() {
			this.dialogVisible = true;
		},

        onCanceled() {
            this.dialogVisible = false;
        },

        onSaved(event) {
            console.log(event);
        }


	},
};
</script>
<style scoped></style>
