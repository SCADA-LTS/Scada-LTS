const storeSynopticPanel = {
    state: { },
    
    getters: { },
    
    mutations: { },

    actions: {
        fetchSynopticPanelList({dispatch}) {
            return dispatch('requestGet', `/synoptic-panel/list`);
        },

        fetchSynopticPanel({dispatch}, id) {
            return dispatch('requestGet', `/synoptic-panel/getId/${id}`);
        },
        
        createSynopticPanel({dispatch}, synopticPanel) {
            return dispatch('requestPost', {
                url: `/synoptic-panel/create`,
                data: synopticPanel
            });
        },

        deleteSynopticPanel({dispatch}, id) {
            return dispatch('requestGet', `/synoptic-panel/deleteId/${id}`);
            //TODO: change request type
        },

        updateSynopticPanel({dispatch}, synopticPanel) {
            return dispatch('requestPost', {
                url: `/synoptic-panel/create`,
                data: synopticPanel
            });
            //TODO: Create separate Put Request
        },    
    }
}

export default storeSynopticPanel;