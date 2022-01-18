import { exampleUser } from "../objects/UserMocks";

const storeUsersMock = {
	state: {},

	mutations: {},

	actions: {
		getAllUsers({ dispatch }) {
			return new Promise((resolve) => {
				let data = [
					{ id: 1, username: 'Admin', email: 'testmail@scada.com', phone: '123456789', admin: true, disabled: false },
					{ id: 2, username: 'Worker', email: 'testworker@scada.com', phone: '00111111111', admin: false, disabled: false},
				];
				resolve(data);
			});
		},

		getUserDetails({ dispatch }, id) {
			return new Promise((resolve, reject) => {
				if(id === 1) {
					resolve(exampleUser);
				} else {
					reject();
				}
			});
		},

		deleteUser({ dispatch }, id) {
			return new Promise((resolve, reject) => {
				if(id === 2) {
					resolve();
				} else {
					reject();
				}
			});
		},

		requestGet({dispatch}, url) {
			return new Promise((resolve, reject) => {
				resolve({
					unique: true
				})
			});
		},

	},

	getters: {},
};

export default storeUsersMock;
