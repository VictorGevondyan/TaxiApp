package com.flycode.paradox.taxiuser.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import com.flycode.paradox.taxiuser.TaxiUserApplication;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by anhaytananun on 25.12.15.
 */
public class GeocodeUtil {
    private static AutocompleteFilter autocompleteFilter;
    private static LatLngBounds placesBounds;
    private static Geocoder geocoder;

    static {
        autocompleteFilter = new AutocompleteFilter
                .Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .build();
        placesBounds = new LatLngBounds(
                new LatLng(40.057913, 44.106212),
                new LatLng(40.369111, 44.807486)
        );
    }

    public static void geocode(final Context context, final double latitude, final double longitude, final GeocodeListener listener) {
        new AsyncTask<Void, String, String>() {
            @Override
            protected String doInBackground(Void... params) {
                if (context == null) {
                    return "";
                }

                if (geocoder == null) {
                    geocoder = new Geocoder(context, Locale.ENGLISH);
                }

                List<Address> addresses;

                try {
                    addresses = geocoder.getFromLocation(latitude, longitude,  1);
                } catch (Exception e) {
                    e.printStackTrace();

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpResponse response;
                    String responseString = null;

                    try {
                        response = httpclient.execute(new HttpGet(
                                "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latitude + "," + longitude
                        ));

                        StatusLine statusLine = response.getStatusLine();

                        if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                            ByteArrayOutputStream out = new ByteArrayOutputStream();
                            response.getEntity().writeTo(out);
                            responseString = out.toString();
                            out.close();

                            JSONObject responseJSON = new JSONObject(responseString);
                            JSONArray resultsJSON = responseJSON.optJSONArray("results");
                            JSONObject firstOne = resultsJSON.optJSONObject(0);
                            return  firstOne.optString("formatted_address").split(",")[0];
                        } else{
                            response.getEntity().getContent().close();
                            throw new IOException(statusLine.getReasonPhrase());
                        }
                    } catch (Exception we) {
                        we.printStackTrace();
                        return "";
                    }
                }

                if (addresses.size() > 0) {
                    return addresses.get(0).getAddressLine(0);
                }

                return "";
            }

            @Override
            protected void onPostExecute(String address) {
                super.onPostExecute(address);
                listener.onGeocodeSuccess(address, latitude, longitude);
            }
        }.execute();
    }

    public static void reverseGeocode(final Context context, final String address, final GeocodeListener listener) {
        Places.GeoDataApi.getAutocompletePredictions(
                TaxiUserApplication.getSharedApplication().getGoogleApiClient(),
                address,
                placesBounds,
                autocompleteFilter
        ).setResultCallback(new ResultCallback<AutocompletePredictionBuffer>() {
            @Override
            public void onResult(AutocompletePredictionBuffer autocompletePredictions) {
                int size = autocompletePredictions.getCount() > 5 ? 5 : autocompletePredictions.getCount();
                String[] addresses = new String[size];
                String[] placeIds = new String[size];
                int index = 0;

                for (AutocompletePrediction prediction : autocompletePredictions) {
                    if (index > 4) {
                        break;
                    }

                    addresses[index] = prediction.getFullText(null).toString();
                    placeIds[index] = prediction.getPlaceId();

                    index++;
                }

                autocompletePredictions.release();

                listener.onReverseGeocodeSuccess(address, addresses, placeIds);
            }
        }, 30, TimeUnit.SECONDS);
    }

    public static void getPlaceDetailsByPlaceId(String placeId, final GeocodeListener listener) {
        Places.GeoDataApi.getPlaceById(
                TaxiUserApplication.getSharedApplication().getGoogleApiClient(),
                placeId
        ).setResultCallback(new ResultCallback<PlaceBuffer>() {
            @Override
            public void onResult(PlaceBuffer places) {
                Place place = places.get(0);

                if (place != null) {
                    listener.onPlaceLocationDetermined(place.getAddress().toString(), place.getLatLng());
                }

                places.release();
            }
        }, 30, TimeUnit.SECONDS);
    }

    public interface GeocodeListener {
        void onGeocodeSuccess(String address, double latitude, double longitude);
        void onReverseGeocodeSuccess(String address, String[] addresses, String[] placeIds);
        void onPlaceLocationDetermined(String address, LatLng placeLocation);
    }
}
