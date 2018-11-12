package com.kieranwaugh.coinz.coinz;


import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class coin {


    public double value;
    public String currency;
    public double lng;
    public double lat;
    private boolean banked;
    private HashMap<String, coin> coins= new HashMap<>(); //all coins with an identifier


    public coin(){

    }

    public coin(String id, double value, String currency, double lng, double lat, boolean banked){
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

    public String id;

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

    public HashMap<String, coin> getCoins() {
        return coins;
    }

    public boolean isBanked() {
        return banked;
    }

    public String getString(){
        return id + " - " + currency;
    }




}
