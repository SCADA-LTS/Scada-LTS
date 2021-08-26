import mailingListTemplate from '../../../src/store/mailingList/templates';

export const storeMailingList = {
	state: {
		mailingListTemplate: mailingListTemplate,
	},

	mutations: {},

	actions: {
		getSimpleMailingLists({ dispatch }) {
			return new Promise((resolve) => {
				let data = [
					{ id: 1, xid: 'ML0001', name: 'Example MailingList' },
					{ id: 2, xid: 'ML0013', name: 'Example RecipientList' },
					{ id: 3, xid: 'ML0341', name: 'Test RecipientList' },
				];
				resolve(data);
			});
		},

		getMailingList({ dispatch }, mailingListId) {
			return new Promise((resolve) => {
				let data = {
					id: 1,
					xid: 'ML0001',
					name: 'Example MailingList',
					entries: [
						{
							userId: 1,
							user: {
								id: 1,
								username: 'admin',
								email: 'admin@yourMangoDomain.com',
								phone: '',
								admin: true,
								disabled: false,
								homeUrl: '',
								lastLogin: 1614587974686,
								firstLogin: false,
							},
							recipientType: 2,
							referenceAddress: null,
							referenceId: 1,
						},
						{
							userId: 3,
							user: {
								id: 3,
								username: 'tester',
								email: 'tester@mail.com',
								phone: '123123131',
								admin: true,
								disabled: false,
								homeUrl: null,
								lastLogin: 0,
								firstLogin: true,
							},
							recipientType: 2,
							referenceAddress: null,
							referenceId: 3,
						},
						{
							address: 'mail@mail.com',
							recipientType: 3,
							referenceAddress: 'mail@mail.com',
							referenceId: 0,
						},
					],
					cronPattern: '1 */15 * * * ?',
					collectInactiveEmails: false,
					dailyLimitSentEmailsNumber: 0,
					dailyLimitSentEmails: false,
					inactiveIntervals: [],
					recipientType: 1,
					referenceAddress: null,
					referenceId: 3,
				};
				resolve(data);
			});
		},

		getUniqueMailingListXid({ dispatch }) {
			return new Promise((resolve) => {
				let data = 'ML_01231';
				resolve(data);
			});
		},

		deleteMailingList({ dispatch }, mailingListId) {
			return new Promise((resolve) => {
				let data = { status: 'deleted' };
				resolve(data);
			});
		},

		createMailingList({ dispatch }, mailingList) {
			return new Promise((resolve) => {
				let data = { status: 'created' };
				resolve(data);
			});
		},

		updateMailingList({ dispatch }, mailingList) {
			return new Promise((resolve) => {
				let data = { status: 'updated' };
				resolve(data);
			});
		},
	},
};

export default storeMailingList;
