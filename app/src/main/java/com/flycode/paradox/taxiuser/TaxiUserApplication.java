package com.flycode.paradox.taxiuser;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;

import io.fabric.sdk.android.Fabric;

/**
 * Created by anhaytananun on 25.01.16.
 */
public class TaxiUserApplication extends Application implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    private static TaxiUserApplication taxiUserApplication;
    private GoogleApiClient googleApiClient;

    @Override
    public void onCreate() {
        super.onCreate();

        Fabric.with(this, new Crashlytics());

        taxiUserApplication = this;

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .build();
        googleApiClient.connect();
    }

    public static TaxiUserApplication getSharedApplication() {
        return taxiUserApplication;
    }

    public GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("aaaa", "bbbb");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("aaaa", "bbbb");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("aaaa", "bbbb");
    }
}
