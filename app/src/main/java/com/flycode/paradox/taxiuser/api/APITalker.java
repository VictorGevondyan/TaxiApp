package com.flycode.paradox.taxiuser.api;

import android.content.Context;
import android.util.Log;

import com.flycode.paradox.taxiuser.factory.ModelFactory;
import com.flycode.paradox.taxiuser.settings.AppSettings;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
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
    private final String DEVICES = "/devices";
    private final String USER_URL = "/me";
    private final String USERS_URL = "/users";
    private final String PASSWORD_URL = "/password";

    private final String USERNAME = "username";
    private final String PASSWORD = "password";
    private final String ORDERS = "orders";
    private final String STATUS = "status";
    private final String STARTING_POINT = "startingPoint";
    private final String NAME = "name";
    private final String GEO = "geo";
    private final String DESCRIPTION = "description";
    private final String CAR_CATEGORY = "carCategory";
    private final String TOKEN = "token";
    private final String TYPE = "type";
    private final String ANDROID = "android";
    private final String DEVICE_ID = "deviceId";
    private final String OLD_PASSWORD = "oldPassword";
    private final String NEW_PASSWODR = "newPassword";
    private final String SEX = "sex";
    private final String EMAIL = "email";
    private final String DATE_OF_BIRTH = "dateOfBirth";
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

    public void getOwnOrders(Context context, String[] statuses, final GetOrdersHandler getOrdersHandler){
        if (!authenticate(context)) {
            return;
        }

        JSONArray statusArray = new JSONArray();

        for (String status : statuses) {
            statusArray.put(status);
        }

        RequestParams params = new RequestParams();
        params.put(STATUS, statusArray.toString());

        String url = BASE_API_URL + ORDERS_URL + OWN_URL;

        asyncHttpClient.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
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

    public void getOrder(Context context, String orderId, final GetOrderHandler getOrderHandler){
        if (!authenticate(context)) {
            return;
        }

        String url = BASE_API_URL + ORDERS_URL +  "/" + orderId;

        asyncHttpClient.get(context, url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                if (getOrderHandler != null) {
                    getOrderHandler.onGetOrderSuccess(ModelFactory.makeOrder(response));
                }
            }

            @Override
            public void onFailure(int statusCode,
                                  Header[] headers,
                                  java.lang.Throwable throwable,
                                  org.json.JSONObject errorResponse) {
                if (getOrderHandler!= null) {
                    Log.d("STATUS CODE", statusCode + "");
                    getOrderHandler.onGetOrderFailure();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (getOrderHandler!= null) {
                    Log.d("STATUS CODE", statusCode + "");
                    Log.d("RESPONSE STRING", responseString);
                    getOrderHandler.onGetOrderFailure();
                }
            }

        });
    }

    public void getOwnTransactions(Context context, final GetOwnTransactionsHandler getOwnTransactionsHandler){
        if (!authenticate(context)) {
            return;
        }

        RequestParams params = new RequestParams();

        String url = BASE_API_URL + TRANSACTIONS_URL + OWN_URL;

        asyncHttpClient.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  JSONObject response) {

                if (getOwnTransactionsHandler!= null) {
                    JSONArray transactions = response.optJSONArray(TRANSACTIONS);
                    getOwnTransactionsHandler.onGetOwnTransactionsSuccess(ModelFactory.makeTransactions(transactions));
                }
            }

            @Override
            public void onFailure(int statusCode,
                                  Header[] headers,
                                  java.lang.Throwable throwable,
                                  org.json.JSONObject errorResponse) {
                if (getOwnTransactionsHandler!= null) {
                    getOwnTransactionsHandler.onGetOwnTransactionsFailure();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (getOwnTransactionsHandler!= null) {
                    Log.d("STATUS CODE", statusCode + "");
                    Log.d("RESPONSE STRING", responseString);
                    getOwnTransactionsHandler.onGetOwnTransactionsFailure();
                }
            }

        });
    }

    public void makeOrder(Context context, String startingPointName, double startingPointLatitude, double startingPointLongitude, Date orderTime, String carCategory, String comments, final MakeOrderListener listener) {
        if (!authenticate(context)) {
            return;
        }

        final JSONObject requestJSON = new JSONObject();

        try {
            JSONObject startingPointJSON = new JSONObject();
            startingPointJSON.put(NAME, startingPointName);
            startingPointJSON.put(GEO, locationToJsonArray(startingPointLatitude, startingPointLongitude));
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

    public void registerGCMToken(Context context, final String registrationId, String androidDeviceId, final OnGCMTokenRegisteredListener listener) {
        if (!authenticate(context)) {
            return;
        }

        final JSONObject requestJSON = new JSONObject();

        try {
            requestJSON.put(TOKEN, registrationId);
            requestJSON.put(DEVICE_ID, androidDeviceId);
            requestJSON.put(TYPE, ANDROID);
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

        String url = BASE_API_URL + DEVICES;

        asyncHttpClient.post(context, url, requestEntity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (listener == null) {
                    return;
                }

                listener.onGCMTokenRegistrationSuccess(registrationId);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (listener == null) {
                    return;
                }

                listener.onGCMTokenRegistrationFailure();
            }
        });
    }

    public void getUser(Context context, final GetUserHandler getUserHandler){
        if (!authenticate(context)) {
            return;
        }

        String url = BASE_API_URL + USERS_URL + USER_URL;

        asyncHttpClient.get(context, url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                if (getUserHandler != null) {
                    getUserHandler.onGetUserSuccess(ModelFactory.makeUser(response));
                }
            }

            @Override
            public void onFailure(int statusCode,
                                  Header[] headers,
                                  Throwable throwable,
                                  JSONObject errorResponse) {
                if (getUserHandler != null) {
                    Log.d("STATUS CODE", statusCode + "");
                    getUserHandler.onGetUserFailure();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (getUserHandler != null) {
                    Log.d("STATUS CODE", statusCode + "");
                    Log.d("RESPONSE STRING", responseString);
                    getUserHandler.onGetUserFailure();
                }
            }

        });
    }

    public void changeUserPassword(final Context context, final String oldPassword, final String newPassword) {
        if (!authenticate(context)) {
            return;
        }

        RequestParams params = new RequestParams();
        params.put(OLD_PASSWORD, oldPassword);
        params.put(NEW_PASSWODR, newPassword);

        String url = BASE_API_URL + USERS_URL + USER_URL + PASSWORD_URL;

        asyncHttpClient.put(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  JSONObject response) {
            }

            @Override
            public void onFailure(int statusCode,
                                  Header[] headers,
                                  java.lang.Throwable throwable,
                                  org.json.JSONObject errorResponse) {
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.d("STATUS CODE", statusCode + "");
                    Log.d("RESPONSE STRING", responseString);
            }


        });
    }

    public void changeNameAndMail(final Context context, final String fullName,  final String email , final ChangeNameAndMailHandler changeNameAndMail) {
        if (!authenticate(context)) {
            return;
        }

        RequestParams params = new RequestParams();
        params.put(NAME, fullName);
        params.put(EMAIL, email);

        String url = BASE_API_URL + USERS_URL + USER_URL;

        asyncHttpClient.put(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  JSONObject response) {

                if (changeNameAndMail!= null) {
                    changeNameAndMail.onChangeNameAndMailSuccess();
                }
            }

            @Override
            public void onFailure(int statusCode,
                                  Header[] headers,
                                  java.lang.Throwable throwable,
                                  org.json.JSONObject errorResponse) {
                if (changeNameAndMail!= null) {
                    changeNameAndMail.onChangeNameAndMailFailure();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (changeNameAndMail!= null) {
                    Log.d("STATUS CODE", statusCode + "");
                    Log.d("RESPONSE STRING", responseString);
                    changeNameAndMail.onChangeNameAndMailFailure();
                }
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
        if (AppSettings.sharedSettings(context).getToken() == null) {
            return false;
        }

        setTokenCookie(context, AppSettings.sharedSettings(context).getToken());
        return true;
    }

    private JSONArray locationToJsonArray(double latitude, double longitude) throws JSONException {
        JSONArray locationJSON = new JSONArray();

        locationJSON.put(latitude);
        locationJSON.put(longitude);

        return locationJSON;
    }
}

