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
    
    private HttpClientConnector httpConn;
    //private static final String SERVER_URL = "http://s1.ripple.com:51234";
    private static final String SERVER_URL = MainApp.getProps()
            .getProperty("server.url", "http://s1.ripple.com:51234");
    private static final long POLLING_INTERVAL_MS = Integer.parseInt(MainApp.getProps()
            .getProperty("server.polling_interval", "5000"));
    private static final int POLL_COUNT = 20;
    

    
    /**
     * Fetch HTTP connector instance, create POST request then continuously poll Rippled server for data over a single connection.
     * As data is retrieved from the server, it will be sent to a file writer and written to file
     * @throws IOException
     * @throws InterruptedException
     * @throws ParseException
     * @throws java.text.ParseException 
     */
    public void pollServerInfo() throws IOException, InterruptedException, ParseException, java.text.ParseException {
        if(httpConn == null)
            httpConn = new HttpClientConnector();
        CloseableHttpClient httpClient = (CloseableHttpClient)httpConn.createHttpClient();
        HttpPost post = new HttpPost(SERVER_URL);
        post.addHeader(HTTP.CONTENT_TYPE, "application/json");
        post.addHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_KEEP_ALIVE);
        post.setEntity(new StringEntity(createServerInfoBody()));        
        
        try {
            String result;
            int count = 0;
            while(count < POLL_COUNT){
                result = httpConn.executeClient(httpClient, post);
                SequenceFileConnector.writeToFile(parseServerInfoResponse(result));
                Thread.sleep(POLLING_INTERVAL_MS);
                count++;
            }
            Logger.getLogger(RippledServer.class.getName()).log(Level.INFO, "Polling completed");
        }finally{
            httpClient.close();
        }
    }
    
    /**
     * Creates JSON message for the server_info command
     * @return formatted JSON request payload
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
     * Parse a properly formatted JSON response and extract the <time> and <validated_ledger.seq> elements
     * @param json A JSON formatted string to parse
     * @return a delimited string of time and sequence
     * @throws ParseException Thrown if unable to parse JSON payload
     */
    private String parseServerInfoResponse(String json) throws ParseException, java.text.ParseException {
        String ret = "";
        JSONObject jo = (JSONObject)new JSONParser().parse(json);
        Map result = (Map)jo.get("result");
        if(result.get("status").equals("success")){
            Map info = (Map)result.get("info");        
            Map vLedger = (Map)info.get("validated_ledger");
            if(vLedger != null){
                Date d = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss").parse(info.get("time").toString());
                String time = new SimpleDateFormat("HH:mm:ss").format(d);
                String seq = vLedger.get("seq").toString();
                ret = time+","+seq;
            }
        }else{
            String msg = "Invalid response. Error Code:"+result.get("error_code")
                    +"; Error Message: "+result.get("error_message");
            Logger.getLogger(RippledServer.class.getName()).log(Level.WARNING, msg);
        }
        return ret;
    }
}
