import { initWebSocket } from "../web-socket";

/**
 * Web Socket Utils Mixin
 * @mixin
 * 
 * Additionnal methods to manage Web Socket in Scada-LTS
 * Establish a connection, send data, receive data or close it.
 * That mixin can be used in many components and can be easily
 * changed to suit your needs.
 * 
 * @version 1.0.0
 * @author Radoslaw Jajko <rjajko@softq.pl>
 */
export const webSocketUtilsMixin = {

    data() {
        return {
            ws: null,
            wsDebug: false,
            wsCallback: () => {throw new Error("wsCallback must be defined by user!")},
        };
    },

    methods: {

        /**
         * Hook before the Web Socket connection is setablished
         * 
         * Override this method to do something before the Web Socket
         * connection is established.
         */
        wsBeforeConnect() {
            if(this.wsDebug) console.debug("WebSocketUtils::wsBeforeConnect()")
        },

        /**
         * Connect to a Web Socket 
         * 
         * Connect to Scada-LTS Web Socket server.
         * Subscribe on specific topics to receive data from server.
         * To do that define a 'wsCallback' data() variable as function
         * in your component. You can use 'wsSubscribeChannel' method
         * to establish a connection to a specific channel.
         * 
         * If wsCallback is not defined this method will throw an error.
         */
        wsConnect() {
            if(this.wsDebug) console.debug("WebSocketUtils::wsConnect()")
            this.wsBeforeConnect();
            this.ws = initWebSocket(
                this.$store.state.webSocketUrl,
                this.wsCallback
            );
        },

        /**
         * Hook before the Web Socket connection is closed
         * 
         * Override this method to do something before the Web Socket
         * connection is closed.
         */
        wsBeforeDisconnect() {
            if(this.wsDebug) console.debug("WebSocketUtils::wsBeforeDisconnect()")
        },

        /**
         * Close active Web Socket connection if it exists
         */
        wsDisconnectWebSocket() {
            if(this.wsDebug) console.debug("WebSocketUtils::wsDisconnectWebSocket()")
            if(!!this.ws) {
                this.wsBeforeDisconnect();
                this.ws.disconnect();
            }
        },

        /**
         * Try to reconnect to the Web Socket server
         */
        wsReconnectWebSocket() {
            if(this.wsDebug) console.debug("WebSocketUtils::wsReconnectWebSocket()")
            this.disconnectWebSocket();
            this.connectWebSocket();
        },

        /**
         * Subscribe to a specific WebSocket channel
         * 
         * Most important function that allows to establish
         * a connection between a User Interface and a
         * backend server endpoint. Channel that you want 
         * to subscribe to must be defined in the Scada-LTS application.
         * It is often use to inform an backend server about
         * a request to establish a spefiic connection.
         * 
         * Provide a URL to the channel you want to subscribe to.
         * And if there is a data that will be received from
         * that channel you can provide a callback function 
         * that will be able to process the data.
         * 
         * @param {String} url - valid channel address to subscribe
         * @param {Function} callback - function to process the response data.
         */
        wsSubscribeChannel(url, callback = () => {}) {
            if(this.wsDebug) console.debug("WebSocketUtils::wsSubscribeChannel()")
            if(!!this.ws) {
                this.ws.subscribe(`/ws-scada/${url}`, callback);
            } else {
                throw new Error("Web Socket is not connected!");
            }
        },

        /**
         * Subscribe to a specific WebSocket Topic for data.
         * 
         * Function that allows to establish a connection 
         * between a User Interface and a backend server endpoint. 
         * Topic that you want to subscribe to must be defined in 
         * the Scada-LTS application. 
         * 
         * Instead of a channel - topic very often is used to send 
         * data to a subscriber. So if you want to receive data
         * use 'wsSubscribeTopic' method and provide a callback.
         * 
         * Provide a URL to the channel you want to subscribe to.
         * And if there is a data that will be received from
         * that channel you can provide a callback function 
         * that will be able to process the data.
         * 
         * @param {String} url - valid channel address to subscribe
         * @param {Function} callback - function to process the response data.
         */
         wsSubscribeTopic(url, callback) {
            if(this.wsDebug) console.debug("WebSocketUtils::wsSubscribeTopic()")
            if(!callback) {
                throw new Error("Callback must be defined!");
            }
            
            if(!!this.ws) {
                this.ws.subscribe(`/topic/${url}`, callback);
            } else {
                throw new Error("Web Socket is not connected!");
            }
        },

    },

    /**
     * Initialize Web Socket on component creation
     * 
     * If the option 'wsLazyInit' is set to true 
     * do not initialize Web Socket connection 
     * during component creation.
     * 
     * Just add 'wsLazyInit: true' to your components
     * data() property to enable that option.
     * Usefull if you want to initialize Web Socket
     * connection later. 
     */
    mounted() {
        if(this.wsDebug) console.debug("WebSocketUtils::mounted()")
        if(!this.wsLazyInit) {
            this.wsConnect();
        }
    },

    beforeDestroy() {
        if(this.wsDebug) console.debug("WebSocketUtils::beforeDestroy()")
        this.wsDisconnectWebSocket();
    },
    
}

export default webSocketUtilsMixin;