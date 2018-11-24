package com.kieranwaugh.coinz.coinz;

public class User {

    private String email;
    private String name;



    private String iconID;

    public User(){

    }

    public User(String email, String name, String iconID){
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

    public String getIconID() {
        return iconID;
    }


}
