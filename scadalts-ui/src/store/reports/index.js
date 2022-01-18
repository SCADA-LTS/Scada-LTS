const storeReports = {
	state: {},

	mutations: {},

	actions: {
		async sendTestEmails({ dispatch }, emails) {
			return dispatch('requestPost', {
				url: `/reports/sendTestEmails`,
				data: { emails },
			});
		},
		fetchReportInstances({ dispatch }) {
			return dispatch('requestGet', `/reports/instances`);
		},
		setPreventPurge({ dispatch }, payload) {
			return dispatch(
				'requestGet',
				`/reports/instances/${payload.id}/preventPurge/${payload.preventPurge}`,
			);
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
			return dispatch('requestDelete', `/reports/instances/${id}`);
		},
	},

	getters: {},
};

export default storeReports;
