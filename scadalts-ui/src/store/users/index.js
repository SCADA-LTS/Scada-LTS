const storeUsers = {
	state: {},

	mutations: {},

	actions: {
		getAllUsers({ dispatch }) {
			return dispatch('requestGet', `/users/`);
		},
	},

	getters: {},
};

export default storeUsers;
