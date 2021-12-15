/**
 * @author grzegorz.bylica@abilit.eu
 */

import storeIsAlive from './storeIsAlive';
import storeHistoryCMP from './storeHisotryCMP';
import storeRefreshViews from './storeRefreshViews';

const gv = {
	modules: {
		storeIsAlive,
		storeHistoryCMP,
		storeRefreshViews,
	},
	state: {},
	mutations: {},
	actions: {
		fetchGraphicalViewsList({dispatch}) {
			return dispatch('requestGet', '/view/getAll');
		}
	},
	getters: {},
};
export default gv;
