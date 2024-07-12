<template>
	<v-col cols="12" md="6">
		<v-card>
			<v-card-title>
				{{ $t('systemsettings.dataRetention.title') }}
				<span v-if="isDataRetentionSettingsEdited">*</span>
				<v-spacer></v-spacer>
				<v-tooltip bottom>
					<template v-slot:activator="{ on, attrs }">
						<v-btn fab icon @click="showPurgeNowDialog()" v-bind="attrs" v-on="on">
							<v-icon>mdi-delete-sweep</v-icon>
						</v-btn>
					</template>
					<span>{{ $t('systemsettings.tooltip.purgeNow') }}</span>
				</v-tooltip>
				<v-tooltip bottom>
					<template v-slot:activator="{ on, attrs }">
						<v-btn fab icon @click="showPurgeDataDialog()" v-bind="attrs" v-on="on">
							<v-icon>mdi-delete</v-icon>
						</v-btn>
					</template>
					<span>{{ $t('systemsettings.tooltip.purgedata') }}</span>
				</v-tooltip>
			</v-card-title>

			<v-card-text>
				<v-row v-if="!!dataRetentionSettings">
					<v-col cols="6">
						<v-text-field
							v-model="dataRetentionSettings.eventPurgePeriods"
							type="number"
							:label="$t('systemsettings.dataRetention.purge.events')"
							@input="watchDataChange()"
							dense
						></v-text-field>
					</v-col>
					<v-col cols="6">
						<v-select
							@change="watchDataChange()"
							v-model="dataRetentionSettings.eventPurgePeriodType"
							:items="eventPurgePeriodTypeItems"
							item-value="value"
							item-text="text"
							dense
						></v-select>
					</v-col>

					<v-col cols="6">
						<v-text-field
							v-model="dataRetentionSettings.reportPurgePeriodType"
							type="number"
							:label="$t('systemsettings.dataRetention.purge.reports')"
							@input="watchDataChange()"
							dense
						></v-text-field>
					</v-col>
					<v-col cols="6">
						<v-select
							@change="watchDataChange()"
							v-model="dataRetentionSettings.reportPurgePeriodType"
							:items="eventPurgePeriodTypeItems"
							item-value="value"
							item-text="text"
							dense
						></v-select>
					</v-col>

					<v-col cols="6">
						<v-text-field
							v-model="dataRetentionSettings.futureDateLimitPeriods"
							type="number"
							:label="$t('systemsettings.dataRetention.purge.future')"
							@input="watchDataChange()"
							dense
						></v-text-field>
					</v-col>
					<v-col cols="6">
						<v-select
							@change="watchDataChange()"
							v-model="dataRetentionSettings.futureDateLimitPeriodType"
							:items="futureDateLimitPeriodTypeItems"
							item-value="value"
							item-text="text"
							dense
						></v-select>
					</v-col>

					<v-col cols="12">
						<v-text-field
							v-model="dataRetentionSettings.valuesLimitForPurge"
							:label="$t('systemsettings.dataRetention.valuesLimitForPurge')"
							@input="watchDataChange()"
							dense
						></v-text-field>
					</v-col>
				</v-row>
			</v-card-text>
		</v-card>

		<ConfirmationDialog
			:btnvisible="false"
			ref="confirmationPurgeDialog"
			:dialog="confirmPurgeDialog"
			:title="$t('systemsettings.alert.purgedata.title')"
			:message="$t('systemsettings.alert.purgedata')"
			@result="onDialogResult"
		></ConfirmationDialog>

	</v-col>
</template>
<script>
import ConfirmationDialog from '@/layout/dialogs/ConfirmationDialog'

