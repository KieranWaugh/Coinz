package com.kieranwaugh.coinz.coinz;

public class User {

    private String email;
    private String name;
    private int iconID;
    private int radius;
    private int multi;

    public User(){

    }

    public User(String email, String name, int iconID, int radius, int multi){
        this.email = email;
        this.name = name;
        this.iconID = iconID;
        this.radius = radius;
        this.multi = multi;
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
}
