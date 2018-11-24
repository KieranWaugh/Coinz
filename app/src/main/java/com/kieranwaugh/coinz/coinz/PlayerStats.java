package com.kieranwaugh.coinz.coinz;

import java.io.Serializable;
import java.sql.Time;

public class PlayerStats implements Serializable{

    private double distance;
    private int quids;
    private int dolrs;
    private int shils;

    public double getDistance() {
        return distance;
    }

    public int getQuids() {
        return quids;
    }

    public int getDolrs() {
        return dolrs;
    }

    public int getShils() {
        return shils;
    }

    public int getPenys() {
        return penys;
    }

    private int penys;
    //private Time time;

    public PlayerStats(){

    }

    public PlayerStats(double distance, int quids, int dolrs, int shils, int penys){
        this.distance = distance;
        this.dolrs = dolrs;
        this.penys = penys;
        this.shils = shils;
        this.quids = quids;
    }


}
