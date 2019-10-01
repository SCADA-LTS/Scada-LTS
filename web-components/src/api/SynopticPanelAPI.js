import axios from 'axios';
const config = {timeout: 5000, useCredentails: true, credentials: 'same-origin'};

export default {

    getSynopticPanelList() {
        return new Promise((resolve, reject) => {
            axios.get(`./api/synoptic-panel/list`, config).then(response => {
                resolve(response.data);
            }).catch(error => {
                reject(error)
            })
        });
    },

    getSynopticPanelId(id) {
        return new Promise((resolve, reject) => {
            axios.get(`./api/synoptic-panel/getId/${id}`, config).then(response => {
                resolve(response.data);
            }).catch(error => {
                reject(error)
            })
        });
    },

    createSynopticPanel(panel) {
        return new Promise((resolve, reject) => {
            axios.post(`./api/synoptic-panel/create`, panel, config).then(response => {
                resolve(response.data);
            }).catch(error => {
                reject(error)
            })
        })
    },

    deleteSynopticPanelId(panelId) {
        return new Promise((resolve, reject) => {
            axios.get(`./api/synoptic-panel/deleteId/${panelId}`, config).then(response => {
                resolve(response.data);
            }).catch(error => {
                reject(error)
            })
        });
    }

}