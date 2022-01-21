# Watchdog component
This is a new version of classic IsAlive component that is able to detect the System offline state. If the client browser is offline, when there is no response from the server or 
when specific datapoint does not meet the specified criteria, the component will show the offline state. If the Watchdog server is defined this compoenent will send the request 
to this server using the TCP socket connection. 

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
