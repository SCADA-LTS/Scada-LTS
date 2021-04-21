import Axios from 'axios';
import { reject, resolve } from 'core-js/fn/promise';

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
							type: 'VDS',
							conn: '5 minutes',
							descr: 'Nothing important',
							loaded: false,
							datapoints: []
						},
						{
							id: 1,
							enabled: true,
							name: 'Tes2',
							type: 'VDS',
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
