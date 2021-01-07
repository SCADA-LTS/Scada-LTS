/**
 * @author Radoslaw Jajko
 *
 */

import axios from 'axios';

const storeSystemSettings = {
	state: {
		systemSettingsApiUrl: './api/systemSettings',
		requestOptions: {
			timeout: 5000,
			useCredentials: true,
			credentials: 'same-origin',
		},
		databaseType: undefined,
		databaseInfo: undefined,
		systemEventTypes: undefined,
		auditEventTypes: undefined,
		systemInfoSettings: undefined,
		emailSettings: undefined,
		httpSettings: undefined,
		miscSettings: undefined,
		smsDomainSettings: undefined,
		systemStartupTime: undefined,
		schemaVersion: undefined,
		scadaConfig: undefined,
		defaultLoggingType: undefined,
	},
	mutations: {
		setDatabaseType(state, databaseType) {
			state.databaseType = databaseType;
		},
		setDatabaseInfo(state, databaseInfo) {
			state.databaseInfo = databaseInfo;
		},
		setSystemEventTypes(state, systemEventTypes) {
			state.systemEventTypes = systemEventTypes;
		},
		setAuditEventTypes(state, auditEventTypes) {
			state.auditEventTypes = auditEventTypes;
		},
		setSystemInfoSettings(state, systemInfoSettings) {
			state.systemInfoSettings = systemInfoSettings;
		},
		setEmailSettings(state, emailSettings) {
			state.emailSettings = emailSettings;
		},
		setHttpSettings(state, httpSettings) {
			state.httpSettings = httpSettings;
		},
		setMiscSettings(state, miscSettings) {
			state.miscSettings = miscSettings;
		},
		setSmsDomainSettings(state, smsDomainSettings) {
			state.smsDomainSettings = smsDomainSettings;
		},
		setSystemStartupTime(state, startupTime) {
			state.systemStartupTime = new Date(Number(startupTime));
		},
		setSchemaVersion(state, schemaVersion) {
			state.schemaVersion = schemaVersion;
		},
		setScadaConfig(state, scadaConfig) {
			state.scadaConfig = scadaConfig;
		},
		setDefaultLoggingType(state, defaultLoggingType) {
			state.defaultLoggingType = defaultLoggingType;
		},
	},
	actions: {
		getDatabaseType(context) {
			return new Promise((resolve, reject) => {
				axios
					.get(
						`${context.state.systemSettingsApiUrl}/getDatabaseType`,
						context.state.requestOptions,
					)
					.then((response) => {
						if (response.status == 200) {
							context.commit('setDatabaseType', response.data);
							resolve(response.data);
						}
						reject(false);
					})
					.catch((err) => {
						console.error(err);
						reject(false);
					});
			});
		},
		saveDatabaseType(context, databaseType) {
			return new Promise((resolve, reject) => {
				axios
					.get(`${context.state.systemSettingsApiUrl}/saveDatabaseType/${databaseType}`)
					.then((response) => {
						if (response.status == 200) {
							resolve(true);
						}
						resolve(false);
					})
					.catch((err) => {
						reject();
					});
			});
		},
		getSchemaVersion(context) {
			return new Promise((resolve, reject) => {
				axios
					.get(`${context.state.systemSettingsApiUrl}/getSchemaVersion`, {
						timeout: 5000,
						useCredentials: true,
						credentials: 'same-origin',
					})
					.then((response) => {
						if (response.status == 200) {
							context.commit('setSchemaVersion', response.data.schemaVersion);
						}
						resolve(true);
					})
					.catch((err) => {
						reject(err);
					});
			});
		},
		getSystemInfoSettings(context) {
			return new Promise((resolve, reject) => {
				axios
					.get(
						`${context.state.systemSettingsApiUrl}/getSystemInfo`,
						context.state.requestOptions,
					)
					.then((response) => {
						if (response.status == 200) {
							context.commit('setSystemInfoSettings', response.data);
							resolve(response.data);
						}
						reject(false);
					})
					.catch((err) => {
						console.error(err);
						reject(false);
					});
			});
		},
		getSystemStartupTime(context) {
			return new Promise((resolve, reject) => {
				axios
					.get(`${context.state.systemSettingsApiUrl}/getStartupTime`, {
						timeout: 5000,
						useCredentials: true,
						credentials: 'same-origin',
					})
					.then((response) => {
						if (response.status == 200) {
							context.commit('setSystemStartupTime', response.data.startupTime);
						}
						resolve(true);
					})
					.catch((err) => {
						reject(err);
					});
			});
		},
		getAllSettings(context) {
			return new Promise((resolve, reject) => {
				axios
					.get(`${context.state.systemSettingsApiUrl}/getSettings`, {
						timeout: 5000,
						useCredentials: true,
						credentials: 'same-origin',
					})
					.then((response) => {
						if (response.status == 200) {
							context.commit('setDatabaseType', response.data.databaseType);
							context.commit('setSystemEventTypes', response.data.systemEventTypes);
							context.commit('setAuditEventTypes', response.data.auditEventTypes);
							context.commit('setSystemInfoSettings', response.data.systemInfoSettings);
							context.commit('setEmailSettings', response.data.emailSettings);
							context.commit('setHttpSettings', response.data.httpSettings);
							context.commit('setMiscSettings', response.data.miscSettings);
							context.commit('setScadaConfig', response.data.scadaConfig);
						}
						resolve(true);
					})
					.catch((err) => {
						reject(err);
					});
			});
		},
		getAuditEventTypes(context) {
			return new Promise((resolve, reject) => {
				axios
					.get(
						`${context.state.systemSettingsApiUrl}/getAuditEventAlarmLevels`,
						context.state.requestOptions,
					)
					.then((response) => {
						if (response.status == 200) {
							context.commit('setAuditEventTypes', response.data);
							resolve(response.data);
						}
						reject(false);
					})
					.catch((err) => {
						console.error(err);
						reject(false);
					});
			});
		},
		getSystemEventTypes(context) {
			return new Promise((resolve, reject) => {
				axios
					.get(
						`${context.state.systemSettingsApiUrl}/getSystemEventAlarmLevels`,
						context.state.requestOptions,
					)
					.then((response) => {
						if (response.status == 200) {
							context.commit('setSystemEventTypes', response.data);
							resolve(response.data);
						}
						reject(false);
					})
					.catch((err) => {
						console.error(err);
						reject(false);
					});
			});
		},
		getEmailSettings(context) {
			return new Promise((resolve, reject) => {
				axios
					.get(
						`${context.state.systemSettingsApiUrl}/getEmail`,
						context.state.requestOptions,
					)
					.then((response) => {
						if (response.status == 200) {
							context.commit('setEmailSettings', response.data);
							resolve(response.data);
						}
						reject(false);
					})
					.catch((err) => {
						console.error(err);
						reject(false);
					});
			});
		},
		getHttpSettings(context) {
			return new Promise((resolve, reject) => {
				axios
					.get(
						`${context.state.systemSettingsApiUrl}/getHttp`,
						context.state.requestOptions,
					)
					.then((response) => {
						if (response.status == 200) {
							context.commit('setHttpSettings', response.data);
							resolve(response.data);
						}
						reject(false);
					})
					.catch((err) => {
						console.error(err);
						reject(false);
					});
			});
		},
		getMiscSettings(context) {
			return new Promise((resolve, reject) => {
				axios
					.get(
						`${context.state.systemSettingsApiUrl}/getMisc`,
						context.state.requestOptions,
					)
					.then((response) => {
						if (response.status == 200) {
							context.commit('setMiscSettings', response.data);
							resolve(response.data);
						}
						reject(false);
					})
					.catch((err) => {
						console.error(err);
						reject(false);
					});
			});
		},
		getSmsDomainSettings(context) {
			return new Promise((resolve, reject) => {
				axios
					.get(
						`${context.state.systemSettingsApiUrl}/getSMSDomain`,
						context.state.requestOptions,
					)
					.then((response) => {
						if (response.status == 200) {
							context.commit('setSmsDomainSettings', response.data);
							resolve(response.data);
						}
						reject(false);
					})
					.catch((err) => {
						console.error(err);
						reject(false);
					});
			});
		},
		getScadaConfiguration(context) {
			return new Promise((resolve, reject) => {
				axios
					.get(
						`${context.state.systemSettingsApiUrl}/getScadaConfig`,
						context.state.requestOptions,
					)
					.then((response) => {
						if (response.status == 200) {
							context.commit('setScadaConfig', response.data);
							resolve(response.data);
						}
						reject(false);
					})
					.catch((err) => {
						console.error(err);
						reject(false);
					});
			});
		},
		getDatabaseSize(context) {
			return new Promise((resolve, reject) => {
				axios
					.get(`${context.state.systemSettingsApiUrl}/getDatabaseSize`, {
						timeout: 5000,
						useCredentials: true,
						credentials: 'same-origin',
					})
					.then((response) => {
						if (response.status == 200) {
							context.commit('setDatabaseInfo', response.data);
						}
						resolve(true);
					})
					.catch((err) => {
						reject(err);
					});
			});
		},
		saveEmailSettings(context) {
			return new Promise((resolve, reject) => {
				axios
					.post(
						`${context.state.systemSettingsApiUrl}/saveEmail`,
						context.state.emailSettings,
						{ timeout: 5000, useCredentials: true, credentials: 'same-origin' },
					)
					.then((response) => {
						if (response.status == 200) {
							resolve(true);
						} else {
							reject(false);
						}
					})
					.catch((err) => {
						reject(err);
					});
			});
		},
		saveMiscSettings(context) {
			return new Promise((resolve, reject) => {
				axios
					.post(
						`${context.state.systemSettingsApiUrl}/saveMisc`,
						context.state.miscSettings,
						{ timeout: 5000, useCredentials: true, credentials: 'same-origin' },
					)
					.then((response) => {
						if (response.status == 200) {
							resolve(true);
						} else {
							reject(false);
						}
					})
					.catch((err) => {
						reject(err);
					});
			});
		},
		saveSmsDomainSettings(context) {
			return new Promise((resolve, reject) => {
				axios
					.post(
						`${context.state.systemSettingsApiUrl}/saveSMSDomain/${context.state.smsDomainSettings}`,
						{ timeout: 5000, useCredentials: true, credentials: 'same-origin' },
					)
					.then((response) => {
						if (response.status == 200) {
							resolve(true);
						} else {
							reject(false);
						}
					})
					.catch((err) => {
						reject(err);
					});
			});
		},
		saveHttpSettings(context) {
			return new Promise((resolve, reject) => {
				axios
					.post(
						`${context.state.systemSettingsApiUrl}/saveHttp`,
						context.state.httpSettings,
						{ timeout: 5000, useCredentials: true, credentials: 'same-origin' },
					)
					.then((response) => {
						if (response.status == 200) {
							resolve(true);
						} else {
							reject(false);
						}
					})
					.catch((err) => {
						reject(err);
					});
			});
		},
		saveSystemEventTypes(context) {
			return new Promise((resolve, reject) => {
				axios
					.post(
						`${context.state.systemSettingsApiUrl}/saveSystemEventAlarmLevels`,
						context.state.systemEventTypes,
						{ timeout: 5000, useCredentials: true, credentials: 'same-origin' },
					)
					.then((response) => {
						if (response.status == 200) {
							resolve(true);
						} else {
							reject(false);
						}
					})
					.catch((err) => {
						reject(err);
					});
			});
		},
		saveAuditEventTypes(context) {
			return new Promise((resolve, reject) => {
				axios
					.post(
						`${context.state.systemSettingsApiUrl}/saveAuditEventAlarmLevels`,
						context.state.auditEventTypes,
						{ timeout: 5000, useCredentials: true, credentials: 'same-origin' },
					)
					.then((response) => {
						if (response.status == 200) {
							resolve(true);
						} else {
							reject(false);
						}
					})
					.catch((err) => {
						reject(err);
					});
			});
		},
		sendTestEmail(context) {
			return new Promise((resolve, reject) => {
				axios
					.get(`${context.state.systemSettingsApiUrl}/sendTestEmail`, {
						timeout: 5000,
						useCredentials: true,
						credentials: 'same-origin',
					})
					.then((response) => {
						if (response.status == 200) {
							resolve(response.data);
						} else {
							reject(false);
						}
					})
					.catch((err) => {
						reject(err);
					});
			});
		},
		purgeData(context) {
			return new Promise((resolve, reject) => {
				axios
					.get(`${context.state.systemSettingsApiUrl}/purgeData`, {
						timeout: 5000,
						useCredentials: true,
						credentials: 'same-origin',
					})
					.then((response) => {
						if (response.status == 200) {
							resolve(true);
						} else {
							reject(false);
						}
					})
					.catch((err) => {
						reject(err);
					});
			});
		},
		saveSystemInfoSettings(context) {
			return new Promise((resolve, reject) => {
				axios
					.post(
						`${context.state.systemSettingsApiUrl}/saveSystemInfo`,
						context.state.systemInfoSettings,
						{ timeout: 5000, useCredentials: true, credentials: 'same-origin' },
					)
					.then((response) => {
						if (response.status == 200) {
							resolve(true);
						} else {
							reject(false);
						}
					})
					.catch((err) => {
						reject(err);
					});
			});
		},
		saveDefaultLoggingType(context) {
			return new Promise((resolve, reject) => {
				axios
					.post(
						`${context.state.systemSettingsApiUrl}/saveDefaultLoggingType/${context.state.defaultLoggingType}`,
						{ timeout: 5000, useCredentials: true, credentials: 'same-origin' },
					)
					.then((response) => {
						if (response.status === 200) {
							resolve(true);
						} else {
							reject(false);
						}
					})
					.catch((err) => {
						reject(err);
					});
			});
		},
		getDefaultLoggingType(context) {
			return new Promise((resolve, reject) => {
				axios
					.get(
						`${context.state.systemSettingsApiUrl}/getDefaultLoggingType`,
						context.state.requestOptions,
					)
					.then((response) => {
						if (response.status === 200) {
							console.debug(response.data);
							context.commit('setDefaultLoggingType', response.data.defaultLoggingType);
							resolve(response.data.defaultLoggingType);
						}
						reject(false);
					})
					.catch((err) => {
						console.error(err);
						reject(false);
					});
			});
		},
		configurationEqual(ctx, objects) {
			return new Promise((resolve, reject) => {
				const keys1 = Object.keys(objects.object1);
				const keys2 = Object.keys(objects.object2);
				if (keys1.length !== keys2.length) {
					resolve(false);
				}
				for (let key of keys1) {
					if (objects.object1[key] !== objects.object2[key]) {
						resolve(false);
					}
				}
				resolve(true);
			});
		},
	},
	getters: {},
};
export default storeSystemSettings;
