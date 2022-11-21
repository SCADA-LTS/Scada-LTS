/**
 * @author grzegorz.bylica@abilit.eu
 */

import axios from 'axios';

const storeIsAlive = {
	state: {
		warning: false,
		success: false,
		danger: true,
		timeWarningEpoch: 5000, // default  5000 [ms]
		timeErrorEpoch: 1000, // default 10000 [ms]
		timeRefreshEpoch: 2000, // default  2000 [ms]
		timeServerEpoch: -1, // -1 not initiate
		timeInWebEpoch: -1, // -1 not initiate
		webhookUrl: '', // url to send positive feedback about connection with scada is alive
		msgErr: '',
		lastTimeCheck: -1,
	},
	mutations: {
		SET_INIT(state, t) {
			state.warning = false;
			state.success = false;
			state.danger = true;
			state.timeWarningEpoch = t.tw;
			state.timeErrorEpoch = t.te;
			state.timeRefreshEpoch = t.tr;
			state.webhookUrl = t.wh;
		},
		CHECK_IS_ALIVE(state, timeFromServer) {
			state.lastTimeCheck = timeFromServer;
		},
		ERR(state, msgErr) {
			state.msgErr = msgErr;
		},
	},
	actions: {
		calculateStateIsAlive({ commit, state }, timeFromServer) {
			return new Promise((resolve) => {
				state.timeServerEpoch = timeFromServer;
				state.timeInWebEpoch = Date.now() / 1000;
				// console.log(`time in web: ${state.timeInWebEpoch}`)
				// console.log(`time from server: ${state.timeServerEpoch}`)
				// console.log(`difference: ${(state.timeInWebEpoch - state.timeServerEpoch)*1000}`)
				// console.log(`time warning: ${state.timeWarningEpoch}`)
				// console.log(`time err: ${state.timeErrorEpoch}`)

				state.danger =
					(state.timeInWebEpoch - state.timeServerEpoch) * 1000 >= state.timeErrorEpoch;
				if (state.danger) {
					state.warning = false;
					state.success = false;
				} else {
					state.warning =
						(state.timeInWebEpoch - state.timeServerEpoch) * 1000 >=
						state.timeWarningEpoch;
					if (state.warning) {
						state.danger = false;
						state.success = false;
					} else {
						state.danger = false;
						state.warning = false;
						state.success = true;
					}
				}

				commit('CHECK_IS_ALIVE', timeFromServer);
				resolve();
			});
		},
		isAlive({ state, commit, dispatch }) {
			return new Promise((resolve, reject) => {
				axios
					.get(`./api/is_alive/time`, { timeout: state.timeRefreshEpoch })
					.then((res) => {
						dispatch('calculateStateIsAlive', res.data).then(() => {
							resolve(res);
							axios.get(state.webhookUrl);
						});
					})
					.catch((err) => {
						dispatch('calculateStateIsAlive', state.timeServerEpoch).then(() => {
							commit('ERR', err);
							reject(err);
						});
					});
			});
		},
		setInitIsAlive({ commit }, times) {
			commit('SET_INIT', times);
		},
	},
	getters: {
		warningIsAlive: (state) => state.warning,
		successIsAlive: (state) => state.success,
		dangerIsAlive: (state) => state.danger,
		timeWarningEpochIsAlive: (state) => state.timeWarningEpoch,
		timeErrorEpochIsAlive: (state) => state.timeErrorEpoch,
		timeRefreshIsAlive: (state) => state.timeRefreshEpoch,
		timeFromServerIsAlive: (state) => state.timeServerEpoch,
		timeInWebIsAlive: (state) => state.timeInWebEpoch,
		msgErrorIsAlive: (state) => state.msgErr,
	},
};
export default storeIsAlive;
