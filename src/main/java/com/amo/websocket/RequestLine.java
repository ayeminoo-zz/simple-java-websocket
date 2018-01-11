package com.amo.websocket;

public interface RequestLine {

    String getMethod();

    String getProtocolVersion();

    String getUri();

}
