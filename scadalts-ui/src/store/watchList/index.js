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
        pointMoved: false,
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

        SET_POINT_MOVED(state, pointListOrder) {
            let change = false;
            for(let i = 0; i < pointListOrder.length; i++) {
                change = pointListOrder[i].order !== i;
                if(change) {
                    break;
                }
            }
            state.pointMoved = change;
        },

        SET_DATAPOINT_HIERARCHY(state, datapointHierarchy) {
            state.datapointHierarchy = datapointHierarchy;
        },

        REMOVE_POINT_FROM_WATCHLIST(state, point) {
            let x = searchDataPointInHierarchy(state.datapointHierarchy, point.id);
            if (!!x) {
                x.selected = false;
            }
            if (!!state.activeWatchList) {
                state.activeWatchList.pointList = state.activeWatchList.pointList.filter(p => p.id !== point.id);
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


        getWatchListPointOrder({ dispatch }, id) {
            return new Promise(async (resolve, reject) => {
                try {
                    let orderMap = new Map();
                    let result = await dispatch('requestGet', `/watch-lists/order/${id}`);
                    Object.keys(result).forEach(key => {
                        orderMap.set(key, result[key]);
                    });
                    resolve(orderMap);
                } catch (e) {
                    console.error(e);
                    reject(new Map());
                }
            });
        },

        updateWatchListPointOrder({ state, dispatch }, watchListId) {
            let data = {
                watchListId,
                pointIds: {}
            };
            for (let order = 0; order < state.pointWatcher.length; order++) {
                data.pointIds[state.pointWatcher[order].id] = order;
            }

            return dispatch('requestPut', {
                url: '/watch-lists/order/',
                data
            });
        },



        getWatchListDetails({ dispatch, commit }, id) {
            return new Promise(async (resolve, reject) => {
                try {
                    let watchList = await dispatch('requestGet', `/watch-lists/${id}`);
                    let details = loadWatchListDetails(id);
                    if (!!details) {
                        watchList.horizontal = details.horizontal;
                        watchList.biggerChart = details.biggerChart;
                    } else {
                        watchList.horizontal = true;
                        watchList.biggerChart = false;
                    }

                    watchList.pointOrder = await dispatch('getWatchListPointOrder', id);
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

        createWatchList({ dispatch, state }) {
            return new Promise((resolve, reject) => {

                dispatch('requestPost', {
                    url: '/watch-lists',
                    data: state.activeWatchList,
                }).then(async (resp) => {
                    let horizontal = state.activeWatchList.horizontal;
                    let biggerChart = state.activeWatchList.biggerChart;
                    await dispatch('getWatchListDetails', resp.id);
                    state.activeWatchList.horizontal = horizontal;
                    state.activeWatchList.biggerChart = biggerChart;
                    saveWatchListDetails(state.activeWatchList);
                    resolve(resp);
                }).catch((e) => {
                    reject(e);
                });
            });
        },

        updateWatchList({ dispatch, state, commit }) {
            saveWatchListDetails(state.activeWatchList);
            dispatch('requestPut', {
                url: '/watch-lists',
                data: state.activeWatchList,
            }).then(() => {
                commit('SET_ACTIVE_WATCHLIST', state.activeWatchList);
                dispatch('updateWatchListPointOrder', state.activeWatchList.id).catch(e => {
                    console.error(e);
                    console.error("Failed to update WatchList Point Order");
                });
            });


        },

        deleteWatchList({ dispatch, state }) {
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
                    let order = state.activeWatchList.pointOrder.get(datapointId);
                    let pointData2 = new WatchListPoint().createWatchListPoint(dp, pv, pe, ds, order);
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

        updateActiveWatchList({ commit, state }, newValue) {
            commit('UPDATE_ACTIVE_WATCHLIST', newValue);
            return state.activeWatchList;
        },

    },

    getters: {
        watchListConfigChanged(state) {
            let change = false;
            if (!!state.activeWatchList && !!state.activeWatchListRevert) {
                change = JSON.stringify(state.activeWatchList) !== JSON.stringify(state.activeWatchListRevert);
            }
            if(!!state.pointMoved) {
                change = true;
            }
            return change;
        },

        getWatchListPointOrder(state) {
            if (!!state.activeWatchList) {
                return state.activeWatchList.pointOrder;
            }
            return null;
        }
    },
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
