package com.amo.websocket.server;

import com.amo.websocket.api.Message;
import com.amo.websocket.api.MessageWriter;
import com.amo.websocket.exception.InvalidMessageException;

/**
 * Created by ayeminoo on 1/7/18.
 */
public class TextMessageWriter implements MessageWriter {
    @Override
    public void write(Message message) {
        if(message == null || !(message instanceof TextMessage) ) throw new InvalidMessageException();

    }
}
