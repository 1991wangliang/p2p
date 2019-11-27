Introduction
================

This is an experimental work to build a unstructured P2P network using Java and Netty. 

It has following capabilities:

- Join to the P2P network
- Leave the network
- List peers in the network
- Leader election
- Send Message to Peer
- Broadcast Message to P2P network
- 2 PingService way  

P2P network is unstructured. Once a peer is started, it can join by connecting to any of the peers in the network. 
 
To maintain connectivity of the P2P network, each peer connects to more than one peer in the network randomly. 

Peers send periodic keep alive messages to their neighbours to notify them about their presence. If a peer does not receive a message from a neighbour for a configured amount of time, it drops the connection.
 
A peer can discover other peers in the network with a Ping-Pong process by sending a Ping message to its neighbours and waiting for the Pong messages for some time. This message also contains how many hops it can go over the network. When a peer receives a Ping message from a neighbour, it replies with a Pong message and dispatches the Ping message to its own neighbours after decreasing the hop count.
  
This mechanism is a very basic implementation of the Ping-Pong mechanism described in the Gnutella P2P network protocol v0.4. To read more about Gnutella, please see [The Annotated Gnutella Protocol Specification v0.4](http://rfc-gnutella.sourceforge.net/developer/stable/)  and [Gnutella Wikipedia](https://en.wikipedia.org/wiki/Gnutella). 
 
In the image below, you can see a Ping-Pong flow. 
 
![Ping Pong](http://rfc-gnutella.sourceforge.net/developer/stable/GnutellaProtocol-v0.4-r1.6_files/gnutella-ping-pong-routing.gif)


It also implements [Bully Algorithm](https://en.wikipedia.org/wiki/Bully_algorithm) for leader election. Since bully algorithm assumes a fully connected network under the hood, it is users' responsibility to create a connection between each peer before running the election. 

All network messages are sent with a fire-and-forget manner without any acknowledgement or retry system.

You can see available configuration options in [Config class](https://github.com/metanet/p2p/blob/master/src/main/java/com/basrikahveci/p2p/peer/Config.java). Main class is also [here](https://github.com/metanet/p2p/blob/master/src/main/java/com/basrikahveci/p2p/Main.java).


Build
================

This is a very simple Maven project which requires Java 8. 
 
It can be compiled with `mvn clean compile` and final jar can be produced with `mvn clean package`.


 
Run
================
  
You can start peer-a,peer-b,peer-c,peer-d. that support 2 PingService.   
1. ForWardPingService. when receive ping message,that can forward all neighbours, also the neighbours can connect this peer.
2. NoForWardPingService.when receive ping message,that only back to the neighbour that pong message .  

peer-a web port is 8081,p2p port is 50602,peer-b is 8082,50602 , all peer can use flow commands: 
* /list  
    see all online peers .
* /connect?host=val&port=val  
    connect peer.
* /leave  
    leave this p2p network.
* /disconnect  
    disconnect peer.
* /send  
    send msg to peer.
* /broadcast  
    send all online peers msg.
    
like  http://127.0.0.1:8081/list      
 
Contribution is welcomed
================
This project is only tested by starting peers in terminal and creating connections among them. Therefore, unit tests and integration tests are welcomed.


