package com.amo.websocket;

import java.io.InputStream;

/**
 * Created by ayeminoo on 1/7/18.
 */
public interface HttpRequest {
    RequestLine getRequestLine();
    InputStream getInputStream();
}
