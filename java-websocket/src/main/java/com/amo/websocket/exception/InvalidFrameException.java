package com.amo.websocket.exception;

import com.amo.websocket.api.CloseReason;

/**
 * Created by ayeminoo on 1/5/18.
 */
public class InvalidFrameException extends RuntimeException {
    private javax.websocket.CloseReason.CloseCode closeCode;

    public InvalidFrameException(String message) {
        super(message);
    }

    public InvalidFrameException() {
        closeCode = javax.websocket.CloseReason.CloseCodes.PROTOCOL_ERROR;
    }

    public InvalidFrameException(javax.websocket.CloseReason.CloseCode closeCode){
        this.closeCode = closeCode;
    }

    public javax.websocket.CloseReason.CloseCode getCloseCode() {
        return closeCode;
    }
}
