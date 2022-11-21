/**
 * @author Radoslaw Jajko
 *
 */

const watchListModule = {
	state: {},

	mutations: {},

	actions: {
		getWatchListDetails({ dispatch }, watchlistId) {
			return dispatch('requestGet', `/watch-lists/${watchlistId}`);
		},
	},
	getters: {},
};
export default watchListModule;
