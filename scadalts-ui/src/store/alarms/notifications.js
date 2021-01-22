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
			alarmLevel: 1,
			duration: 5,
			durationType: 1,
			binaryState: true,
		},
		ehTemplate: {
			id: -1,
			xid: `PLC`,
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
			let requestData = JSON.parse(JSON.stringify(state.pedTemplate));

			requestData.xid = requestData.xid + `_${datapointId}`;
			requestData.alias = requestData.alias + `_${datapointId}`;

			return dispatch('requestPost', {
				url: `/eventDetector/set/binary/state/${datapointId}`,
				data: requestData,
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
				return ehStatus;
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

			let requestData = JSON.parse(JSON.stringify(state.ehTemplate));
			let type = payload.handlerType === 2 ? 'mail' : 'sms';

			requestData.xid = 'EH_' + requestData.xid + `_${type}_${dpId}_${edId}_${mlId}`;
			requestData.alias = requestData.alias + `_${type}_${dpId}_${edId}_${mlId}`;

			let recipientList = [
				{
					recipientType: 1,
					referenceId: mlId,
					referenceAddress: null,
				},
			];

			requestData.activeRecipients = recipientList;
			let eventHandler;

			try {
				eventHandler = await dispatch('requestPost', {
					url: `/eventHandler/set/1/${dpId}/${edId}/${payload.handlerType}`,
					data: requestData,
				});
			} catch (error) {
				throw 'POST request failed!';
			}
			return { edId, ehId: eventHandler.id };
		},

		async createDoubleEventHandler({state, dispatch}, payload) {
			console.log("Vuex::createDoubleEventHandler")
			let pedId = await dispatch('createPointEventDetector', payload.datapointId);
			let edId = pedId.id;
			let dpId = payload.datapointId;
			let mlId = payload.mailingListId;

			let requestDataMail = JSON.parse(JSON.stringify(state.ehTemplate));
			let requestDataSms  = JSON.parse(JSON.stringify(state.ehTemplate));

			requestDataMail.xid = 'EH_' + requestDataMail.xid + `_mail_${dpId}_${edId}_${mlId}`;
			requestDataMail.alias = requestDataMail.alias + `_mail_${dpId}_${edId}_${mlId}`;

			requestDataSms.xid = 'EH_' + requestDataSms.xid + `_sms_${dpId}_${edId}_${mlId}`;
			requestDataSms.alias = requestDataSms.alias + `_sms_${dpId}_${edId}_${mlId}`;

			let recipientList = [
				{
					recipientType: 1,
					referenceId: mlId,
					referenceAddress: null,
				},
			];

			requestDataMail.activeRecipients = recipientList;
			requestDataSms.activeRecipients = recipientList;
			let eventHandler;

			try {
				eventHandler = await dispatch('requestPost', {
					url: `/eventHandler/set/1/${dpId}/${edId}/2`,
					data: requestDataMail,
				});
				eventHandler = await dispatch('requestPost', {
					url: `/eventHandler/set/1/${dpId}/${edId}/5`,
					data: requestDataSms,
				});
			} catch (error) {
				throw 'POST request failed!';
			}
			return { edId, ehId: eventHandler.id };
		}
	},

	getters: {},
};
export default storeAlarmsNotifications;
