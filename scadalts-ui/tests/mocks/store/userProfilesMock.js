const storeUserProfilesMock = {
	state: {},

	mutations: {},

	actions: {
		getUserProfilesList({ dispatch }) {
			return new Promise((resolve) => {
				let data = [
					{ id: 1, name: 'UserProfile', xid: 'UP_0311231' },
				];
				resolve(data);
			});
		},
	},

	getters: {},
};

export default storeUserProfilesMock;
