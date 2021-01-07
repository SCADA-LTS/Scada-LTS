<template>
	<v-col cols="12" md="6">
		<v-card>
			<v-card-title>
				{{ $t('systemsettings.loggingtype.title') }}
				<span v-if="isLoggingTypeSettingsEdited">*</span>
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
			loggingTypeList: [
				{
					id: 1,
					type: 'ON_CHANGE',
					label: this.$t('pointEdit.logging.type.change'),
				},
				{ id: 2, type: 'ALL', label: this.$t('pointEdit.logging.type.all') },
				{ id: 3, type: 'NONE', label: this.$t('pointEdit.logging.type.never') },
				{
					id: 4,
					type: 'INTERVAL',
					label: this.$t('pointEdit.logging.type.interval'),
				},
				{
					id: 5,
					type: 'ON_TS_CHANGE',
					label: this.$t('pointEdit.logging.type.tsChange'),
				},
			],
		};
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
						this.$notify({
							placement: 'top-right',
							type: 'success',
							content: this.$t('systemsettings.notification.save.logging'),
						});
					}
				})
				.catch(() => {
					this.$notify({
						placement: 'top-right',
						type: 'danger',
						content: this.$t('systemsettings.notification.fail'),
					});
				});
		},
		restoreData() {
			this.fetchData();
			this.defaultLoggingType = null;
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
