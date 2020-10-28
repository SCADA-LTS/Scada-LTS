/**
 * @author rjajko@softq.pl
 */

import axios from "axios"


const storePlcNotifications = {
    state: {
        datapointList: [],
        userList: [],
        rangeList: []
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
        }
    },
    actions: {
        getDataPointList() {
            return new Promise((resolve, reject) => {
                axios.get(`./api/datapoint/getAll`).then(response => {
                    console.log(response);
                    resolve(response.data);
                }).catch(error => {
                    reject(error);
                });
            });
        },
        getUserList() {
            return new Promise((resolve, reject) => {
                axios.get(`./api/user/getAll`).then(response => {
                    console.log(response);
                    resolve(response.data);
                }).catch(error => {
                    reject(error);
                });
            });
        },
        getRangeList() {
            return new Promise((resolve, reject) => {
                axios.get(`./api/alarms/notification/getAllRanges`).then(response => {
                    console.log(response);
                    resolve(response.data);
                }).catch(error => {
                    reject(error);
                });
            });
        },
        getSchedulersList() {
            return new Promise((resolve, reject) => {
                axios.get(`./api/alarms/notification/getAllSchedulers`).then(response => {
                    console.log(response);
                    resolve(response.data);
                }).catch(error => {
                    reject(error);
                });
            });
        },
        getSchedulerRaw({commit}, schedulerId) {
            return new Promise((resolve, reject) => {
                axios.get(`./api/alarms/notification/getScheduler/id/${schedulerId}`).then(response => {
                    console.log(response);
                    resolve(response.data);
                }).catch(error => {
                    reject(error);
                });
            });
        },
        getSchedulersByUser({commit}, userId) {
            return new Promise((resolve, reject) => {
                axios.get(`./api/alarms/notification/getSchedulers/user/${userId}`).then(response => {
                    console.log(response);
                    resolve(response.data);
                }).catch(error => {
                    reject(error);
                });
            });
        },
        getSchedulersByDataPoint({commit}, datapointId) {
            return new Promise((resolve, reject) => {
                axios.get(`./api/alarms/notification/getSchedulers/dataPoint/${datapointId}`).then(response => {
                    console.log(response);
                    resolve(response.data);
                }).catch(error => {
                    reject(error);
                });
            });
        },
        postSchedulerWithNotification({commit}, datapointData) {
            let notification = {
                id: null,
                perEmail: datapointData.mail,
                perSms: datapointData.sms,
                mtime: "2020-10-22 00:00:00"
            }
            return new Promise((resolve, reject) => {
                axios.post(`./api/alarms/notification/setSchedulerWithNotification/${datapointData.range}/${datapointData.user}/${datapointData.id}`, notification).then(response => {
                    console.log(response);
                    resolve(response.data);
                }).catch(error => {
                    reject(error);
                });
            });
        },
        postRange({commit}, range) {
            return new Promise((resolve, reject) => {
                axios.post(`./api/alarms/notification/setRange`, range).then(response => {
                    console.log(response);
                    resolve(response.data);
                }).catch(error => {
                    reject(error);
                });
            });
        },
        updateRange({commit}, range) {
            return new Promise((resolve, reject) => {
                axios.put(`./api/alarms/notification/updateRange`, range).then(response => {
                    console.log(response);
                    resolve(response.data);
                }).catch(error => {
                    reject(error);
                });
            });
        },
        deleteRange({commit}, range) {
            return new Promise((resolve, reject) => {
                axios.delete(`./api/alarms/notification/deleteRange/${range.id}`).then(response => {
                    console.log(response);
                    resolve(response.status);
                }).catch(error => {
                    reject(error);
                });
            });
        },
        getNotificationList() {
            return new Promise((resolve, reject) => {
                axios.get(`./api/alarms/notification/getAllNotifications`).then(response => {
                    console.log(response);
                    resolve(response.data);
                }).catch(error => {
                    reject(error);
                });
            });
        },
        updateNotification({commit}, notification) {
            return new Promise((resolve, reject) => {
                axios.put(`./api/alarms/notification/updateNotification`, notification).then(response => {
                    console.log(response);
                    resolve(response.data);
                }).catch(error => {
                    reject(error);
                });
            });
        },

    },
    getters: {
        
    }
}
export default storePlcNotifications;
