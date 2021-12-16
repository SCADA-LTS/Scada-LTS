import {
	chartRenderersTemplates,
	eventRenderersTemplates,
	textRenderesTemplates,
} from '../../../src/store/dataPoint/templates';

export const dataPoint = {
	state: {
		chartRenderersTemplates: chartRenderersTemplates,
		eventRenderersTemplates: eventRenderersTemplates,
		textRenderesTemplates: textRenderesTemplates,

		textRenderesList: [
			{ id: 0, label: 'Analog' },
			{ id: 1, label: 'Binary' },
			{ id: 2, label: 'Multistate' },
			{ id: 3, label: 'Plain' },
			{ id: 4, label: 'Range' },
			{ id: 5, label: 'Time' },
		],

		loggingTypeList: [
			{ id: 1, type: 'ON_CHANGE', label: 'Change' },
			{ id: 2, type: 'ALL', label: 'All data' },
			{ id: 3, type: 'NONE', label: 'Do no log' },
			{ id: 4, type: 'INTERVAL', label: 'Interval' },
			{ id: 5, type: 'ON_TS_CHANGE', label: 'On Ts Change' },
		],

		chartRenderersList: [
			{ id: -1, label: 'None' },
			{ id: 0, label: 'Table' },
			{ id: 1, label: 'Image' },
			{ id: 2, label: 'Stats' },
		],
	},

	mutations: {},

	actions: {
		toggleDataPoint({ dispatch }, datapointId) {
			return new Promise((resolve, reject) => {
				resolve({ enabled: false });
			});
		},

		clearDataPointCache({ dispatch }, datapointId) {
			return new Promise((resolve, reject) => {
				resolve(true);
			});
		},

		getDataPointValue({ dispatch }, datapointId) {
			return new Promise((resolve, reject) => {
				resolve({value: 10});
			})
		},

		getDataPointValueFromTimeperiod({dispatch}, payload) {
			return new Promise((resolve) => {
				const point = {
					name: "TestPoint",
					type: "Numeric",
					values: [ { value: "11.0", ts: 1637237555393}],
				}
				resolve(point);
			});
		}
	},
};

export default dataPoint;
