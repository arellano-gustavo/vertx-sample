package mx.unam.dgpe.sample.controller;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

public class RestUtil {
    private static final Logger logger = Logger.getLogger(RestUtil.class);
    
    public static String sendGet(final String request) throws Exception {
        String result = null;
        SSLContext sslContext = SSLContext.getInstance("SSL");
        HostnameVerifier hostnameVerifier = SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;

        // set up a TrustManager that trusts everything
        sslContext.init(null, new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                logger.debug("getAcceptedIssuers =============>");
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
                logger.debug("checkClientTrusted =============>");
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
                logger.debug("checkServerTrusted =============>");
            }
        } }, new SecureRandom());

        SSLSocketFactory sslSocketFactory = new SSLSocketFactory(sslContext);
        X509HostnameVerifier hostnameVerifier2 = (X509HostnameVerifier) hostnameVerifier;
        sslSocketFactory.setHostnameVerifier(hostnameVerifier2);
        
        CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslSocketFactory).build();

        logger.debug("Se envía el request: --->" + request + "<--- al Broker via GET");
        
        //HttpPost httpPost = new HttpPost(request);
        HttpGet httpGet = new HttpGet(request);
        
        //ByteArrayEntity postDataEntity = new ByteArrayEntity(postData.getBytes());
        //httpPost.setEntity(postDataEntity);
        CloseableHttpResponse response = httpclient.execute(httpGet);
        try {
            logger.debug("Se obtiene la respuesta del Broker de cryptomonedas");
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
        } finally {
            logger.debug("Se cierra el HttpResponse");
            response.close();
        }
        return result;
    }

    public static String sendPost(final String request, final String postData)
            throws ClientProtocolException, IOException, NoSuchAlgorithmException, KeyManagementException {

        String result = null;
        SSLContext sslContext = SSLContext.getInstance("SSL");
        HostnameVerifier hostnameVerifier = SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;

        // set up a TrustManager that trusts everything
        sslContext.init(null, new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                logger.info("getAcceptedIssuers =============>");
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
                logger.info("checkClientTrusted =============>");
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
                logger.info("checkServerTrusted =============>");
            }
        } }, new SecureRandom());

        SSLSocketFactory sslSocketFactory = new SSLSocketFactory(sslContext);
        X509HostnameVerifier hostnameVerifier2 = (X509HostnameVerifier) hostnameVerifier;
        sslSocketFactory.setHostnameVerifier(hostnameVerifier2);
        
        CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslSocketFactory).build();

        logger.info("Se envía el request: --->" + request + "<--- al DP via POST");
        HttpPost httpPost = new HttpPost(request);
        ByteArrayEntity postDataEntity = new ByteArrayEntity(postData.getBytes());
        httpPost.setEntity(postDataEntity);
        CloseableHttpResponse response = httpclient.execute(httpPost);
        try {
            logger.info("Se obtiene la respuesta del DP");
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
        } finally {
            logger.info("Se cierra el HttpResponse");
            response.close();
        }
        return result;
    }

}
