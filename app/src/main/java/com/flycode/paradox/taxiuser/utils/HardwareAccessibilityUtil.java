package com.flycode.paradox.taxiuser.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.ContextCompat;

/**
 * Created by anhaytananun on 25.01.16.
 */
public class HardwareAccessibilityUtil {
    public static boolean isNetworkEnabled(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static boolean isGPSEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public static boolean checkIfHasPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context.getApplicationContext(), permission) == PackageManager.PERMISSION_GRANTED;
    }
}
