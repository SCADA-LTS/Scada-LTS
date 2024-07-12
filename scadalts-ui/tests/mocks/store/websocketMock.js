const webSocketModule = {
    state: {
        webSocket: null,
        webSocketConnection: false,
        webSocketUrl: 'ws-scada',
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