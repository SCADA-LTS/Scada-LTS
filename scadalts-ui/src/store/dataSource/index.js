import Axios from 'axios';

const ds = {
	state: {
		datasourcesApiUrl: './api/datasource',
		requestOptions: {
			timeout: 5000,
			useCredentials: true,
			credentials: 'same-origin',
		},
	},
	mutations: {},
	actions: {
		getDataSources({ dispatch }) {
			return new Promise((resolve, reject) => {
				setTimeout(() => {
					const data = [
						{
							id: 0,
							enabled: true,
							name: 'Test',
							type: 'virtualdatasource',
							xid: 'DS_012311',
							conn: '5 minutes',
							descr: 'Nothing important',
							loaded: false,
							datapoints: []
						},
						{
							id: 1,
							enabled: true,
							name: 'Tes2',
							type: 'snmpdatasource',
							xid: 'DS_01232131',
							conn: '30 seconds',
							descr: 'User has changed on/off status',
							loaded: false,
							datapoints: [],
						},
					];
					resolve(data);
				}, 2000);
				
			});
		},

		fetchDataSourceDetails({dispatch}, dataSourceId) {
			return new Promise((resolve) => {
				setTimeout(() => {
					let data;
					if(dataSourceId === 0) {
						data = {
							id: 1,
							enabled: true,
							name: 'Tes2',
							xid: 'DS_01232131',
							updatePeriod: 5,
							updatePeriodType: 1,
						}
					} else if(dataSourceId === 1) {
						data = {
							id: 2,
							enabled: true,
							name: 'Tes2ErSNMP',
							xid: 'DS_01214',
							updatePeriod: 5,
							updatePeriodType: 1,
							host: 'localhost',
							port:'161',
						}
					}
					
					resolve(data)
				}, 1000);
			})
		},

		fetchDataPointsForDS({dispatch}, dataSourceId) {
			return new Promise((resolve, reject) => {
				setTimeout(() => {
					const data = [
						{
							enabled: false,
							name: 'DP-01',
							type: 'Binary',
							desc: 'Additional Descr',
							xid: 'DP_0121323',
						},
						{
							enabled: true,
							name: 'DP-02',
							type: 'Numeric',
							desc: 'Descr',
							xid: 'DP_0121314',
						},
					];
					resolve(data);
				}, 2000)
			});
		},

		getAllDataSources(context) {
			return new Promise((resolve, reject) => {
				Axios.get(
					`${context.state.datasourcesApiUrl}/getAll`,
					context.state.requestOptions
				)
					.then((r) => {
						if (r.status === 200) {
							resolve(r.data);
						} else {
							reject(false);
						}
					})
					.catch((error) => {
						console.error(error);
						reject(false);
					});
			});
		},

		getAllPlcDataSources(context) {
			return new Promise((resolve, reject) => {
				Axios.get(
					`${context.state.datasourcesApiUrl}/getAllPlc`,
					context.state.requestOptions
				)
					.then((r) => {
						if (r.status === 200) {
							resolve(r.data);
						} else {
							reject(false);
						}
					})
					.catch((error) => {
						console.error(error);
						reject(false);
					});
			});
		},
	},
	getters: {},
};
export default ds;
