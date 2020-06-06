/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.uche.rippled;

import com.uche.rippleassessment.MainApp;
import com.uche.rippleassessment.SequenceFileConnector;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Drey
 */
public class RippledServer {
    private HttpClientConnection httpConn;
    //private static final String SERVER_URL = "http://s1.ripple.com:51234";
    private static final String SERVER_URL = MainApp.getProps().getProperty("server.url", "http://s1.ripple.com:51234");
    private static final long POLLING_INTERVAL_MS = Integer.parseInt(MainApp.getProps().getProperty("server.polling_interval", "5000"));
    

    
    public void pollServerInfo() throws IOException, InterruptedException, ParseException {
        if(httpConn == null)
            httpConn = new HttpClientConnection();
        CloseableHttpClient httpClient = (CloseableHttpClient)httpConn.createHttpClient();
        //Create a HTTP POST request and set options
        HttpPost post = new HttpPost(SERVER_URL);
        post.addHeader(HTTP.CONTENT_TYPE, "application/json");
        post.addHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_KEEP_ALIVE);
        post.setEntity(new StringEntity(createServerInfoBody()));        
        
        try {
            String result;
            int count = 0;
            while(count < 20){
                result = httpConn.executeClient(httpClient, post);
                SequenceFileConnector.writeToFile(parseServerInfoResponse(result));
                Thread.sleep(POLLING_INTERVAL_MS);
                count++;
            }
        }finally{
            Logger.getLogger(RippledServer.class.getName()).log(Level.INFO, "Polling completed");
            httpClient.close();
        }
    }
    
    /**
     * Creates JSON message for the server_info command
     * @return formatted JSON string
     */
    private String createServerInfoBody(){
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"method\":\"server_info\",");
        json.append("\"params\":[{}]");
        json.append("}");

        return json.toString();
    }
    
    /**
     * Parse a JSON formatted response and extract the time and sequence elements
     * @param json A JSON formatted string to parse
     * @return a delimited string of time and sequence
     * @throws ParseException 
     */
    private String parseServerInfoResponse(String json) throws ParseException {
        JSONObject jo = (JSONObject)new JSONParser().parse(json);
        Map info = ((Map)((Map)jo.get("result")).get("info"));        
        
        String time = info.get("time").toString();
        String seq = ((Map)info.get("validated_ledger")).get("seq").toString();
        
        String tm = "";
        Date d;
        try {
            d = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss").parse(time);
            tm = new SimpleDateFormat("HH:mm:ss").format(d);
        } catch (java.text.ParseException ex) {
            Logger.getLogger(RippledServer.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return tm +","+seq;
    }
}
