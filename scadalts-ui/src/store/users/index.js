const storeUsers = {
	state: {
		userTemplate: {
			id: -1,
			username: '',
			firstName: '',
			lastName: '',
			email: '',
			phone: '',
			admin: false,
			disabled: false,
			homeUrl: '',
			lastLogin: 0,
			receiveAlarmEmails: 0,
			receiveOwnAuditEvents: false,
			theme: 'DEFAULT',
			hideMenu: false,
			userProfile: -1,
			enableFullScreen: false,
			hideShortcutDisableFullScreen: false,
			forceFullScreenMode: false,
			forceHideShortcutDisableFulLScreen: false
		}
	},

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
		},

		createUser({dispatch}, requestData) {
			return dispatch('requestPost', {
				url: `/users/`,
				data: requestData
			});

		},

		updateUser({dispatch}, requestData) {
			return dispatch('requestPut', {
				url: `/users/`,
				data: requestData
			});
		},

		deleteUser({dispatch}, id) {
			return dispatch('requestDelete', `/users/${id}`);
		}
	},

	getters: {},
};

export default storeUsers;
