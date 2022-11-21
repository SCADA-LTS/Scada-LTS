import Vue from 'vue';
import Vuex from 'vuex';
import dataSource from './dataSource';
import dataPoint from './dataPoint';
import storeEvents from './events';
import eventDetectorModule from './dataPoint/eventDetecotrs';
import graphicView from './graphicView';
import pointHierarchy from './pointHierarchy';
import alarms from './alarms';
import storeUsers from './users';
import storeMailingList from './mailingList';
import storeAlarmsNotifications from './alarms/notifications';
import systemSettings from './systemSettings';
import watchListModule from './modernWatchList';
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
		eventDetectorModule,
		storeEvents,
		graphicView,
		pointHierarchy,
		alarms,
		storeUsers,
		systemSettings,
		storeMailingList,
		storeAlarmsNotifications,
		watchListModule,
	},
	state: {
		loggedUser: null,
		packageVersion: process.env.PACKAGE_VERSION || '0',
		packageTag: process.env.PACKAGE_TAG || '0',
		scadaLtsMilestone: process.env.SCADA_LTS_MILESTONE || '0',
		scadaLtsBuild: process.env.SCADA_LTS_BUILD || '0',
		scadaLtsBranch: process.env.SCADA_LTS_BRANCH || 'local',
		scadaLtsCommit: process.env.SCADA_LTS_COMMIT || 'N/A',
		scadaLtsPullRequestNumber: process.env.SCADA_LTS_PULLREQUEST_NUMBER || 'false',
		scadaLtsPullRequestBranch: process.env.SCADA_LTS_PULLREQUEST_BRANCH || '',
		applicationUrl: './api',
		applicationDebug: false,
		requestConfig: {
			withCredentials: true,
			timeout: 5000,
			// useCredentials: true,
			// credentials: 'same-origin',
			
			
		},
		webSocketUrl: 'http://localhost:8080/ScadaBR/ws/alarmLevel',

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
	mutations: {
		updateWebSocketUrl(state) {
			let locale = window.location.pathname.split('/')[1];
    		let protocol = window.location.protocol;
    		let host = window.location.host.split(":");

			state.webSocketUrl = `${protocol}//${host[0]}:${host[1]}/${locale}/ws/alarmLevel`;
		},

		updateRequestTimeout(state, timeout) {
			state.requestConfig.timeout = timeout > 1000 ? timeout : 1000;
		}
	},
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

		async loginUser({dispatch}, userdata) {
			axios.defaults.withCredentials = true;
			let answer = await dispatch('requestGet', `/auth/${userdata.username}/${userdata.password}`);
			if(answer) {
				dispatch('getUserInfo');
			}
			return answer;
		},

		logoutUser({state}) {
			state.loggedUser = null;
		},

		/**
		 * Fetch User Data from REST API
		 *
		 * @param {*} param0 - Vuex Store variables
		 */
		async getUserInfo({ state, dispatch, commit }) {
			state.loggedUser = await dispatch('requestGet', '/auth/user');
			commit('updateWebSocketUrl');
		},

		/**
		 * HTTP Request GET method to fetch data from the REST API
		 *
		 * @param {*} param0 - Vuex Store variables
		 * @param {*} requestUrl - URL to specific resource of the Application
		 */
		requestGet({ state, dispatch }, requestUrl) {
			return new Promise((resolve, reject) => {
				axios
					.get(state.applicationUrl + requestUrl, state.requestConfig)
					.then(async (r) => {
						await dispatch('validateResponse', r) ? resolve(r.data) : reject(r.data);
					})
					.catch(async (error) => {
						await dispatch('validateResponse', error.response) ? console.warn('Request Exception...') : reject(error.response);
					});
			});
		},

		/**
		 * HTTP Request POST method to push data to the REST API
		 *
		 * @param {*} param0 - Vuex Store variables
		 * @param {*} payload - {url, data} JS object with request data.
		 */
		requestPost({ state, dispatch }, payload) {
			return new Promise((resolve, reject) => {
				axios
					.post(state.applicationUrl + payload.url, payload.data, state.requestConfig)
					.then(async (r) => {
						await dispatch('validateResponse', r) ? resolve(r.data) : reject(r.data);
					})
					.catch(async (error) => {
						await dispatch('validateResponse', error.response) ? console.warn('Request Exception...') : reject(error.response);
					});
			});
		},

		/**
		 * HTTP Request GET method to fetch data from the REST API
		 *
		 * @param {*} param0 - Vuex Store variables
		 * @param {*} requestUrl - URL to specific resource of the Application
		 */
		requestDelete({ state, dispatch }, requestUrl) {
			return new Promise((resolve, reject) => {
				axios
					.delete(state.applicationUrl + requestUrl, state.requestConfig)
					.then(async (r) => {
						await dispatch('validateResponse', r) ? resolve(r.data) : reject(r.data);
					})
					.catch(async (error) => {
						await dispatch('validateResponse', error.response) ? console.warn('Request Exception...') : reject(error.response);
					});
			});
		},

		/**
		 * HTTP Request PUT method to update data by the REST API
		 *
		 * @param {*} param0 - Vuex Store variables
		 * @param {*} payload - {url, data} JS object with request data.
		 */
		requestPut({ state, dispatch }, payload) {
			return new Promise((resolve, reject) => {
				axios
					.put(state.applicationUrl + payload.url, payload.data, state.requestConfig)
					.then(async (r) => {
						await dispatch('validateResponse', r) ? resolve(r.data) : reject(r.data);
					})
					.catch(async (error) => {
						await dispatch('validateResponse', error.response) ? console.warn('Request Exception...') : reject(error.response);
					});
			});
		},

		/**
		 * HTTP Request PATCH method to partialy update data by the REST API
		 *
		 * @param {*} param0 - Vuex Store variables
		 * @param {*} payload - {url, data} JS object with request data.
		 */
		requestPatch({ state, dispatch }, payload) {
			return new Promise((resolve, reject) => {
				axios
					.patch(state.applicationUrl + payload.url, payload.data, state.requestConfig)
					.then(async (r) => {
						await dispatch('validateResponse', r) ? resolve(r.data) : reject(r.data);
					})
					.catch(async (error) => {
						await dispatch('validateResponse', error.response) ? console.warn('Request Exception...') : reject(error.response);
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

		/**
		 * 
		 * Validate server response
		 * 
		 * Check if the response status code from server 
		 * is one of the Successful responses. If not report
		 * proper message in the browser console and block
		 * the response handling and change to error handling.
		 * It is possible to create catch chain to take 
		 * specific action if request is failed.
		 * 
		 * @private
		 * @param {HTTP Response} response - JSON Response from server
		 * @returns true|false
		 */
		validateResponse({state}, response) {
			if(!!response) {
				if (response.status >= 200 && response.status < 300) {
					return true;
				} else if (response.status === 401) {
					console.error('⛔️ - User is not Authorized!');
				} else if (response.status === 400) {
					console.error('❌️ - Bad Request! Check request data');
				} else if (response.status === 500) {
					console.error('🚫️ - Internal server error!\n Something went wrong!');
				}
			} else {
				console.error('🚫️ - No internet connection!\n Something went wrong!');
			}
			
			return false;
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
		appCommit: (state) => {
			if (state.scadaLtsCommit.length > 6) {
				return state.scadaLtsCommit.substring(0, 7);
			} else {
				return state.scadaLtsCommit;
			}
		},
		appCommitLink: (state) => {
			if (state.scadaLtsCommit.length > 6) {
				return `https://github.com/SCADA-LTS/Scada-LTS/commit/${state.scadaLtsCommit}`;
			} else {
				return false;
			}
		},
		appPullRequestNumber: (state) => {
			return state.scadaLtsPullRequestNumber;
		},
		appPullRequestBranch: (state) => {
			return state.scadaLtsPullRequestBranch;
		},
	},
	plugins: [myLoggerForVuexMutation],
});
