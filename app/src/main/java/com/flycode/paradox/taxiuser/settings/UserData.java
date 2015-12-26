package com.flycode.paradox.taxiuser.settings;

import android.content.Context;
import android.content.SharedPreferences;

import com.flycode.paradox.taxiuser.factory.ModelFactory;

import java.util.Date;

/**
 * Created by victor on 12/25/15.
 */
public class UserData {

    private static UserData sharedData;

    private final String PREFERENCES_NAME = "DriverDataPreferences";

    private static final String ID = "_id";
    private static final String ROLE = "role";
    private static final String USERNAME = "username";
    private static final String NAME = "name";
    private static final String SEX = "sex";
    private static final String EMAIL = "email";
    private static final String DATE_OF_BIRTH = "dateOfBirth";
    private static final String STATUS = "status";
    private static final String BALANCE = "balance";

    private final SharedPreferences.Editor dataEditor;

    private String id;
    private String role;
    private String username;
    private String name;
    private String sex;
    private String email;
    private Date dateOfBirth;
    private String status;
    private int balance;

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
        role = dataPreferences.getString(ROLE, "");
        username = dataPreferences.getString(USERNAME, "");
        name = dataPreferences.getString(NAME,"");
        sex = dataPreferences.getString(SEX, "");
        email = dataPreferences.getString(EMAIL, "");
        ModelFactory.dateFromString(dataPreferences.getString(SEX, ""), dateOfBirth);
        status = dataPreferences.getString(STATUS, "");
        balance = dataPreferences.getInt(BALANCE, 0);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        dataEditor.putString(id, ID).commit();

    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
        dataEditor.putString(ROLE, role).commit();
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

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
        dataEditor.putString(SEX, sex).commit();
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        dataEditor.putString(DATE_OF_BIRTH, dateOfBirth.toString()).commit();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        dataEditor.putString(STATUS, status).commit();
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
}


















