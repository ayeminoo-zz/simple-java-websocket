package com.amo.websocket.server;

import com.amo.websocket.api.Message;

/**
 * Created by ayeminoo on 1/7/18.
 */
public class BinaryMessage implements Message<byte[]>{
    @Override
    public byte[] getData() {
        return new byte[0];
    }
}
