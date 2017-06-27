# TCP Relay

This project contains a TCP Relay and an EchoServer implementation that uses the relay. It is written
in Kotlin, and uses Java NIO (and a small number of 3rd party libraries: Guava, commons-lang, 
argparse4j, slf4j/logback)

## Overview and Glossary

### relay-server
The relay-server can act as a TCP relay between systems that can't normally communicate (e.g. NAT firewall).

### relay-requester
A service that wants to be communicated with through the relay-server. In order to set up communication
through the relay-server, the client must follow some specific steps (outlined below)

The relay-requester requests a 'relay' be created within the relay-server. The relay-server responds
with the host:port that clients can use to communicate to that relay.

When a client connects to the relay, a second type of message is sent to the relay-requester -- a clientID,
and the relay-requester must respond by opening another TCP socket to the relay-server and replying with the
clientID it was just passed.

#### Example: echo-server
Included in this project is an example EchoServer. It registers itself with the relay-server,
and responds to requests for client relay sockets.

### relay-client
Any TCP client can communicate through the relay-server -- it just has to know the address of the host 
and port the relay-server has setup for the specific relay-requester that the relay-client wishes to 
communicate with.

## How to make use of the relay-server

To create your own service that uses the relay-server, do the following:
* Open a TCP socket to the ```host:port``` where the relay-server is listening.
* To request a new relay be created for you, write the message ```NewRelay``` to the socket.
* The relay-server will respond with the ```host:port``` of the new relay created for your service.
* Once a client connects to the relay, the relay-server will send your service another message. This is
  the relay-server's clientID for that client, and your service must open a new socket to relay-server
  and write that exact clientID to the socket.

Example workflow:
* relay-server is running on ```relayhost:8080```
    * the relay-server has been configured to use ports 8081-9081 for the relays it creates
* your service is running on your dev box. 
    * Connect to ```relayhost:8080``` and write 'NewRelay' to the socket
* relay-server responds with ```relayhost:8081``` , the address of the relay created for your service
* a TCP client application for your service is running on your friend's box. the client connects to
  ```relayhost:8081```
* the relay-server sends your service a message with the clientId. 
   * in the current implementation, the ```clientId``` will look like ```<relayId>;<clientInfo>``` where 
   ```relayId``` is the address of the relay:```relayhost:8081```, and ```clientInfo``` is the remote 
    address of the client.
   * NOTE: your service should open a _new_ socket to ```relayhost:8080``` (IMPORTANT: This is the port # 
      of the relay server, _not_ the port # of the relay). Your service must write the exact clientId to that socket.
* once the new socket is connected, relay-server will start transferring any data from the client
   to that new socket and vice-versa.

## Building 

Prerequisites: JDK 8

* Create package for distribution
    ```
    > gradlew clean distZip
    
    ```
    (or, use distTar if you prefer tgz)
    
    Look in ```./dist/build/distributions/``` for the archive file

* Copy the archive file wherever, and unzip/untar it.
* It will have created the directory structure
    ```
    tcp_relay-1.0-SNAPSHOT
    +---bin
    \---lib
    ```
    The bin directory contains shell and batch scripts to run the relay and echoserver.


## Commandline  usage

Prerequisites: JRE 8

### TCP Relay Server
```
usage: relay [-h] [--bindhost [BINDHOST]] [--remoteHostname [HOSTNAME]]
             [--minPort [MIN]] [--maxPort [MAX]] [RELAYPORT]

start a TCP Relay

positional arguments:
  RELAYPORT              The port # of the TCP Relay server (default: 8080)

optional arguments:
  -h, --help             show this help message and exit
  --bindhost [BINDHOST]  the hostname  to  bind  our  server  to  (default:
                         0.0.0.0)
  --remoteHostname [HOSTNAME]
                         the hostname  relays  should  use  to  access  the
                         relay-server (default: SLCE413687)
  --minPort [MIN]        the min  port  value  to  use  for  created relays
                         (default: 8081)
  --maxPort [MAX]        the max  port  value  to  use  for  created relays
                         (default: 9081)
```
### Echo Server
```
usage: echoserver [-h] [RELAYHOST] [RELAYPORT]

start  an  echoserver  which   coordinates   with   a  specific  TCP  relay
implementation

positional arguments:
  RELAYHOST              The hostname  of  the  host  implementing  the TCP
                         Relay (default: localhost)
  RELAYPORT              The port # of the TCP Relay server (default: 8080)

optional arguments:
  -h, --help             show this help message and exit
```