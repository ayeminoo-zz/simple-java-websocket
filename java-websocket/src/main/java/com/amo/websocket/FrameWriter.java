package com.amo.websocket;

import java.io.IOException;

/**
 * Created by ayeminoo on 1/4/18.
 */
public interface FrameWriter {
    void write(Frame frame) throws IOException;
    void close() throws IOException;
}
