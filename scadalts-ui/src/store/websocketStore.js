import SockJS from 'sockjs-client';
import Stomp from 'webstomp-client';

const HEADERS = {
    login: 'admin',
    passcode: 'passcode',
    client_id: '564389',
}

const webSocketModule = {
    state: {
        webSocket: null,
        webSocketConnection: false,
        webSocketUrl: 'http://localhost:8080/ScadaBR/ws-scada/alarmLevel',
        debugMode: false,
    },

    mutations: {
        INIT_WEBSOCKET(state) {
            let socket = new SockJS(state.webSocketUrl);
            let client = Stomp.over(socket);
            if(!state.debugMode) {
                client.debug = () => {};
            }
            client.connect(HEADERS, () => {
                state.webSocketConnection = true;
                console.log('Connected to WebSocket');
            }, () => {
                state.webSocketConnection = false;
                console.log('Failed to establish WebSocket connection');
            });
            state.webSocket = client;
        },
        INIT_WEBSOCKET_URL(state) {
            let locale = window.location.pathname.split('/')[1];
			if(!!locale) {
				locale += '/';
			}
    		let protocol = window.location.protocol;
    		let host = window.location.host.split(":");

			state.webSocketUrl = `${protocol}//${host[0]}:${host[1]}/${locale}ws-scada/alarmLevel`;
        }
    },

    actions: {
    },

    getters: {
    },
};

export default webSocketModule;