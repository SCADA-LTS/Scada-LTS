/**
 * Improved WatchList Vuex store
 * 
 * Now actions are defined in a separate file "actions" next to this
 * index file. That can improve the code maintenance and user will know
 * from what are available actions to dispatch. 
 * 
 * @author Radoslaw Jajko
 * @version 1.1.0
 */
import { searchDataPointInHierarchy } from './utils';
import { createWatchList, deleteWatchList, getWatchList, loadWatchList, setupWatchList, updateWatchList } from './actions';
import WatchListPoint from '@/models/WatchListPoint'
import WatchListPointHierarchyNode from '@/models/WatchListPointHierarchyNode';
import WatchList from '@/models/watchlist/WatchListEntry';
import WatchListJson from '@/models/watchlist/WatchListJson';

const watchListModule = {
    state: {
        activeWatchList: null,
        activeWatchListRevert: null,
        pointWatcher: [], //More detailed information about the point
        datapointHierarchy: [],
    },

    mutations: {
        SET_ACTIVE_WATCHLIST(state, watchList) {
            watchList.pointList = watchList.pointList.map(point => ({...point, onChart: true}));
            state.activeWatchList = watchList;
            state.activeWatchListRevert = JSON.parse(JSON.stringify(watchList));
        },

        UPDATE_ACTIVE_WATCHLIST(state, watchList) {
            state.activeWatchList = watchList;
        },

        SET_BLANK_ACTIVE_WATCHLIST(state, uniqueXid = "WL_00001") {
            state.activeWatchList = new WatchList();
            state.activeWatchList.xid = uniqueXid;
        },

        SET_POINT_WATCHER(state, pointArray) {
            state.pointWatcher = pointArray
        },

        SET_DATAPOINT_HIERARCHY(state, datapointHierarchy) {
            state.datapointHierarchy = datapointHierarchy;
        },

        TOGGLE_POINT_VISIBILITY_ON_CHART(state, point) {
            let dp = state.pointWatcher.find(p => p.id === point.id);
            dp.onChart = !dp.onChart;
        },

        REMOVE_POINT_FROM_WATCHLIST(state, point) {
            let x = searchDataPointInHierarchy(state.datapointHierarchy, point.id);
            if (!!x) {
                x.selected = false;
            }
            if (!!state.activeWatchList) {
                state.activeWatchList.pointList = state.activeWatchList.pointList.filter(p => p.identifier.id !== point.id);
                state.pointWatcher = state.pointWatcher.filter(p => p.id !== point.id);
            }

        },

        CHECK_POINT_IN_PH(state, pointId) {
            let x = searchDataPointInHierarchy(state.datapointHierarchy, pointId);
            if (!!x) {
                x.selected = true;
            }
        },

        ADD_POINT_TO_WATCHLIST(state, point) {

            if (!!state.activeWatchList) {
                const exists = state.activeWatchList.pointList.find(x => x.identifier.id === point.id);
                if(!exists) {
                    let p = {
                        accessType: 1,
                        description: '',
                        onChart: true,
                        identifier: {
                            id: point.id,
                            xid: point.xid,
                            name: point.name,
                        }
                    }
                    state.activeWatchList.pointList.push(p);    
                }
            }
        },

        ADD_POINT_TO_WATCHER(state, point) {
            const exists = state.pointWatcher.find(x => x.id === point.id);
            if(!exists) {
                state.pointWatcher.push(point);
            }
            
        },

        UPDATE_POINT_VALUE(state, data) {
            let point = JSON.parse(data.body);
            let dp = state.pointWatcher.find(p => p.id === point.pointId);
            if (!!dp) {
                dp.value = point.value;
                dp.timestamp = new Date().toLocaleString();
            }

        },

        UPDATE_POINT_EVENTS(state, data) {
            let dp = state.pointWatcher.find(p => p.id === data.dpId);
            if (!!dp) {
                dp.events = data.event;
            }
        },

        UPDATE_POINT_STATE(state, data) {
            let point = JSON.parse(data.body);
            let dp = state.pointWatcher.find(p => p.id === point.pointId);
            if (!!dp) {
                dp.enabled = point.enabled;
                if (point.enabled) {
                    dp.dataSourceEnabled = true;
                }
                state.pointWatcher.push({ id: -1 })
                state.pointWatcher = state.pointWatcher.filter(p => p.id !== -1);
            }
        }
    },

    actions: {

        // --- REST-API CRUD SECTION --- //
        getAllWatchLists({ dispatch }) {
            return dispatch('requestGet', '/watch-lists/');
        },

        [loadWatchList]({dispatch}, watchListId) {
            dispatch(getWatchList, watchListId).then((data) => {
                dispatch(setupWatchList, data);
            });
        },

        async [setupWatchList]({dispatch, commit}, watchList) {
            try {
                //let userData = await dispatch('getUserDetails', watchList.userId);
                let wl = WatchList.create(watchList, {
                    'id' : watchList.userId
                });
                commit('SET_ACTIVE_WATCHLIST', wl);
                return wl;
            } catch (e) {
                commit('SET_ACTIVE_WATCHLIST', new WatchList());
                console.error("Failed to setup watchlist", watchList);
            }
        },

        [getWatchList]({dispatch}, watchListId) {
            return new Promise((resolve, reject) => {
                dispatch('requestGet', `/watch-lists/${watchListId}`).then((data => {
                    resolve(data);
                })).catch(e => {
                    reject(e);
                })
            })
        },

        [createWatchList]({dispatch, state}) {
            return new Promise((resolve, reject) => {
                dispatch('requestPost', {
                    url: '/watch-lists', data: WatchListJson.map(state.activeWatchList)
                }).then(resp => {
                    state.activeWatchList.saveDetails();
                    dispatch(setupWatchList, resp).then(wl => {
                        resolve(wl);
                    });
                }).catch((e) => {
                    console.error("Failed to save", e);
                    reject(e);
                });
            });
        },

        [updateWatchList]({dispatch, state}) {
            return new Promise((resolve, reject) => {
                dispatch('requestPut', {
                    url: '/watch-lists', data: WatchListJson.map(state.activeWatchList, state.pointWatcher)
                }).then(resp => {
                    state.activeWatchList.saveDetails();
                    dispatch(setupWatchList, resp).then(wl => {
                        resolve(wl);
                    })
                }).catch((e) => {
                    console.error("Failed to update", e);
                    reject(e);
                })
            })
        },

        [deleteWatchList]({ dispatch, state }) {
            return dispatch('requestDelete', `/watch-lists/${state.activeWatchList.id}`);
        },

        getWatchListUniqueXid({ dispatch }) {
            return dispatch('requestGet', `/watch-lists/generateXid`);
        },

        // --- WATCHLIST POINT HIERARCHY SECTION --- //
        async loadWatchListPointHierarchyNode({ state, dispatch, commit }, node) {
            let pointHierarchy = await dispatch('fetchReducedPointHierarchyNode', node.id);
            let responseArray = [];
            pointHierarchy.forEach(ph => {
                responseArray.push(
                    new WatchListPointHierarchyNode(ph, state.activeWatchList)
                );
            });

            if (!!node.parentNode) {
                node.parentNode.push(...responseArray);
            } else {
                commit('SET_DATAPOINT_HIERARCHY', responseArray);
            }
        },

        loadWatchListDataPointDetails({ state, dispatch, commit }, datapointId) {
            console.debug(`Loading DataPoint Details for WatchList...\nDataPointID: ${datapointId}`);
            return new Promise(async (resolve, reject) => {
                try {
                    let dp = await dispatch('getDataPointDetails', datapointId);
                    let pv = await dispatch('getDataPointValue', datapointId);
                    let pe = await dispatch('fetchDataPointEvents', { datapointId, limit: 10 })
                    let ds = await dispatch('getDatasourceByXid', dp.dataSourceXid);
                    let pointData2 = new WatchListPoint().createWatchListPoint(dp, pv, pe, ds);
                    commit('ADD_POINT_TO_WATCHER', pointData2);
                    resolve(pointData2);
                } catch (e) {
                    reject(e);
                }
            });
        },

        updateWatchListEventList({ state, dispatch, commit }) {
            return new Promise((resolve) => {
                state.pointWatcher.forEach(async p => {
                    try {
                        let pe = await dispatch('fetchDataPointEvents', { datapointId: p.id, limit: 10 });
                        commit('UPDATE_POINT_EVENTS', { dpId: p.id, event: pe });
                    } catch (e) {
                        console.error(e);
                    }
                });
                resolve();
            });
        },

        updateWatchListEvent({ dispatch, commit }, datapointId) {
            return new Promise(async (resolve, reject) => {
                try {
                    let pe = await dispatch('fetchDataPointEvents', { datapointId, limit: 10 });
                    commit('UPDATE_POINT_EVENTS', { dpId: datapointId, event: pe });
                    resolve();
                } catch (e) {
                    reject(e);
                }
            });
        },

    },

    getters: {
        watchListConfigChanged: (state) => {
            let change = false;
            if (!!state.activeWatchList && !!state.activeWatchListRevert) {
                change = JSON.stringify(state.activeWatchList) !== JSON.stringify(state.activeWatchListRevert);
            }
            return change;
        },

        getWatchListPointOrder: (state) => {
            if (!!state.activeWatchList) {
                return state.activeWatchList.pointOrder;
            }
            return null;
        },

        getWatchListChartPoints: (state) => {
            return state.pointWatcher.filter(p => p.onChart);
        }
    },
};
export default watchListModule;
