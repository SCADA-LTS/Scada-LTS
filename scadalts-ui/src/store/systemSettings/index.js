/**
 * @author Radoslaw Jajko
 * 
 */

import axios from "axios"
import { resolve } from "core-js/fn/promise";

const storeSystemSettings = {
    state: {
        systemSettingsApiUrl: './api/systemSettings',
        databaseType: undefined,
        systemEventTypes: undefined,
        auditEventTypes: undefined,
        systemInfoSettings: undefined,
        emailSettings: undefined,
        httpSettings: undefined,
        miscSettings: undefined
    },
    mutations: {
        setDatabaseType(state, databaseType) {
            state.databaseType = databaseType;
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
        }
    },
    actions: {
        getDatabaseType() {
            return new Promise((resolve, reject) => {
                axios.get(`${this.systemSettingsApiUrl}/getDatabaseType`).then(response => {
                    console.debug(response)
                    resolve(response.data)
                }).catch(err => {
                    reject()
                })
            })
        },
        getAllSettings(context) {
            return new Promise((resolve, reject) => {
                axios.get(`${context.state.systemSettingsApiUrl}/getSettings`, 
                {timeout: 5000, useCredentials: true, credentials: 'same-origin'}).then(response => {
                    if(response.status == 200) {
                        context.commit('setDatabaseType', response.data.databaseType);
                        context.commit('setSystemEventTypes', response.data.systemEventTypes);
                        context.commit('setAuditEventTypes', response.data.auditEventTypes);
                        context.commit('setSystemInfoSettings', response.data.systemInfoSettings);
                        context.commit('setEmailSettings', response.data.emailSettings);
                        context.commit('setHttpSettings', response.data.httpSettings);
                        context.commit('setMiscSettings', response.data.miscSettings);
                    }
                    resolve(true)
                }).catch(err => {
                    reject(err)
                })
            })
        },
        saveEmailSettings(context) {
            return new Promise((resolve, reject) => {
                axios.post(`${context.state.systemSettingsApiUrl}/saveEmail`, context.state.emailSettings,
                {timeout: 5000, useCredentials: true, credentials: 'same-origin'}).then(response => {
                    if(response.status == 200) {
                        resolve(true)
                    } else {
                        reject(false)
                    }
                }).catch(err => {
                    reject(err)
                })
            })
        },
        saveMiscSettings(context) {
            return new Promise((resolve, reject) => {
                axios.post(`${context.state.systemSettingsApiUrl}/saveMisc`, context.state.miscSettings,
                {timeout: 5000, useCredentials: true, credentials: 'same-origin'}).then(response => {
                    if(response.status == 200) {
                        resolve(true)
                    } else {
                        reject(false)
                    }
                }).catch(err => {
                    reject(err)
                })
            })
        },
        saveHttpSettings(context) {
            return new Promise((resolve, reject) => {
                axios.post(`${context.state.systemSettingsApiUrl}/saveHttp`, context.state.httpSettings,
                {timeout: 5000, useCredentials: true, credentials: 'same-origin'}).then(response => {
                    if(response.status == 200) {
                        resolve(true)
                    } else {
                        reject(false)
                    }
                }).catch(err => {
                    reject(err)
                })
            })
        },
        saveSystemEventTypes(context) {
            return new Promise((resolve, reject) => {
                axios.post(`${context.state.systemSettingsApiUrl}/saveSystemEventAlarmLevels`, context.state.systemEventTypes,
                {timeout: 5000, useCredentials: true, credentials: 'same-origin'}).then(response => {
                    if(response.status == 200) {
                        resolve(true)
                    } else {
                        reject(false)
                    }
                }).catch(err => {
                    reject(err)
                })
            })
        },
        saveAuditEventTypes(context) {
            return new Promise((resolve, reject) => {
                axios.post(`${context.state.systemSettingsApiUrl}/saveAuditEventAlarmLevels`, context.state.auditEventTypes,
                {timeout: 5000, useCredentials: true, credentials: 'same-origin'}).then(response => {
                    if(response.status == 200) {
                        resolve(true)
                    } else {
                        reject(false)
                    }
                }).catch(err => {
                    reject(err)
                })
            })
        }

    },
    getters: {

    }
}
export default storeSystemSettings