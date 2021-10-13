const storeEvents = {
	state: {},

	mutations: {},

	actions: {
		searchEvents({ dispatch }, payload) {
			return dispatch('requestPost', {
				url:`/events/search`,
				data: {
					alarmLevel: payload.alarmLevel,
					eventSourceType: payload.eventSourceType,
					status: payload.status,
					keywords: payload.keywords,
					datapoint: null,
					limit: 10,
					offset: 10 * (payload.page-1),
					sortBy: payload.sortBy,
					sortDesc: payload.sortDesc
				}
			});
		},

		fetchDataPointEvents({ dispatch }, payload) {
			let url = `/events/datapoint/${payload.datapointId}`;

			if (!!payload.limit) {
				url += `?limit=${payload.limit}`;
			}

			if (!!payload.offset && !!payload.limit) {
				url += '&';
			}

			if (!!payload.offset) {
				if (!payload.limit) {
					url += `?`;
				}
				url += `offset=${payload.offset}`;
			}

			return dispatch('requestGet', url);
		},

		ackEvent({ dispatch }, eventId) {
			return dispatch('requestPut', {
				url: `/events/ack/${eventId}`,
				data: null,
			});
		},
	},

	getters: {},
};

export default storeEvents;
