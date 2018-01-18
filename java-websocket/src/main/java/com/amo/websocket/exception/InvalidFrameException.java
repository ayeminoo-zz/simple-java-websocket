package com.amo.websocket.exception;

import javax.websocket.CloseReason;

/**
 * Created by ayeminoo on 1/5/18.
 */
public class InvalidFrameException extends WebsocketException{
    public InvalidFrameException() {
        super(CloseReason.CloseCodes.PROTOCOL_ERROR);
    }
}
