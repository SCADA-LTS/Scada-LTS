import SynopticPanelAPI from "../../api/SynopticPanelAPI";

export default {
    state: {
        synopticPanelList: [],
        synopticPanelSelected: undefined
    },
    getters: {
        selectedPanelId(state) {
            if (state.synopticPanelSelected !== undefined) {
                if (state.synopticPanelSelected !== null) {
                    return state.synopticPanelSelected.id;
                }
            }
            return null;
        }
    },
    mutations: {
        setSynopticPanelList(state, synopticPanels) {
            state.synopticPanelList = synopticPanels;
        },
        setSelectedSynopticPanel(state, synopticPanel) {
            state.synopticPanelSelected = synopticPanel;
        },
        deleteSynopticPanelListItem(state, synopticPanelId) {
            state.synopticPanelList = state.synopticPanelList.filter(function (panel) {
                return panel.id !== synopticPanelId;
            });
        }
    },
    actions: {
        fetchSynopticPanelList(context) {
            return new Promise((resolve, reject) => {
                SynopticPanelAPI.getSynopticPanelList().then(panels => {
                    context.commit('setSynopticPanelList', panels)
                    resolve()
                })
            })
        },
        fetchSynopticPanelId(context, id) {
            return new Promise((resolve, reject) => {
                SynopticPanelAPI.getSynopticPanelId(id).then(panel => {
                    context.commit('setSelectedSynopticPanel', panel)
                    resolve()
                })
            })
        },
        createSynopticPanel(context, synopticPanel) {
            return new Promise((resolve, reject) => {
                SynopticPanelAPI.createSynopticPanel(synopticPanel).then(() => {
                    context.dispatch('fetchSynopticPanelList');
                    resolve()
                })
            })
        },
        updateSynopticPanel(context, synopticPanel) {
            return new Promise((resolve, reject) => {
                SynopticPanelAPI.createSynopticPanel(synopticPanel).then(() => {
                    resolve()
                })
            })
        },
        deleteSynopticPanel(context, id) {
            return new Promise((resolve, reject) => {
                SynopticPanelAPI.deleteSynopticPanelId(id).then(() => {
                    context.commit('setSelectedSynopticPanel', null)
                    context.commit('deleteSynopticPanelListItem', id);
                    resolve()
                })
            })
        }
    }
}