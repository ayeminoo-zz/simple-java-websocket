package com.amo.websocket.server;

import com.amo.websocket.HandshakeHandler;
import com.amo.websocket.HttpRequest;
import com.amo.websocket.HttpResponse;
import com.amo.websocket.RequestLine;
import com.amo.websocket.KeyStoreType;
import com.amo.websocket.api.Endpoint;
import com.amo.websocket.api.Session;
import com.amo.websocket.exception.EndPointAlreadyRegister;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.websocket.CloseReason;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by ayeminoo on 1/7/18.
 */
public class BasicContainer implements com.amo.websocket.api.Container {
    private Map<String, Endpoint> endpointMap = new HashMap<>();
    private Map<Long, Session> sessionMap = new HashMap<>();
    private int port = 80;
    private int maxBuffer = 100000000; //100MB
    private HandshakeHandler handshakeHandler = new BasicHandshakeHandler();
    private SSLServerSocketFactory sslServerSocketFactory;

    @Override
    public void registerEndpoint(String uri, Endpoint endpoint) throws URISyntaxException {
        validateUri(uri);
        if (endpointMap.containsKey(uri)) throw new EndPointAlreadyRegister();
        endpointMap.put(uri, endpoint);
    }

    @Override
    public void unRegisterEndpoint(String uri) {
        endpointMap.remove(uri);
    }

    @Override
    public void registerHandShakeHandler(HandshakeHandler handshakeHandler) {
        this.handshakeHandler = handshakeHandler;
    }

    @Override
    public void registerSSLFactory(SSLServerSocketFactory factory) {
        this.sslServerSocketFactory = factory;
    }

    @Override
    public void setTLSKeyStore(String keyStoreFilePath, String keyPass, String storePass, String alias, KeyStoreType storeType) throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyManagementException {
        setTLSKeyStore(new FileInputStream(keyStoreFilePath), keyPass, storePass, alias, storeType);
    }

    @Override
    public void setTLSKeyStore(File keyStoreFile, String keyPass, String storePass, String alias, KeyStoreType storeType) throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyManagementException {
        setTLSKeyStore(new FileInputStream(keyStoreFile), keyPass, storePass, alias, storeType);
    }

    @Override
    public void setTLSKeyStore(InputStream keyStoreInputStream, String keyPass, String storePass, String alias, KeyStoreType storeType) throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyManagementException {
        char[] storepass = storePass.toCharArray();
        char[] keypass = keyPass.toCharArray();
        KeyStore keystore = KeyStore.getInstance(storeType.name());
        keystore.load(keyStoreInputStream, storepass);
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(keystore, keypass);

        // setup the trust manager factory
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(keystore);

        // create ssl context
        SSLContext sslContext = SSLContext.getInstance("TLS");

        // setup the HTTPS context and parameters
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        sslServerSocketFactory = sslContext.getServerSocketFactory();
    }

    @Override
    public void close() {
        sessionMap.forEach((k, s) -> {
            if(!s.isClose()){
                try {
                    s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        endpointMap.forEach((k, e) -> {
            e.onClose(new CloseReason(CloseReason.CloseCodes.GOING_AWAY, "Server going down"));
        });
    }

    @Override
    public void listen(int port) {
        this.port = port;
        listen();
    }

    @Override
    public void listen(){
    ServerSocket server = null;
        try {
            if(sslServerSocketFactory != null){
               server = sslServerSocketFactory.createServerSocket(port);
            }else{
                server = new ServerSocket(port);
            }
            debug("websocket server is listening on port " + port);
            while(true){
                Socket socket = server.accept();
                new Thread(){
                    @Override
                    public void run(){
                        HttpRequest request = null;
                        try {
                            request = createHttpRequest(socket.getInputStream());
                            String uri = request.getRequestLine().getUri();
                            Optional<Endpoint> endpoint = getEndPoint(uri);
                            if(endpoint.isPresent() == false){
                                debug("A client connected but not endpoint registered on ." + uri);
                                return;
                            }
                            HttpResponse response = createHttpResponse(socket.getOutputStream());
                            handshakeHandler.doHandshake(request, response);
                            Optional<Session> session = createSession(socket, endpoint.get(), maxBuffer);
                            session.ifPresent(s -> {
                                endpoint.ifPresent((e -> {e.onConnect(s);}));
                                s.setWebsocketHandler(new BasicWebsocketHandler(s));
                                sessionMap.put(s.getSessionId(), s);
                            });
                        } catch (IOException e) {
                            e.printStackTrace(debugStream);
                        }
                    }
                }.start();
            }
        } catch (IOException e) {
            e.printStackTrace(debugStream);
        }
    }

    public int getMaxBuffer() {
        return maxBuffer;
    }

    public void setMaxBuffer(int maxBuffer) {
        this.maxBuffer = maxBuffer;
    }

    private Optional<Session> createSession(Socket socket, Endpoint endpoint, int maxBuffer) {
        //create new session
        Session session = null;
        try {
            session = new BasicSession(socket, endpoint, maxBuffer);
        } catch (IOException e) {
            e.printStackTrace(debugStream);
        }
        return Optional.ofNullable(session);
    }

    private Optional<Endpoint> getEndPoint(String uri) {
        return Optional.ofNullable(endpointMap.get(uri));
    }

    private HttpResponse createHttpResponse(OutputStream outputStream) {
        //todo:
        return new HttpResponse() {
            @Override
            public OutputStream getOutputStream() {
                return outputStream;
            }
        };
    }

    private HttpRequest createHttpRequest(InputStream inputStream) {
        //todo:
        return new HttpRequest() {
            @Override
            public RequestLine getRequestLine() {
                return new RequestLine() {
                    @Override
                    public String getMethod() {
                        return "GET";
                    }

                    @Override
                    public String getProtocolVersion() {
                        return "1.1";
                    }

                    @Override
                    public String getUri() {
                        return "/";
                    }
                };
            }

            @Override
            public InputStream getInputStream() {
                return inputStream;
            }
        };
    }

    private void validateUri(String uri) throws URISyntaxException {
        //todo: implement validation logic
    }

    // set this to a print stream if you want debug info
    // sent to it; otherwise, leave it null
    static private PrintStream debugStream = System.out;

    public static PrintStream getDebugStream(){
        return debugStream;
    }
    // we have two versions of this ...
    static public void setDebugStream( PrintStream ps ) {
        debugStream = ps;
    }

    // ... just for convenience
    static public void setDebugStream( OutputStream out ) {
        debugStream = new PrintStream( out );
    }

    // send debug info to the print stream, if there is one
    static public void debug( String s ) {
        if (debugStream != null)
            debugStream.println( s );
    }
}
