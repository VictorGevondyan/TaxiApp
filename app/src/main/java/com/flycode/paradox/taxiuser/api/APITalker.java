package com.flycode.paradox.taxiuser.api;

import android.content.Context;

import com.flycode.paradox.taxiuser.database.Database;
import com.flycode.paradox.taxiuser.factory.ModelFactory;
import com.flycode.paradox.taxiuser.models.Order;
import com.flycode.paradox.taxiuser.models.Transaction;
import com.flycode.paradox.taxiuser.models.Translation;
import com.flycode.paradox.taxiuser.settings.AppSettings;
import com.flycode.paradox.taxiuser.settings.UserData;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by victor on 12/14/15.
 */
public class APITalker {
    // Url constants
    private final String BASE_URL = "http://taxivip.am:9001";
//    private final String BASE_URL = "http://107.155.108.131:9000";
//    private final String BASE_URL = "http://192.168.0.105:9001";
//    private final String BASE_URL = "http://192.168.0.110:9001";
//    private final String BASE_URL = "http://192.168.1.110:9001";
    private final String BASE_API_URL = BASE_URL+ "/api";
    private final String LOGIN_URL = "/auth/local";
    private final String ORDERS_URL = "/orders";
    private final String FEEDBACK_URL = "/feedback";
    private final String OWN_URL = "/own";
    private final String POINTS_URL = "/points";
    private final String STATUS_URL = "/status";
    private final String TRANSACTIONS_URL = "/transactions";
    private final String CAR_CATEGORIES_URL = "/carCategories";
    private final String DEVICES = "/devices";
    private final String ME_URL = "/me";
    private final String USERS_URL = "/users";
    private final String PASSWORD_URL = "/password";
    private final String TRANSLATION_URL = "/translations";

    private final String USERNAME = "username";
    private final String PASSWORD = "password";
    private final String STARTING_POINT = "startingPoint";
    private final String NAME = "name";
    private final String FEEDBACK = "feedback";
    private final String STARS = "stars";
    private final String GEO = "geo";
    private final String DESCRIPTION = "description";
    private final String CAR_CATEGORY = "carCategory";
    private final String TOKEN = "token";
    private final String TYPE = "type";
    private final String ANDROID = "android";
    private final String DEVICE_ID = "deviceId";
    private final String OLD_PASSWORD = "oldPassword";
    private final String NEW_PASSWORD = "newPassword";
    private final String ORDER = "order";
    private final String ORDERS = "orders";
    private final String STATUS = "status";
    private final String EMAIL = "email";
    private final String LIMIT = "limit";
    private final String TRANSACTIONS = "transactions";
    private final String ONLY_COUNT = "onlyCount";
    private final String COUNT = "count";
    private final String DATE = "date";
    private final String START = "start";
    private final String TRUE = "true";
    private final String UPDATED = "updated";
    private final String ORDER_TIME = "orderTime";
    private final String CASH_ONLY = "onlyCash";
    private final String KEY = "key";

    /*
	 * Singletone
	 */

    private static APITalker apiTalker = null;

    private AsyncHttpClient asyncHttpClient;

