const storeEvents = {
	state: {},

	mutations: {},

	actions: {
		fetchScripts({ dispatch }, payload) {

			return dispatch('requestPost', {
				url: `/scripts/search`,
				data: payload,
			});
		}		
		
	},

	getters: {},
};

export default storeEvents;
