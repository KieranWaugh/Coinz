package com.kieranwaugh.coinz.coinz;


import android.util.Log;

import com.mapbox.mapboxsdk.annotations.Marker;

import java.io.Serializable;
import java.util.HashMap;

public class coin implements Serializable {

    public String id;
    public double value;
    public String currency;



    public double lng;
    public double lat;
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

//    public Marker getMarker() {
//        return marker;
//    }




}
