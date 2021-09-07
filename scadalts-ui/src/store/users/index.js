const storeUsers = {
	state: {},

	mutations: {},

	actions: {
		getAllUsers({ dispatch }) {
			return dispatch('requestGet', `/users/`);
		},

		getUserDetails({ dispatch }, id) {
			return dispatch('requestGet', `/users/${id}`);
        },

		updatePassword({ dispatch }, requestData) {
			return dispatch('requestPut', {
				url: `/users/password`,
				data: requestData
			});
		}
	},

	getters: {},
};

export default storeUsers;
