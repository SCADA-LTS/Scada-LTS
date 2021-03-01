import Vue from 'vue';
import Vuex from 'vuex';
import dataSource from './dataSource';
import dataPoint from './dataPoint';
import storeEvents from './events';
import storeEventDetectors from './dataPoint/eventDetecotrs';
import graphicView from './graphicView';
import pointHierarchy from './pointHierarchy';
import amcharts from './amcharts';
import alarms from './alarms';
import storeAlarmsNotifications from './alarms/notifications';
import systemSettings from './systemSettings';
import axios from 'axios';

import i18n from '@/i18n';

Vue.use(Vuex);

const myLoggerForVuexMutation = (store) => {
	store.subscribe((mutation, state) => {});
};

export default new Vuex.Store({
	modules: {
		dataSource,
		dataPoint,
		storeEventDetectors,
		storeEvents,
		graphicView,
		pointHierarchy,
		amcharts,
		alarms,
		systemSettings,
		storeAlarmsNotifications,
	},
	state: {
		loggedUser: null,
		packageVersion: process.env.PACKAGE_VERSION || '0',
		packageTag: process.env.PACKAGE_TAG || '0',
		scadaLtsMilestone: process.env.SCADA_LTS_MILESTONE || '0',
		scadaLtsBuild: process.env.SCADA_LTS_BUILD || '0',
		scadaLtsBranch: process.env.SCADA_LTS_BRANCH || 'local',
		applicationUrl: './api',
		applicationDebug: false,
		requestConfig: {
			timeout: 5000,
			useCredentials: true,
			credentials: 'same-origin',
		},

		timePeriods: [
			{ id: 1, label: i18n.t('common.timeperiod.seconds') },
			{ id: 2, label: i18n.t('common.timeperiod.minutes') },
			{ id: 3, label: i18n.t('common.timeperiod.hours') },
			{ id: 4, label: i18n.t('common.timeperiod.days') },
			{ id: 5, label: i18n.t('common.timeperiod.weeks') },
			{ id: 6, label: i18n.t('common.timeperiod.months') },
			{ id: 7, label: i18n.t('common.timeperiod.years') },
			{ id: 8, label: i18n.t('common.timeperiod.miliseconds') },
		],

		alarmLevels: [
			{ id: 0, label: i18n.t('common.alarmlevels.none') },
			{ id: 1, label: i18n.t('common.alarmlevels.information') },
			{ id: 2, label: i18n.t('common.alarmlevels.urgent') },
			{ id: 3, label: i18n.t('common.alarmlevels.critical') },
			{ id: 4, label: i18n.t('common.alarmlevels.lifesafety') },
		],
	},
	mutations: {},
	actions: {
		getUserRole() {
			return new Promise((resolve, reject) => {
				axios
					.get('./api/auth/isRoleAdmin', {
						timeout: 5000,
						useCredentials: true,
						credentials: 'same-origin',
					})
					.then((resp) => {
						resolve(resp.data);
					})
					.catch((error) => {
						console.error(error);
						reject(error);
					});
			});
		},

		/**
		 * Fetch User Data from REST API
		 *
		 * @param {*} param0 - Vuex Store variables
		 */
		async getUserInfo({ state, dispatch }) {
			state.loggedUser = await dispatch('requestGet', '/auth/user');
		},

		/**
		 * HTTP Request GET method to fetch data from the REST API
		 *
		 * @param {*} param0 - Vuex Store variables
		 * @param {*} requestUrl - URL to specific resource of the Application
		 */
		requestGet({ state }, requestUrl) {
			return new Promise((resolve, reject) => {
				axios
					.get(state.applicationUrl + requestUrl, state.requestConfig)
					.then((r) => {
						if (r.status === 200) {
							resolve(r.data);
						} else {
							reject(false);
						}
					})
					.catch((error) => {
						console.error(error);
						reject(false);
					});
			});
		},

		/**
		 * HTTP Request POST method to push data to the REST API
		 *
		 * @param {*} param0 - Vuex Store variables
		 * @param {*} payload - {url, data} JS object with request data.
		 */
		requestPost({ state }, payload) {
			return new Promise((resolve, reject) => {
				axios
					.post(state.applicationUrl + payload.url, payload.data, state.requestConfig)
					.then((r) => {
						if (r.status === 201 || r.status === 200) {
							resolve(r.data);
						} else {
							reject(false);
						}
					})
					.catch((error) => {
						console.error(error);
						reject(false);
					});
			});
		},

		/**
		 * HTTP Request GET method to fetch data from the REST API
		 *
		 * @param {*} param0 - Vuex Store variables
		 * @param {*} requestUrl - URL to specific resource of the Application
		 */
		requestDelete({ state }, requestUrl) {
			return new Promise((resolve, reject) => {
				axios
					.delete(state.applicationUrl + requestUrl, state.requestConfig)
					.then((r) => {
						if (r.status === 200) {
							resolve(r.data);
						} else {
							reject(false);
						}
					})
					.catch((error) => {
						console.error(error);
						reject(false);
					});
			});
		},

		/**
		 * HTTP Request PUT method to update data by the REST API
		 *
		 * @param {*} param0 - Vuex Store variables
		 * @param {*} payload - {url, data} JS object with request data.
		 */
		requestPut({ state }, payload) {
			return new Promise((resolve, reject) => {
				axios
					.put(state.applicationUrl + payload.url, payload.data, state.requestConfig)
					.then((r) => {
						if (r.status === 201 || r.status === 200) {
							resolve(r.data);
						} else {
							reject(false);
						}
					})
					.catch((error) => {
						console.error(error);
						reject(false);
					});
			});
		},

		/**
		 * HTTP Request PATCH method to partialy update data by the REST API
		 *
		 * @param {*} param0 - Vuex Store variables
		 * @param {*} payload - {url, data} JS object with request data.
		 */
		requestPatch({ state }, payload) {
			return new Promise((resolve, reject) => {
				axios
					.patch(state.applicationUrl + payload.url, payload.data, state.requestConfig)
					.then((r) => {
						if (r.status === 201 || r.status === 200) {
							resolve(r.data);
						} else {
							reject(false);
						}
					})
					.catch((error) => {
						console.error(error);
						reject(false);
					});
			});
		},

		/**
		 * Convert from select to specific Timestamp since past.
		 *
		 * @param {*} param0
		 * @param {*} payload - {period, type} Time and Period Type
		 */
		convertSinceTimePeriodToTimestamp({ state }, payload) {
			let result = payload.period;
			let now = new Date();
			if (payload.type === 1) {
				result = result * 1000;
			} else if (payload.type === 2) {
				result = result * 1000 * 60;
			} else if (payload.type === 3) {
				result = result * 1000 * 60 * 60;
			} else if (payload.type === 4) {
				result = result * 1000 * 60 * 60 * 24;
			} else if (payload.type === 5) {
				result = result * 1000 * 60 * 60 * 24 * 7;
			} else if (payload.type === 6) {
				result = result * 1000 * 60 * 60 * 24 * 7 * 4;
			} else if (payload.type === 7) {
				result = result * 1000 * 60 * 60 * 24 * 7 * 4 * 12;
			}

			return new Date(now - result);
		},

		async getLocaleInfo({ dispatch }) {
			let temp = await dispatch('requestGet', '/systemSettings/getSystemInfo');
			i18n.locale = temp.language;
		},
	},
	getters: {
		appVersion: (state) => {
			return state.packageVersion;
		},
		appTag: (state) => {
			return state.packageTag;
		},
		appMilestone: (state) => {
			return state.scadaLtsMilestone;
		},
		appBuild: (state) => {
			return state.scadaLtsBuild;
		},
		appBranch: (state) => {
			return state.scadaLtsBranch;
		},
	},
	plugins: [myLoggerForVuexMutation],
});
