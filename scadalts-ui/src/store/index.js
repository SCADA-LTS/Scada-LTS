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
			{ id: 1, label: 'Seconds' },
			{ id: 2, label: 'Minutes' },
			{ id: 3, label: 'Hours' },
			{ id: 4, label: 'Days' },
			{ id: 5, label: 'Weeks' },
			{ id: 6, label: 'Months' },
			{ id: 7, label: 'Years' },
			{ id: 8, label: 'Miliseconds' },
		],

		alarmLevels: [
			{ id: 0, label: i18n.t('alarmlevels.none') },
			{ id: 1, label: i18n.t('alarmlevels.information') },
			{ id: 2, label: i18n.t('alarmlevels.urgent') },
			{ id: 3, label: i18n.t('alarmlevels.critical') },
			{ id: 4, label: i18n.t('alarmlevels.lifesafety') },
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
			console.log('VUEX::GET_USER_INFO::', state.loggedUser);
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
