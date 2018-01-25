package com.amo.websocket.examples;

import com.amo.websocket.server.BasicContainer;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by ayeminoo on 1/25/18.
 */
public class NormalEchoServer {
    public static void main(String[] args) throws URISyntaxException, InterruptedException, IOException {
        BasicContainer bc = new BasicContainer();
        bc.registerEndpoint("/", new EchoEndpoint());
        bc.listen(8080);
        System.in.read();
    }
}
