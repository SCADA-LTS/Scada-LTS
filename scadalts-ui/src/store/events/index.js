const storeEvents = {
	
    state: { },

	mutations: {},

	actions: {
        fetchDataPointEvents({dispatch}, datapointId) {
            return new Promise(resolve => {

                let data = [
                    {
                        id: 1, typeId: 4, typeRef1: 1, typeRef2: 0, 
                        activeTs: 1612958627192, rtnApplicable:'N', rtnTs: 0, rtnCause: 0,
                        alarmLevel: 1, message: 'ExampleMessage', 
                        ackTs: 0, ackUserId: null, alternateAckSource: null
                    },
                    {
                        id: 1, typeId: 4, typeRef1: 1, typeRef2: 0, 
                        activeTs: 1613136130991, rtnApplicable:'N', rtnTs: 0, rtnCause: 0,
                        alarmLevel: 3, message: 'Waring!', 
                        ackTs: 0, ackUserId: null, alternateAckSource: null
                    },
                ]
                resolve(data);
            })
        }
     },

	getters: {},
};

export default storeEvents;
