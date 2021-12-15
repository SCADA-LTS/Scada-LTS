export let mainStore = {
	modules: {},

	state: {
		timePeriods: [
			{ id: 1, label: 'Seconds' },
			{ id: 2, label: 'Minutes' },
			{ id: 3, label: 'Hours' },
			{ id: 4, label: 'Days' },
			{ id: 5, label: 'Weeks' },
			{ id: 6, label: 'Months' },
			{ id: 7, label: 'Years' },
			{ id: 8, label: 'Miliseconds' },
		],

		alarmLevels: [
			{ id: 0, label: 'None' },
			{ id: 1, label: 'Information' },
			{ id: 2, label: 'Urgent' },
			{ id: 3, label: 'Critical' },
			{ id: 4, label: 'Life safety' },
		],

		loggedUser: {
			admin: false,
			id: 1,
		},
	},
	mutations: {},

	actions: {},
};

export default mainStore;
