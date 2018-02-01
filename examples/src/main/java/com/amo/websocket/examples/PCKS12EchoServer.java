package com.amo.websocket.examples;

import com.amo.websocket.KeyStoreType;
import com.amo.websocket.server.BasicContainer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

/**
 * Created by ayeminoo on 2/1/18.
 */
public class PCKS12EchoServer {
    public static void main(String[] args) throws URISyntaxException, InterruptedException, IOException, UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        String filePath = "certificate.p12";
        String keyPass = "123456";
        String storePass = "123456";
        String alias = "selfsigned";

        BasicContainer bc = new BasicContainer();
        bc.registerEndpoint("/", new EchoEndpoint());

        //load the key file
        //since our current self-signed keyfile is in resource folder, we can load it using classloader
        bc.setTLSKeyStore(ClassLoader.getSystemResourceAsStream(filePath), keyPass, storePass, alias, KeyStoreType.PKCS12);

        // we can also load by giving full path
        // bc.setTLSKeyStore("/home/ayeminoo/data/projects-in-progress/simple-java-websocket/examples/src/main/resources/keystore.jks", keyPass, storePass, alias);

        bc.listen(8080);
        System.in.read();

    }
}
