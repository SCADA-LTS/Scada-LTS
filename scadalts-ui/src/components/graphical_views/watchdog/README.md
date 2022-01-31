# Watchdog component
This is a new version of classic IsAlive component that is able to detect the System offline state. If the client browser is offline, when there is no response from the server or 
when specific datapoint does not meet the specified criteria, the component will show the offline state. If the Watchdog server is defined this compoenent will send the request 
to this server using the TCP socket connection. 

Watchdog server will receive the message with format:
```
<requestHostAddr>|<message>
```
where the `requestHostAddr` is the address of the client browser that was sending the request. 
`message` by default will be "ping" unless user not defined the `dp-message` argument in his
UI component. This is required because the Scada-LTS application runs on the specific server machine that might be different from the client browser. So to identify the browser that 
is sending the request we are providing that address and additional message to the socket
connection. 


## Configuration

Example datapoint condition configuration:
```javascript
[{
    xid: 'DP01',
    value: 1,
    check: 'equal'
}]
```

User can provide the data point configuration to check if the system is online based on the 
datapoint condition value. 

| Check type | Description |
| ---------- | ----------- |
| `equal` | Check if the value is equal to the specified value |
| `less` | Check if the value is less than the specified value |
| `greater` | Check if the value is greater than the specified value |
| `less_equal` | Check if the value is less than or equal to the specified value |
| `greater_equal` | Check if the value is greater than or equal to the specified value |

## API

| Parameter name | type |  Default | Description |
| ---------- | ----------- | ----------- | ----------- |
| `name` | string (optional) | 'IsAlive2' | Name that will be displayed as the menu title |
| `interval` | number (optional) | 10000 | Interval time between checking the application health |
| `wd-host` | string (optional) | null | IP address to the WatchDog server |
| `wd-port` | number (optional) | null | Port number to WatchDog server |
| `wd-message` | string (optional) | 'ping' | Message that will be send thought the TCP socket connection to the WatchDog server |
| `dp-validation` | array (optional) | null | Array of objects that contains the DP check validation |
| `dp-break` | boolean (optional) | false | If that argument is set the datapoint check will be break if the datapoint is not valid |
| `dp-failure` | boolean | false | If that argument exists that means the DP check error will be treated as failure |



## Example usage

Watchdog component can be used in the following way:
```html
<div id="app-isalive2" 
    name="test2" 
    interval="3000" 
    wd-host="127.0.0.1" 
    wd-port="1234" 
    wd-message="Monitoring"
    dp-validation='[{"xid":"DP_EN1", "value":1, "check":"equal"}]' 
    dp-failure
    dp-break
></div>
```
To embed this component in the page we need to add the new HTML component to our view and then create a following div with "app-isalive2" id. Rest of the parameters are optional.

To create your own watchdog server you can use the following command:
```bash
nc -4 -lk 127.0.0.1 1234
```
When the component will check every step you should see the message received from the Scada client.
