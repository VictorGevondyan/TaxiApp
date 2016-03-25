package com.flycode.paradox.taxiuser.models;

import android.content.Context;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.flycode.paradox.taxiuser.constants.OrderStatusConstants;
import com.flycode.paradox.taxiuser.database.Database;
import com.flycode.paradox.taxiuser.settings.UserData;

import java.util.ArrayList;
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
    private Date updatedTime;

    private int moneyAmount;
    private int bonus;
    private double distance;
    private String paymentType;

    private String username;

    private Location startingPointGeo;
    private Location endingPointGeo;

    private Driver driver;
    private String carCategory;

    private int feedbackRating;
    private boolean hasFeedback;

    private boolean cashOnly;

    public Order(String id, String status, String description, String username,
                 Date orderTime, Date userPickupTime, Date finishTime, Date update,
                 String startingPointName, String endingPointName,
                 Location startingPointGeo, Location endingPointGeo,
                 String paymentType, int moneyAmount, int bonus, double distance,
                 Driver driver, String carCategory, int feedbackRating,
                 boolean hasFeedback, boolean cashOnly) {
        this.id = id;
        this.status = status;
        this.description = description;
        this.username = username;

        this.orderTime = orderTime;
        this.userPickupTime = userPickupTime;
        this.finishTime = finishTime;
        this.updatedTime = update;

        this.startingPointName = startingPointName;
        this.endingPointName = endingPointName;

        this.startingPointGeo = startingPointGeo;
        this.endingPointGeo = endingPointGeo;

        this.paymentType = paymentType;
        this.moneyAmount = moneyAmount;
        this.bonus = bonus;
        this.distance = distance;

        this.driver = driver;
        this.carCategory = carCategory;

        this.feedbackRating = feedbackRating;
        this.hasFeedback = hasFeedback;

        this.cashOnly = cashOnly;
    }

    public Order(Parcel in) {
        id = in.readString();
        status = in.readString();
        startingPointName = in.readString();
        endingPointName = in.readString();
        description = in.readString();
        updatedTime = new Date(in.readLong());
        orderTime = new Date(in.readLong());

        long finishTimeMillis = in.readLong();

        if (finishTimeMillis > 0) {
            finishTime = new Date(finishTimeMillis);
        }

        moneyAmount = in.readInt();
        bonus = in.readInt();
        paymentType = in.readString();
        distance = in.readDouble();

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

        if (!status.equals(OrderStatusConstants.NOT_TAKEN)
                && !status.equals(OrderStatusConstants.CANCELED)) {
            driver = in.readParcelable(Driver.class.getClassLoader());
        }

        carCategory = in.readString();

        feedbackRating = in.readInt();

        hasFeedback = in.readInt() == 1;
        cashOnly = in.readInt() == 1;
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

    public String getPaymentType() {
        return paymentType;
    }

    public int getMoneyAmount() {
        return moneyAmount;
    }

    public Date getUserPickupTime() {
        return userPickupTime;
    }

    public Location getStartingPointGeo() {
        return startingPointGeo;
    }

    public double getDistance() {
        return distance;
    }

    public String getDescription() {
        return description;
    }

    public String getUsername() {
        return username;
    }

    public Location getEndingPointGeo() {
        return endingPointGeo;
    }

    public Driver getDriver() {
        return driver;
    }

    public String getCarCategory(){
        return carCategory;
    }

    public int getBonus() {
        return bonus;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public int getFeedbackRating() {
        return feedbackRating;
    }

    public boolean getHasFeedback() {
        return hasFeedback;
    }

    public boolean getCashOnly() {
        return cashOnly;
    }

    public void setHasFeedback(boolean hasFeedback) {
        this.hasFeedback = hasFeedback;
    }

    public void setFeedbackRating(int feedbackRating) {
        this.feedbackRating = feedbackRating;
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

        dest.writeLong(updatedTime.getTime());
        dest.writeLong(orderTime.getTime());
        dest.writeLong(finishTime == null ? 0 : finishTime.getTime());

        dest.writeInt(moneyAmount);
        dest.writeInt(bonus);
        dest.writeString(paymentType);
        dest.writeDouble(distance);

        dest.writeString(username);
        dest.writeLong(userPickupTime == null ? 0 : userPickupTime.getTime());

        dest.writeParcelable(startingPointGeo, flags);
        dest.writeParcelable(endingPointGeo == null ? new Location("Null Provider") : endingPointGeo, flags);

        if (!status.equals(OrderStatusConstants.NOT_TAKEN)
                && !status.equals(OrderStatusConstants.CANCELED)) {
            dest.writeParcelable(driver, flags);
        }

        dest.writeString(carCategory);
        dest.writeInt(feedbackRating);
        dest.writeInt(hasFeedback ? 1 : 0);
        dest.writeInt(cashOnly ? 1 : 0);
    }

    public static final Creator CREATOR = new Creator() {
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    public static boolean isRealUpdate(Order updatedOrder, Context context) {
        ArrayList<Order> orders = Database.sharedDatabase(context).getOrders(0, 1, new String[0], updatedOrder.getId(), UserData.sharedData(context).getUsername());

        if (!orders.isEmpty()) {
            return updatedOrder.getUpdatedTime().after(orders.get(0).getUpdatedTime())
                    || updatedOrder.getUpdatedTime().equals(orders.get(0).getUpdatedTime());
        }

        return true;
    }

    public static boolean isOldVersion(Order updatedOrder, Context context) {
        ArrayList<Order> orders = Database.sharedDatabase(context).getOrders(0, 1, new String[0], updatedOrder.getId(), UserData.sharedData(context).getUsername());

        if (!orders.isEmpty()) {
            return updatedOrder.getUpdatedTime().before(orders.get(0).getUpdatedTime());
        }

        return false;
    }
}
