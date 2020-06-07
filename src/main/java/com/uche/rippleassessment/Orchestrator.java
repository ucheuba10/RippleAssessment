/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.uche.rippleassessment;

import com.uche.rippled.RippledServer;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Drey
 */
public class Orchestrator {
    
    private static Properties properties;
    private RippledServer rServer;    
    private static long pollingIntervalMs;
    private static int pollCount;
    
    
    
    private Orchestrator() {
        try {
            properties = new Properties();
            properties.load(getClass().getClassLoader().getResourceAsStream("application.properties"));
            pollingIntervalMs = Long.parseLong(properties.getProperty("server.polling_interval_ms", "2000"));
            pollCount = Integer.parseInt(properties.getProperty("server.poll_count", "10"));
            rServer = new RippledServer();  
        } catch (IOException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
        }
    }   

    
    
    /**
     * Fetch HTTP connector instance, create POST request then continuously poll Rippled server for data over a single connection.
     * As data is retrieved from the server, it will be sent to a file writer and written to file
     * @throws IOException
     * @throws InterruptedException
     * @throws ParseException
     * @throws java.text.ParseException 
     */
    public void pollServerInfo() throws IOException, InterruptedException, ParseException, java.text.ParseException {
        
        CloseableHttpClient httpClient = (CloseableHttpClient)rServer.getHttpClientConnector().createHttpClient();
        HttpPost post = rServer.createRequest();
        
        try {
            String result;
            int count = 0;
            while(count < pollCount){
                result = rServer.getHttpClientConnector().executeClient(httpClient, post);
                SequenceFileConnector.writeToFile(rServer.parseServerInfoResponse(result));
                Thread.sleep(pollingIntervalMs);
                count++;
            }
            Logger.getLogger(RippledServer.class.getName()).log(Level.INFO, "Polling completed");
        }finally{
            httpClient.close();
        }
    }
    
    /**
     * Call algorithm to calculate Min, Max and Average time for the ledger validation
     */
    private CalculateStats calculateStats = new CalculateStats();
    public void calcTimeStats(Date d, long seq){
        calculateStats.calcMinMaxTime(d, seq);
        calculateStats.calcAverageTime();
    }
    
    
    /**
     * Fetches properties object
     * @return 
     */
    public static Properties getProps(){        
        return properties;
    }
    
    
    public static Orchestrator getInstance() {
        return OrchestratorHolder.INSTANCE;
    }
    
    private static class OrchestratorHolder {

        private static final Orchestrator INSTANCE = new Orchestrator();
    }
    
    
    public static void main(String[] args) {
        try {
            getInstance().pollServerInfo();
        } catch (IOException | InterruptedException | ParseException | java.text.ParseException e) {
            Logger.getLogger(Orchestrator.class.getName()).log(Level.SEVERE, e.getMessage(), e);
        }
    }  
}
