package com.amo.websocket;

import com.amo.websocket.exception.HandshakeFailureException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public interface HandshakeHandler {
    boolean doHandshake(InputStream in, OutputStream out) throws HandshakeFailureException;
    boolean doHandshake(Socket socket) throws HandshakeFailureException, IOException;
    boolean doHandshake(HttpRequest request, HttpResponse response);
}