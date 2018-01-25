# Simple WebSocket Server in Pure Java

[![Build Status](https://travis-ci.org/ayeminoo/simple-java-websocket.svg?branch=master)](https://travis-ci.org/ayeminoo/simple-java-websocket)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.java-websocket/Java-WebSocket/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.java-websocket/Java-WebSocket)
[![Javadocs](https://www.javadoc.io/badge/org.java-websocket/Java-WebSocket.svg)](https://www.javadoc.io/doc/org.java-websocket/Java-WebSocket)

This repository contains a very simple websocket server implementation in java.
This implementation is 100% conformant to the websocket protocol specification [RFC 6455](http://tools.ietf.org/html/rfc6455)
and passing all test cases in Autobahn|Testsuite.  

What is websocket?
==================
It is a protocol that handles the dual communication over a single TCP connection. 
It is designed to use over the web. In short, it is a socket over web (http). You can read
more details [here](https://en.wikipedia.org/wiki/WebSocket).

For who we developed
====================
+ For those who want simplest and easiest way to use 
+ For those who want to learn how to build websocket server in plain java
+ For those who want to run websocket server without a dedicated web server like [Tomcat](http://tomcat.apache.org/)
, [Jetty](https://www.eclipse.org/jetty/) (or) something else.Of course you can use this library with them as well.
+ For those who want to run standalone websocket server on small computers or embedded systems like mobile phones or routers or etc..


Autobahn|Testsuite
==================

The Autobahn|Testsuite is a fully automated test suite to verify client and server 
implementations of the `WebSocket Protocol` for specification conformance and implementation robustness.
Learn more about [Autobahn|Test](https://github.com/crossbario/autobahn-testsuite/blob/master/doc/README.rst).

Example Usages
==============
Creating a websocket server using our library is as simple as the following two tasks.

1. Create the Endpoint class
2. Register it with BasicContainer 

Done

See the following piece of code that registered the Endpoint class with container
 
```Java
BasicContainer bc = new BasicContainer();
bc.registerEndpoint("/echo", new EchoEndpoint());
bc.listen(8080)
```
Here we have configure the `Echopoint` to listen on `8080` port. All requests coming with `ws://host:8080/echo` 
will be handle by the `EchoEndpoint` 

Implementing `EchoEndpoint` is also simple. Just extend `Endpoint` class and override 
+ `onTextMessage`
+ `onConnect`

```java
class EchoEndpoint implements Endpoint {

    private Session session;

    @Override
    public void onConnect(Session session) {
        this.session = session;
    }

    @Override
    public void onTextMessage(String data) {
        session.getWebsocketHandler().sendMessage(data);
    }
 }
```

Full example of `EchoServer` along with many others can be seen 
[here](https://github.com/ayeminoo/simple-java-websocket/tree/master/examples)

[API Docs]()
==========
Still working on it

System Requirements to build
===========================
+ Java 1.8 or higher

How to build
============
We used gradle to build the project.So after cloning this repo, go to root of the project and run the following 

``` bash
cd project_root
./gradlew build
```

WSS Support
===========
If you want to run it over `TLS` to be secure so that no middle man can eavesdrop, add the key file like the following

```java
BasicContainer bc = new BasicContainer();
bc.registerEndpoint("/", new EchoEndpoint());
bc.setTLSKeyStore(keyFilePath, keyPass, storePass, alias);
bc.listen(443);
```

see full example in [examples](https://github.com/ayeminoo/simple-java-websocket/tree/master/examples)

#####Supported KeyStore Type
* JKS
* ~~PKCS12~~ (still in progress)

License
=======
Free to used any part of the codes any way you want. 


> We valued your feedback most.
> Please give any kind of feedback you can. Suggestions, Issues Reporting or anything.
> You are more than welcome to contribute the project or report any kind of issue you faced 
> We will fixed instantly

**Report Issues**
[Here](https://github.com/ayeminoo/simple-java-websocket/issues)

**Contribute the project**
[Here](https://github.com/ayeminoo/simple-java-websocket/pulls)