import i18n from '@/i18n';
import { eventDetectorTemplates } from './templates';

const storeEventDetectors = {
	state: {

        eventDetectorList: [
            { id: 1, label: 'High Limit'},
            { id: 2, label: 'Low Limit'},
            { id: 3, label: 'Binary State Detector'},
            { id: 4, label: 'Multistate State Detector'},
            { id: 5, label: 'Point Change Detector'},
            { id: 6, label: 'State Change Detector'},
            { id: 7, label: 'No Change Detector'},
            { id: 8, label: 'No Update Detector'},
            { id: 9, label: 'Aplhanumeric State Detector'},
            { id: 10, label: 'Positive CUSUM'},
            { id: 11, label: 'Negative CUSUM'},
        ],

        eventDetectorTemplates: eventDetectorTemplates,
		
	},

	mutations: {},

	actions: {
        createEventDetector({dispatch}, payload) {
            return dispatch('requestPost', {
                url: `/eventDetector/set/${payload.datapointId}`,
                data: payload.requestData
            });
        },

        updateEventDetector({dispatch}, payload) {
            return dispatch('requestPut', {
                url: `/eventDetector/update/${payload.datapointId}/${payload.pointEventDetectorId}`,
                data: payload.requestData
            });
        },

        deleteEventDetector({dispatch}, payload) {
			return dispatch(
				'requestDelete',
				`/eventDetector/delete/${payload.datapointId}/${payload.pointEventDetectorId}`,
			);
        },
		
	},

	getters: {},
};

export default storeEventDetectors;
