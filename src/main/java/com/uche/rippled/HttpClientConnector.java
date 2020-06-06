/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.uche.rippled;

/**
 *
 * @author Drey
 */



import com.uche.rippleassessment.MainApp;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.protocol.HttpContext;

public class HttpClientConnector {
    
    
    public HttpClientConnector(){ }
    
    
    /**
     * Fetches a HttpClient instance with configured default settings to be used in communicating with the server
     * Can be extended to provide a connection pool
     * @return an instance of HttpClient from a connection factory
     */
    public HttpClient createHttpClient() {
        CloseableHttpClient httpClient = HttpClients.custom()
            .setDefaultRequestConfig(getDefaultConfig())
            .setKeepAliveStrategy(myStrategy)
            .setRetryHandler(myRetryHandler)
            .build();
        return httpClient;
    }
    
    
    /**
     * Define a set of default configuration that can be applied to the client or request
     * @return default configuration
     */
    public RequestConfig getDefaultConfig(){
        int timeout = Integer.parseInt(MainApp.getProps().getProperty("connection.timeout"));
        RequestConfig defaultRequestConfig = RequestConfig.custom()
            .setSocketTimeout(timeout)
            .setConnectTimeout(timeout)
            .setConnectionRequestTimeout(timeout)
            .build();
        return defaultRequestConfig;
    }
    
    /**
     * Execute a HTTP request and uses a custom response handler for the response
     * @param client HttpClient instance
     * @param request HTTP Request method (GET / POST)
     * @return a String derived from the HTTP response message
     * @throws IOException
     * @throws InterruptedException 
     */
    public String executeClient(HttpClient client, HttpUriRequest request) 
        throws IOException, InterruptedException {

        // Create a custom response handler
        ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
            @Override
            public String handleResponse(final HttpResponse response) 
                    throws ClientProtocolException, IOException {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            }
        };
        return client.execute(request, responseHandler);
    }
    
    
    /**
     * Custom Keep Alive handler attached to the connection to handle keep alive connection method
     */
    ConnectionKeepAliveStrategy myStrategy = new ConnectionKeepAliveStrategy() {
        @Override
        public long getKeepAliveDuration(HttpResponse hr, HttpContext hc) {
//            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            return Integer.parseInt(MainApp.getProps().getProperty("connection.keep_alive"));
        }
    };
    
    /**
     * Custom retry handler attached to the connection to handle retries
     */
    HttpRequestRetryHandler myRetryHandler = new HttpRequestRetryHandler() {
        @Override
        public boolean retryRequest(IOException ex, int execCount, HttpContext context) {
            return false;
        }
    };
}
