/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.uche.rippleassessment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Drey
 */
public class SequenceFileConnector {
    
    private static final String OUTPUT_FILE = MainApp.getProps().getProperty("file.output_file", "src/main/resources/output.csv");
    
    public SequenceFileConnector(){}
    
    
    /**
     * 
     * @param line 
     */
    public static void writeToFile(String line){
        BufferedWriter writer = null;
        try {            
            File outputFile = Paths.get(OUTPUT_FILE).toFile();            
            writer = new BufferedWriter(new FileWriter(outputFile, true)); 
            writer.append(line+"\n");
        } catch (IOException ex) {
            Logger.getLogger(SequenceFileConnector.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            try {
                if(writer != null)
                    writer.close();
            } catch (IOException ex) {
                Logger.getLogger(SequenceFileConnector.class.getName()).log(Level.SEVERE, null, ex);
            }
       }
    }
    
}