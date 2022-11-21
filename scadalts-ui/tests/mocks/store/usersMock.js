const storeUsersMock = {
	state: {
		
	},

	mutations: {},

	actions: {
        getAllUsers({dispatch}) {
            return new Promise(resolve => {
                let data = [
                    {id: 1, username: 'Admin', email: 'testmail@scada.com', phone: '123456789'}
                ]
                resolve(data);
            })
        },    
	},

	getters: {},
};

export default storeUsersMock;
