# Scada-LTS UI Utils
This is a set of additional 'Mixins' for the Scada-LTS UI.
It provides an additional methods for the UI components.
To start using them just declare a mixin and import the spefific
one from the 'utils' folder. Example usage is presented below
in a gerenal concept.

```js
import wsMixin from '@/utils/web-socket-utils';

...

export default {
    ...
    
    mixins: [wsMixin],

    ...

}
```
--- 

## WebSocket Utils
This mixin provides a set of methods for the WebSocket connection.

### Parameters
| Parameter name | Type | Required | Description |
| --- | --- | --- | --- |
| `wsCallback` | function() | true | Place of definition for any of subscribtions. Callback method is related with specific web socket and base on that it can modify something in a code. |
| `wsDebug` | boolean | false | Debug the method initialization flow. |
| `wsLazyInit` | boolean | false | If it is set to `true` the classic initialization onMounted is not invoked. You have to manually connect web socket to the server. |

### Methods

| Method name | Description |
| --- | --- | 
| `wsBeforeConnect()` | Method invoked before initializing the web socket |
| `wsConnect()` | Initialize the WebSocket connection. Using `wsCallback()` variable for additional options. |
| `wsBeforeDisconnect()` | Method invoked before closing the web socket connection |
| `wsDisconnectWebSocket()` | Disconnect the WebSocket |
| `wsReconnectWebSocket()` | Reconnect the WebSocket |
| `wsSubscribeChannel(url, callback)` | Subscribe to a specific web socket channel |
| `wsSubscribeTopic(url, callback)` | Subscribe to a specific Scada-LTS web socket topic that often returns a specific data |

---
## Connection Status Utils
Handle the internet connection status and monitor the application state. Configure additional method that will be invoked when the connection status changes.

### Parameters
| Parameter name | Type | Required | Description |
| --- | --- | --- | --- |
| `appOnline` | boolean | false | Internet status. If it is online the value is `true` |
| `onAppOnline` | function() | false | Callback function that is invoked when the internet connection returns |
| `onAppOffline` | function() | false | Callback function that is invoked when the internet connection is broke. It could be the WiFi signal lost or other event. |

