package com.flycode.paradox.taxiuser.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.util.List;

/**
 * Created by anhaytananun on 25.12.15.
 */
public class GeocodeUtil {
    public static void geocode(final Context context, final LatLng location, final GeocodeListener listener) {
        new AsyncTask<Void, String, String>() {
            @Override
            protected String doInBackground(Void... params) {
                Geocoder geocoder = new Geocoder(context);
                List<Address> addresses;

                try {
                    addresses = geocoder.getFromLocation(location.latitude, location.longitude,  1);
                } catch (IOException e) {
                    e.printStackTrace();
                    return "";
                } catch (BufferOverflowException e){
                    e.printStackTrace();
                    return "";
                }

                if (addresses.size() > 0) {
                    return addresses.get(0).getAddressLine(0);
                }

                return "";
            }

            @Override
            protected void onPostExecute(String address) {
                super.onPostExecute(address);
                listener.onGeocodeSuccess(address);
            }
        }.execute();
    }

    public interface GeocodeListener {
        void onGeocodeSuccess(String address);
    }
}
