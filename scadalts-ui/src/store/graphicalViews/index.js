import { annonymousAccess, canvasResolutions } from "./constants";
import { graphicalViewPage, imageSetFan, imageSets } from "./mocks";

export const graphicalViewModule = {
    state: {
        graphicalPageEdit: false,
        graphicalPageIconify: false,
        graphicalPage: null,
        graphicalPageBackup: null,
        componentEdit: null,
        componentEditBackup: null,
        imageSets: [],
        annonymousAccess: annonymousAccess,
        canvasResolutions: canvasResolutions,
    },

    mutations: {
        SET_GRAPHICAL_PAGE_EDIT(state, payload) {
            state.graphicalPageEdit = payload;
            if(!payload) {
                state.graphicalPageIconify = false;
            }
        },
        SET_GRAPHICAL_PAGE_ICONIFY(state, payload) {
            state.graphicalPageIconify = payload;
        },
        TOGGLE_GRAPHICAL_PAGE_ICONIFY(state) {
            state.graphicalPageIconify = !state.graphicalPageIconify;
        },
        SET_GRAPHICAL_PAGE(state, payload) {
            state.graphicalPage = payload;
        },
        SET_GRAPHICAL_PAGE_BACKUP(state, payload) {
            state.graphicalPageBackup = JSON.parse(JSON.stringify(payload));
        },
        REVERT_GRAPHICAL_PAGE(state) {
            state.graphicalPage = JSON.parse(JSON.stringify(state.graphicalPageBackup));
        },
        SET_COMPONENT_EDIT(state, payload) {
            state.componentEdit = state.graphicalPage.viewComponents.findIndex(cmp => (
                cmp.type === payload.type && 
                cmp.x === payload.x &&
                cmp.y === payload.y &&
                cmp.z === payload.z
            ));
            console.log(state.componentEdit);
            state.componentEditBackup = JSON.parse(JSON.stringify(payload));
        },
        REVERT_COMPONENT_EDIT(state) {
            console.log(state.componentEditBackup);
            state.graphicalPage.viewComponents[state.componentEdit] = JSON.parse(JSON.stringify(state.componentEditBackup));            
            console.log(state.graphicalPage.viewComponents);
            console.log(state.graphicalPage);
        },
        DELETE_COMPONENT_EDIT(state) {
            state.graphicalPage.viewComponents.splice(state.componentEdit, 1);
            console.log(state.graphicalPage.viewComponents);
        },
        SET_IMAGE_SETS(state, payload) {
            state.imageSets = payload;
        },
    },

    actions: {
        fetchGraphicalViewsList({ dispatch }) {
            return dispatch('requestGet', '/view/getAll');
        },

        getGraphicalViewById({ commit, dispatch }, id) {
            console.log(id);
            return new Promise((resolve, reject) => {
                commit('SET_GRAPHICAL_PAGE', graphicalViewPage);
                commit('SET_GRAPHICAL_PAGE_BACKUP', graphicalViewPage);
                resolve(graphicalViewPage)
            });
        },

        createGraphicalView({ state, dispatch }) {
            console.log(state.graphicalPage);
            return new Promise((resolve) => {
                resolve(true);
            });
        },

        saveGraphicalView({ state, dispatch }) {
            console.log(state.graphicalPage);
            return new Promise((resolve) => {
                resolve(true);
            });
        },

        deleteGraphicalView({ state, dispatch }) {
            console.warn("DELTED GV");
            return new Promise((resolve) => {
                resolve(true);
            })
        },

        getImageSets({dispatch, commit}) {
            return new Promise((resolve) => {
                commit('SET_IMAGE_SETS', imageSets);
            });
        },
        getImageSetDetails({dispatch}, imageSetName) {
            return new Promise(resolve => {
                resolve(imageSetFan);
            });
        }
    },

    getters: {
        viewComponentsGetter(state){
            return state.graphicalPage.viewComponents;
        }

    }

}


export default graphicalViewModule;