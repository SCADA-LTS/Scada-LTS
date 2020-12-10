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

        // getAllMailingLists(context) {
        //     return new Promise((resolve, reject) => {
        //         Axios.get(`${context.state.apiUrl}/watchlist/getAll`).then(response => {
        //             if(response.status === 200) {
        //                 resolve(response.data)
        //             } else {
        //                 reject(false)
        //             }
        //         }).catch(error => {
        //             console.error(error);
        //             reject(false);
        //         })
        //     })
        // },

        getAllMailingLists(context) {
            return new Promise((resolve, reject) => {
                let mailingListsMock = [
                    {id: 1, xid: 'ML_01', name: 'MailinList 01 Mock'},
                    {id: 2, xid: 'ML_02', name: 'MailinList 02 Mock'},
                    {id: 3, xid: 'ML_03', name: 'MailinList 03 Mock'},
                ];
                resolve(mailingListsMock);
            })
        }

    }, 

    getters: {
        
    }
}
export default storeAlarmsNotifications