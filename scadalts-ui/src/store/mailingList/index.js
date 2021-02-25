import mailingListTemplate from "./templates";

const storeMailingList = {
	state: {
		mailingListTemplate: mailingListTemplate,
	},

	mutations: {},

	actions: {
		getSimpleMailingLists({ dispatch }) {
			console.debug('Simple Mailing List');
			return dispatch('requestGet', '/mailingList/getAll');
			// TODO: Create simpler API with just ML id, xid and name as below
			// let data = [
			//     {id: 1, xid: 'ML_00001', name: 'Example Mailing List'},
			//     {id: 2, xid: 'ML_00002', name: 'Example ML Test'}, ...
			// ];
		},

		getMailingList({ dispatch }, mailingListId) {
			return dispatch('requestGet', `/mailingList/get/id/${mailingListId}`);
		},

		createMailingList({ dispatch }, mailingList) {
			console.log('CREATED', mailingList);
		},

		updateMailingList({ dispatch }, mailingList) {
			console.log('UPDATED', mailingList);
		},
	},

	getters: {},
};

export default storeMailingList;
