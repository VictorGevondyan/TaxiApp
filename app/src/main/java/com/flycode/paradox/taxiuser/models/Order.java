package com.flycode.paradox.taxiuser.models;

import android.location.Location;
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
    private String description;

    private Date finishTime;
    private Date orderTime;
    private Date userPickupTime;

    private int moneyAmount;
    private String paymentType;

    private String username;

    private Location startingPointGeo;
    private Location endingPointGeo;

    public Order(String id, String status, Date orderTime, String startingPointName, String endingPointName, Date finishTime,
                 String description, String paymentType, int moneyAmount, String username, Date userPickupTime,
                 Location startingPointGeo, Location endingPointGeo) {
        this.id = id;
        this.status = status;
        this.orderTime = orderTime;
        this.startingPointName = startingPointName;
        this.endingPointName = endingPointName;
        this.finishTime = finishTime;
        this.description = description;

        this.paymentType = paymentType;
        this.moneyAmount = moneyAmount;

        this.username = username;
        this.userPickupTime = userPickupTime;

        this.startingPointGeo = startingPointGeo;
        this.endingPointGeo = endingPointGeo;
    }

    public Order(Parcel in) {
        id = in.readString();
        status = in.readString();
        startingPointName = in.readString();
        endingPointName = in.readString();
        description = in.readString();
        orderTime = new Date(in.readLong());

        long finishTimeMillis = in.readLong();

        if (finishTimeMillis > 0) {
            finishTime = new Date(finishTimeMillis);
        }

        moneyAmount = in.readInt();
        paymentType = in.readString();

        username = in.readString();

        long userPickupTimeMillis = in.readLong();

        if (userPickupTimeMillis > 0) {
            userPickupTime = new Date(userPickupTimeMillis);
        }

        startingPointGeo = in.readParcelable(Location.class.getClassLoader());

        Location location = in.readParcelable(Location.class.getClassLoader());

        if (!location.getProvider().equals("Null Provider")) {
            endingPointGeo = location;
        }
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public Date getOrderTime() {
        return orderTime;
    }

    public String getStartingPointName() {
        return startingPointName;
    }

    public String getEndingPointName() {
        return endingPointName;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public String getDescription() {
        return description;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public int getMoneyAmount() {
        return moneyAmount;
    }

    public String getUsername() {
        return username;
    }

    public Date getUserPickupTime() {
        return userPickupTime;
    }

    public Location getStartingPointGeo() {
        return startingPointGeo;
    }

    public Location getEndingPointGeo() {
        return endingPointGeo;
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
        dest.writeString(description);

        dest.writeLong(orderTime.getTime());
        dest.writeLong(finishTime == null ? 0 : finishTime.getTime());

        dest.writeInt(moneyAmount);
        dest.writeString(paymentType);

        dest.writeString(username);
        dest.writeLong(userPickupTime == null ? 0 : userPickupTime.getTime());

        dest.writeParcelable(startingPointGeo, flags);
        dest.writeParcelable(endingPointGeo == null ? new Location("Null Provider") : endingPointGeo, flags);
    }

    public static final Creator CREATOR = new Creator() {
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    public void setStatus(String status) {
        this.status = status;
    }
}
