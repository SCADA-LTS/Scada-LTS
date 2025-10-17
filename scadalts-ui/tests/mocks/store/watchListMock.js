export const storeWatchList = {
	state: { },

	mutations: {},

	actions: {
        getDataPointValue({ dispatch }) {
			return new Promise((resolve) => {
                let details = {
                    id: 1,
                    xid: 'DP_MOCK_01',
                    name: 'Mock Data Point',
                    type: 3
                }
                resolve(details);
            });
		},
    },
};

export default storeWatchList;
