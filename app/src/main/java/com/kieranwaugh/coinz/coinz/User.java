package com.kieranwaugh.coinz.coinz;

@SuppressWarnings({"unused", "WeakerAccess"})
public class User { // user object

    private String email;
    private String name;
    private int iconID;
    private int radius;
    private int multi;
    private double gold;

    public User(){

    }

    public User(String email, String name, int iconID, int radius, int multi, double gold){ // stores all data needed for a new user of the app
        this.email = email;
        this.name = name;
        this.iconID = iconID;
        this.radius = radius;
        this.multi = multi;
        this.gold = gold;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public int getIconID() {
        return iconID;
    }

    public int getRadius() {
        return radius;
    }

    public int getMulti() {
        return multi;
    }

    public double getGold() {
        return gold;
    }
}
