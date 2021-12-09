export const dataSourceMock = {
	state: {
        dataSources: [],
        dataSourceList: [],
    },

	mutations: {},

	actions: {
        getDataSources({dispatch}) {
            return new Promise((resolve, reject) => {
				resolve();
			});
        },
		getUniqueDataSourceXid({ dispatch }) {
			return new Promise((resolve, reject) => {
				resolve("UNIT_XID_01");
			});
		},
        requestGet({dispatch}, url) {
            if(url.includes("validate?xid")) {
                return new Promise((resolve, reject) => {
                    if(url.includes("NOT_UNIQUE")) {
                        resolve({unique: false});
                    } else {
                        resolve({unique: true});
                    }
                });
            }
        }
	},
};

export default dataSourceMock;
