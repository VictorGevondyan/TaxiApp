package com.flycode.paradox.taxiuser.models;

import java.util.Date;

/**
 * Created by victor on 12/12/15.
 */
public class Transaction {
    private String recipientUsername;
    private String recipientRole;
    private String senderUsername;
    private String senderRole;
    private String description;
    private String paymentType;
    private String moneyAmount;
    private Date date;

    public Transaction(String recipientUsername, String recipientRole, String senderUsername, String senderRole,
                       Date date, String description, String paymentType, String moneyAmount) {
        this.recipientUsername = recipientUsername;
        this.recipientRole = recipientRole;
        this.senderUsername = senderUsername;
        this.senderRole = senderRole;
        this.description = description;
        this.paymentType = paymentType;
        this.moneyAmount = moneyAmount;
        this.date = date;
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

    public String getMoneyAmount() {
        return moneyAmount;
    }
}



