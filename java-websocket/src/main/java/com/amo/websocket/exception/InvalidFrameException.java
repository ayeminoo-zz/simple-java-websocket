package com.amo.websocket.exception;

/**
 * Created by ayeminoo on 1/5/18.
 */
public class InvalidFrameException extends RuntimeException {
    public InvalidFrameException(String message) {
        super(message);
    }

    public InvalidFrameException() {

    }

}
