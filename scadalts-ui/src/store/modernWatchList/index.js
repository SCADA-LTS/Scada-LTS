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

		fetchWatchLists({ dispatch }) {
			return dispatch('requestGet', '/watch-lists/');
		}
	},
	getters: {},
};
export default watchListModule;
