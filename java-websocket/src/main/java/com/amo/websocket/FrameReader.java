package com.amo.websocket;

import java.io.IOException;

/**
 * Created by ayeminoo on 1/4/18.
 */
public interface FrameReader {
    Frame read() throws IOException;

    /**
     * this will read the input steam until the buffer is filled or end of file is detected or IOException is occurred.
     * @param buffer
     * @throws IOException
     */
    void read(byte[]buffer) throws IOException;
    void close() throws IOException;
}
