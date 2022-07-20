# Scada-LTS Web Sockets

Scada-LTS is migrating to external User Interface. No mater what 
will be the technology of the UI. WebSocket design will allow user to
write Real-Time application that will receive updates immediately
when such event takes place in the core application. To make that possible
the WebSocket module has been provided.

There are two types of endpoints:
 - `./ws-scada/topic`   
 New one for receive data from core app. Thin endpoint returns
data from the backend application.
 - `./ws-scada/alarmLevel`  
  Older for handling the AlarmFlag implementation in Point Hierarchy

## Implementation:
When some businesses object handle the WebSocket communication
design the `ScadaWebSockets` interface should be implemented.
This approach will make the project management more intuitive, 
and it will be easier to find what is using the Web Sockets.

User from the User Interface should only subscribe on a 
specific WebSocket URL channel to be notified about changes.
Each message is processed by dedicated WebSocket Java services.

## Types of WebSocket endpoints:

For current moment there are only that WebSocket modules available:
- DataPointValue
- AlarmEvent