/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.uche.rippled;

import com.uche.rippleassessment.CalculateStats;
import com.uche.rippleassessment.Orchestrator;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
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

    private static final String SERVER_URL = Orchestrator.getProps()
            .getProperty("server.url", "http://s1.ripple.com:51234");

    

    /**
     * Returns a HttpClientConnector instance
     * @return 
     */
    public HttpClientConnector getHttpClientConnector(){
        if(httpConn == null)
            httpConn = new HttpClientConnector();
        return httpConn;
    }
    
            
    /**
     * Creates a HTTP POST request and sets its headers and body
     * @return a HTTP post instance
     * @throws UnsupportedEncodingException 
     */
    public HttpPost createRequest() throws UnsupportedEncodingException {
        HttpPost post = new HttpPost(SERVER_URL);
        post.addHeader(HTTP.CONTENT_TYPE, "application/json");
        post.addHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_KEEP_ALIVE);
        post.setEntity(new StringEntity(createServerInfoBody())); 
        
        return post;
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
     * Parse a properly formatted JSON response and extract the <i>time</i> and <i>validated_ledger.seq</i> elements
     * Supplies data to algorithm that calculates Min, Max and Average time for ledger validation
     * @param json A JSON formatted string to parse
     * @return a delimited string of time and sequence
     * @throws ParseException Thrown if unable to parse JSON payload
     */
    public String parseServerInfoResponse(String json) throws ParseException, java.text.ParseException {
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
                
                //Enhancement to calculate min, max and average time
                Orchestrator.getInstance().calcTimeStats(d, Long.parseLong(seq));
            }
        }else{
            String msg = "Invalid response. Error Code:"+result.get("error_code")
                    +"; Error Message: "+result.get("error_message");
            Logger.getLogger(RippledServer.class.getName()).log(Level.WARNING, msg);
        }
        return ret;
    }
}
