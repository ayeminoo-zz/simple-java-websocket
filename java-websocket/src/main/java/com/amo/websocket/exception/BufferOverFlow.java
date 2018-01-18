package com.amo.websocket.exception;

import javax.websocket.CloseReason;
import java.nio.BufferOverflowException;

/**
 * Created by ayeminoo on 1/5/18.
 */
public class BufferOverFlow extends  WebsocketException{
    public BufferOverFlow() {
        super(CloseReason.CloseCodes.TOO_BIG);
    }
}
