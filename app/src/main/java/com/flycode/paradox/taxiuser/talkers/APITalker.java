package com.flycode.paradox.taxiuser.talkers;

/**
 * Created by victor on 12/14/15.
 */

import android.content.Context;
import android.location.Location;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

package com.flycode.paradox.taxidriver.talkers;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.flycode.paradox.taxidriver.constants.OrderStatusConstants;
import com.flycode.paradox.taxidriver.models.Order;
import com.flycode.paradox.taxidriver.models.Transaction;
import com.flycode.paradox.taxidriver.settings.AppSettings;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.client.CookieStore;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.cookie.BasicClientCookie;

/**
 * Created by victor on 12/9/15.
 */
public class APITalker {
    // Url constants
    private final String BASE_URL = "http://107.155.108.131:9000";
    private final String BASE_API_URL = BASE_URL+ "/api";
    private final String LOGIN_URL = "/auth/local";
    private final String ORDERS_URL = "/orders";
    private final String OWN_URL = "/own";
    private final String CUSTOM_TRIP_URL = "/customTrip";
    private final String POINTS_URL = "/points";
    private final String STATUS_URL = "/status";
    private final String TRANSACTIONS_URL = "/transactions";


    // User specific constants
    private final String USERNAME = "username";
    private final String PASSWORD = "password";
    private final String TOKEN = "token";
    private final String NAME = "name";
    private final String GEO = "geo";
    private final String ORDER = "order";
    private final String ORDERS = "orders";
    private final String STATUS = "status";
    private final String ID = "_id";
    private final String DISTANCE = "distance";
    private final String MONEY_AMOUNT = "moneyAmount";
    private final String TRANSACTIONS = "transactions";


    /*
	 * Singletone
	 */

    private static APITalker apiTalker = null;

    private AsyncHttpClient asyncHttpClient;

    private APITalker() {
        asyncHttpClient = new AsyncHttpClient();
    }

    public static APITalker sharedTalker() {
        if (apiTalker == null) {
            apiTalker = new APITalker();
        }

        return apiTalker;
    }

    public void login(final Context context, final String username, final String password, final LoginHandler loginHandler) {
        RequestParams params = new RequestParams();
        params.put(USERNAME, username);
        params.put(PASSWORD, password);

        String url = BASE_URL + LOGIN_URL;

        asyncHttpClient.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers,
                                  JSONObject response) {

                if (loginHandler != null) {
                    String token = response.optString(TOKEN);
                    AppSettings.sharedSettings(context).setToken(token);
                    setTokenCookie(context, token);
                    AppSettings.sharedSettings(context).setToken(token);
                    loginHandler.onLoginSuccess();
                }
            }

            @Override
            public void onFailure(int statusCode,
                                  cz.msebera.android.httpclient.Header[] headers,
                                  java.lang.Throwable throwable,
                                  org.json.JSONObject errorResponse) {
                if (loginHandler != null) {
                    loginHandler.onLoginFailure(TalkersConstants.JUST_FAILURE);
                }
            }
        });
    }


    public void pingLocation(Context context, final String order, final String status, final Location geoLocation){
        JSONArray geoLocationJSON;

        try {
            geoLocationJSON = locationToJsonArray(geoLocation);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        RequestParams params = new RequestParams();
        params.put(ORDER, order);
        params.put(STATUS, status);
        params.put(GEO, geoLocationJSON.toString());

        JSONObject requestJSON = new JSONObject();

        try {
            if (order != null && !order.isEmpty()) {
                requestJSON.put(ORDER, order);
            }

            requestJSON.put(STATUS, status);
            requestJSON.put(GEO, geoLocationJSON);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        StringEntity stringEntity;

        try {
            stringEntity = new StringEntity(requestJSON.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }

        String url = BASE_API_URL + POINTS_URL;

        if (!authenticate(context)) {
            return;
        }

        asyncHttpClient.post(context, url, stringEntity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers,
                                  JSONObject response) {
            }

            @Override
            public void onFailure(int statusCode,
                                  cz.msebera.android.httpclient.Header[] headers,
                                  java.lang.Throwable throwable,
                                  org.json.JSONObject errorResponse) {
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            }
        });
    }


    private void setTokenCookie(Context context, String token) {
        CookieStore cookieStore = new PersistentCookieStore(context);
        asyncHttpClient.setCookieStore(cookieStore);
        cookieStore.clear();
        cookieStore.addCookie(new BasicClientCookie(TOKEN, token));
        asyncHttpClient.addHeader("Authorization", "Bearer " + token);
    }

    private boolean authenticate(Context context) {
        if (AppSettings.sharedSettings(context).isUserLoggedIn()) {
            setTokenCookie(context, AppSettings.sharedSettings(context).getToken());
            return true;
        }

        return false;
    }
}

