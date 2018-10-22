package com.kieranwaugh.coinz.coinz;


import java.util.HashMap;

public class coin {

    public String id;
    public double value;
    public String currency;
    public double lng;
    public double lat;
    public HashMap<String, coin> coins= new HashMap<>(); //all coins with an identifier

    public coin(String id, double value, String currency, double lng, double lat){
        this.id = id;
        this.value = value;
        this.currency = currency;
        this.lng = lng;
        this.lat = lat;
    }
}
