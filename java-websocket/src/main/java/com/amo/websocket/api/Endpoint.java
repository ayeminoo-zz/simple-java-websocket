package com.amo.websocket.api;

import com.amo.websocket.server.BinaryMessage;

import javax.websocket.CloseReason;

/**
 * Created by ayeminoo on 1/7/18.
 */
public interface Endpoint {
    void onConnect(Session session);
    void onTextMessage(String data);
    void onBinaryMessage(byte[]data);
    void onError(Throwable throwable);
    void onClose(CloseReason closeReason);
}

