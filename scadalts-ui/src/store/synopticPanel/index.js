const storeSynopticPanel = {
	state: {},

	getters: {},

	mutations: {},

	actions: {
		fetchSynopticPanelList({ dispatch }) {
			return dispatch('requestGet', `/synoptic-panels/`);
		},

		fetchSynopticPanel({ dispatch }, id) {
			return dispatch('requestGet', `/synoptic-panels/${id}`);
		},

		createSynopticPanel({ dispatch }, synopticPanel) {
			return dispatch('requestPost', {
				url: `/synoptic-panels/`,
				data: synopticPanel,
			});
		},

		deleteSynopticPanel({ dispatch }, id) {
			return dispatch('requestDelete', `/synoptic-panels/${id}`);
		},

		updateSynopticPanel({ dispatch }, synopticPanel) {
			return dispatch('requestPut', {
				url: `/synoptic-panels/`,
				data: synopticPanel,
			});
		},
	},
};

export default storeSynopticPanel;
