/**
 * @author rjajko@softq.pl
 */

import axios from "axios"


const storePlcNotifications = {
    state: {
        schedulerList: undefined,
        datapointList: undefined, 
        userList: undefined,
        rangeList: undefined,
        notificationList: undefined,
        debug: true,
    },
    mutations: {
        setDatapointList(state, datapointList) {
            state.datapointList = datapointList;
        },
        setUserList(state, userList) {
            state.userList = userList;
        },
        setRangeList(state, rangeList) {
            state.rangeList = rangeList;
        },
        SET_SCHEDULER_LIST(state, schedulerList) {
            state.schedulerList = schedulerList;
        },
        ADD_SCHEDULER(state, scheduler) {
            state.schedulerList.push(scheduler);
        },
        DELETE_SCHEDULER(state, scheduler) {
            state.schedulerList = state.schedulerList.filter((s) => s.id !== scheduler.id);
        },
        SET_DATAPOINT_LIST(state, datapointList) {
            state.datapointList = datapointList;
        },
        SET_USER_LIST(state, userList) {
            state.userList = userList;
        },
        SET_RANGE_LIST(state, rangeList) {
            state.rangeList = rangeList;
        },
        ADD_RANGE(state, range) {
            state.rangeList.push(range);
        },
        UPDATE_RANGE(state, range) {
            state.rangeList[state.rangeList.findIndex((r) => r.id == range.id)] = range;
        },
        DELETE_RANGE(state, range) {
            state.rangeList = state.rangeList.filter((r) => r.id !== range.id);
        },
        SET_NOTIFICATION_LIST(state, notificationList) {
            state.notificationList = notificationList;
        },
        ADD_NOTIFICATION(state, notification) {
            state.notificationList.push(notification);
        },
        UPDATE_NOTIFICATION(state, notification) {
            state.notificationList[state.notificationList.findIndex((n) => n.id == notification.id)] = notification;
        },
        DELETE_NOTIFICATION(state, notification) {
            state.notificationList = state.notificationList.filter((n) => n.id !== notification.id);
        }
    },
    actions: {
        getFromAPI(context, requestUrl) {
            return new Promise((resolve, reject) => {
                axios.get(requestUrl).then(response => {
                    resolve(response.data)
                }).catch(error => {
                    reject(false)
                })
            })
        },
        getDataPointListV2({dispatch, commit}) {
            dispatch('getFromAPI', './api/datapoint/getAll').then(data => {
                if(data) commit('SET_DATAPOINT_LIST', data);
            });
        },
        getUserListV2({dispatch, commit}) {
            dispatch('getFromAPI', './api/user/getAll').then(data => {
                if(data) commit('SET_USER_LIST', data);
            });
        },
        getRangeListV2({dispatch, commit}) {
            dispatch('getFromAPI', './api/alarms/notification/getAllRanges').then(data => {
                if(data) commit('SET_RANGE_LIST', data);
            });
        },
        getNotificationListV2({dispatch, commit}) {
            dispatch('getFromAPI', './api/alarms/notification/getAllNotifications').then(data => {
                if(data) commit('SET_NOTIFICATION_LIST', data);
            });
        },
        getSchedulerList({dispatch, commit}) {
            dispatch('getFromAPI', './api/alarms/notification/getAllSchedulers').then(data => {
                if(data) commit('SET_SCHEDULER_LIST', data);
            });
        },
        getSchedulerDetails({dispatch}, schedulerId) {
            return new Promise(resolve => {
                const infoPromise = dispatch('getFromAPI', `./api/alarms/notification/getScheduler/id/${schedulerId}`);
                const dpidsPromise = dispatch('getSchedulerDataPoints', schedulerId);
                const usridsPromise = dispatch('getSchedulerUsers', schedulerId);
                Promise.all([infoPromise, dpidsPromise, usridsPromise]).then(values => {
                    let scheduler = {
                        info: values[0],
                        dpids: values[1],
                        usrids: values[2]
                    }
                    resolve(scheduler);
                });
            })
        },
        getSchedulerDataPoints({state, dispatch}, schedulerId) {
            if(state.debug) console.debug(`Vuex:: getSchedulerDataPoints(${schedulerId})`)
            return new Promise(resolve => {
                dispatch('getFromAPI', `./api/alarms/notification/getScheduler/id/${schedulerId}/dp`).then(datapointIds => {
                    resolve(state.datapointList.filter(e => datapointIds.indexOf(e.id) > -1));
                })
            })
        },
        getSchedulerUsers({state, dispatch}, schedulerId) {
            if(state.debug) console.debug(`Vuex:: getSchedulerUsers(${schedulerId})`)
            return new Promise(resolve => {
                dispatch('getFromAPI', `./api/alarms/notification/getScheduler/id/${schedulerId}/users`).then(userIds => {
                    resolve(state.userList.filter(e => userIds.indexOf(e.id) > -1));
                });
            })
        },
        getPointHierarchy({state, dispatch}, key) {
            if(state.debug) console.debug(`Vuex::getPointHierarchy(${key})`)
            return new Promise(resolve => {
                dispatch('getFromAPI', `.//pointHierarchy/${key}`).then(data => resolve(data));
            });
        },
        /*UNUSED*/
        getSchedulersByUser({commit}, userId) {
            return new Promise((resolve, reject) => {
                axios.get(`./api/alarms/notification/getSchedulers/user/${userId}`).then(response => {
                    resolve(response.data);
                }).catch(error => {
                    reject(error);
                });
            });
        },
        /*UNUSED*/
        getSchedulersByDataPoint({commit}, datapointId) {
            return new Promise((resolve, reject) => {
                axios.get(`./api/alarms/notification/getSchedulers/dataPoint/${datapointId}`).then(response => {
                    resolve(response.data);
                }).catch(error => {
                    reject(error);
                });
            });
        },
        createScheduler({commit}, scheduler) {
            return new Promise((resolve, reject) => {
                axios.post(`./api/alarms/notification/setScheduler/${scheduler.range}/${scheduler.notification.id}`).then(response => {
                    if(response.status == 201) {
                        commit("ADD_SCHEDULER", response.data);
                        resolve(response.data);
                    }
                    reject(false);
                }).catch(error => {
                    console.error(error);
                    reject(false);
                })
            })

        },
        postSchedulerWithNotification({commit}, datapointData) {
            let now = new Date();
            let date = `${now.getFullYear()}-${now.getMonth()+1}-${now.getDate()}`;
            let time = `${now.getHours()}:${now.getMinutes()}:${now.getSeconds()}`;
            let notificationData = {
                id: null,
                perEmail: !!datapointData.mail,
                perSms: !!datapointData.sms,
                mtime: `${date} ${time}`
            }
            return new Promise((resolve, reject) => {
                axios.post(`./api/alarms/notification/setSchedulerWithNotification/${datapointData.range}/${datapointData.user}/${datapointData.id}`, notificationData).then(response => {
                    resolve(response.data);
                }).catch(error => {
                    reject(error);
                });
            });
        },
        updateScheduler({state}, scheduler) {
            return new Promise((resolve, reject) => {
                axios.put(`./api/alarms/notification/updateScheduler/${scheduler.id}/${scheduler.ranges_id}/${scheduler.notifications_id}`).then(response => {
                    state.schedulerList[state.schedulerList.findIndex((s) => s.id == response.data.id)] = response.data;
                    resolve(response.data);
                }).catch(error => {
                    console.error(error)
                    reject(false);
                });
            });
        },
        bindSchedulerWithUser({commit}, payload) {
            return new Promise((resolve, reject) => {
                axios.get(`./api/alarms/notification/bindScheduler/${payload.sid}/user/${payload.uid}`).then(response => {
                    resolve(response.data);
                }).catch(error => {
                    reject(error);
                });
            });
        },
        bindSchedulerWithDataPoint({commit}, payload) {
            return new Promise((resolve, reject) => {
                axios.get(`./api/alarms/notification/bindScheduler/${payload.sid}/dp/${payload.dpid}`).then(response => {
                    resolve(response.data);
                }).catch(error => {
                    reject(error);
                });
            });
        },
        deleteScheduler({commit}, scheduler) {
            return new Promise((resolve, reject) => {
                axios.delete(`./api/alarms/notification/deleteScheduler/${scheduler.id}`).then(response => {
                    commit("DELETE_SCHEDULER", scheduler);
                    resolve(response.data);
                }).catch(error => {
                    reject(error);
                });
            });
        },
        postRange({state, commit}, range) {
            return new Promise((resolve, reject) => {
                axios.post(`./api/alarms/notification/setRange`, range).then(response => {
                    commit("ADD_RANGE", response.data);
                    resolve(response.data);
                }).catch(error => {
                    reject(error);
                });
            });
        },
        updateRange({commit}, range) {
            return new Promise((resolve, reject) => {
                axios.put(`./api/alarms/notification/updateRange`, range).then(response => {
                    commit("UPDATE_RANGE", response.data);
                    resolve(response.data);
                }).catch(error => {
                    reject(error);
                });
            });
        },
        deleteRange({commit}, range) {
            return new Promise((resolve, reject) => {
                axios.delete(`./api/alarms/notification/deleteRange/${range.id}`).then(response => {
                    commit("DELETE_RANGE", range);
                    resolve(response.status);
                }).catch(error => {
                    reject(error);
                });
            });
        },
        getNotificationList() {
            return new Promise((resolve, reject) => {
                axios.get(`./api/alarms/notification/getAllNotifications`).then(response => {
                    resolve(response.data);
                }).catch(error => {
                    reject(error);
                });
            });
        },
        createNotification({commit}, notification) {
            return new Promise((resolve, reject) => {
                axios.post(`./api/alarms/notification/setNotification`, notification).then(response => {
                    commit("ADD_NOTIFICATION", response.data);
                    resolve(response.data);
                }).catch(error => {
                    reject(error)
                })
            })
        },
        updateNotification({commit}, notification) {
            return new Promise((resolve, reject) => {
                axios.put(`./api/alarms/notification/updateNotification`, notification).then(response => {
                    commit("UPDATE_NOTIFICATION", response.data);
                    resolve(response.data);
                }).catch(error => {
                    reject(error);
                });
            });
        },
        deleteNotification({commit}, notification) {
            return new Promise((resolve, reject) => {
                axios.delete(`./api/alarms/notification/deleteNotification/${notification.id}`).then(response => {
                    commit("DELETE_NOTIFICATION", notification);
                    resolve(response.data);
                }).catch(error => {
                    reject(error);
                });
            });
        }

    },
    getters: {
        
    }
}
export default storePlcNotifications;
