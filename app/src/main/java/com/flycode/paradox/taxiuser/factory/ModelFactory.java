package com.flycode.paradox.taxiuser.factory;

import com.flycode.paradox.taxiuser.models.CarCategory;
import com.flycode.paradox.taxiuser.models.Order;
import com.flycode.paradox.taxiuser.models.User;

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

    private static final String TIME_PRICE = "timePrice";
    private static final String ROUT_PRICE = "routPrice";
    private static final String MIN_PRICE = "minPrice";

    private static final String SEX = "sex";
    private static final String DATE_OF_BIRTH = "dateOfBirth";
    private static final String BALANCE = "balance";
    private static final String CAR_NUMBER = "carNumber";
    private static final String EMAIL = "email";

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
        String startingPointName = null;

        JSONObject startingPointJSON = orderJSON.optJSONObject(STARTING_POINT);
        startingPointName = startingPointJSON.optString(NAME);

        // Ending Point
        String endingPointName = null;

        if (orderJSON.has(ENDING_POINT)) {
            JSONObject endingPointJSON = orderJSON.optJSONObject(ENDING_POINT);
            endingPointName = endingPointJSON.optString(NAME);
        }

        // Transaction
        JSONObject transactionJSONObject = orderJSON.optJSONObject(TRANSACTION);
        String paymentType = transactionJSONObject.optString(PAYMENT_TYPE);
        int moneyAmount = transactionJSONObject.optInt(MONEY_AMOUNT);

        return new Order(id, status,  startingPointName, endingPointName, orderTime,moneyAmount, paymentType,"Standart");
    }

    public static CarCategory[] makeCarCategories(JSONArray carCategoriesArray) {
        CarCategory[] carCategories = new CarCategory[carCategoriesArray.length()];

        for (int index = 0 ; index < carCategoriesArray.length() ; index++) {
            JSONObject carCategoryJSON = carCategoriesArray.optJSONObject(index);

            carCategories[index] = makeCarCategory(carCategoryJSON);
        }

        return carCategories;
    }

    public static CarCategory makeCarCategory(JSONObject carCategoryJSON) {
        String id = carCategoryJSON.optString(ID);
        String name = carCategoryJSON.optString(NAME);
        String description = carCategoryJSON.optString(DESCRIPTION);
        double timePrice = carCategoryJSON.optDouble(TIME_PRICE);
        double routPrice = carCategoryJSON.optDouble(ROUT_PRICE);
        double minPrice = carCategoryJSON.optDouble(MIN_PRICE, 6 * routPrice);
        // TODO: TAKE MIN PRICE FROM SERVER ONLY

        return new CarCategory(id, name, description, timePrice, routPrice, minPrice);
    }

    public static User makeUser(JSONObject userJSON) {
        String id = userJSON.optString(ID);
        String role = userJSON.optString(ROLE);
        String username = userJSON.optString(USERNAME);
        String name = userJSON.optString(NAME);
        String sex = userJSON.optString(SEX);
        String email = userJSON.optString(EMAIL);
        Date dateOfBirth = new Date();
        dateFromString(userJSON.optString(DATE_OF_BIRTH), dateOfBirth);
        String status = userJSON.optString(STATUS);
        int carNumber = userJSON.optInt(CAR_NUMBER);
        int balance = userJSON.optInt(BALANCE);

        return new User(id, role, username, name, sex, email, dateOfBirth, status,
                balance, carNumber);
    }

    public static void dateFromString(String dateString, Date date) {
        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
