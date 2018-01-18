package com.amo.websocket.exception;

/**
 * Created by ayeminoo on 1/19/18.
 */
public class WebsocketException extends RuntimeException{
    private javax.websocket.CloseReason.CloseCode closeCode;

    public WebsocketException(javax.websocket.CloseReason.CloseCode closeCode){
        this.closeCode = closeCode;
    }

    public javax.websocket.CloseReason.CloseCode getCloseCode() {
        return closeCode;
    }
}
