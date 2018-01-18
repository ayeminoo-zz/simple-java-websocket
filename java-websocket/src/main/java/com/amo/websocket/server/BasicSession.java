package com.amo.websocket.server;

import com.amo.websocket.FrameReader;
import com.amo.websocket.FrameWriter;
import com.amo.websocket.api.Endpoint;
import com.amo.websocket.api.Session;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by ayeminoo on 1/7/18.
 */
public class BasicSession implements Session {
    private long sessionId;
    private Endpoint endpoint;
    private Socket socket;
    private BasicWebsocketHandler websocketHandler;
    private boolean close = false;
    private FrameReader frameReader;
    private FrameWriter frameWriter;
    private int MAX_BUFFER_SIZE;
    private static AtomicLong atomicLong = new AtomicLong(System.currentTimeMillis());

    public BasicSession(Socket socket, Endpoint endpoint, int maxBuffer) throws IOException {
        this(socket, endpoint,
                new BasicFrameReader(socket.getInputStream(), maxBuffer),
                new BasicFrameWriter(socket.getOutputStream()), maxBuffer);
    }

    public BasicSession(Socket socket, Endpoint endpoint, FrameReader frameReader, FrameWriter frameWriter, int maxBuffer){
        this.socket = socket;
        this.endpoint = endpoint;
        sessionId = atomicLong.getAndIncrement();
        this.frameReader = frameReader;
        this.frameWriter = frameWriter;
        this.MAX_BUFFER_SIZE = maxBuffer;
    }

    @Override
    public long getSessionId() {
        return sessionId;
    }

    @Override
    public void close() throws IOException {
        close = true;
        socket.close();
    }

    @Override
    public boolean isClose() {
        return close;
    }

    @Override
    public Endpoint getEndpoint() {
        return endpoint;
    }

    @Override
    public FrameReader getFrameReader() {
        return frameReader;
    }

    @Override
    public void setWebsocketHandler(BasicWebsocketHandler basicWebsocketHandler) {
        this.websocketHandler = basicWebsocketHandler;
    }

    @Override
    public BasicWebsocketHandler getWebsocketHandler(){
        return websocketHandler;
    }

    @Override
    public void setMaxBufferSize(int length){
        MAX_BUFFER_SIZE = length;
    }

    @Override
    public FrameWriter getFrameWriter() {
        return frameWriter;
    }

    @Override
    public int getMaxBufferSize() {
        return MAX_BUFFER_SIZE;
    }
}
