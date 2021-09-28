/**
 * @author Radoslaw Jajko
 *
 */


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

        SET_BLANK_ACTIVE_WATCHLIST(state) {
            state.activeWatchList = {
                id: -1,
                name: '',
                xid: '',
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
        getWatchListDetails({ dispatch, commit }, watchlistId) {
            return new Promise(async (resolve, reject) => {
                try {
                    let watchList = await dispatch('requestGet', `/watch-lists/${watchlistId}`);
                    let details = loadWatchListDetails(watchlistId);
                    if(!!details) {
                        watchList.horizontal = details.horizontal;
                        watchList.biggerChart = details.biggerChart;
                    } else {
                        watchList.horizontal = true;
                        watchList.biggerChart = false;
                    }
                    commit('SET_ACTIVE_WATCHLIST', watchList);
                    resolve(watchList);
                } catch (e) {
                    reject(e);
                }
            })
		},

        async loadWatchListPointHierarchyNode({ state, dispatch, commit }, node) {
            console.debug(`Loading Point Hierarchy for WatchList...\nNodeID: ${node.id}`);
            
            let pointHierarchy = await dispatch('fetchPointHierarchyNode', node.id);
            let responseArray = [];
            pointHierarchy.forEach(ph => {
                let item = {
                    name: ph.title,
                    xid: ph.xid,
                    folder: ph.folder,
                    id: ph.folder ? `f${ph.key}` : `p${ph.key}`,
                }
                if(ph.folder) {
                    item.children = [];
                } else {
                    if(!!state.activeWatchList) {
                        item.selected = !!state.activeWatchList.pointList.find(p => p.id === ph.key);
                    } else {
                        item.selected = false;
                    }
                    
                }
                responseArray.push(item);
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
                    let pointData = {
                        id: dp.id,
                        xid: dp.xid,
                        name: dp.name,
                        description: dp.description,
                        enabled: dp.enabled,
                        settable: dp.pointLocator.settable,
                        type: dp.pointLocator.dataTypeId,
                        value: pv.value,
                        timestamp: new Date(pv.ts).toLocaleTimeString(),
                        events: pe,
                    }
                    commit('ADD_POINT_TO_WATCHER', pointData);
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
        }

    },

	getters: {},
};
export default watchListModule;

function searchDataPointInHierarchy(array, elementId) {
    if(!!array && array.length > 0) {
        let result = array.find(item => (!item.folder && Number(item.id.slice(1)) === elementId));
        if(!!result) {
            console.debug("Found!")
            return result;
        } else {
            console.debug("Not found in that level\nSearching in")
            for(let i = 0; i < array.length; i++) {
                if(array[i].folder && !!array[i].children && array[i].children.length > 0) {
                    return searchDataPointInHierarchy(array[i].children, elementId);
                }
            }            
        }
    }
    return null;
    
}

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
