package com.kieranwaugh.coinz.coinz;


import android.util.Log;
import java.io.Serializable;


public class coin implements Serializable { // coin object implement serializable to allow for Intent.putExtra()

    private String id;
    private double value;
    private String currency;
    private double lng;
    private double lat;
    private boolean banked;

    public coin(){

    }


    public coin(String id, double value, String currency, double lng, double lat, boolean banked ){
        this.id = id;
        this.value = value;
        this.currency = currency;
        this.lng = lng;
        this.lat = lat;
        this.banked = banked;

        Log.d("Coin", "[coin] Created coin with id " + id);
    }

    public String getId() {
        return id;
    }



    public double getValue() {
        return value;
    }

    public String getCurrency() {
        return currency;
    }

    public double getLng() {
        return lng;
    }

    public double getLat() {
        return lat;
    }

    public boolean isBanked() {
        return banked;
    }

    public void setId(String id) {
        this.id = id;
    }


}
