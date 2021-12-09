<template>
	<v-dialog v-model="dialogVisible" max-width="800">
		<component
			:is="`${selectedType}editor`"
			ref="datasourceDialog"
			@canceled="onCanceled()"
			@saved="onSaved($event)"
		>
			<template v-slot:title> Create Data Log</template>
			<template v-slot:selector>
				<v-select v-model="selectedType" :items="dataSourceList"></v-select>
			</template>
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
			dialogVisible: false,
			selectedType: 'virtualdatasource',
		};
	},

	methods: {
		showDialog() {
			this.dialogVisible = true;
			this.$nextTick().then(() => {
				this.$refs.datasourceDialog.onShow();
			});
		},

		onCanceled() {
			this.dialogVisible = false;
		},

		onSaved(event) {
			console.debug('DataSourceCreator.vue::onSaved()');
			this.dialogVisible = false;
			event.type = this.selectedType;
			this.$emit('savedDS', event);
		},
	},
};
</script>
<style scoped></style>
