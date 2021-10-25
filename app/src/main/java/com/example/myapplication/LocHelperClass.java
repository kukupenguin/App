package com.example.myapplication;

public class LocHelperClass {

    double lat, lng;


    public LocHelperClass() {

    }

    public LocHelperClass(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public double getlat() {
        return lat;
    }

    public void setlat(double lat) {
        this.lat = lat;
    }

    public double getlng() {
        return lng;
    }

    public void setlng(double lng) {
        this.lng = lng;
    }
}
