import i18n from '@/i18n';
import {
	chartRenderersTemplates,
	eventRenderersTemplates,
	textRenderesTemplates,
} from './templates';

const storeDataPoint = {
	state: {
		loggingTypeList: [
			{
				id: 1,
				type: 'ON_CHANGE',
				label: i18n.t('pointEdit.logging.type.change'),
			},
			{
				id: 2,
				type: 'ALL',
				label: i18n.t('pointEdit.logging.type.all'),
			},
			{
				id: 3,
				type: 'NONE',
				label: i18n.t('pointEdit.logging.type.never'),
			},
			{
				id: 4,
				type: 'INTERVAL',
				label: i18n.t('pointEdit.logging.type.interval'),
			},
			{
				id: 5,
				type: 'ON_TS_CHANGE',
				label: i18n.t('pointEdit.logging.type.tsChange'),
			},
		],

		textRenderesList: [
			{ id: 0, label: i18n.t('textRenderer.analog') },
			{ id: 1, label: i18n.t('textRenderer.binary') },
			{ id: 2, label: i18n.t('textRenderer.multistate') },
			{ id: 3, label: i18n.t('textRenderer.plain') },
			{ id: 4, label: i18n.t('textRenderer.range') },
			{ id: 5, label: i18n.t('textRenderer.time') },
		],

		chartRenderersList: [
			{
				id: -1,
				label: i18n.t('datapointDetails.pointProperties.chartRenderer.type.none'),
			},
			{
				id: 0,
				label: i18n.t('datapointDetails.pointProperties.chartRenderer.type.table'),
			},
			{
				id: 1,
				label: i18n.t('datapointDetails.pointProperties.chartRenderer.type.image'),
			},
			{
				id: 2,
				label: i18n.t('datapointDetails.pointProperties.chartRenderer.type.stats'),
			},
		],

		textRenderesTemplates: textRenderesTemplates,

		chartRenderersTemplates: chartRenderersTemplates,

		eventRenderersTemplates: eventRenderersTemplates,

		datapointSimpleList: undefined,

		valueTypeList: [
			{ id: 1, label: i18n.t('pointEdit.logging.value.instant') },
			{ id: 2, label: i18n.t('pointEdit.logging.value.maximum') },
			{ id: 3, label: i18n.t('pointEdit.logging.value.minimum') },
			{ id: 4, label: i18n.t('pointEdit.logging.value.average') },
		],

		purgeStrategyList: [
			{
				id: 1,
				type: 'PERIOD',
				label: i18n.t('pointEdit.logging.purge.type.period'),
			},
			{
				id: 2,
				type: 'LIMIT',
				label: i18n.t('pointEdit.logging.purge.type.limit'),
			},
		],
	},

	mutations: {},

	actions: {
		async fetchDataPointSimpleList({ state, dispatch }) {
			state.datapointSimpleList = await dispatch('requestGet', `/datapoint/getAll`);
		},

		getDataPointSimpleFilteredList({ state }, value) {
			let data = Object.assign([], state.datapointSimpleList);
			data = data.filter((e) => {
				return (e.name || '').toLowerCase().indexOf((value || '').toLowerCase()) > -1;
			});
			return data;
		},

		getAllDatapoints({ dispatch }) {
			return dispatch('requestGet', `/datapoint/getAll`);
		},

		getAllDataPointsTable({ dispatch }) {
			return dispatch('requestGet', `/datapoints`);
		},

		getUniqueDataPointXid({dispatch}) {
			return dispatch("requestGet", "/datapoint/generateUniqueXid");
		},

		getDataPointDetails({ dispatch }, datapointId) {
			return dispatch('requestGet', `/datapoint?id=${datapointId}`);
		},

		getDataPointValue({ dispatch }, datapointId) {
			return dispatch('requestGet', `/point_value/getValue/id/${datapointId}`);
		},

		getDataPointValueByXid({ dispatch }, datapointXid) {
			return dispatch('requestGet', `/point_value/getValue/${datapointXid}`);
		},

		setDataPointValue({ dispatch }, payload) {
			return dispatch('requestPost', {
				url: `/point_value/setValue/${payload.xid}/${payload.type}`,
				data: `${payload.value}`,
			});
		},

		setCmpValue({dispatch}, payload) {
			return dispatch('requestPost', {
				url: `/cmp/set/${payload.id}/${payload.name}`,
				data: payload.requestData
			});
		},

		getDataPointValueFromTimeperiod({ dispatch }, payload) {
			return dispatch(
				'requestGet',
				`/point_value/getValuesFromTimePeriod/${payload.datapointId}/${payload.startTs}/${payload.endTs}`,
			);
		},

		saveDataPointDetails({ dispatch }, payload) {
			return dispatch('requestPut', {
				url: `/point_properties/updateProperties?id=${payload.id}`,
				data: payload,
			});
		},

		clearDataPointCache({ dispatch }, datapointId) {
			return dispatch('requestPatch', {
				url: `/point_properties/${datapointId}/clearcache`,
				data: null,
			});
		},

		purgeNowPeriod({dispatch}, {datapointId, type, period}) {
			return dispatch('requestPatch', {
				url: `/point_properties/${datapointId}/purgeNowPeriod?type=${type}&period=${period}`,
				data: null,
			});
		},

		purgeNowLimit({dispatch}, {datapointId, limit}) {
			return dispatch('requestPatch', {
				url: `/point_properties/${datapointId}/purgeNowLimit?limit=${limit}`,
				data: null,
			});
		},

		purgeNowAll({dispatch}, datapointId) {
			return dispatch('requestPatch', {
				url: `/point_properties/${datapointId}/purgeNowAll`,
				data: null,
			});
		},

		purgeDataPointValues({ dispatch }, payload) {
			let request = '?';
			if (payload.allData) {
				request = request + 'all=true';
			}
			if (!!payload.type && !!payload.period) {
				if (payload.allData) {
					request = request + '&';
				}
				request = request + `type=${payload.type}&period=${payload.period}`;
			}

			return dispatch('requestPatch', {
				url: `/point_properties/${payload.datapointId}/purge${request}`,
				data: null,
			});
		},

		toggleDataPoint({ dispatch }, datapointId) {
			return dispatch('requestPatch', {
				url: `/point_properties/${datapointId}/toggle`,
				data: null,
			});
		},

		addUserComment({ dispatch }, payload) {
			return dispatch('requestPost', {
				url: `/userComment/${payload.typeId}/${payload.refId}`,
				data: { commentText: payload.comment.comment },
			});
		},

		delUserComment({ dispatch }, payload) {
			return dispatch(
				'requestDelete',
				`/userComment/${payload.typeId}/${payload.refId}/${payload.userId}/${payload.ts}`,
			);
		},

		fetchDataPointsFromDataSource({ dispatch }, dataSourceId) {
			return dispatch('requestGet', `/datapoints/datasource?id=${dataSourceId}`);
		},

		getDatasourceByXid({ dispatch }, xid) {
			return dispatch('requestGet', `/datasource?xid=${xid}`);
		},
	},

	getters: {},
};

export default storeDataPoint;
