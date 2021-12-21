const webSocketModule = {
    state: {
        webSocket: null,
        webSocketConnection: false,
        webSocketUrl: 'http://localhost:8080/ScadaBR/ws-scada/alarmLevel',
        debugMode: false,
    },

    mutations: {
        INIT_WEBSOCKET(state) {
            console.log('INIT_WEBSOCKET');            
        },
        INIT_WEBSOCKET_URL(state) {
            console.log('INIT_WEBSOCKET_URL');
        }
    },

    actions: {
    },

    getters: {
    },
};

export default webSocketModule;