package com.koren.homexpense.Classes;

import com.google.android.gms.maps.model.LatLng;

public class ExpensePlace {

    private String placeName = null;
    private String placeAddress = null;
    private String expenseType = null;
    private LatLng placeCoordinates = null;

    public ExpensePlace(String placeName, String placeAddress, String expenseType, LatLng placeCoordinates) {

        this.placeName = placeName;
        this.placeAddress = placeAddress;
        this.expenseType = expenseType;
        this.placeCoordinates = placeCoordinates;
    }

    public String getPlaceName() {
        return placeName;
    }

    private void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPlaceAddress() {
        return placeAddress;
    }

    private void setPlaceAddress(String placeAddress) {
        this.placeAddress = placeAddress;
    }

    public String getExpenseType() {
        return expenseType;
    }

    private void setExpenseType(String expenseType) {
        this.expenseType = expenseType;
    }

    public LatLng getPlaceCoordinates() {
        return placeCoordinates;
    }

    private void setPlaceCoordinates(LatLng placeCoordinates) {
        this.placeCoordinates = placeCoordinates;
    }

}
