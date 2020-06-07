/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.uche.rippleassessment;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Drey
 */
public class CalculateStats {
    
    private long mSecPrev = 0;
    private long seqPrev = 0; 
//    private long seqDiff=0;
    private long seqDiffMin;
    private long seqDiffMax;
    private long timeMax;
    private long timeMin;
    
//    private long mSecCurrent;          
//    private long mSecDiff;
    
    public void calsStats(Date d, long seq){
        Map<Integer, Integer> map = new LinkedHashMap<>();
        
        
        if(mSecPrev == 0 && seqPrev == 0){//First in the sequence
            mSecPrev = d.getTime();
            seqPrev = seq;
            seqDiffMin = seq;
            seqDiffMax = 0;
        }
        
//        mSecDiff = (d.getTime() - mSecPrev);
//        seqDiff = s - seqPrev;
        

        /**
         * <seqDiff> is the difference between current and previous sequence. Initially it is zero.
         * Subsequently, if last <seqDiff> is less than the new difference between current and previous sequence
         * then we had more ledgers validated within the polling interval. 
         * This is used to calculate Min time for new validation
         */
        if(seqDiffMin < (seq - seqPrev) ) { //We have more ledgers in 5 sec; min time
            seqDiffMin = (seq - seqPrev);
            timeMin = (d.getTime() - mSecPrev)/seqDiffMin;
        }
        if(seqDiffMax > (seq - seqPrev)){//we have fewer ledgers in 5 secs; max time
            seqDiffMax = (seq - seqPrev);
            timeMax = (d.getTime() - mSecPrev)/seqDiffMax;
        }
        
        System.out.println("\t Duration: "+(d.getTime() - mSecPrev)+", Sequence: "+(seq - seqPrev));
        System.out.println("\t Min Time: "+timeMin+", Max Time: "+timeMax);
        
        mSecPrev = d.getTime();
        seqPrev = seq;
        
        
        //Average is total sequences divided by time span
        //Min is 2 sec
        //Max is 4 sec
    }
}
