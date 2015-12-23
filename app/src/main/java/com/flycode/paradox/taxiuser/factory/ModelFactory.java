package com.flycode.paradox.taxiuser.factory;

import com.flycode.paradox.taxiuser.models.Order;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by victor on 12/19/15.
 */
public class ModelFactory {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);

    static {
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    private static final String ID = "_id";
    private static final String STATUS = "status";
    private static final String STARTING_POINT = "startingPoint";
    private static final String ORDER_TIME = "orderTime";
    private static final String ENDING_POINT = "endingPoint";
    private static final String FINISH_TIME = "finishTime";
    private static final String DESCRIPTION = "description";

    private static final String TRANSACTION = "transaction";
    private static final String PAYMENT_TYPE = "paymentType";
    private static final String MONEY_AMOUNT = "moneyAmount";

    private static final String USER = "user";
    private static final String USERNAME = "username";
    private static final String USER_PICKUP_TIME = "userPickupTime";

    private static final String NAME = "name";
    private static final String GEO = "geo";

    private static final String TO = "to";
    private static final String FROM = "from";
    private static final String ROLE = "role";
    private static final String DATE = "date";


    public static ArrayList<Order> makeOrders(JSONArray ordersJSONArray) {
        ArrayList<Order> orders = new ArrayList<>();

        for (int index = 0 ; index < ordersJSONArray.length() ; index++) {
            orders.add(makeOrder(ordersJSONArray.optJSONObject(index)));
        }

        return orders;
    }

    public static Order makeOrder(JSONObject orderJSON) {
        String id = orderJSON.optString(ID);
        String status = orderJSON.optString(STATUS);

        // Time Values
        Date orderTime = new Date();

        dateFromString(orderJSON.optString(ORDER_TIME), orderTime);

        // Starting Point
        JSONObject startingPointJSON = orderJSON.optJSONObject(STARTING_POINT);
        String startingPointName = startingPointJSON.optString(NAME);

        return new Order(id, status, startingPointName, orderTime);
    }

    private static void dateFromString(String dateString, Date date) {
        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}