export default {
	name: 'DataRetentionSettingsComponent',

    components: {
        ConfirmationDialog
    },

	data() {
		return {
			dialogOp: null,
			confirmPurgeDialog: false,
			dataRetentionSettings: null,
			dataRetentionSettingsStore: null,
			isDataRetentionSettingsEdited: false,
			futureDateLimitPeriodTypeItems: [
				{ value: 2, text: this.$t('timeperiod.minutes') },
				{ value: 3, text: this.$t('timeperiod.hours') },
			],
			eventPurgePeriodTypeItems: [
				{ value: 4, text: this.$t('timeperiod.days') },
				{ value: 5, text: this.$t('timeperiod.weeks') },
				{ value: 6, text: this.$t('timeperiod.months') },
				{ value: 7, text: this.$t('timeperiod.years') },
			],
		};
	},

	mounted() {
		this.fetchData();
	},

	methods: {
		async fetchData() {
			this.dataRetentionSettings = await this.$store.dispatch('getDataRetentionSettings');
			this.dataRetentionSettingsStore = this.copyDataFromStore();
		},

		saveData() {
			console.log('Saved!');
			this.$store.commit('setDataRetentionSettings', this.dataRetentionSettings);
			this.dataRetentionSettingsStore = this.copyDataFromStore();
			this.$store
				.dispatch('saveDataRetentionSettings')
				.then((resp) => {
					if (resp) {
						this.restoreData();
						this.$store.dispatch('showSuccessNotification', this.$t('systemsettings.notification.save.dataRetention'));						
					}
				})
				.catch(() => {
					this.$store.dispatch('showErrorNotification', this.$t('systemsettings.notification.fail'));
				});
		},

		restoreData() {
			this.fetchData();
			this.isDataRetentionSettingsEdited = false;
		},

		copyDataFromStore() {
			return JSON.parse(
				JSON.stringify(this.$store.state.systemSettings.dataRetentionSettings),
			);
		},

		async watchDataChange() {
			this.isDataRetentionSettingsEdited = await this.isDataChanged();
			this.emitData(this.isDataRetentionSettingsEdited);
		},

		async isDataChanged() {
			return !(await this.$store.dispatch('configurationEqual', {
				object1: this.dataRetentionSettings,
				object2: this.dataRetentionSettingsStore,
			}));
		},

		emitData(changed) {
			this.$emit('changed', {
				component: 'dataRetentionSettingsComponent',
				title: 'systemsettings.dataRetention.title',
				changed: changed,
				data: this.sumarizeDataChanges(),
				valid: true,
			});
		},

		sumarizeDataChanges() {
			let data = [];
			for (let key in this.dataRetentionSettings) {
				if (this.dataRetentionSettings[key] !== this.dataRetentionSettingsStore[key]) {
					data.push({
						label: `systemsettings.dataRetention.${key}`,
						originalData: this.convertTimePeriod(
							this.dataRetentionSettingsStore[key],
							key,
						),
						changedData: this.convertTimePeriod(this.dataRetentionSettings[key], key),
					});
				}
			}
			return data;
		},

		showPurgeDataDialog() {
			this.dialogOp = 'purgeData';
			this.confirmPurgeDialog = true;
		},

		showPurgeNowDialog() {
			this.dialogOp = 'purgeNow';
			this.confirmPurgeDialog = true;
		},

		onDialogResult(result) {
            this.confirmPurgeDialog = false;
			if (!!result) {
				if (this.dialogOp === 'purgeData') {
					this.purgeData();
				} else if (this.dialogOp === 'purgeNow') {
					this.purgeNow();
				}
			}
		},

		purgeData() {
			this.$store.dispatch('purgeData').then((resp) => {
				if (resp === true) {
					this.$store.dispatch('showSuccessNotification', this.$t('systemsettings.notification.purgedata'));
				}
			});
		},

		purgeNow() {
			this.$store
				.dispatch('purgeNow')
				.then((r) => {
					if (r.status === 'done') {
						this.$store.dispatch('showSuccessNotification', this.$t('systemsettings.notification.purgedata'));
					}
				})
				.catch(() => {
					this.$store.dispatch('showErrorNotification', this.$t('systemsettings.notification.fail'));
				});
		},

		convertTimePeriod: function (value, key) {
			if (key.includes('Type')) {
				switch (Number(value)) {
					case 2:
						return this.$t('timeperiod.minutes');
					case 3:
						return this.$t('timeperiod.hours');
					case 4:
						return this.$t('timeperiod.days');
					case 5:
						return this.$t('timeperiod.weeks');
					case 6:
						return this.$t('timeperiod.months');
					case 7:
						return this.$t('timeperiod.years');
				}
			}
			return value;
		},
	},
};
</script>
<style></style>
