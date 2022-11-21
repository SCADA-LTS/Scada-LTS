const storeUsers = {
	state: {
		
	},

	mutations: {},

	actions: {
        getAllUsers({dispatch}) {
            return dispatch('requestGet', `/user/getAll`);
        },    
	},

	getters: {},
};

export default storeUsers;
