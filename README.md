# BSDS-project1

## Available Scripts

After unzipping the project, type this in the terminal

```
cd out/artifacts   :   to enter the folder where all the jar files are present
```

### To test UDP client and server,

To start UDP server, navigate to artifacts folder and type

```
java -jar UDPServer.jar <port>
```

For UDP client, open another terminal window, navigate to artifacts folder and type

```
java -jar UDPClient.jar <host-name> <port>
```

Once the two commands have been typed, you can start sending requests via the client. Requests are
tab separated which means you have to send the request in the following format

```
GET \t+ key
PUT \t+ key \t+ value can be space separated
DELETE \t+ key can also be space seprated      
```

Here `\t+` denotes one or more tab key presses. If you use space instead of tab, then the requests
will throw errors

### To test TCP client and server,

To start TCP server, navigate to artifacts folder and type

```
java -jar UDPServer.jar <port>
```

For TCP client, open another terminal window, navigate to artifacts folder and type

```
java -jar UDPClient.jar <host-name> <port>
```

Once these two commands have been types, you can start sending request in the format shown above.

### To check the screen shots uploaded as part of this assignment

Do this step if you are in artifacts directory

```
cd ../screenshots
```
