package com.flycode.paradox.taxiuser.models;

import java.util.Date;

/**
 * Created by victor on 12/11/15.
 */
public class Order /*implements Parcelable */{
    private String id;
    private String status;
    private String startingPointName;
    private Date orderTime;

//    public Order(Parcel in) {
//        id = in.readString();
//        status = in.readString();
//        startingPointName = in.readString();
//        endingPointName = in.readString();
//        description = in.readString();
//        orderTime = new Date(in.readLong());
//
//        long finishTimeMillis = in.readLong();
//
//        if (finishTimeMillis > 0) {
//            finishTime = new Date(finishTimeMillis);
//        }
//
//        moneyAmount = in.readInt();
//        paymentType = in.readString();
//
//        username = in.readString();
//
//        long userPickupTimeMillis = in.readLong();
//
//        if (userPickupTimeMillis > 0) {
//            userPickupTime = new Date(userPickupTimeMillis);
//        }
//
//        startingPointGeo = in.readParcelable(Location.class.getClassLoader());
//
//        Location location = in.readParcelable(Location.class.getClassLoader());
//
//        if (!location.getProvider().equals("Null Provider")) {
//            endingPointGeo = location;
//        }
//    }


    public Order(String id, String status, String startingPointName, Date orderTime) {
        this.id = id;
        this.status = status;
        this.startingPointName = startingPointName;
        this.orderTime = orderTime;
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

    public Date getOrderTime() {
        return orderTime;
    }

//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(id);
//        dest.writeString(status);
//        dest.writeString(startingPointName);
//        dest.writeString(endingPointName);
//        dest.writeString(description);
//
//        dest.writeLong(orderTime.getTime());
//        dest.writeLong(finishTime == null ? 0 : finishTime.getTime());
//
//        dest.writeInt(moneyAmount);
//        dest.writeString(paymentType);
//
//        dest.writeString(username);
//        dest.writeLong(userPickupTime == null ? 0 : userPickupTime.getTime());
//
//        dest.writeParcelable(startingPointGeo, flags);
//        dest.writeParcelable(endingPointGeo == null ? new Location("Null Provider") : endingPointGeo, flags);
//    }
//
//    public static final Creator CREATOR = new Creator() {
//        public Order createFromParcel(Parcel in) {
//            return new Order(in);
//        }
//
//        public Order[] newArray(int size) {
//            return new Order[size];
//        }
//    };
}