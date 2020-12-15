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
    state: { },

    mutations: { },

    actions: {
        getPlcDataPoints({dispatch}, datasourceId) {
            return dispatch("requestGet", `/datapoint/${datasourceId}/getAllPlc`)
        },

        //Event Handlers
        getPlcEventHandlers({dispatch}) {
            return dispatch("requestGet", "/eventHandler/getAllPlc");
        },

        getPlcEventHandlerById({dispatch}, eventHandlerId) {
            return dispatch("requestGet", `/eventHandler/get/id/${eventHandlerId}`);
        },

        deleteEventHandler({dispatch}, eventHandlerId) {
            return dispatch("requestDelete", `/eventHandler/delete/id/${eventHandlerId}`);
        },

        //Mailing Lists
        getAllMailingLists({dispatch}) {
            return dispatch("requestGet", "/mailingList/getAll");
        },

        //Event detectors
        createPointEventDetector({dispatch}, datapointId) {
            let body = {
                xid: `PEDPLC_${datapointId}`,
                alias: `PlcDetector_${datapointId}`,
                alarmLevel: 2,
                duration: 10,
                durationType: 2,
                binartState: true
            };
            //TODO: Prepare configuration box for that field. 

            return dispatch("requestPost", { 
                url: `/eventDetector/set/binary/state/${datapointId}`,
                data: body,
            })
        },

        deleteEventDetector({dispatch}, payload) {
            return dispatch("requestDelete", `/eventDetector/delete/${payload.dpId}/${payload.edId}`);
        },

        deleteMailingListFromEventHandler(context, payload) {
            payload.eventHandler.activeRecipients = payload.eventHandler.activeRecipients.filter(e => {
                return e.referenceId !== payload.activeMailingList
            });
            return payload.eventHandler;
        },

        addMailingListToEventHandler(context, payload) {
            if(!payload.eventHandler.activeRecipients) {
                payload.eventHandler.activeRecipients = [];
            }
            let mailingList = {
                recipientType: 1,
                referenceId: payload.activeMailingList,
                referenceAddress: null
            }
            payload.eventHandler.activeRecipients.push(mailingList);
            console.log(payload.eventHandler);
            return payload.eventHandler;
        },

        async updateEventHandler({dispatch}, payload) {
            let eventHandler = await dispatch("getPlcEventHandlerById", payload.ehId);

            if(payload.method === "add") {
                eventHandler = await dispatch("addMailingListToEventHandler", {
                    eventHandler: eventHandler, activeMailingList: payload.activeMailingList
                });
            } else if (payload.method === "delete") {
                eventHandler = await dispatch("deleteMailingListFromEventHandler", {
                    eventHandler: eventHandler, activeMailingList: payload.activeMailingList
                });
            }

            if(eventHandler.activeRecipients.length === 0 ) {
                let ehStatus = await dispatch("deleteEventHandler", eventHandler.id);
                let edStatus = await dispatch("deleteEventDetector", {dpId: payload.typeRef1,edId:payload.typeRef2});
                return ehStatus && edStatus;
            } else {
                return dispatch("requestPut", {
                    url: `/eventHandler/update/1/${payload.typeRef1}/${payload.typeRef2}`, 
                    data: eventHandler
                });
            }  
        },

        async createEmailEventHandler({dispatch}, payload) {

            let pedId = await dispatch("createPointEventDetector", payload.datapointId);
            let edId = pedId.id;

            let dpId = payload.datapointId;
            let mlId = payload.mailingListId;

            let body = {
                id: -1,
                xid: `EEH_${dpId}_${edId}_${mlId}`,
                alias: `EEH_${dpId}_${edId}_${mlId}`,
                disabled: false,
                activeRecipients: [{
                    recipientType: 1,
                    referenceId: mlId,
                    referenceAddress: null
                }],
                sendEscalation: false,
                escalationDelayType: 1,
                escalationDelay: 0,
                escalationRecipients: null,
                sendInactive: false,
                inactiveOverride: false,
                inactiveRecipients: null
            }

            return dispatch("requestPost", {
                url: `/eventHandler/set/1/${dpId}/${edId}/2`,
                data: body,
            });
        }
    },

    getters: {
        
    }
}
export default storeAlarmsNotifications