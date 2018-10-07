package com.koren.homexpense.Classes;

import com.google.android.gms.maps.model.LatLng;

public class ExpensePlace {

    private String name = null;
    private String address = null;
    private String expenseType = null;
    private LatLng coordinates = null;

    public ExpensePlace(){
    }

    public ExpensePlace(String name, String address, String expenseType, LatLng coordinates) {

        this.name = name;
        this.address = address;
        this.expenseType = expenseType;
        this.coordinates = coordinates;
    }

    public String getPlaceName() {
        return name;
    }

    public void setPlaceName(String placeName) {
        this.name = placeName;
    }

    public String getPlaceAddress() {
        return address;
    }

    public void setPlaceAddress(String placeAddress) {
        this.address = placeAddress;
    }

    public String getExpenseType() {
        return expenseType;
    }

    public void setExpenseType(String expenseType) {
        this.expenseType = expenseType;
    }

    public LatLng getPlaceCoordinates() {
        return coordinates;
    }

    public void setPlaceCoordinates(LatLng coordinates) {
        this.coordinates = coordinates;
    }

}
