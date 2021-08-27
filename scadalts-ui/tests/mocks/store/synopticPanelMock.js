export const storeSynopticPanel = {
	state: { },

	mutations: {},

	actions: {
        fetchSynopticPanelList({ dispatch }) {
			return new Promise((resolve) => {
                let data = [
					{ id: 1, xid: 'SP001', name: 'Test Synoptic Panel' },
                    { id: 2, xid: 'SP002', name: 'Test Panels' },
                    { id: 3, xid: 'SP003', name: 'Example Synoptic Panel' },
				];
                resolve(data)
            });
		},

    },
};

export default storeSynopticPanel;
