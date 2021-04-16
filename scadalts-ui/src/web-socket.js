/**
 * @author Radoslaw Jajko <rjajko@softq.pl>
 * @version 1.0.0
 */
import SockJS from 'sockjs-client';
import Stomp from 'webstomp-client';

// WebSocket Headers defined by Jerzy Piejko
// for Scada-LTS Point Hierarchy page.
const HEADERS = {
    login: 'admin',
	passcode: 'passcode',
	client_id: '564389'
}

/**
 * Initialize WebSocket for Vue component
 * 
 * Using Web Socket mechanism application is able to receive
 * messages directly from the WebServer without polling.
 * That reduce the amount of data transfered and increase
 * the performance of the Scada-LTS.
 * 
 * @param {String} url - Web Socket Endpoint URL
 * @param {Function} connectCallback - Method to be evaluated when the frame will be recived.
 * @param {Boolean} debug - (optional) Enable Debug mode to see incoming frames
 * @param {Object} headers - (optional) For further development when WS will have user authentication
 */
export function initWebSocket(url, connectCallback, debug = false, headers = HEADERS) {
    let socket = new SockJS(url);
    let client = Stomp.over(socket);
    if(!debug) {
        client.debug = () => {};
    }
    client.connect(headers, connectCallback)
    return client;
}