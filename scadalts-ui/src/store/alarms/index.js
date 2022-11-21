/**
 * @author grzegorz.bylica@abilit.eu
 */

import axios from 'axios';

const TIMEOUT_API_ALARMS = 5000;
const storeAlarms = {
	state: {
		msgErr: '',
	},
	mutations: {
		ERR(state, msgErr) {
			state.msgErr = msgErr;
		},
	},
	actions: {
		getLiveAlarms({ commit }, { offset, limit }) {
			return new Promise( (resolve, reject) => {
				axios
					.get(`./api/alarms/live/${offset}/${limit}`)
					.then((res) => {
						resolve(res.data);
					})
					.catch((err) => {
						commit('ERR', err);
						reject();
					});
			});
		},
		getHistoryAlarms({ commit }, { dateDay, filterRLike, offset, limit }) {
			return new Promise((resolve, reject) => {
				const execute = `./api/alarms/history/${dateDay}/${filterRLike}/${offset}/${limit}`;
				console.log(execute);
				axios
					.get(execute)
					.then((res) => {
						resolve(res.data);
					})
					.catch((err) => {
						commit('ERR', err);
						reject();
					});
			});
		},
		setAcknowledge({ commit }, { id }) {
			return new Promise((resolve, reject) => {
				axios
					.post(`./api/alarms/acknowledge/${id}`)
					.then((res) => {
						resolve(res.data);
					})
					.catch((err) => {
						commit('ERR', err);
						reject();
					});
			});
		},
		fakeGetLiveAlarms({ commit }, { offset, limit }) {
			return new Promise(function (resolve) {
				setTimeout(function () {
					resolve([
						{
							'id:': 111,
							'activation-time': '2020-03-10 07:13:34',
							'inactivation-time': '',
							name:
								'Be ST ALG_StoAllg1.0 Durchflussmessung Drosselkammer Störung Steuersicherung ausgelöst',
							level: '5',
						},
						{
							'id:': 112,
							'activation-time': '2020-03-10 07:13:24',
							'inactivation-time': '',
							name: 'Be AL GM01 gas alarm',
							level: '4',
						},
						{
							'id:': 113,
							'activation-time': '2020-03-10 07:13:24',
							'inactivation-time': '2020-03-10 06:33:54',
							name: 'Se AL GM02 gas alarm',
							level: '4',
						},
						{
							'id:': 114,
							'activation-time': '2020-02-10 05:13:24',
							'inactivation-time': '2020-03-10 06:30:54',
							name: 'Bx AL GM03 gas alarm',
							level: '4',
						},
					]);
				}, 500);
			});
		},

		fakeGetHistoryAlarms({ commit }, { dateDay, filterRLike, offset, limit }) {
			//console.log(`dataDay:${dateDay} ${typeof dateDay} filterRLike:${filterRLike} ${typeof filterRLike} offset:${offset} ${typeof offset} limit:${limit} ${typeof limit}`)
			return new Promise(function (resolve) {
				setTimeout(
					resolve([
						{
							time: '2020-03-10 07:13:24',
							name:
								'Be ST ALG_StoAllg1.0 Durchflussmessung Drosselkammer Störung Steuersicherung ausgelöst',
							description: 'Störung kommt',
						},
						{
							time: '2020-03-10 07:13:42',
							name:
								'Be ST ALG_StoAllg1.0 Durchflussmessung Drosselkammer Störung Steuersicherung ausgelöst',
							description: 'Störung ist gegangen',
						},
						{
							time: '2020-03-10 07:13:24',
							name: 'Be AL GM01 gas alarm',
							description: 'Alarm ausgelöst',
						},
						{
							time: '2020-03-10 07:13:42',
							name: 'Be AL GM01 gas alarm',
							description: 'ist gegangen',
						},
					]),
				);
			});
		},
		fakeAcknowledge({ commit }, { id }) {
			return new Promise(function (resolve) {
				setTimeout(
					resolve({
						id: 111,
						request: 'OK',
						error: 'none',
					}),
				);
			});
		},
	},
	getters: {
		msgError: (state) => state.msgErr,
	},
};
export default storeAlarms;
