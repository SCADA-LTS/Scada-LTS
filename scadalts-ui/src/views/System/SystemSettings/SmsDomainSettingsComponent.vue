<template>
	<v-col cols="12" md="6">
		<v-card>
			<v-card-title>
				{{ $t('systemsettings.smsdomain.title') }}
				<span v-if="isSmsDomainSettingsEdited">*</span>
			</v-card-title>
			<v-card-text>
				<v-row>
					<v-col cols="12">
						<v-text-field
							v-model="domainSettings"
							:label="$t('systemsettings.smsdomain.label')"
							@input="watchDataChange()"
							dense
						></v-text-field>
					</v-col>
				</v-row>
			</v-card-text>
		</v-card>
		
	</v-col>
</template>

<script>
/**
 * @author grzegorz.bylica@gmail.com
 * Modified for New System Settings by Radek Jajko
 *
 */

export default {
	name: 'SmsDomainSettingsComponent',

	data() {
		return {
			domainSettings: undefined,
			domainSettingsStore: undefined,
			isSmsDomainSettingsEdited: false,
		};
	},

	mounted() {
		this.fetchData();
	},

	methods: {
		async fetchData() {
			this.domainSettings = await this.$store.dispatch('getSmsDomainSettings');
			this.domainSettingsStore = this.copyDataFromStore();
		},

		saveData() {
			this.$store.commit('setSmsDomainSettings', this.domainSettings);
			this.domainSettingsStore = this.copyDataFromStore();
			this.$store
				.dispatch('saveSmsDomainSettings')
				.then((resp) => {
					if (resp) {
						this.restoreData();
						this.$store.dispatch('showSuccessNotification', this.$t('systemsettings.notification.save.smsdomain'));
					}
				})
				.catch(() => {
					this.$store.dispatch('showErrorNotification', this.$t('systemsettings.notification.fail'));
				});
		},

		restoreData() {
			this.fetchData();
			this.isSmsDomainSettingsEdited = false;
		},

		copyDataFromStore() {
			return JSON.parse(
				JSON.stringify(this.$store.state.systemSettings.smsDomainSettings),
			);
		},

		async watchDataChange() {
			this.isSmsDomainSettingsEdited = await this.isDataChanged();
			this.emitData(this.isSmsDomainSettingsEdited);
		},

		async isDataChanged() {
			return this.domainSettingsStore.localeCompare(this.domainSettings) !== 0;
		},

		emitData(changed) {
			this.$emit('changed', {
				component: 'smsDomainSettingsComponent',
				title: 'systemsettings.smsdomain.title',
				changed: changed,
				data: this.sumarizeDataChanges(),
				valid: true,
			});
		},

		sumarizeDataChanges() {
			let data = [];
			data.push({
				label: `systemsettings.smsdomain.label`,
				originalData: this.domainSettingsStore,
				changedData: this.domainSettings,
			});
			return data;
		},
	},
};
</script>

<style scoped></style>
