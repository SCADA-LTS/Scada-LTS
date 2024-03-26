<template>
	<v-col cols="12" md="6">
		<v-card>
			<v-card-title>
				{{ $t('systemsettings.email.title') }}
				<span v-if="isEmailSettingsEdited">*</span>
				<v-spacer></v-spacer>
				<v-tooltip bottom>
					<template v-slot:activator="{ on, attrs }">
						<v-btn fab icon @click="sendTestEmail()" v-bind="attrs" v-on="on">
							<v-icon>mdi-send</v-icon>
						</v-btn>
					</template>
					<span>{{ $t('systemsettings.tooltip.sendtestemail') }}</span>
				</v-tooltip>
			</v-card-title>
			<v-card-text>
				<v-row>
					<v-col cols="6">
						<v-text-field
							v-model="emailSettings.host"
							:label="$t('systemsettings.email.host')"
							@input="watchDataChange()"
							dense
						></v-text-field>
					</v-col>
					<v-col cols="6">
						<v-text-field
							v-model="emailSettings.port"
							:label="$t('systemsettings.email.port')"
							@input="watchDataChange()"
							dense
						></v-text-field>
					</v-col>
					<v-col cols="6">
						<v-text-field
							v-model="emailSettings.from"
							:label="$t('systemsettings.email.address')"
							@input="watchDataChange()"
							dense
						></v-text-field>
					</v-col>
					<v-col cols="6">
						<v-text-field
							v-model="emailSettings.name"
							:label="$t('systemsettings.email.name')"
							@input="watchDataChange()"
							dense
						></v-text-field>
					</v-col>
					<v-col cols="12">
						<v-select
							@change="watchDataChange()"
							v-model="emailSettings.contentType"
							:items="contentTypeItems"
							item-value="value"
							item-text="text"
							:label="$t('systemsettings.email.contentType')"
							dense
						></v-select>
					</v-col>
					<v-col cols="12">
						<v-switch
							v-model="emailSettings.auth"
							:label="$t('systemsettings.email.auth.enable')"
							@click="watchRadioDataChagne(emailSettings.auth, 'auth')"
						></v-switch>
					</v-col>
					<v-col cols="6" v-if="emailSettings.auth">
						<v-text-field
							v-model="emailSettings.username"
							:label="$t('systemsettings.email.username')"
							@input="watchDataChange()"
							dense
						></v-text-field>
					</v-col>
					<v-col cols="6" v-if="emailSettings.auth">
						<v-text-field
							v-model="emailSettings.password"
							:append-icon="show1 ? 'mdi-eye' : 'mdi-eye-off'"
							:type="show1 ? 'text' : 'password'"
							:label="$t('systemsettings.email.password')"
							@input="watchDataChange()"
							@click:append="show1 = !show1"
							dense
						></v-text-field>
					</v-col>
					<v-col cols="12">
						<v-switch
							v-model="emailSettings.tls"
							:label="$t('systemsettings.email.tls.enable')"
							@click="watchRadioDataChagne(emailSettings.tls, 'tls')"
						></v-switch>
					</v-col>
				</v-row>
			</v-card-text>
		</v-card>

	</v-col>
</template>
<script>
import { object } from '@amcharts/amcharts4/core';

export default {
	name: 'EmailSettingsComponent',

	data() {
		return {
			emailSettings: undefined,
			emailSettingsStore: undefined,
			isEmailSettingsEdited: false,
			show1: false,
			contentTypeItems: [
				{
					value: 0,
					text: this.$t('systemsettings.email.contenttype.htmltext'),
				},
				{ value: 1, text: this.$t('systemsettings.email.contenttype.html') },
				{ value: 2, text: this.$t('systemsettings.email.contenttype.text') },
			],
		};
	},

	mounted() {
		this.fetchData();
	},

	methods: {
		async fetchData() {
			this.emailSettings = await this.$store.dispatch('getEmailSettings');
			this.emailSettingsStore = this.copyDataFromStore();
		},

		saveData() {
			console.log('Saved Email!');
			this.$store.commit('setEmailSettings', this.emailSettings);
			this.emailSettingsStore = this.copyDataFromStore();
			this.$store
				.dispatch('saveEmailSettings')
				.then((resp) => {
					if (resp) {
						this.restoreData();
						this.$store.dispatch('showSuccessNotification', this.$t('systemsettings.notification.save.email'));
					}
				})
				.catch(() => {
					this.$store.dispatch('showErrorNotification', this.$t('systemsettings.notification.fail'));
				});
		},

		restoreData() {
			console.log('Restored Email!');
			this.fetchData();
			this.isEmailSettingsEdited = false;
		},

		copyDataFromStore() {
			return JSON.parse(JSON.stringify(this.$store.state.systemSettings.emailSettings));
		},

		watchRadioDataChagne(value, parameter) {
			if (parameter == 'auth') {
				this.emailSettings.auth = value;
			} else if (parameter == 'tls') {
				this.emailSettings.tls = value;
			}
			this.watchDataChange();
		},

		async watchDataChange() {
			this.isEmailSettingsEdited = await this.isDataChanged();
			this.emitData(this.isEmailSettingsEdited);
		},

		async isDataChanged() {
			return !(await this.$store.dispatch('configurationEqual', {
				object1: this.emailSettings,
				object2: this.emailSettingsStore,
			}));
		},

		emitData(changed) {
			this.$emit('changed', {
				component: 'emailSettingsComponent',
				title: 'systemsettings.email.title',
				changed: changed,
				data: this.sumarizeDataChanges(),
				valid: true,
			});
		},

		sumarizeDataChanges() {
			let data = [];
			for (let key in this.emailSettings) {
				if (this.emailSettings[key] !== this.emailSettingsStore[key]) {
					data.push({
						label: `systemsettings.email.${key}`,
						originalData: this.emailSettingsStore[key],
						changedData: this.emailSettings[key],
					});
				}
			}
			return data;
		},

		togglePassword(elementId) {
			let x = document.getElementById(elementId);
			if (x.type === 'password') {
				x.type = 'text';
			} else {
				x.type = 'password';
			}
		},

		sendTestEmail() {
			this.$store
				.dispatch('sendTestEmail')
				.then((resp) => {
					if (resp) {
						this.response = {
							status: true,
							message: `${this.$t('systemsettings.notification.send.email')} ${
								resp.recipient
							}`,
							color: 'success',
						};
					}
				})
				.catch(() => {
					this.response = {
						status: true,
						message: this.$t('systemsettings.notification.fail'),
						color: 'danger',
					};
				});
		},
	},
};
</script>
<style></style>
