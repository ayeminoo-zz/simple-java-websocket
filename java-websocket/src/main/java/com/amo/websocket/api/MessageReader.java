package com.amo.websocket.api;

import java.io.IOException;

/**
 * Created by ayeminoo on 1/7/18.
 */
public interface MessageReader {
    public Message read() throws IOException;
}
