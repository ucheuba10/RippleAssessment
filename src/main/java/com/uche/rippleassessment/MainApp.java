/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.uche.rippleassessment;

import com.uche.rippled.RippledServer;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Drey
 */
public class MainApp {
    
    private static Properties properties;
    
    private MainApp() {
        properties = new Properties();
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream("application.properties"));
        } catch (IOException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
        }
    }
    
    public static MainApp getInstance() {
        return MainAppHolder.INSTANCE;
    }
    
    private static class MainAppHolder {

        private static final MainApp INSTANCE = new MainApp();
    }
    
    public static Properties getProps(){        
        return properties;
    }
    
    
    
    public static void main(String[] args) {
        try {
            getInstance();
            RippledServer server = new RippledServer();
            server.pollServerInfo();
        } catch (IOException | InterruptedException | ParseException |java.text.ParseException e) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, e.getMessage(), e);
        }
    }  
}
