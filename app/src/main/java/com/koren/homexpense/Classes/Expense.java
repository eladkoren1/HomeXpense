package com.koren.homexpense.Classes;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Elad on 27/01/2018.
 */

public class Expense {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    private long dateId;
    private String expenseMethod;
    private String expenseType;
    private String expensePlace;
    private Double amount;
    private final String date = dateFormat.format(Calendar.getInstance().getTime());


    public Expense(String expenseMethod, String expenseType, String expensePlace, Double amount) {
        this.dateId = Calendar.getInstance().getTimeInMillis();
        this.expenseMethod = expenseMethod;
        this.expenseType = expenseType;
        this.expensePlace = expensePlace;
        this.amount = amount;

    }

    public long getDateId() {
        return dateId;
    }

    public void  setDateId(long dateId) {
        this.dateId = dateId;
    }

    public String getStore() {
        return this.expensePlace;
    }

    public void setStore(String store) {
        this.expensePlace = store;
    }

    public Double getPrice() {
        return amount;
    }

    public void setPrice(Double price) {
        this.amount = price;
    }

    public String getDate() {
        return this.date;
    }

    public String getPaymentMethod() {
        return expenseMethod;
    }

    public String getPaymentType() {
        return expenseType;
    }

}
