import { chartRenderersTemplates } from "../../../src/store/dataPoint/templates";

const dataPoint = {
	state: {
        chartRenderersTemplates: chartRenderersTemplates,
    },

	mutations: {},

	actions: {
        toggleDataPoint({dispatch}, datapointId) {
            return new Promise((resolve, reject) => {
                resolve({enabled: false});
            });
        }
    },
};

export const modules = {
	dataPoint
};
export default modules