    private APITalker() {
        asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setMaxRetriesAndTimeout(0, 10000);
        asyncHttpClient.setConnectTimeout(10000);
            asyncHttpClient.setResponseTimeout(10000);
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
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (loginHandler != null) {
                    String token = response.optString(TOKEN);
                    AppSettings.sharedSettings(context).setToken(token);
                    setTokenCookie(context, token);
                    loginHandler.onLoginSuccess();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, org.json.JSONObject errorResponse) {
                if (loginHandler != null) {
                    loginHandler.onLoginFailure(statusCode);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (loginHandler != null) {
                    loginHandler.onLoginFailure(statusCode);
                }
            }
        });
    }

    public void receivePassword(String username, final OnReceivePasswordRequestListener listener) {
        RequestParams params = new RequestParams();
        params.put(USERNAME, username);

        String url = BASE_API_URL + USERS_URL;

        asyncHttpClient.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (listener != null) {
                    listener.onReceivePasswordRequestSuccess();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (listener != null) {
                    listener.onReceivePasswordRequestFail(statusCode);
                }
            }
        });
    }

    public void getCarCategories(final CarCategoriesListener listener) {
        String url = BASE_API_URL + CAR_CATEGORIES_URL;

        asyncHttpClient.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                if (listener != null) {
                    listener.onGetCarCategoriesSuccess(ModelFactory.makeCarCategories(response));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (listener != null) {
                    listener.onGetCarCategoriesFail(statusCode);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (listener != null) {
                    listener.onGetCarCategoriesFail(statusCode);
                }
            }
        });
    }

    public void getOwnOrders(final Context context, final String[] statuses, Date fromDate, final GetOrdersHandler getOrdersHandler){
        if (!authenticate(context)) {
            getOrdersHandler.onGetOrdersFailure(401);

            return;
        }

        RequestParams params = new RequestParams();
        params.put(LIMIT, 100);

        if (fromDate != null) {
            try {
                SimpleDateFormat simpleDateFormat                                                                                                                                                                                                                                                                                                                                                                                                                                                                = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
                JSONObject dateJSON = new JSONObject();
                dateJSON.put(START, simpleDateFormat.format(fromDate));
                params.put(UPDATED, dateJSON);
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
        }
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    
        String url = BASE_API_URL + ORDERS_URL + OWN_URL;

        asyncHttpClient.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (getOrdersHandler != null) {
                    JSONArray ordersArray = response.optJSONArray(ORDERS);

                    ArrayList<Order> orders = ModelFactory.makeOrders(ordersArray);
                    Database.sharedDatabase(context.getApplicationContext()).storeOrders(orders, statuses, getOrdersHandler);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, org.json.JSONObject errorResponse) {
                if (getOrdersHandler != null) {
                    getOrdersHandler.onGetOrdersFailure(statusCode);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (getOrdersHandler != null) {
                    getOrdersHandler.onGetOrdersFailure(statusCode);
                }
            }
        });
    }

    public void getOrder(final Context context, String orderId, final GetOrderHandler getOrderHandler){
        if (!authenticate(context)) {
            getOrderHandler.onGetOrderFailure(401);

            return;
        }

        String url = BASE_API_URL + ORDERS_URL +  "/" + orderId;

        asyncHttpClient.get(context, url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (getOrderHandler != null) {
                    Order order = ModelFactory.makeOrder(response);
                    Database.sharedDatabase(context.getApplicationContext()).storeOrder(order);
                    getOrderHandler.onGetOrderSuccess(order);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, org.json.JSONObject errorResponse) {
                if (getOrderHandler!= null) {
                    getOrderHandler.onGetOrderFailure(statusCode);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (getOrderHandler!= null) {
                    getOrderHandler.onGetOrderFailure(statusCode);
                }
            }
        });
    }

    public void getOwnTransactions(final Context context, final GetOwnTransactionsHandler getOwnTransactionsHandler, Date fromDate) {
        if (!authenticate(context)) {
            getOwnTransactionsHandler.onGetOwnTransactionsFailure(401);

            return;
        }

        RequestParams requestParams = new RequestParams();
        requestParams.put(LIMIT, 100);

        if (fromDate != null) {
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
                JSONObject dateJSON = new JSONObject();
                fromDate.setTime(fromDate.getTime() + 1);
                dateJSON.put(START, simpleDateFormat.format(fromDate));
                requestParams.put(DATE, dateJSON);
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
        }

        String url = BASE_API_URL + TRANSACTIONS_URL + OWN_URL;

        asyncHttpClient.get(url, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (getOwnTransactionsHandler != null) {
                    JSONArray transactionsArray = response.optJSONArray(TRANSACTIONS);
                    ArrayList<Transaction> transactions = ModelFactory.makeTransactions(transactionsArray);
                    getOwnTransactionsHandler.onGetOwnTransactionsSuccess(transactions);
                    Database
                            .sharedDatabase(context)
                            .storeTransactions(
                                    transactions,
                                    UserData.sharedData(context).getUsername());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, org.json.JSONObject errorResponse) {
                if (getOwnTransactionsHandler != null) {
                    getOwnTransactionsHandler.onGetOwnTransactionsFailure(statusCode);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (getOwnTransactionsHandler != null) {
                    getOwnTransactionsHandler.onGetOwnTransactionsFailure(statusCode);
                }
            }
        });
    }

    public void makeOrder(final Context context, String startingPointName, double startingPointLatitude, double startingPointLongitude, Date orderTime, String carCategory, String comments, boolean isCashOnly, final MakeOrderListener listener) {
        if (!authenticate(context)) {
            listener.onMakeOrderFail(401);

            return;
        }

        final JSONObject requestJSON = new JSONObject();

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
            JSONObject startingPointJSON = new JSONObject();
            startingPointJSON.put(NAME, startingPointName);
            startingPointJSON.put(GEO, locationToJsonArray(startingPointLatitude, startingPointLongitude));
            requestJSON.put(STARTING_POINT, startingPointJSON);
            requestJSON.put(DESCRIPTION, comments);
            requestJSON.put(CAR_CATEGORY, carCategory);
            requestJSON.put(ORDER_TIME, simpleDateFormat.format(orderTime));
            requestJSON.put(CASH_ONLY, isCashOnly);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        StringEntity requestEntity;

        try {
            requestEntity = new StringEntity(requestJSON.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }

        String url = BASE_API_URL + ORDERS_URL;

        asyncHttpClient.post(context, url, requestEntity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (listener != null) {
                    Order order = ModelFactory.makeOrder(response);
                    Database.sharedDatabase(context).storeOrder(order);
                    listener.onMakeOrderSuccess(order);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (listener != null) {
                    listener.onMakeOrderFail(statusCode);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (listener != null) {
                    listener.onMakeOrderFail(statusCode);
                }
            }
        });
    }

    public void registerGCMToken(Context context, final String registrationId, String androidDeviceId, final OnGCMTokenRegisteredListener listener) {
        if (!authenticate(context)) {
            listener.onGCMTokenRegistrationFailure(401);

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
            requestEntity = new StringEntity(requestJSON.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }

        String url = BASE_API_URL + DEVICES;

        asyncHttpClient.post(context, url, requestEntity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (listener != null) {
                    listener.onGCMTokenRegistrationSuccess(registrationId);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (listener != null) {
                    listener.onGCMTokenRegistrationFailure(statusCode);
                }
            }
        });
    }

    public void getUser(Context context, final GetUserHandler getUserHandler){
        if (!authenticate(context)) {
            getUserHandler.onGetUserFailure(401);

            return;
        }

        String url = BASE_API_URL + USERS_URL + ME_URL;

        asyncHttpClient.get(context, url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (getUserHandler != null) {
                    getUserHandler.onGetUserSuccess(ModelFactory.makeUser(response));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (getUserHandler != null) {
                    getUserHandler.onGetUserFailure(statusCode);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (getUserHandler != null) {
                    getUserHandler.onGetUserFailure(statusCode);
                }
            }

        });
    }

    public void changeUserPassword(final Context context, final String oldPassword, final String newPassword, final ChangePasswordRequestHandler handler) {
        if (!authenticate(context)) {
            handler.onChangePasswordRequestFailure(401);

            return;
        }

        RequestParams params = new RequestParams();
        params.put(OLD_PASSWORD, oldPassword);
        params.put(NEW_PASSWORD, newPassword);

        String url = BASE_API_URL + USERS_URL + ME_URL + PASSWORD_URL;

        asyncHttpClient.put(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                handler.onChangePasswordRequestSuccess();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, org.json.JSONObject errorResponse) {
                handler.onChangePasswordRequestFailure(statusCode);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                handler.onChangePasswordRequestFailure(statusCode);
            }
        });
    }

    public void changeNameAndMail(final Context context, final String fullName,  final String email , final ChangeNameAndMailRequestListener changeNameAndMail) {
        if (!authenticate(context)) {
            changeNameAndMail.onChangeNameAndMailFailure(401);

            return;
        }

        RequestParams params = new RequestParams();
        params.put(NAME, fullName);
        params.put(EMAIL, email);

        String url = BASE_API_URL + USERS_URL + ME_URL;

        asyncHttpClient.put(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  JSONObject response) {

                if (changeNameAndMail != null) {
                    changeNameAndMail.onChangeNameAndMailSuccess();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, org.json.JSONObject errorResponse) {
                if (changeNameAndMail != null) {
                    changeNameAndMail.onChangeNameAndMailFailure(statusCode);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (changeNameAndMail != null) {
                    changeNameAndMail.onChangeNameAndMailFailure(statusCode);
                }
            }
        });
    }

    public void getPointsForOrder(String orderId, String status, int limit, final PointsForOrderListener listener) {
        RequestParams requestParams = new RequestParams();
        requestParams.put(ORDER, orderId);
        requestParams.put(STATUS, status);

        if (limit > 0) {
            requestParams.put(LIMIT, limit);
        }

        String url = BASE_API_URL + POINTS_URL;

        asyncHttpClient.get(url, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                if (listener != null) {
                    listener.onGetPointsForOrderSuccess(response);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (listener != null) {
                    listener.onGetPointsForOrderFail(statusCode);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (listener != null) {
                    listener.onGetPointsForOrderFail(statusCode);
                }
            }
        });
    }

    public void sendFeedback(final Context context, String comment, int rating, String orderId, final FeedbackRequestHandler handler) {
        if (!authenticate(context)) {
            handler.onFeedbackRequestFailure(401);

            return;
        }

        RequestParams params = new RequestParams();
        params.put(FEEDBACK, comment);
        params.put(STARS, rating);

        String url = BASE_API_URL + ORDERS_URL + "/" + orderId + FEEDBACK_URL;

        asyncHttpClient.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (handler != null) {
                    Database.sharedDatabase(context).storeOrder(ModelFactory.makeOrder(response));
                    handler.onFeedbackRequestSuccess();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, org.json.JSONObject errorResponse) {
                if (handler != null) {
                    handler.onFeedbackRequestFailure(statusCode);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (handler != null) {
                    handler.onFeedbackRequestFailure(statusCode);
                }
            }
        });
    }

    public void updateOrderStatus(final Context context,
                                  String orderId, String status,
                                   final UpdateOrderStatusHandler updateOrderStatusHandler) {
        if (!authenticate(context)) {
            updateOrderStatusHandler.onUpdateOrderStatusFailure(401);

            return;
        }

        JSONObject requestJSON = new JSONObject();

        try {
            requestJSON.put(STATUS, status);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        StringEntity requestEntity;

        try {
            requestEntity = new StringEntity(requestJSON.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }

        String url = BASE_API_URL + ORDERS_URL + "/" + orderId + STATUS_URL;

        asyncHttpClient.put(context, url, requestEntity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (updateOrderStatusHandler != null) {
                    Order order = ModelFactory.makeOrder(response);
                    Database.sharedDatabase(context).storeOrder(order);
                    updateOrderStatusHandler.onUpdateOrderStatusSuccess(ModelFactory.makeOrder(response));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (updateOrderStatusHandler != null) {
                    updateOrderStatusHandler.onUpdateOrderStatusFailure(statusCode);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (updateOrderStatusHandler != null) {
                    updateOrderStatusHandler.onUpdateOrderStatusFailure(statusCode);
                }
            }
        });
    }

    public void getTranslation(final Context context, final String[] keys, final OnGetTranslationResultHandler handler) {
        if (!authenticate(context)) {
            handler.onGetTranslationFailure(401);

            return;
        }

        RequestParams requestParams = new RequestParams();
        requestParams.put(KEY, keys);

        String url = BASE_API_URL + TRANSLATION_URL;

        asyncHttpClient.get(url, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Translation[] translations = ModelFactory.makeTranslations(response);

                Database.sharedDatabase(context).storeTranslations(translations);

                if (handler != null) {
                    handler.onGetTranslationSuccess(translations);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (handler != null) {
                    handler.onGetTranslationFailure(statusCode);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (handler != null) {
                    handler.onGetTranslationFailure(statusCode);
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
