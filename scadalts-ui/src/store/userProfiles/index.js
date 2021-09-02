const userProfileModule = {
	state: {},

	mutations: {},

	actions: {
		getUserProfilesList({ dispatch }) {
			return dispatch('requestGet', `/userProfiles/`);
		},

        getUserProfile({ dispatch }, id) {
            return dispatch('requestGet', `/userProfiles/${id}`);
        },

		getUserProfileUniqueXid({ dispatch }) {
			return dispatch('requestGet', `/userProfiles/generateXid`);
		},

		createUserProfile({ dispatch }, userProfile) {
            return dispatch('requestPost', {
				url: `/userProfiles/`,
				data: userProfile
			});
        },

		updateUserProfile({ dispatch }, userProfile) {
            return dispatch('requestPut', {
				url: `/userProfiles/`,
				data: userProfile
			});
        },

		deleteUserProfile({ dispatch }, id) {
            return dispatch('requestDelete', `/userProfiles/${id}`);
        }
	},

	getters: {},
};

export default userProfileModule;
