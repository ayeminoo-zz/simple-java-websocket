package com.amo.websocket;

import java.io.IOException;

/**
 * Created by ayeminoo on 1/4/18.
 */
public interface FrameReader {
    Frame read() throws IOException;
    void close() throws IOException;
}
