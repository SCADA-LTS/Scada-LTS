import ScadaReportInstance from "../../models/ScadaReportInstance";
import { SET_REPORT_INSTANCES, READED_REPORT_INSTANCE, TOGGLE_PURGE, SET_REPORT_TEMPLATES } from "./types";
const storeReports = {
	state: {
		reportInstances: [],
		reportTemplates: [],
		unreadedInstance: false,
	},

	mutations: {
		[SET_REPORT_INSTANCES](state, reports) {
			if(!!reports && reports.length !== state.reportInstances.length) {
				state.unreadedInstance = true;
				state.reportInstances = reports;
			}			
		},

		[SET_REPORT_TEMPLATES](state, templates) {
			state.reportTemplates = templates;
		},

		[READED_REPORT_INSTANCE](state) {
			state.unreadedInstance = false;
		},

		[TOGGLE_PURGE](state, reportInstanceId) {
			let instance = state.reportInstances.find(r => r.id === reportInstanceId);
			instance.tooglePurge();
		},
	},

	actions: {
		fetchReportInstances({ commit, dispatch }) {
			dispatch('requestGet', `/reports/instances`)
				.then(r => { commit(SET_REPORT_INSTANCES, r.map(i => ScadaReportInstance.fromAPI(i)));})
				.catch(() => { dispatch('showErrorNotification', 'Reports not loaded')});
		},
		fetchReportTemplates({ commit, dispatch }) {
			return dispatch('requestPost', {
				url: `/reports/search`,
				data: {
					keywords:'',
					limit: 0,
					offset: 0,
					sortBy: [],
					sortDesc: [],
				}
			})
			.then((r) => { commit(SET_REPORT_TEMPLATES, r)})
			.catch(() => { dispatch('showErrorNotification', 'Reports not loaded')});
		},
		setPreventPurge({ dispatch }, payload) {
			dispatch('requestGet', `/reports/instances/${payload.id}/preventPurge/${payload.preventPurge}`)
				.then(() => { commit(TOGGLE_PURGE, payload.id)})
				.catch(() => { dispatch('showErrorNotification', 'Failed to save this property')});
		},
		
		async sendTestEmails({ dispatch }, emails) {
			return dispatch('requestPost', {
				url: `/reports/sendTestEmails`,
				data: { emails },
			});
		},
		
		saveReport({ dispatch }, payload) {
			return dispatch('requestPost', { url: `/reports/save`, data: payload });
		},

		runReport({ dispatch }, id) {
			return dispatch('requestGet', `/reports/run/${id}`);
		},
		fetchReports({ dispatch }, payload) {
			return dispatch('requestPost', {
				url: `/reports/search`,
				data: {
					keywords: payload.keywords,
					limit: payload.itemsPerPage,
					offset: payload.itemsPerPage * (payload.page - 1),
					sortBy: !payload.sortBy.length ? [] : payload.sortBy,
					sortDesc: !payload.sortDesc.length ? [] : payload.sortDesc,
				},
			});
		},
		deleteReport({ dispatch }, id) {
			return dispatch('requestDelete', `/reports/${id}`);
		},
		deleteReportInstance({ dispatch }, id) {
			return dispatch('requestDelete', `/reports/instances/${id}`);
		}
	},

	getters: {
		filteredReportInstances: (state) => (expr) => {
			return state.reportInstances.filter(i => i.name.includes(expr));
		},
		filteredReportTemplates: (state) => (expr) => {
			return state.reportTemplates.filter(i => i.name.includes(expr));
		}
	},
};

export default storeReports;
