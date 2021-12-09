/**
 * @author Radoslaw Jajko
 *
 */
 const storeGraphPage = {
	state: {
        graphicalPage: null,
        graphicalPageEdit: false,
        imageSets: [],
        resolution: {
            width: 1080,
            height: 720,
        }
		
	},
	mutations: {
		
	},
	actions: {
        getGraphicalViewById({state,dispatch}, id) {
            return new Promise((resolve, reject) => {
                state.graphicalPage = {
                    id: id,
                };
            })
        },

        getImageSets({state}) {
            return new Promise((resolve, reject) => {
                state.imageSets = [
                    {
                        id: 'SLTS-FAN',
                        name: "Scada-LTS Fan",
                        imageCount: 3,
                    },
                    {
                        id: 'SLTS-PUMP',
                        name: "Scada-LTS Pump",
                        imageCount: 6,
                    },
                ]
            });
        }
		
	},
	getters: {},
};
export default storeGraphPage;
