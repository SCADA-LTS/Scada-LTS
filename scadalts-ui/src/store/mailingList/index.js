import mailingListTemplate from './templates';

const storeMailingList = {
	state: {
		mailingListTemplate: mailingListTemplate,
		defaultCronPattern: '1 */15 * * * ?',
	},

	mutations: {},

	actions: {
		getSimpleMailingLists({ dispatch }) {
			return dispatch('requestGet', '/mailingList/getAllSimple');
		},

		getMailingList({ dispatch }, mailingListId) {
			return dispatch('requestGet', `/mailingList/get/id/${mailingListId}`);
		},

		getUniqueMailingListXid({ dispatch }) {
			return dispatch('requestGet', '/mailingList/generateUniqueXid');
		},
		

		deleteMailingList({ dispatch }, mailingListId) {
			return dispatch('requestDelete', `/mailingList/${mailingListId}`);
		},

		createMailingList({ state, dispatch }, mailingList) {
			if (!mailingList.cronPattern) {
				mailingList.cronPattern = state.defaultCronPattern;
			}
			return dispatch('requestPost', {
				url: `/mailingList/`,
				data: mailingList,
			});
		},

		updateMailingList({ state, dispatch }, mailingList) {
			let requestPayload = Object.assign({}, mailingList);
			if (!requestPayload.cronPattern) {
				requestPayload.cronPattern = state.defaultCronPattern;
			}
			requestPayload.entries.forEach((e) => {
				delete e.user;
			});
			return dispatch('requestPut', {
				url: `/mailingList/`,
				data: requestPayload,
			});
		},
	},

	getters: {},
};

export default storeMailingList;
