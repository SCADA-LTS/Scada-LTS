const autoManualMock = {
	state: {
		
	},

	mutations: {},

	actions: {
        requestGet({dispatch}, url) {
            let pointXid = url.split('/')[3];
            console.log('requestGet', pointXid);
            if(pointXid === 'DP_PLC_M') {
                return [{
                    value: "true",
                    xid: "DP_PLC_M",                    
                }]
            } else if (pointXid === "DP_pump_01") {
                return [{
                    value: "false",
                    xid: "DP_pump_01",                    
                }]
            }
        }
	},

	getters: {},
};

export default autoManualMock;
