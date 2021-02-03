import { reject } from "core-js/fn/promise"

const storeDataPoint = {

    state: {

    },

    mutations: {

    },

    actions: {

        getDataPointSimpleList(context) {
            return new Promise((resolve) => {
                let data = [
                    {name: '1 minute Load', id: 1, xid: 'DP_250372'},
                    {name: '5 minutes Load', id: 2, xid: 'DP_335775'},
                    {name: 'Point', id: 5, xid: 'DP_712779'},
                    {name: 'Numeric', id: 6, xid: 'DP_954927'},
                ];
                resolve(data);
            })
        }

    },

    getters: {

    },

} 