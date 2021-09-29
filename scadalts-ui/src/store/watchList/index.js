/**
 * @author Radoslaw Jajko
 *
 */
import { searchDataPointInHierarchy } from './utils';
import WatchListPoint from '@/models/WatchListPoint'
import WatchListPointHierarchyNode from '@/models/WatchListPointHierarchyNode';

 const watchListModule = {
	state: {
        activeWatchList: null,
        activeWatchListRevert: null,
        pointWatcher: [], //More detailed information about the point
        datapointHierarchy: [],
    },

	mutations: {
        SET_ACTIVE_WATCHLIST(state, watchList) {
            state.activeWatchList = watchList;
            state.activeWatchListRevert = JSON.parse(JSON.stringify(watchList));
        },

        UPDATE_ACTIVE_WATCHLIST(state, watchList) {
            state.activeWatchList = watchList;
        },

        SET_BLANK_ACTIVE_WATCHLIST(state, uniqueXid = "WL_00001") {
            state.activeWatchList = {
                id: -1,
                name: '',
                xid: uniqueXid,
                userId: '',
                pointList: [],
                watchListUsers: [],
            }
        },

        SET_POINT_WATCHER(state, pointArray) {
            state.pointWatcher = pointArray
        },

        SET_DATAPOINT_HIERARCHY(state, datapointHierarchy) {
            state.datapointHierarchy = datapointHierarchy;
        },

        REMOVE_POINT_FROM_WATCHLIST(state, point) {
            console.log("IMPORTANNNNT")
            console.log(point);
            console.log(state.activeWatchList);
            console.log(state.pointWatcher);
            let x = searchDataPointInHierarchy(state.datapointHierarchy, point.id);
            console.log(x);
            if(!!x) {
                x.selected = false;
            }
            if(!!state.activeWatchList) {
                state.activeWatchList.pointList = state.activeWatchList.pointList.filter(p => p.id !== point.id);
                state.pointWatcher = state.pointWatcher.filter(p => p.id !== point.id);
            }

        },

        CHECK_POINT_IN_PH(state, pointId) {
            let x = searchDataPointInHierarchy(state.datapointHierarchy, pointId);
            if(!!x) {
                x.selected = true;
            }
        },

        ADD_POINT_TO_WATCHLIST(state, point) {
            console.log(point);
            console.log(state.activeWatchList);
            if(!!state.activeWatchList) {
                let p = {
                    id: point.id,
                    xid: point.xid,
                    name: point.name,
                };
                state.activeWatchList.pointList.push(p);
            }
        },

        ADD_POINT_TO_WATCHER(state, point) {
            state.pointWatcher.push(point);
        },

        UPDATE_POINT_VALUE(state, data) {
            console.log(data);
            let point = JSON.parse(data.body);
            let dp = state.pointWatcher.find(p => p.id === point.pointId);
            if(!!dp) {
                dp.value = point.value;
                dp.timestamp = new Date().toLocaleString();
            }

        },

        UPDATE_POINT_EVENTS(state, data) {
            let dp = state.pointWatcher.find(p => p.id === data.dpId);
            if(!!dp) {
                dp.events = data.event;
            }
        },

        UPDATE_POINT_STATE(state, data) {
            let point = JSON.parse(data.body);
            let dp = state.pointWatcher.find(p => p.id === point.pointId);
            if(!!dp) {
                dp.enabled = point.enabled;
                state.pointWatcher.push({id:-1})
                state.pointWatcher = state.pointWatcher.filter(p => p.id !== -1);
            }
        }
    },

	actions: {

        // --- REST-API CRUD SECTION --- //
        getAllWatchLists({ dispatch }) {
            return dispatch('requestGet', '/watch-lists/');
        },

        getWatchListDetails({ dispatch, commit }, id) {
            return new Promise(async (resolve, reject) => {
                try {
                    let watchList = await dispatch('requestGet', `/watch-lists/${id}`);
                    let details = loadWatchListDetails(id);
                    if(!!details) {
                        watchList.horizontal = details.horizontal;
                        watchList.biggerChart = details.biggerChart;
                    } else {
                        watchList.horizontal = true;
                        watchList.biggerChart = false;
                    }
                    watchList.user = await dispatch('getUserDetails', watchList.userId);
                    commit('SET_ACTIVE_WATCHLIST', watchList);
                    resolve(watchList);
                } catch (e) {
                    reject(e);
                }
            })
		},

        getWatchListUniqueXid({ dispatch }) {
			return dispatch('requestGet', `/watch-lists/generateXid`);
		},

        createWatchList({dispatch, state}) {
            saveWatchListDetails(state.activeWatchList);
            return dispatch('requestPost', {
                url: '/watch-lists',
                data: state.activeWatchList,
            });
        },

        updateWatchList({dispatch, state}) {
            saveWatchListDetails(state.activeWatchList);
            return dispatch('requestPut', {
                url: '/watch-lists',
                data: state.activeWatchList,
            });

        },

        deleteWatchList({dispatch, state}) {
            return dispatch('requestDelete', `/watch-lists/${state.activeWatchList.id}`);
        },

        // --- WATCHLIST POINT HIERARCHY SECTION --- //
        async loadWatchListPointHierarchyNode({ state, dispatch, commit }, node) {            
            let pointHierarchy = await dispatch('fetchPointHierarchyNode', node.id);
            let responseArray = [];
            pointHierarchy.forEach(ph => {
                responseArray.push(
                    new WatchListPointHierarchyNode(ph, state.activeWatchList)
                );
            });

            if(!!node.parentNode) {
                node.parentNode.push(...responseArray);
            } else {
                commit('SET_DATAPOINT_HIERARCHY', responseArray);
            }
        },

        loadWatchListDataPointDetails({state, dispatch, commit}, datapointId) {
            console.debug(`Loading DataPoint Details for WatchList...\nDataPointID: ${datapointId}`);
            return new Promise(async (resolve, reject) => {
                try {
                    let dp = await dispatch('getDataPointDetails', datapointId);
                    let pv = await dispatch('getDataPointValue', datapointId);
                    let pe = await dispatch('fetchDataPointEvents', {datapointId, limit:10})
                    console.log(pv);
                    let pointData2 = new WatchListPoint().createWatchListPoint(dp, pv, pe);

                    commit('ADD_POINT_TO_WATCHER', pointData2);
                    resolve(pointData);
                } catch (e) {
                    reject(e);
                }
            });
        },

        updateWatchListEventList({state, dispatch, commit}) {
            return new Promise((resolve) => {
                state.pointWatcher.forEach(async p => {
                    try {
                        let pe = await dispatch('fetchDataPointEvents', {datapointId: p.id, limit:10});
                        commit('UPDATE_POINT_EVENTS', {dpId: p.id, event: pe});
                    } catch (e) {
                        console.error(e);
                    }
                });
                resolve();
            });
        },

        updateWatchListEvent({dispatch, commit}, datapointId) {
            return new Promise(async (resolve, reject) => {
                try {
                    let pe = await dispatch('fetchDataPointEvents', {datapointId, limit:10});
                    commit('UPDATE_POINT_EVENTS', {dpId: datapointId, event: pe});
                    resolve();
                } catch(e) {
                    reject(e);
                }
            });
        },

        updateActiveWatchList({commit, state}, newValue) {
            commit('UPDATE_ACTIVE_WATCHLIST', newValue);
            return state.activeWatchList;
        },

    },

	getters: {},
};
export default watchListModule;

function saveWatchListDetails(watchList) {
    let saveData = {
        horizontal: watchList.horizontal,
        biggerChart: watchList.biggerChart,
    };
    localStorage.setItem(`MWLD_${watchList.id}`, JSON.stringify(saveData));
}

function loadWatchListDetails(watchListId) {
    return JSON.parse(localStorage.getItem(`MWLD_${watchListId}`));
}