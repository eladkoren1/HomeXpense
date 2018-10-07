package com.koren.homexpense.Classes;

import java.util.ArrayList;
import java.util.HashMap;

public class User {

    private String firstName = null;
    private String lastName = null;
    private ArrayList<String> expenseMethods = new ArrayList();
    private HashMap<String,Expense> expenses = new HashMap<>();
    private String preferredExpenseMethod = null;

    public User() {
        expenseMethods = new ArrayList<>();
    }

    public User(
                String firstName,
                String lastName,
                ArrayList<String> paymentMethods,
                String preferredExpenseMethod) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.expenseMethods = paymentMethods;
        this.preferredExpenseMethod = preferredExpenseMethod;
    }

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

    public ArrayList<String> getExpenseMethods() {
        return expenseMethods;
    }

    public void addExpenseMethod(String paymentMethod) {
        this.expenseMethods.add(paymentMethod);
    }

    public String getPreferredExpenseMethod() {
        return preferredExpenseMethod;
    }

    public void setPreferredExpenseMethod(String preferredExpenseMethod) {

        this.preferredExpenseMethod = preferredExpenseMethod;

    }
}
