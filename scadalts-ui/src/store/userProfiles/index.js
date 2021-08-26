const userProfileModule = {
	state: {},

	mutations: {},

	actions: {
		getUserProfilesList({ dispatch }) {
			return dispatch('requestGet', `/userProfiles/`);
		},

        getUserProfile({ dispatch }, id) {
            return dispatch('requestGet', `/userProfiles/${id}`);
        }
	},

	getters: {},
};

export default userProfileModule;
