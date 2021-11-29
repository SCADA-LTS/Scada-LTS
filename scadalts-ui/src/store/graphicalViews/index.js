import { annonymousAccess, canvasResolutions } from "./constants";
import { backgroundImages, graphicalViewPage, imageSetFan, imageSets } from "./mocks";

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
        resolution: { width: 1024, height: 768 },
    },

    mutations: {
        SET_GRAPHICAL_PAGE_EDIT(state, payload) {
            state.graphicalPageEdit = payload;
            if (!payload) {
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
            if (!!payload.backgroundFilename) {
                state.resolution = {
                    width: payload.width,
                    height: payload.height
                }
            } else {
                state.resolution = {
                    width: payload.resolution.split('x')[0].substring(1),
                    height: payload.resolution.split('x')[1]
                }
            }
        },
        SET_GRAPHICAL_PAGE_BACKUP(state, payload) {
            state.graphicalPageBackup = JSON.parse(JSON.stringify(payload));
        },
        REVERT_GRAPHICAL_PAGE(state) {
            state.graphicalPage = JSON.parse(JSON.stringify(state.graphicalPageBackup));
            state.resolution = {
                width: state.graphicalPage.resolution.split('x')[0].substring(1),
                height: state.graphicalPage.resolution.split('x')[1]
            }
        },
        SET_GRAPHICAL_PAGE_BACKGROUND(state, payload) {
            state.graphicalPage.backgroundFilename = payload.imgUrl;
            state.graphicalPage.width = payload.width;
            state.graphicalPage.height = payload.height;
            state.resolution = {
                width: payload.width,
                height: payload.height
            }
        },
        RESET_GRAPHICAL_PAGE_BACKGROUND(state) {
            state.graphicalPage.backgroundFilename = null;
            state.resolution = {
                width: state.graphicalPage.resolution.split('x')[0].substring(1),
                height: state.graphicalPage.resolution.split('x')[1]
            }
        },
        UPDATE_GRAPHICAL_PAGE_RESOLUTION(state) {
            state.resolution = {
                width: state.graphicalPage.resolution.split('x')[0].substring(1),
                height: state.graphicalPage.resolution.split('x')[1]
            }
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
        ADD_COMPONENT_TO_PAGE(state, payload) {
            if (state.graphicalPage.viewComponents.length > 0) {
                const maxIndex = state.graphicalPage.viewComponents.reduce((prev, current) => {
                    return (prev.index > current.index) ? prev.index : current.index;
                });
                payload.index = maxIndex + 1;
            } else {
                payload.index = 0;
            }
            state.graphicalPage.viewComponents.push(payload);
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

        getImageSets({ dispatch, commit }) {
            return new Promise((resolve) => {
                commit('SET_IMAGE_SETS', imageSets);
            });
        },
        getImageSetDetails({ dispatch }, imageSetName) {
            return new Promise(resolve => {
                resolve(imageSetFan);
            });
        },

        getUploadedBackgrounds({ dispatch }) {
            return new Promise(resolve => {
                resolve(backgroundImages);
            });
        },

        uploadBackground({dispatch}, payload) {
            return dispatch('requestPostFile', {
                url: '/view/uploads',
                data: payload
            });
        },

        evaluateScriptComponent({ dispatch }, payload) {
            return new Promise((resolve) => {
                console.log("Processing script...");
                console.log(payload);
                resolve("Finished" + new Date().getTime());
            });
        }
    },

    getters: {
        viewComponentsGetter(state) {
            return state.graphicalPage.viewComponents;
        }

    }

}

function getBaseUrl() {
    let locale = window.location.pathname.split('/')[1];
    if (!!locale) {
        locale += '/';
    }
    let protocol = window.location.protocol;
    let host = window.location.host.split(":");
    return `${protocol}//${host[0]}:${host[1]}/${locale}`;
}


export default graphicalViewModule;