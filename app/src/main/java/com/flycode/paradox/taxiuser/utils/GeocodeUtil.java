package com.flycode.paradox.taxiuser.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import com.mapbox.mapboxsdk.geometry.LatLng;

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
                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(),  1);
                } catch (Exception e) {
                    e.printStackTrace();

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpResponse response;
                    String responseString = null;

                    try {
                        response = httpclient.execute(new HttpGet(
                                "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + location.getLatitude() + "," + location.getLongitude()
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
                            //Closes the connection.
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
                listener.onGeocodeSuccess(address, location);
            }
        }.execute();
    }

    public interface GeocodeListener {
        void onGeocodeSuccess(String address, LatLng location);
    }
}
