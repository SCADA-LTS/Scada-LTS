import SockJS from 'sockjs-client';
import Stomp from 'webstomp-client';
import {getAppLocation} from '../utils/common';

const HEADERS = {
    login: 'admin',
    passcode: 'passcode',
    client_id: '564389',
}

const webSocketModule = {
    state: {
        webSocket: null,
        webSocketConnection: false,
        webSocketUrl: 'ws-scada',
        debugMode: false,
    },

    mutations: {
        INIT_WEBSOCKET(state) {
            let base = getAppLocation();
            if(!state.webSocketUrl.includes(base)) {
                state.webSocketUrl = base + state.webSocketUrl;
            }
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
			state.webSocketUrl = 'ws-scada';
        }
    },

    actions: {
    },

    getters: {
    },
};

export default webSocketModule;