const storeScripts = {
	state: {},

	mutations: {},

	actions: {
		searchScripts({ dispatch }, payload) {
			return dispatch('requestGet', `/scripts/search`);
		},

		runScript({ dispatch }, xid) {
			let url = `/scripts/execute/${xid}`;
			return dispatch('requestPost', {url});
		},

		deleteScript({ dispatch }, id) {
			let url = `/scripts/${id}`;
			return dispatch('requestDelete', url);
		},
	},
	

	getters: {},
};

export default storeScripts;
