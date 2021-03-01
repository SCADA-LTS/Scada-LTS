import mailingListTemplate from "./templates";

const storeMailingList = {
	state: {
		mailingListTemplate: mailingListTemplate,
	},

	mutations: {},

	actions: {
		getSimpleMailingLists({ dispatch }) {
			return dispatch('requestGet', '/mailingList/getAllSimple');
		},

		getMailingList({ dispatch }, mailingListId) {
			return dispatch('requestGet', `/mailingList/get/id/${mailingListId}`);
		},

		getUniqueMailingListXid({dispatch}) {
			return dispatch('requestGet', '/mailingList/generateUniqueXid');
		},

		deleteMailingList({dispatch}, mailingListId) {
			return dispatch('requestDelete', `/mailingList/${mailingListId}`);
		},

		createMailingList({ dispatch }, mailingList) {
			return dispatch('requestPost', {
				url: `/mailingList`,
				data: mailingList
			})
		},

		updateMailingList({ dispatch }, mailingList) {
			let requestPayload = Object.assign({}, mailingList);
			requestPayload.entries.forEach(e => {
				delete e.user
			});
			return dispatch('requestPut', {
				url: `/mailingList`,
				data: requestPayload
			})
		},

	},

	getters: {},
};

export default storeMailingList;
