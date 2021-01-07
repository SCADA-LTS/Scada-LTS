/**
 * PLC Sms and Email Notification Vuex Store
 *
 * Vue bussines logic for SMS and Email notification page.
 *
 * @author Radoslaw Jajko <rjajko@softq.pl>
 * @since 2.5.0
 *
 */
const storeAlarmsNotifications = {
	state: {
		pedTemplate: {
			xid: 'PED-PLC',
			alias: 'PlcEventDetector',
			alarmLevel: 0,
			duration: 0,
			durationType: 1,
			binartState: true,
		},
		ehTemplate: {
			id: -1,
			xid: `PLC_0001`,
			alias: `PLC-EventHandler`,
			disabled: false,
			activeRecipients: null,
			sendEscalation: false,
			escalationDelayType: 1,
			escalationDelay: 0,
			escalationRecipients: null,
			sendInactive: false,
			inactiveOverride: false,
			inactiveRecipients: null,
		},
	},

	mutations: {},

	actions: {
		getPlcDataPoints({ dispatch }, datasourceId) {
			return dispatch('requestGet', `/datapoint/${datasourceId}/getAllPlc`);
		},

		//Event Handlers
		getPlcEventHandlers({ dispatch }) {
			return dispatch('requestGet', '/eventHandler/getAllPlc');
		},

		getPlcEventHandlerById({ dispatch }, eventHandlerId) {
			return dispatch('requestGet', `/eventHandler/get/id/${eventHandlerId}`);
		},

		deleteEventHandler({ dispatch }, eventHandlerId) {
			return dispatch('requestDelete', `/eventHandler/delete/id/${eventHandlerId}`);
		},

		//Mailing Lists
		getAllMailingLists({ dispatch }) {
			return dispatch('requestGet', '/mailingList/getAll');
		},

		//Event detectors
		createPointEventDetector({ state, dispatch }, datapointId) {
			state.pedTemplate.xid = state.pedTemplate.xid + `_${datapointId}`;
			state.pedTemplate.alias = state.pedTemplate.alias + `_${datapointId}`;

			return dispatch('requestPost', {
				url: `/eventDetector/set/binary/state/${datapointId}`,
				data: state.pedTemplate,
			});
		},

		deleteEventDetector({ dispatch }, payload) {
			return dispatch(
				'requestDelete',
				`/eventDetector/delete/${payload.dpId}/${payload.edId}`,
			);
		},

		deleteMailingListFromEventHandler(context, payload) {
			payload.eventHandler.activeRecipients = payload.eventHandler.activeRecipients.filter(
				(e) => {
					return e.referenceId !== payload.activeMailingList;
				},
			);
			return payload.eventHandler;
		},

		addMailingListToEventHandler(context, payload) {
			if (!payload.eventHandler.activeRecipients) {
				payload.eventHandler.activeRecipients = [];
			}
			let mailingList = {
				recipientType: 1,
				referenceId: payload.activeMailingList,
				referenceAddress: null,
			};
			payload.eventHandler.activeRecipients.push(mailingList);
			console.log(payload.eventHandler);
			return payload.eventHandler;
		},

		async updateEventHandler({ dispatch }, payload) {
			let eventHandler = await dispatch('getPlcEventHandlerById', payload.ehId);

			if (payload.method === 'add') {
				eventHandler = await dispatch('addMailingListToEventHandler', {
					eventHandler: eventHandler,
					activeMailingList: payload.activeMailingList,
				});
			} else if (payload.method === 'delete') {
				eventHandler = await dispatch('deleteMailingListFromEventHandler', {
					eventHandler: eventHandler,
					activeMailingList: payload.activeMailingList,
				});
			}

			if (eventHandler.activeRecipients.length === 0) {
				let ehStatus = await dispatch('deleteEventHandler', eventHandler.id);
				let edStatus = await dispatch('deleteEventDetector', {
					dpId: payload.typeRef1,
					edId: payload.typeRef2,
				});
				return ehStatus && edStatus;
			} else {
				return dispatch('requestPut', {
					url: `/eventHandler/update/1/${payload.typeRef1}/${payload.typeRef2}`,
					data: eventHandler,
				});
			}
		},

		async createEventHandler({ state, dispatch }, payload) {
			let pedId = await dispatch('createPointEventDetector', payload.datapointId);
			let edId = pedId.id;
			let dpId = payload.datapointId;
			let mlId = payload.mailingListId;

			state.ehTemplate.xid = 'EH_' + state.ehTemplate.xid + `_${dpId}_${edId}_${mlId}`;
			state.ehTemplate.alias = state.ehTemplate.alias + `_${dpId}_${edId}_${mlId}`;

			let recipientList = [
				{
					recipientType: 1,
					referenceId: mlId,
					referenceAddress: null,
				},
			];

			state.ehTemplate.activeRecipients = recipientList;

			return dispatch('requestPost', {
				url: `/eventHandler/set/1/${dpId}/${edId}/${payload.handlerType}`,
				data: state.ehTemplate,
			});
		},
	},

	getters: {},
};
export default storeAlarmsNotifications;
