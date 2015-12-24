package com.flycode.paradox.taxiuser.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by victor on 12/11/15.
 */
public class Order implements Parcelable {
    private String id;
    private String status;
    private String startingPointName;
    private String endingPointName;
    private Date orderTime;
    private int moneyAmount;
    private String paymentType;
    private String orderType;

    public Order(Parcel in) {
        id = in.readString();
        status = in.readString();
        startingPointName = in.readString();
        endingPointName = in.readString();
        orderTime = new Date(in.readLong());
        moneyAmount = in.readInt();
        paymentType = in.readString();
        orderType = in.readString();

    }

    public Order(String id, String status, String startingPointName, String endingPointName, Date orderTime,
                 int moneyAmount, String paymentType, String orderType) {
        this.id = id;
        this.status = status;
        this.startingPointName = startingPointName;
        this.endingPointName = endingPointName;
        this.orderTime = orderTime;
        this.moneyAmount = moneyAmount;
        this.paymentType = paymentType;
        this.orderType = orderType;
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String getStartingPointName() {
        return startingPointName;
    }

    public String getEndingPointName() {
        return endingPointName;
    }

    public Date getOrderTime() {
        return orderTime;
    }

    public int getMoneyAmount() {
        return moneyAmount;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public String getOrderType() {
        return orderType;
    }

    public static Creator getCREATOR() {
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(status);
        dest.writeString(startingPointName);
        dest.writeString(endingPointName);
        dest.writeLong(orderTime.getTime());
        dest.writeInt(moneyAmount);
        dest.writeString(paymentType);
        dest.writeString(orderType);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        public Order[] newArray(int size) {
            return new Order[size];
        }
    };
}
