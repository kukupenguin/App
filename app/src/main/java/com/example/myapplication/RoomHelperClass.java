package com.example.myapplication;

public class RoomHelperClass {

    String rName, password;


    public RoomHelperClass() {

    }

    public RoomHelperClass(String rName, String password) {
        this.rName = rName;
        this.password = password;
    }

    public String getrName() {
        return rName;
    }

    public void setrName(String rName) {
        this.rName = rName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
