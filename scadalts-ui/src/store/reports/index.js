const storeReports = {
	state: {},

	mutations: {},

	actions: {
		fetchReportInstances({ dispatch }) {
			return dispatch('requestGet', `/reports/instances`);
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

	getters: {
		highestUnsilencedAlarmLevel: (state) => state.highestUnsilencedAlarmLevel,
	},
};

export default storeReports;
