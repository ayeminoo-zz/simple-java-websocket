package com.amo.websocket.api;

import com.amo.websocket.HandshakeHandler;
import com.amo.websocket.KeyStoreType;

import javax.net.ssl.SSLServerSocketFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

/**
 * Created by ayeminoo on 1/7/18.
 */
public interface Container {
    void registerEndpoint(String uri, Endpoint endpoint) throws URISyntaxException;
    void unRegisterEndpoint(String uri);
    void listen(int port);
    void listen();
    void registerHandShakeHandler(HandshakeHandler handshakeHandler);
    void registerSSLFactory(SSLServerSocketFactory factory);
    void setTLSKeyStore(String keyStoreFilePath, String keyPass, String storePass, String alias, KeyStoreType storeType) throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyManagementException;
    void setTLSKeyStore(File keyStoreFile, String keyPass, String storePass, String alias, KeyStoreType storeType) throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyManagementException;
    void setTLSKeyStore(InputStream keyStoreInputStream, String keyPass, String storePass, String alias, KeyStoreType keyStoreType) throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyManagementException;
    void close();
}
