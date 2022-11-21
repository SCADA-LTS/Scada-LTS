import i18n from '@/i18n';
import { eventDetectorTemplate } from './templates';

const storeEventDetectors = {
	state: {
		eventDetectorList: [
			{ id: 1, label: i18n.t('eventDetector.type.highLimit') },
			{ id: 2, label: i18n.t('eventDetector.type.lowLimit') },
			{ id: 3, label: i18n.t('eventDetector.type.binary') },
			{ id: 4, label: i18n.t('eventDetector.type.multistate') },
			{ id: 5, label: i18n.t('eventDetector.type.point') },
			{ id: 6, label: i18n.t('eventDetector.type.change') },
			{ id: 7, label: i18n.t('eventDetector.type.noChange') },
			{ id: 8, label: i18n.t('eventDetector.type.noUpdate') },
			{ id: 9, label: i18n.t('eventDetector.type.alphanumeric') },
			{ id: 10, label: i18n.t('eventDetector.type.cusum.positive') },
			{ id: 11, label: i18n.t('eventDetector.type.cusum.negative') },
		],

		eventDetectorTemplate: eventDetectorTemplate,

	},

	mutations: {},

	actions: {
		createEventDetector({ dispatch }, payload) {
			return dispatch('requestPost', {
				url: `/eventDetector/set/${payload.datapointId}`,
				data: payload.requestData,
			});
		},

		updateEventDetector({ dispatch }, payload) {
			return dispatch('requestPut', {
				url: `/eventDetector/update/${payload.datapointId}/${payload.pointEventDetectorId}`,
				data: payload.requestData,
			});
		},

		deleteEventDetector({ dispatch }, payload) {
			return dispatch(
				'requestDelete',
				`/eventDetector/delete/${payload.datapointId}/${payload.pointEventDetectorId}`,
			);
		},
	},

	getters: {},
};

export default storeEventDetectors;
