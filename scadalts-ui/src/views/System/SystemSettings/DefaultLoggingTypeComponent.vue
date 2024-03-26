<template>
	<v-col cols="12" md="6">
		<v-card>
			<v-card-title>
				{{ $t('systemsettings.loggingtype.title') }}
				<span v-if="isDefaultLoggingTypeEdited">*</span>
			</v-card-title>
			<v-card-text>
				<v-select
					@change="watchLoggingTypeChange"
					v-model="defaultLoggingType"
					:items="loggingTypeList"
					item-value="id"
					item-text="label"
					dense
				></v-select>
			</v-card-text>
		</v-card>
	</v-col>
</template>
<script>
export default {
	name: 'DefaultLoggingTypeSettingsComponent',
	data() {
		return {
			defaultLoggingType: undefined,
			defaultLoggingTypeStore: undefined,
			isDefaultLoggingTypeEdited: false,
		};
	},

	computed: {
		loggingTypeList() {
			return this.$store.state.dataPoint.loggingTypeList;
		},
	},

	mounted() {
		this.fetchData();
	},
	methods: {
		async fetchData() {
			this.defaultLoggingType = await this.$store.dispatch('getDefaultLoggingType');
			this.defaultLoggingTypeStore = this.copyDataFromStore();
		},
		saveData() {
			console.log('Saved!');
			this.$store.commit('setDefaultLoggingType', this.defaultLoggingType);
			this.defaultLoggingTypeStore = this.copyDataFromStore();
			this.$store
				.dispatch('saveDefaultLoggingType')
				.then((resp) => {
					if (resp) {
						this.restoreData();
						this.$store.dispatch('showSuccessNotification',this.$t('systemsettings.notification.save.logging'));
					}
				})
				.catch(() => {
					this.$store.dispatch('showErrorNotification',this.$t('systemsettings.notification.fail'));
				});
		},
		restoreData() {
			this.fetchData();
			this.defaultLoggingType = null;
			this.isDefaultLoggingTypeEdited = false;
		},
		copyDataFromStore() {
			return JSON.parse(
				JSON.stringify(this.$store.state.systemSettings.defaultLoggingType),
			);
		},
		async watchLoggingTypeChange() {
			this.isDefaultLoggingTypeEdited = await this.isDataChanged();
			this.emitData(this.isDefaultLoggingTypeEdited);
		},
		async isDataChanged() {
			return this.defaultLoggingType !== this.defaultLoggingTypeStore;
		},
		emitData(changed) {
			this.$emit('changed', {
				component: 'defaultLoggingTypeSettingsComponent',
				title: 'systemsettings.loggingtype.title',
				changed: changed,
				data: this.sumarizeDataChanges(),
				valid: true,
			});
		},
		sumarizeDataChanges() {
			let data = [
				{
					label: `systemsettings.loggingtype.value`,
					originalData: this.loggingTypeList[this.defaultLoggingTypeStore - 1].label,
					changedData: this.loggingTypeList[this.defaultLoggingType - 1].label,
				},
			];
			return data;
		},
	},
};
</script>
<style></style>
