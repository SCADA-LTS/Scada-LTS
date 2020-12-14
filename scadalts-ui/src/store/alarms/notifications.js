import Axios from "axios"

const storeAlarmsNotifications = {
    state: {
        apiUrl: './api',
        eventHandlers: [],

    },

    mutations: {
        SET_EVENT_HANDLERS(state, eventHandlers) {
            state.eventHandlers = eventHandlers;
        }

    },

    actions: {
        getPlcDataPoints(context, datasourceId) {
            return new Promise((resolve, reject) => {
                Axios.get(`${context.state.apiUrl}/datapoint/${datasourceId}/getAllPlc`).then(response => {
                    if(response.status === 200) {
                        resolve(response.data)
                    } else {
                        reject(false)
                    }
                }).catch(error => {
                    console.error(error)
                    reject(false)
                })
            })
        },

        getPlcEventHandlers(context) {
            return new Promise((resolve, reject) => {
                Axios.get(`${context.state.apiUrl}/eventHandler/getAllPlc`).then(response => {
                    if(response.status === 200) {
                        context.commit("SET_EVENT_HANDLERS", response.data);
                        resolve(response.data) 
                    } else {
                        reject(false)
                    }
                }).catch(error => {
                    console.error(error)
                    reject(false)
                })
            })
        },


        getAllMailingLists(context) {
            return new Promise((resolve, reject) => {
                Axios.get(`${context.state.apiUrl}/mailingList/getAll`).then(response => {
                    if(response.status === 200) {
                        resolve(response.data)
                    } else {
                        reject(false)
                    }
                }).catch(error => {
                    console.error(error);
                    reject(false);
                })
            })
        },

        createPointEventDetector(context, datapointId) {
            let body = {
                xid: `PEDPLC_${datapointId}`,
                alias: `PlcDetector_${datapointId}`,
                alarmLevel: 2,
                duration: 10,
                durationType: 2,
                binartState: true
            };
            return new Promise((resolve, reject) => {
                Axios.post(`${context.state.apiUrl}/eventDetector/set/binary/state/${datapointId}`, body).then(response => {
                    if(response.status === 200) {
                        resolve(response.data)
                    } else {
                        reject(false)
                    }
                }).catch(error => {
                    console.error(error);
                    reject(false);
                })
            })
        },


        getEventHandlerById(context, eventHandlerId) {
            return new Promise((resolve, reject) => {
                Axios.get(`${context.state.apiUrl}/eventHandler/get/id/${eventHandlerId}`).then(response => {
                    if(response.status === 200) {
                        resolve(response.data);
                    } else {
                        reject(false);
                    }
                }).catch(error => {
                    console.error(error);
                    reject(false);
                })
            })
        },

        deleteMailingListFromEventHandler(context, payload) {
            payload.eventHandler.activeRecipients = payload.eventHandler.activeRecipients.filter(e => {
                return e.referenceId !== payload.activeMailingList
            });
            return payload.eventHandler;
        },

        addMailingListToEventHandler(context, payload) {
            if(!payload.eventHandler.activeRecipients) {
                payload.eventHandler.activeRecipients = [];
            }
            let mailingList = {
                recipientType: 1,
                referenceId: payload.activeMailingList,
                referenceAddress: null
            }
            payload.eventHandler.activeRecipients.push(mailingList);
            console.log(payload.eventHandler);
            return payload.eventHandler;
        },

        async updateEventHandler(context, payload) {
            let eventHandler = await context.dispatch("getEventHandlerById", payload.ehId);
            console.log(eventHandler);
            if(payload.method === "add") {
                eventHandler = await context.dispatch("addMailingListToEventHandler", {
                    eventHandler: eventHandler, activeMailingList: payload.activeMailingList
                });
            } else if (payload.method === "delete") {
                eventHandler = await context.dispatch("deleteMailingListFromEventHandler", {
                    eventHandler: eventHandler, activeMailingList: payload.activeMailingList
                });
            }
            console.log(eventHandler);

            if(eventHandler.activeRecipients.length === 0 ) {
                let ehStatus = await context.dispatch("deleteEventHandler", eventHandler.id);
                let edStatus = await context.dispatch("deleteEventDetector", {dpId: payload.typeRef1,edId:payload.typeRef2});
                return ehStatus && edStatus;
            } else {
                return new Promise((resolve, reject) => {
                    Axios.put(`${context.state.apiUrl}/eventHandler/update/1/${payload.typeRef1}/${payload.typeRef2}`, eventHandler).then(response => {
                        console.log(response)
                        if(response.status === 200) {
                            resolve(true);
                        } else {
                            reject(false);
                        }
                    }).catch(error => {
                        console.error(error);
                        reject(false);
                    })
                })
            }        
        },

        deleteEventHandler(context, eventHandlerId) {
            return new Promise((resolve, reject) => {
                Axios.delete(`${context.state.apiUrl}/eventHandler/delete/id/${eventHandlerId}`).then(response => {
                    if(response.status === 200) {
                        resolve(true);
                    } else {
                        reject(false);
                    }
                }).catch(error => {
                    console.error(error);
                    reject(false);
                })
            })
        },

        deleteEventDetector(context, payload) {
            return new Promise((resolve, reject) => {
                Axios.delete(`${context.state.apiUrl}/eventDetector/delete/${payload.dpId}/${payload.edId}`).then(response => {
                    if(response.status === 200) {
                        resolve(true);
                    } else {
                        reject(false);
                    }
                }).catch(error => {
                    console.error(error);
                    reject(false);
                })
            })
        },



        async createEmailEventHandler(context, payload) {

            let pedId = await context.dispatch("createPointEventDetector", payload.datapointId);
            let edId = pedId.id;

            let dpId = payload.datapointId;
            let mlId = payload.mailingListId;

            let body = {
                id: -1,
                xid: `EEH_${dpId}_${edId}_${mlId}`,
                alias: `EEH_${dpId}_${edId}_${mlId}`,
                disabled: false,
                activeRecipients: [{
                    recipientType: 1,
                    referenceId: mlId,
                    referenceAddress: null
                }],
                sendEscalation: false,
                escalationDelayType: 1,
                escalationDelay: 0,
                escalationRecipients: null,
                sendInactive: false,
                inactiveOverride: false,
                inactiveRecipients: null
            }

            return new Promise((resolve, reject) => {
                Axios.post(`${context.state.apiUrl}/eventHandler/set/1/${dpId}/${edId}/2`, body).then(response => {
                    if(response.status === 200) {
                        resolve(true)
                    } else {
                        reject(false)
                    }
                }).catch(error => {
                    console.error(error);
                    reject(false);
                })
            })

        }

    },

    getters: {
        
    }
}
export default storeAlarmsNotifications