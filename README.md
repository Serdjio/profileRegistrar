# profileRegistrar
Test project for scheduled profile executions and send message to client via WebSocket
To start web socket, navigate to root project folder and execute: 
mvn jetty:run

To start profile registrtation:
For testing purposes main method automaticaly register new Profiles with 10sec delay values after start. Executors schedule run of profile according to profile's data:

Example:
--start time--      --end time--      --charge value--
   "00:01:00",        "00:05:00",         "15 Amper"
   
Possible corner cases:
-Server unable to send request tpo client due to timeOut and have to retry request (not covered)
