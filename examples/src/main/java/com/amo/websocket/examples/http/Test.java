package com.amo.websocket.examples.http;

/**
 * Created by ayeminoo on 1/20/18.
 */
public class Test {
    public static void main(String[] args) {
        SimpleHttpsServer httpsServer = new SimpleHttpsServer();
        httpsServer.Start(9999);
    }
}
