package com.koren.homexpense.Classes;

import java.util.ArrayList;

public class User {

    private String userName = null;
    private String firstName = null;
    private String lastName = null;
    private ArrayList<String> paymentMethods = new ArrayList();
    private String preferredPaymentMethod = null;

    public User() {

    }

    public User(String userName,
                String firstName,
                String lastName,
                ArrayList<String> paymentMethods,
                String preferredPaymentMethod) {

        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.paymentMethods = paymentMethods;
        this.preferredPaymentMethod = preferredPaymentMethod;
    }

    public String getUserName(){return userName;}

    public void setUserName(String userName){this.userName=userName;}

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public ArrayList<String> getPaymentMethods() {
        return paymentMethods;
    }

    public void setPaymentMethods(ArrayList<String> paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    public String getPreferredPaymentMethod() {
        return preferredPaymentMethod;
    }

    public void setPreferredPaymentMethod(String preferredPaymentMethod) {
        this.preferredPaymentMethod = preferredPaymentMethod;
    }
}
