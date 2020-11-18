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
        getPointHierarchy(context, key) {
            return new Promise((resolve, reject) => {
                axios.get(`.//pointHierarchy/${key}`).then(response => {
                    if (response.status == 200) {
                        resolve(response.data)
                    }
                    reject(false);
                }).catch(error => {
                    console.log(error)
                    reject(false);
                })
            })
        },
        getDataPointList() {
            return new Promise((resolve, reject) => {
                axios.get(`./api/datapoint/getAll`).then(response => {
                    resolve(response.data);
                }).catch(error => {
                    reject(error);
                });
            });
        },
        getUserList() {
            return new Promise((resolve, reject) => {
                axios.get(`./api/user/getAll`).then(response => {
                    resolve(response.data);
                }).catch(error => {
                    reject(error);
                });
            });
        },
        getRangeList() {
            return new Promise((resolve, reject) => {
                axios.get(`./api/alarms/notification/getAllRanges`).then(response => {
                    resolve(response.data);
                }).catch(error => {
                    reject(error);
                });
            });
        },
        getSchedulersList() {
            return new Promise((resolve, reject) => {
                axios.get(`./api/alarms/notification/getAllSchedulers`).then(response => {
                    resolve(response.data);
                }).catch(error => {
                    reject(error);
                });
            });
        },
        getSchedulerRaw({commit}, schedulerId) {
            return new Promise((resolve, reject) => {
                axios.get(`./api/alarms/notification/getScheduler/id/${schedulerId}`).then(response => {
                    resolve(response.data);
                }).catch(error => {
                    reject(error);
                });
            });
        },
        getSchedulerDpIds({commit}, schedulerId) {
            return new Promise((resolve, reject) => {
                axios.get(`./api/alarms/notification/getScheduler/id/${schedulerId}/dp`).then(response => {
                    resolve(response.data);
                }).catch(error => {
                    reject(error);
                });
            });
        },
        getSchedulerUserIds({commit}, schedulerId) {
            return new Promise((resolve, reject) => {
                axios.get(`./api/alarms/notification/getScheduler/id/${schedulerId}/users`).then(response => {
                    resolve(response.data);
                }).catch(error => {
                    reject(error);
                });
            });
        },
        getSchedulersByUser({commit}, userId) {
            return new Promise((resolve, reject) => {
                axios.get(`./api/alarms/notification/getSchedulers/user/${userId}`).then(response => {
                    resolve(response.data);
                }).catch(error => {
                    reject(error);
                });
            });
        },
        getSchedulersByDataPoint({commit}, datapointId) {
            return new Promise((resolve, reject) => {
                axios.get(`./api/alarms/notification/getSchedulers/dataPoint/${datapointId}`).then(response => {
                    resolve(response.data);
                }).catch(error => {
                    reject(error);
                });
            });
        },
        createScheduler({commit}, object) {
            return new Promise((resolve, reject) => {
                axios.post(`./api/alarms/notification/setScheduler/${object.rangeId}/${object.notificationId}`).then(response => {
                    if(response.status == 201) {
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
        updateScheduler({commit}, scheduler) {
            return new Promise((resolve, reject) => {
                axios.put(`./api/alarms/notification/updateScheduler/${scheduler.id}/${scheduler.ranges_id}/${scheduler.notifications_id}`).then(response => {
                    resolve(response.data);
                }).catch(error => {
                    reject(error);
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
                    resolve(response.data);
                }).catch(error => {
                    reject(error);
                });
            });
        },
        postRange({commit}, range) {
            return new Promise((resolve, reject) => {
                axios.post(`./api/alarms/notification/setRange`, range).then(response => {
                    resolve(response.data);
                }).catch(error => {
                    reject(error);
                });
            });
        },
        updateRange({commit}, range) {
            return new Promise((resolve, reject) => {
                axios.put(`./api/alarms/notification/updateRange`, range).then(response => {
                    resolve(response.data);
                }).catch(error => {
                    reject(error);
                });
            });
        },
        deleteRange({commit}, range) {
            return new Promise((resolve, reject) => {
                axios.delete(`./api/alarms/notification/deleteRange/${range.id}`).then(response => {
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
                    resolve(response.data);
                }).catch(error => {
                    reject(error)
                })
            })
        },
        updateNotification({commit}, notification) {
            return new Promise((resolve, reject) => {
                axios.put(`./api/alarms/notification/updateNotification`, notification).then(response => {
                    resolve(response.data);
                }).catch(error => {
                    reject(error);
                });
            });
        },
        deleteNotification({commit}, notification) {
            return new Promise((resolve, reject) => {
                axios.delete(`./api/alarms/notification/deleteNotification/${notification.id}`).then(response => {
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
