/**
 * @author grzegorz.bylica@abilit.eu
 */

import axios from 'axios';

const TIMEOUT_API_HIS_CMP = 5000;
const storeHistoryCMP = {
	state: {
		msgErr: '',
	},
	mutations: {
		ERR(state, msgErr) {
			state.msgErr = msgErr;
		},
	},
	actions: {
		getHisotryCMP({ commit }, xIdViewAndIdCmp) {
			return new Promise((resolve, reject) => {
				axios
					.get(`./api/cmp/history/${xIdViewAndIdCmp}`, {
						timeout: TIMEOUT_API_HIS_CMP,
					})
					.then((res) => {
						resolve(res);
					})
					.catch((err) => {
						commit('ERR', err);
						reject();
					});
			});
		},
	},
	getters: {
		msgErrorHistoryCMP: (state) => state.msgErr,
	},
};
export default storeHistoryCMP;
