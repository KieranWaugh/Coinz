package com.kieranwaugh.coinz.coinz;

public class User {

    private String email;
    private String name;
    private int iconID;

    public User(){

    }

    public User(String email, String name, int iconID){
        this.email = email;
        this.name = name;
        this.iconID = iconID;
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


}
