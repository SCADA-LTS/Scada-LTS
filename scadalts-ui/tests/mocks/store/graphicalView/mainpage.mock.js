/**
 * @author Radoslaw Jajko
 *
 */
 const storeGraphPage = {
	state: {
        graphicalPage: null,
        graphicalPageEdit: false,
        imageSets: [],
        canvasResolutions: [],
        annonymousAccess: [],
        resolution: {
            width: 1080,
            height: 720,
        },
        userPermission: 2,
		
	},
	mutations: {
        SET_GRAPHICAL_PAGE(state, payload) {
            state.graphicalPage = payload;
        },
        REVERT_GRAPHICAL_PAGE(state) {

        },
        SET_GRAPHICAL_PAGE_EDIT(state, payload) {
            state.graphicalPageEdit = payload;
        },
        SET_USER_ACCESS(state, payload) {
            state.userPermission = payload;
        }
		
	},
	actions: {
        fetchGraphicalViewsList() {
            return new Promise((resolve, reject) => {
                const response = [{
                    id: 1,
                    name: "Gv1",
                    xid: "GV_01"
                }]
                resolve(response);
            })
        },
        isGraphicalViewXidUnique({state}, payload) {
            return new Promise((resolve, reject) => {
                if(payload === "BAD_XID") {
                    resolve({uniqie: false})
                } else {
                    resolve({uniqie: true})
                }
            });

        }
		
	},
	getters: {
        userGraphicViewAccess(state) {
            return state.userPermission;
        }
    },
};
export default storeGraphPage;
