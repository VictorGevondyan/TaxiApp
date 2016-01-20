package com.flycode.paradox.taxiuser.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by anhaytananun on 16.01.16.
 */
public class Driver implements Parcelable {
    private String carNumber;
    private String username;

    public Driver(String carNumber, String username) {
        this.carNumber = carNumber;
        this.username = username;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public String getUsername() {
        return username;
    }

    public Driver(Parcel in) {
        carNumber = in.readString();
        username = in.readString();
    }

        @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(carNumber);
        dest.writeString(username);
    }

    public static final Creator CREATOR = new Creator() {
        public Driver createFromParcel(Parcel in) {
            return new Driver(in);
        }

        public Driver[] newArray(int size) {
            return new Driver[size];
        }
    };
}
