/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.uche.rippleassessment;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Drey
 */
public class CalculateStats {
    
    private long mSecPrev = 0;
    private long seqPrev = 0; 
    private long seqDiffMin;
    private long seqDiffMax;
    private float timeMin;
    private float timeMax;
    private float timeAvg;
    Map<Long, Long> timeSequenceMap;
    
    
    public CalculateStats(){}
    
    
    public float getTimeMin(){
        return timeMin;
    }
    
    public float getTimeMax(){
        return timeMax;
    }
    
    public float getTimeAvg(){
        return timeAvg;
    }
    
    
    /**
     * Derive how many new ledger sequences occur within a polling interval
     * Use that number to calculate an estimate min and max time in each polling interval.
     * -- Polling interval with most sequences infers a minimum time per ledger validation
     * -- Polling interval with least sequences infers a maximum time per ledger validation
     * @param d time the server was polled for data
     * @param seq the ledger index returned for that poll
     */
    public void calcMinMaxTime(Date d, long seq) {
        if(timeSequenceMap == null)
            timeSequenceMap = new LinkedHashMap<>();
        timeSequenceMap.put(d.getTime(), seq);
        
        if(mSecPrev == 0 && seqPrev == 0){//First in the sequence
//            mSecPrev = d.getTime();
//            seqPrev = seq;
            seqDiffMin = 0;
            seqDiffMax = seq;
        }else{
             ///////////////////////////////////////////////////////////////////
             //* <seqDiff> is the difference between current and previous sequence. Initially it is zero.
             //* Subsequently, if last <seqDiff> is less than the new difference between current and previous sequence
             //* then we had more ledgers validated within the polling interval. 
             //* This is used to calculate Min time for new validation
             ///////////////////////////////////////////////////////////////////
            
            //We have more ledgers within the time period; min time per ledger
            if(seqDiffMin < (seq - seqPrev) ) { 
                seqDiffMin = (seq - seqPrev);
                timeMin = (d.getTime() - mSecPrev)/seqDiffMin;
            }
            //we have fewer ledgers within the time period; max time per ledger
            if(seqDiffMax > (seq - seqPrev)){
                seqDiffMax = (seq - seqPrev);
                timeMax = (d.getTime() - mSecPrev)/seqDiffMax;
            }
            System.out.println("\t Duration: "+(d.getTime() - mSecPrev)+", Sequence: "+(seq - seqPrev));
            System.out.println("\t Min Time(ms): "+timeMin+", Max Time(ms): "+timeMax);
        }
        mSecPrev = d.getTime();
        seqPrev = seq;
    }
    
    /**
     * Iterate through the map of time and sequence for each data point to get the 
     * first and last entries and use to calculate average time
     */
    public void calcAverageTime(){
        if(timeSequenceMap != null && !timeSequenceMap.isEmpty()){
            Iterator<Map.Entry<Long, Long>> itr1 = timeSequenceMap.entrySet().iterator(); 
            Map.Entry first = itr1.next();
            Map.Entry last = first;
            while (itr1.hasNext()) { 
                last = itr1.next(); 
            }
            long totalSeq = (Long)last.getValue() - (Long)first.getValue();
            long totalTime = (Long)last.getKey() - (Long)first.getKey();
            timeAvg = (totalSeq > 0) ? totalTime/totalSeq : 0;
            System.out.println("\t Average Time(ms): "+timeAvg);             
        }
    }
}
