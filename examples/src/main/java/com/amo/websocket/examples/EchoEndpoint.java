package com.amo.websocket.examples;

import com.amo.websocket.api.Endpoint;
import com.amo.websocket.api.Session;

import javax.websocket.CloseReason;

public class EchoEndpoint implements Endpoint {

    private Session session;

    @Override
    public void onConnect(Session session) {
        this.session = session;
        System.out.println("Received a connecton. Session id: " + session.getSessionId());
    }

    @Override
    public void onTextMessage(String data) {
        System.out.println("Received text " + data);
        session.getWebsocketHandler().sendMessage(data);
    }

    @Override
    public void onBinaryMessage(byte[] data) {
        System.out.println("Received binary " + data);
        session.getWebsocketHandler().sendBinaryMessage(data);
    }

    @Override
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
    }

    @Override
    public void onClose(CloseReason closeReason) {
        System.out.println("Received close: " + closeReason.getCloseCode());
    }
}