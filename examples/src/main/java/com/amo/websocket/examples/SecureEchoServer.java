package com.amo.websocket.examples;

import com.amo.websocket.server.BasicContainer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

/**
 * Created by ayeminoo on 1/25/18.
 */
public class SecureEchoServer {
    public static void main(String[] args) throws URISyntaxException, InterruptedException, IOException, UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        String filePath = "keystore.jks";
        String keyPass = "password";
        String storePass = "password";
        String alias = "selfsigned";

        BasicContainer bc = new BasicContainer();
        bc.registerEndpoint("/", new EchoEndpoint());

        //load the key file
        //since our current self-signed keyfile is in resource folder, we can load it using classloader
        bc.setTLSKeyStore(ClassLoader.getSystemResourceAsStream(filePath), keyPass, storePass, alias);

        // we can also load by giving full path
        // bc.setTLSKeyStore("/home/ayeminoo/data/projects-in-progress/simple-java-websocket/examples/src/main/resources/keystore.jks", keyPass, storePass, alias);

        bc.listen(8080);
        System.in.read();

    }
}
