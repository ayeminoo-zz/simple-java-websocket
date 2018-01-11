package com.amo.websocket.api;

import com.amo.websocket.HandshakeHandler;

import java.io.PrintStream;
import java.net.URISyntaxException;

/**
 * Created by ayeminoo on 1/7/18.
 */
public interface Container {
    void registerEndpoint(String uri, Endpoint endpoint) throws URISyntaxException;
    void unRegisterEndpoint(String uri);
    void listen(int port);
    void listen();
    void registerHandShakeHandler(HandshakeHandler handshakeHandler);
    void close();
}
