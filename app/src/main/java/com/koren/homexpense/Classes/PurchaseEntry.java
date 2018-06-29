package com.koren.homexpense.Classes;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Elad on 27/01/2018.
 */

public class PurchaseEntry {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    private long id;
    private String paymentMethod;
    private String paymentType;
    private String store;
    private Double price;
    private final String date = dateFormat.format(Calendar.getInstance().getTime());


    public PurchaseEntry(String paymentMethod, String paymentType, String store, Double price) {
        this.id = Calendar.getInstance().getTimeInMillis();
        this.paymentMethod = paymentMethod;
        this.paymentType = paymentType;
        this.store = store;
        this.price = price;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStore() {
        return this.store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDate() {
        return this.date;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getPaymentType() {
        return paymentType;
    }

}
