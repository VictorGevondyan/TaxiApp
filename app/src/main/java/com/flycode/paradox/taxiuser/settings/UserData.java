package com.flycode.paradox.taxiuser.settings;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by victor on 12/25/15.
 */
public class UserData {

    private static UserData sharedData;

    private final String PREFERENCES_NAME = "DriverDataPreferences";

    private static final String ID = "_id";
    private static final String USERNAME = "username";
    private static final String NAME = "name";
    private static final String EMAIL = "email";
    private static final String BALANCE = "balance";
    private static final String ORDERS_COUNT = "onlyCount";


    private final SharedPreferences.Editor dataEditor;

    private String id;
    private String username;
    private String name;
    private String email;
    private int balance;
    private int orderCount;

    public static UserData sharedData(Context context) {
        if (sharedData == null) {
            sharedData = new UserData(context);
        }

        return sharedData;
    }

    private UserData(Context context) {
        SharedPreferences dataPreferences = context.getSharedPreferences(PREFERENCES_NAME, 0);
        dataEditor = dataPreferences.edit();

        id = dataPreferences.getString(ID, "");
        username = dataPreferences.getString(USERNAME, "");
        name = dataPreferences.getString(NAME, "");
        email = dataPreferences.getString(EMAIL, "");
        balance = dataPreferences.getInt(BALANCE, 0);
        orderCount = dataPreferences.getInt(ORDERS_COUNT, 0);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        dataEditor.putString(id, ID).commit();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        dataEditor.putString(USERNAME, username).commit();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        dataEditor.putString(NAME, name).commit();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        dataEditor.putString(EMAIL, email).commit();
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
        dataEditor.putInt(BALANCE, balance).commit();
    }

    public int getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(int orderCount) {
        this.orderCount = orderCount;
        dataEditor.putInt(ORDERS_COUNT, orderCount).commit();
    }
}


















