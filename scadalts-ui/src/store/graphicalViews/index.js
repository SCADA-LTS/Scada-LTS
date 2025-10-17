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
            setResolution(state, payload);
        },
        SET_GRAPHICAL_PAGE_BACKUP(state, payload) {
            state.graphicalPageBackup = JSON.parse(JSON.stringify(payload));
        },        
        REVERT_GRAPHICAL_PAGE(state) {
            state.graphicalPage = JSON.parse(JSON.stringify(state.graphicalPageBackup));
            if(!!state.graphicalPage) {
                state.resolution = {
                    width: state.graphicalPage.width,
                    height: state.graphicalPage.height
                }
            } else {
                state.resolution = {
                    width: 1024,
                    height: 768
                }
            }
        },
        SET_GRAPHICAL_PAGE_BACKGROUND(state, payload) {
            state.graphicalPage.backgroundFilename = payload.imgUrl;
            setResolution(state, payload);
        },
        RESET_GRAPHICAL_PAGE_BACKGROUND(state) {
            const resolution = state.canvasResolutions.find(res => res.value === state.graphicalPage.resolution);
            state.graphicalPage.backgroundFilename = null;
            setResolution(state, resolution);
        },
        UPDATE_GRAPHICAL_PAGE_RESOLUTION(state) {
            const resolution = state.canvasResolutions.find(res => res.value === state.graphicalPage.resolution);
            setResolution(state, resolution);
        },

        SET_COMPONENT_EDIT(state, payload) {
            state.componentEdit = state.graphicalPage.viewComponents.findIndex(cmp => (
                cmp.type === payload.type &&
                cmp.x === payload.x &&
                cmp.y === payload.y &&
                cmp.z === payload.z
            ));
            state.componentEditBackup = JSON.parse(JSON.stringify(payload));
        },
        REVERT_COMPONENT_EDIT(state) {
            state.graphicalPage.viewComponents[state.componentEdit] = JSON.parse(JSON.stringify(state.componentEditBackup));
        },
        ADD_COMPONENT_TO_PAGE(state, payload) {
            if (state.graphicalPage.viewComponents.length > 0) {
                const maxIndex = state.graphicalPage.viewComponents.reduce((prev, current) => {
                    return (prev.index > current.index) ? prev.index : current.index;
                });
                if(typeof maxIndex === 'object') {
                    payload.index = maxIndex.index + 1;
                } else {
                    payload.index = maxIndex + 1;
                }
                
            } else {
                payload.index = 0;
            }
            state.graphicalPage.viewComponents.push(payload);
        },
        DELETE_COMPONENT_EDIT(state) {
            state.graphicalPage.viewComponents.splice(state.componentEdit, 1);
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
            return new Promise(async (resolve, reject) => {
                try {
                    const response = await dispatch('requestGet', `/view?id=${id}`);
                    commit('SET_GRAPHICAL_PAGE', response);
                    commit('SET_GRAPHICAL_PAGE_BACKUP', response);
                    resolve(response)
                } catch (e) {
                    reject(e);
                }                
            });
        },

        getUniqeGraphicalViewXid({dispatch }) {
            return dispatch('requestGet', `/view/generateXid`);
        },

        isGraphicalViewXidUnique({ dispatch }, {xid, id}) {
            return dispatch('requestGet', `/view/validate?xid=${xid}&id=${id}`);
        },

        createGraphicalView({ state, commit, dispatch }) {
            return new Promise(async (resolve, reject) => {
                try {
                    const response = await dispatch('requestPost', {
                        url: '/view',
                        data: state.graphicalPage
                    });
                    state.graphicalPage.id = response.viewId;
                    commit('SET_GRAPHICAL_PAGE', state.graphicalPage);
                    commit('SET_GRAPHICAL_PAGE_BACKUP', state.graphicalPage);
                    resolve(state.graphicalPage);
                } catch (e) {
                    reject(e);
                }
            });
        },

        saveGraphicalView({ state, dispatch }) {
            return new Promise(async (resolve, reject) => {
                try {
                    const response = await dispatch('requestPut', {
                        url: '/view',
                        data: state.graphicalPage
                    });
                    resolve(response);
                } catch (e) {
                    reject(e);
                }
            });
        },

        deleteGraphicalView({ state, dispatch }) {
            return new Promise((resolve, reject) => {
                try {
                    const response = dispatch('requestDelete', `/view?id=${state.graphicalPage.id}`);
                    resolve(response);
                } catch (e) {
                    reject(e);
                }
            })
        },

        getImageSets({ dispatch, commit }) {
            return new Promise(async (resolve, reject) => {
                try {
                    const response = await dispatch('requestGet', '/view/imageSets');
                    commit('SET_IMAGE_SETS', response);
                } catch (e) {
                    reject(e);
                }
            });
        },
        getImageSetDetails({ dispatch }, imageSetId) {
            return new Promise(async (resolve, reject) => {
                try {
                    const response = await dispatch('requestGet', `/view/imageSets/${imageSetId}`);
                    resolve(response);
                } catch (e) {
                    reject(e);
                }
            });
        },

        getUploadedBackgrounds({ dispatch }) {
            return new Promise(async (resolve, reject) => {
                try {
                    const response = await dispatch('requestGet', '/view/uploads/');
                    resolve(response);
                } catch (e) {
                    reject(e);
                }
            });
        },

        uploadBackground({dispatch}, payload) {
            return dispatch('requestPostFile', {
                url: '/view/uploads/',
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
        },
        userGraphicViewAccess(state, getters, rootState) {
            const user = rootState.loggedUser;
            if(!!user) {
                if(user.admin) {
                    return 2;
                } else {
                    const gv = state.graphicalPage;
                    if(!!gv) {
                        if(gv.userId === user.id) {
                            return 2;
                        }
                        return gv.viewUsers.find(a => a.userId === user.id).accessType || 0;
                    }
                }
            }
            return 0;
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

/**
 * Set Canvas Resolution
 * 
 * Update the state variable with the new resolution data.
 * Update the graphical page object and the canvas size object.
 * 
 * @param {Object} state - Vuex state object to modify
 * @param {Object} payload  - Object containing the new resolution data with width and height properties
 */
function setResolution(state, payload = {width: 1024, height: 768}) {
    state.graphicalPage.width = payload.width;
    state.graphicalPage.height = payload.height;
    state.resolution = {
        width: payload.width,
        height: payload.height
    }
}

export default graphicalViewModule;