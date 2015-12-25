package com.flycode.paradox.taxiuser.api;

import android.content.Context;
import android.util.Log;

import com.flycode.paradox.taxiuser.factory.ModelFactory;
import com.flycode.paradox.taxiuser.settings.AppSettings;
import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.client.CookieStore;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * Created by victor on 12/14/15.
 */
public class APITalker {
    // Url constants
//    private final String BASE_URL = "http://107.155.108.131:9000";
    private final String BASE_URL = "http://192.168.0.110:9000";
    private final String BASE_API_URL = BASE_URL+ "/api";
    private final String LOGIN_URL = "/auth/local";
    private final String ORDERS_URL = "/orders";
    private final String OWN_URL = "/own";
    private final String POINTS_URL = "/points";
    private final String TRANSACTIONS_URL = "/transactions";
    private final String CAR_CATEGORIES_URL = "/carCategories";

    // User specific constants
    private final String USERNAME = "username";
    private final String PASSWORD = "password";
    private final String TOKEN = "token";
    private final String ORDERS = "orders";
    private final String STATUS = "status";
    private final String STARTING_POINT = "startingPoint";
    private final String NAME = "name";
    private final String GEO = "gep";
    private final String DESCRIPTION = "description";
    private final String CAR_CATEGORY = "carCategory";

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

    /**
     * Public Methods
     */

    public void login(final Context context, final String username, final String password, final LoginHandler loginHandler) {
        RequestParams params = new RequestParams();
        params.put(USERNAME, username);
        params.put(PASSWORD, password);

        String url = BASE_URL + LOGIN_URL;

        asyncHttpClient.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers,
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
                                  Header[] headers,
                                  java.lang.Throwable throwable,
                                  org.json.JSONObject errorResponse) {
                if (loginHandler != null) {
                    loginHandler.onLoginFailure(TalkersConstants.JUST_FAILURE);
                }
            }
        });
    }

    public void getCarCategories(final CarCategoriesListener listener) {
        String url = BASE_API_URL + CAR_CATEGORIES_URL;

        asyncHttpClient.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                if (response.length() == 0) {
                    listener.onGetCarCategoriesFail();
                }

                listener.onGetCarCategoriesSuccess(ModelFactory.makeCarCategories(response));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                listener.onGetCarCategoriesFail();
            }
        });
    }

    public void getOwnOrders(Context context, String status , final GetOrdersHandler getOrdersHandler){
        if (!authenticate(context)) {
            return;
        }

        RequestParams params = new RequestParams();
        params.put(STATUS, status);

        String url = BASE_API_URL + ORDERS_URL + OWN_URL;

        asyncHttpClient.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  JSONObject response) {

                if (getOrdersHandler != null) {
                    JSONArray orders = response.optJSONArray(ORDERS);
                    getOrdersHandler.onGetOrdersSuccess(ModelFactory.makeOrders(orders));
                }
            }

            @Override
            public void onFailure(int statusCode,
                                  Header[] headers,
                                  java.lang.Throwable throwable,
                                  org.json.JSONObject errorResponse) {
                if (getOrdersHandler != null) {
                    getOrdersHandler.onGetOrdersFailure();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (getOrdersHandler != null) {
                    Log.d("STATUS CODE", statusCode + "");
                    Log.d("RESPONSE STRING", responseString);
                    getOrdersHandler.onGetOrdersFailure();
                }
            }

        });
    }

    public void makeOrder(Context context, String startingPointName, LatLng startingPointLocation, Date orderTime, String carCategory, String comments, final MakeOrderListener listener) {
        if (!authenticate(context)) {
            return;
        }

        final JSONObject requestJSON = new JSONObject();

        try {
            JSONObject startingPointJSON = new JSONObject();
            startingPointJSON.put(NAME, startingPointName);
            startingPointJSON.put(GEO, locationToJsonArray(startingPointLocation));
            requestJSON.put(STARTING_POINT, startingPointJSON);
            requestJSON.put(DESCRIPTION, comments);
            requestJSON.put(CAR_CATEGORY, carCategory);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        StringEntity requestEntity;

        try {
            requestEntity = new StringEntity(requestJSON.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }

        String url = BASE_API_URL + ORDERS_URL;

        asyncHttpClient.post(context, url, requestEntity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                listener.onMakeOrderSuccess(ModelFactory.makeOrder(response));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

                listener.onMakeOrderFail();
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

    private JSONArray locationToJsonArray(LatLng location) throws JSONException {
        JSONArray locationJSON = new JSONArray();

        locationJSON.put(location.latitude);
        locationJSON.put(location.longitude);

        return locationJSON;
    }
}

