import Axios from "axios"

const storeAlarmsNotifications = {
    state: {
        apiUrl: './api',

    },

    mutations: {

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
                        resolve(true) //TODO: At least EventDetector ID is required. 
                    } else {
                        reject(false)
                    }
                }).catch(error => {
                    console.error(error);
                    reject(false);
                })
            })
        },

        createEmailEventHandler(context, payload) {
            let dpId = payload.datapointId;
            let edId = payload.eventDetectorId;
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