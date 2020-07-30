package com.syc.go4lunch.model;
import com.google.android.gms.maps.model.LatLng;

public class RestaurantModel {

    private String address;
    private String name;
    private double rating;
    private LatLng latLng;
    //private String type;

    public RestaurantModel(  ) {  }

    public RestaurantModel(String address, String name, double rating, LatLng latLng ) {
        this.address = address;
        this.name = name;
        this.rating = rating;
        this.latLng = latLng;
        //this.type = type;
    }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public LatLng getLatLng() { return latLng; }
    public void setLatLng(LatLng latLng) { this.latLng = latLng; }

    //public String getType() { return type; }
    //public void setType(String type) { this.type = type; }
}
