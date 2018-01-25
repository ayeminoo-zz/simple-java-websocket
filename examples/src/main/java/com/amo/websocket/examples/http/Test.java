package com.amo.websocket.examples.http;

/**
 * Created by ayeminoo on 1/20/18.
 */
public class Test {

    public static void main(String[] args) {
//		// start http server
//		SimpleHttpServer httpServer = new SimpleHttpServer();
//		httpServer.Start(port);

        // start https server
        SimpleHttpsServer httpsServer = new SimpleHttpsServer();
        httpsServer.Start(9999);

//		System.out.println(System.getProperty("user.dir"));
//		System.out.println(Main.class.getClassLoader().getResource("").getPath());

    }

//    public static void main(String [] args) throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyManagementException {
//        // load certificate
//        String keystoreFilename = "/home/ayeminoo/data/Testing Projects/ssldemo/mykey.keystore";
//        char[] storepass = "mypassword".toCharArray();
//        char[] keypass = "mypassword".toCharArray();
//        String alias = "alias";
//        FileInputStream fIn = new FileInputStream(keystoreFilename);
//        KeyStore keystore = KeyStore.getInstance("JKS");
//        keystore.load(fIn, storepass);
//// display certificate
//        Certificate cert = keystore.getCertificate(alias);
//        System.out.println(cert);
//// setup the key manager factory
//        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
//        kmf.init(keystore, keypass);
//// setup the trust manager factory
//        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
//        tmf.init(keystore);
//
//
//        // create https server
//        HttpsServer server = HttpsServer.create(new InetSocketAddress(8889), 0);
//// create ssl context
//        SSLContext sslContext = SSLContext.getInstance("TLS");
//// setup the HTTPS context and parameters
//        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
//        server.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
//            public void configure(HttpsParameters params) {
//                try {
//                    // initialise the SSL context
//                    SSLContext c = SSLContext.getDefault();
//                    SSLEngine engine = c.createSSLEngine();
//                    params.setNeedClientAuth(false);
//                    params.setCipherSuites(engine.getEnabledCipherSuites());
//                    params.setProtocols(engine.getEnabledProtocols());
//                    // get the default parameters
//                    SSLParameters defaultSSLParameters = c.getDefaultSSLParameters();
//                    params.setSSLParameters(defaultSSLParameters);
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                    System.out.println("Failed to create HTTPS server");
//                }
//            }
//        });
//        server.start();
//    }
}
