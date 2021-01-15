import Axios from 'axios';

import BaseChart from '../../components/amcharts/BaseChart';

/**
 *
 */
//TODO: Prepare Vuex store to manage state of ModernCharts
const modernCharts = {
	modules: {},
	state: {
		domain: '.',
		timeout: 2000,
	},
	mutations: {},
	actions: {
		loadData({ state, dispatch }, pointId, startTimestamp, endTimestamp) {
			const url = `${state.domain}/api/point_value/getValuesFromTimePeriod`;
			let timestamp = validateInputData(startTimestamp, endTimestamp);
			return new Promise((resolve, reject) => {
				try {
					Axios.get(`${url}/${pointId}/${timestamp.start}/${timestamp.end}`, {
						timeout: state.timeout,
						useCredentails: true,
						credentials: 'same-origin',
					})
						.then((response) => {
							resolve(response.data);
						})
						.catch((webError) => {
							reject(webError);
						});
				} catch (error) {
					reject(error);
				}
			});
		},
		getPointValueById({ dispatch }, pointId) {
			const url = `${state.domain}/api/point_value/getValue/id`;
			return new Promise((resolve, reject) => {
				try {
					Axios.get(`${url}/${pointId}`, {
						timeout: state.timeout,
						useCredentails: true,
						credentials: 'same-origin',
					})
						.then((resp) => {
							resolve(resp.data);
						})
						.catch((webError) => {
							reject(webError);
						});
				} catch (error) {
					reject(error);
				}
			});
		},
	},
	getters: {},
};
function validateInputData(startTimestamp, endTimestamp) {
	if (startTimestamp === undefined || startTimestamp === null) {
		startTimestamp = new Date().getTime() - 3600000;
		endTimestamp = new Date().getTime();
	} else if (
		startTimestamp !== undefined &&
		startTimestamp !== null &&
		(endTimestamp === undefined || endTimestamp === null)
	) {
		startTimestamp = BaseChart.convertDate(startTimestamp);
		endTimestamp = new Date().getTime();
		if (isNaN(startTimestamp)) {
			console.warn(
				'Parameter start-date is not valid!\nConnecting to API with default values',
			);
			startTimestamp = new Date().getTime() - 3600000;
		}
	} else if (
		startTimestamp !== undefined &&
		startTimestamp !== null &&
		endTimestamp !== undefined &&
		endTimestamp !== null
	) {
		startTimestamp = new Date(startTimestamp).getTime();
		endTimestamp = new Date(endTimestamp).getTime();
		if (isNaN(startTimestamp) || isNaN(endTimestamp)) {
			console.warn(
				'Parameter [start-date | end-date] are not valid!\nConnecting to API with default values',
			);
			startTimestamp = new Date().getTime() - 3600000;
			endTimestamp = new Date().getTime();
		}
	}
	return { start: startTimestamp, end: endTimestamp };
}
export default modernCharts;
