package com.flycode.paradox.taxiuser.models;

import java.util.Date;

/**
 * Created by victor on 12/12/15.
 */
public class Transaction {
    private String id;
    private String recipientUsername;
    private String recipientRole;
    private String senderUsername;
    private String senderRole;
    private String description;
    private String paymentType;
    private String orderId;
    private double moneyAmount;
    private Date date;

    public Transaction(String id, String recipientUsername, String recipientRole, String senderUsername, String senderRole,
                       Date date, String description, String paymentType, double moneyAmount, String orderId) {
        this.id = id;
        this.recipientUsername = recipientUsername;
        this.recipientRole = recipientRole;
        this.senderUsername = senderUsername;
        this.senderRole = senderRole;
        this.description = description;
        this.paymentType = paymentType;
        this.orderId = orderId;
        this.moneyAmount = moneyAmount;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public String getRecipientRole() {
        return recipientRole;
    }

    public String getSenderRole() {
        return senderRole;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getRecipientUsername() {
        return recipientUsername;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public Date getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public double getMoneyAmount() {
        return moneyAmount;
    }
}



