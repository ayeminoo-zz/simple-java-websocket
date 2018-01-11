package com.amo.websocket.server;

import com.amo.websocket.HandshakeHandler;
import com.amo.websocket.HttpRequest;
import com.amo.websocket.HttpResponse;
import com.amo.websocket.exception.HandshakeFailureException;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ayeminoo on 1/5/18.
 */
public class BasicHandshakeHandler implements HandshakeHandler {
    //todo: replaced this method with better implementation
    @Override
    public boolean doHandshake(InputStream in, OutputStream out) throws HandshakeFailureException {
        //translate bytes of request to string
        String data = new Scanner(in, "UTF-8").useDelimiter("\\r\\n\\r\\n").next();

        Matcher get = Pattern.compile("^GET").matcher(data);
        try {
            if (get.find()) {
                Matcher match = Pattern.compile("Sec-WebSocket-Key: (.*)").matcher(data);
                match.find();
                byte[] response = ("HTTP/1.1 101 Switching Protocols\r\n"
                        + "Connection: Upgrade\r\n"
                        + "Upgrade: websocket\r\n"
                        + "Sec-WebSocket-Accept: "
                        + DatatypeConverter
                        .printBase64Binary(
                                MessageDigest
                                        .getInstance("SHA-1")
                                        .digest((match.group(1) + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11")
                                                .getBytes("UTF-8")))
                        + "\r\n\r\n")
                        .getBytes("UTF-8");

                out.write(response, 0, response.length);
            } else {
                System.out.println("we must send Bad Request HTTP Response code to client");
            }
        } catch (Exception ex) {
            System.out.println("we must send Bad Request HTTP Response code to client");
        }
        return true;
    }

    @Override
    public boolean doHandshake(Socket socket) throws HandshakeFailureException, IOException {
        return doHandshake(socket.getInputStream(), socket.getOutputStream());
    }

    @Override
    public boolean doHandshake(HttpRequest request, HttpResponse response) {
        return doHandshake(request.getInputStream(), response.getOutputStream());
    }
}
