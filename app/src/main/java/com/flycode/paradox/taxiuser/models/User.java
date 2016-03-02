package com.flycode.paradox.taxiuser.models;

import java.util.Date;

/**
 * Created by victor on 12/25/15.
 */
public class User {

    private String id;
    private String role;
    private String username;
    private String name;
    private String sex;
    private String email;
    private String status;
    private int balance;
    private int carNumber;

    public User(String id, String role, String username, String name, String sex, String email, Date dateOfBirth, String status,
                int balance, int carNumber) {
        this.id = id;
        this.role = role;
        this.username = username;
        this.name = name;
        this.sex = sex;
        this.email = email;
        this.status = status;
        this.balance = balance;
        this.carNumber = carNumber;
    }

    public String getId() {
        return id;
    }

    public String getRole() {
        return role;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getSex() {
        return sex;
    }

    public String getStatus() {
        return status;
    }

    public int getBalance() {
        return balance;
    }

    public int getCarNumber() {
        return carNumber;
    }
}
