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
			return dispatch('requestPut', {
				url: `/mailingList`,
				data: mailingList
			})
		},

	},

	getters: {},
};

export default storeMailingList;
