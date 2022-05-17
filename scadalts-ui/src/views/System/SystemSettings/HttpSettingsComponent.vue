<template>
	<v-col cols="12" md="6">
		<v-card>
			<v-card-title>
				{{ $t('systemsettings.http.title') }}
				<span v-if="isHttpSettingsEdited">*</span>
			</v-card-title>
			<v-card-text>
				<v-row>
					<v-col cols="12">
						<v-switch
							v-model="httpSettings.useProxy"
							:label="$t('systemsettings.http.proxy.enable')"
							@click="watchRadioDataChagne(emailSettings.tls)"
						></v-switch>
					</v-col>
					<v-col cols="6" v-if="httpSettings.useProxy">
						<v-text-field
							v-model="httpSettings.host"
							:label="$t('systemsettings.http.proxy.host')"
							@input="watchDataChange()"
							dense
						></v-text-field>
					</v-col>
					<v-col cols="6" v-if="httpSettings.useProxy">
						<v-text-field
							v-model="httpSettings.port"
							type="number"
							:label="$t('systemsettings.http.proxy.port')"
							@input="watchDataChange()"
							dense
						></v-text-field>
					</v-col>
					<v-col cols="6" v-if="httpSettings.useProxy">
						<v-text-field
							v-model="httpSettings.username"
							:label="$t('systemsettings.http.proxy.username')"
							@input="watchDataChange()"
							dense
						></v-text-field>
					</v-col>
					<v-col cols="6" v-if="httpSettings.useProxy">
						<v-text-field
							v-model="httpSettings.password"
							:append-icon="passwordVisible ? 'mdi-eye' : 'mdi-eye-off'"
							:type="passwordVisible ? 'text' : 'password'"
							:label="$t('systemsettings.http.proxy.password')"
							@input="watchDataChange()"
							@click:append="passwordVisible = !passwordVisible"
							dense
						></v-text-field>
					</v-col>
					<v-col cols="12">
                        <v-textarea
                            @change="watchDataChange()"
                            v-model="httpSettings.httpResponseHeaders"
                            :label="$t('systemsettings.http.response.headers')"
                            dense
                        ></v-textarea>
                    </v-col>
				</v-row>
			</v-card-text>
		</v-card>
	</v-col>
</template>
<script>
import { object } from '@amcharts/amcharts4/core';

export default {
	name: 'HttpSettingsComponent',

	data() {
		return {
			httpSettings: undefined,
			httpSettingsStore: undefined,
			isHttpSettingsEdited: false,
			passwordVisible: false,
		};
	},

	mounted() {
		this.fetchData();
	},

	methods: {
		async fetchData() {
			this.httpSettings = await this.$store.dispatch('getHttpSettings');
			this.httpSettingsStore = this.copyDataFromStore();
		},

		saveData() {
			console.log('Saved!');
			this.$store.commit('setHttpSettings', this.httpSettings);
			this.httpSettingsStore = this.copyDataFromStore();
			this.$store
				.dispatch('saveHttpSettings')
				.then((resp) => {
					if (resp) {
						this.restoreData();
						this.$store.dispatch('showSuccessNotification', this.$t('systemsettings.notification.save.http'));
					}
				})
				.catch(() => {
					this.$store.dispatch('showErrorNotification', this.$t('systemsettings.notification.fail'));
				});
		},

		restoreData() {
			this.fetchData();
			this.isHttpSettingsEdited = false;
		},

		copyDataFromStore() {
			return JSON.parse(JSON.stringify(this.$store.state.systemSettings.httpSettings));
		},

		watchRadioDataChagne(value) {
			this.httpSettings.useProxy = value;
			this.watchDataChange();
		},

		async watchDataChange() {
			this.isHttpSettingsEdited = await this.isDataChanged();
			this.emitData(this.isHttpSettingsEdited);
		},

		async isDataChanged() {
			return !(await this.$store.dispatch('configurationEqual', {
				object1: this.httpSettings,
				object2: this.httpSettingsStore,
			}));
		},

		emitData(changed) {
			this.$emit('changed', {
				component: 'httpSettingsComponent',
				title: 'systemsettings.http.title',
				changed: changed,
				data: this.sumarizeDataChanges(),
			});
		},

		sumarizeDataChanges() {
			let data = [];
			for (let key in this.httpSettings) {
				if (this.httpSettings[key] !== this.httpSettingsStore[key]) {
					data.push({
						label: `systemsettings.http.proxy.${key}`,
						originalData: this.httpSettingsStore[key],
						changedData: this.httpSettings[key],
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
	},
};
</script>
<style></style>
