const storeDataPoint = {
	state: {},

	mutations: {},

	actions: {
		getDataPointSimpleFilteredList({ dispatch }, value) {
			return new Promise((resolve) => {
				//MOCK of DATAPOINT UI
				let data = [
					{ name: '1 minute Load', id: 1, xid: 'DP_250372' },
					{ name: '5 minutes Load', id: 2, xid: 'DP_335775' },
					{ name: 'Point', id: 5, xid: 'DP_712779' },
					{ name: 'Numeric', id: 6, xid: 'DP_954927' },
					{ name: 'SysUptime', id: 7, xid: 'DP_305240' },
					{ name: 'TrapTest', id: 10, xid: 'DP_568054' },
				];
				data = data.filter((e) => {
					return (e.name || '').toLowerCase().indexOf((value || '').toLowerCase()) > -1;
				});
				resolve(data);
			});
		},

		getDataPointDetails({ dispatch }, datapointId) {
			return new Promise((resolve) => {
				let data = [
					{ name: '1 minute Load', id: 1, xid: 'DP_250372', description: 'First Point' },
					{ name: '5 minutes Load', id: 2, xid: 'DP_335775', description: 'Example Point' },
					{ name: 'Point', id: 5, xid: 'DP_712779', description: 'Just a point' },
					{ name: 'Numeric', id: 6, xid: 'DP_954927', description: 'Not binary' },
					{ name: 'SysUptime', id: 7, xid: 'DP_305240', description: 'Runtime' },
					{ name: 'TrapTest', id: 10, xid: 'DP_568054', description: 'Not worked' },
				];
                data = data.find(({ id }) => id === Number(datapointId));

				resolve(data);
			});
		},
	},

	getters: {},
};

export default storeDataPoint;
