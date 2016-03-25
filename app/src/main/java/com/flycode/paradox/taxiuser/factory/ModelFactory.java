package com.flycode.paradox.taxiuser.factory;

import android.location.Location;

import com.flycode.paradox.taxiuser.models.CarCategory;
import com.flycode.paradox.taxiuser.models.Driver;
import com.flycode.paradox.taxiuser.models.Order;
import com.flycode.paradox.taxiuser.models.Transaction;
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
    private static final String ORDER = "order";
    private static final String STATUS = "status";
    private static final String STARTING_POINT = "startingPoint";
    private static final String ORDER_TIME = "orderTime";
    private static final String ENDING_POINT = "endingPoint";
    private static final String FINISH_TIME = "finishTime";
    private static final String UPDATED = "updated";
    private static final String DESCRIPTION = "description";
    private static final String BONUS = "bonus";
    private static final String FEEDBACK = "feedback";
    private static final String FEEDBACK_RATING = "stars";


    private static final String TRANSACTION = "transaction";
    private static final String PAYMENT_TYPE = "paymentType";
    private static final String MONEY_AMOUNT = "moneyAmount";
    private static final String DISTANCE = "distance";

    private static final String USER = "user";
    private static final String USERNAME = "username";
    private static final String USER_PICKUP_TIME = "userPickupTime";

    private static final String NAME = "name";
    private static final String GEO = "geo";

    private static final String TO = "to";
    private static final String FROM = "from";
    private static final String ROLE = "role";
    private static final String DATE = "date";
    private static final String CASH_ONLY = "cashOnly";

    private static final String TIME_PRICE = "timePrice";
    private static final String ROUT_PRICE = "routPrice";
    private static final String MIN_PRICE = "minimumPrice";

    private static final String SEX = "sex";
    private static   final String DATE_OF_BIRTH = "dateOfBirth";
    private static final String BALANCE = "balance";
    private static final String CAR_NUMBER = "carNumber";
    private static final String DRIVER = "driver";
    private static final String EMAIL = "email";
    private static final String CAR_CATEGORY= "carCategory";

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
        Date orderTime = dateFromString(orderJSON.optString(ORDER_TIME));
        Date finishTime = dateFromString(orderJSON.optString(FINISH_TIME));
        Date userPickupTime = dateFromString(orderJSON.optString(USER_PICKUP_TIME));
        Date updatedTime = dateFromString(orderJSON.optString(UPDATED));

        // Distance
        double distance = orderJSON.optDouble(DISTANCE, 0.0) * 1000;

        // Starting Point

        JSONObject startingPointJSON = orderJSON.optJSONObject(STARTING_POINT);
        String startingPointName = startingPointJSON.optString(NAME);
        JSONArray startPointGeoArray = startingPointJSON.optJSONArray(GEO);
        Location startingPointGeo = new Location("Server Provider");
        startingPointGeo.setLatitude(startPointGeoArray.optDouble(0));
        startingPointGeo.setLongitude(startPointGeoArray.optDouble(1));

        // Ending Point
        String endingPointName = null;
        Location endingPointGeo = null;

        if (orderJSON.has(ENDING_POINT)) {
            JSONObject endingPointJSON = orderJSON.optJSONObject(ENDING_POINT);
            endingPointName = endingPointJSON.optString(NAME);
            JSONArray endingPointGeoArray = endingPointJSON.optJSONArray(GEO);
            endingPointGeo = new Location("Server Provider");
            endingPointGeo.setLatitude(endingPointGeoArray.optDouble(0));
            endingPointGeo.setLongitude(endingPointGeoArray.optDouble(1));
        }

        String description = orderJSON.optString(DESCRIPTION);

        if (description == null) {
            description = "";
        }

        // Driver
        Driver driver = null;
        JSONObject driverJSON = orderJSON.optJSONObject(DRIVER);

        if (driverJSON != null) {
            String carNumber = driverJSON.optString(CAR_NUMBER, "");
            String username = driverJSON.optString(USERNAME, "");

            driver = new Driver(carNumber, username);
        }

        // Car Category
        JSONObject carCategoryJSON = orderJSON.optJSONObject(CAR_CATEGORY);
        String carCategory = "";

        if (carCategoryJSON != null) {
            carCategory = carCategoryJSON.optString(NAME);
        }

        // Transaction
        JSONObject transactionJSONObject = orderJSON.optJSONObject(TRANSACTION);
        String paymentType = "";
        int moneyAmount = 0;

        if (transactionJSONObject != null) {
            paymentType = transactionJSONObject.optString(PAYMENT_TYPE);
            moneyAmount = transactionJSONObject.optInt(MONEY_AMOUNT, 0);
        }

        // User
        JSONObject userJSONObject = orderJSON.optJSONObject(USER);
        String username = "";

        if (userJSONObject != null) {
            username = userJSONObject.optString(USERNAME);
        }

        // Bonus
        JSONObject bonusJSON = orderJSON.optJSONObject(BONUS);
        int bonus = 0;

        if (bonusJSON != null) {
            bonus = bonusJSON.optInt(MONEY_AMOUNT);
        }

        // Feedback
        JSONObject feedbackJSONJsonObject = orderJSON.optJSONObject(FEEDBACK);
        boolean hasFeedback = feedbackJSONJsonObject != null && feedbackJSONJsonObject.keys().hasNext();

        int feedbackRating = 0;
        if(hasFeedback) {
            feedbackRating = feedbackJSONJsonObject.optInt(FEEDBACK_RATING, 0);
        }

        boolean cashOnly = orderJSON.optBoolean(CASH_ONLY, false);

        return new Order(
                id, status, description, username,
                orderTime, userPickupTime, finishTime, updatedTime,
                startingPointName, endingPointName,
                startingPointGeo, endingPointGeo,
                paymentType, moneyAmount, bonus, distance,
                driver, carCategory, feedbackRating,
                hasFeedback, cashOnly);
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

        return new CarCategory(id, name, description, timePrice, routPrice, minPrice);
    }

    public static User makeUser(JSONObject userJSON) {
        String id = userJSON.optString(ID);
        String role = userJSON.optString(ROLE);
        String username = userJSON.optString(USERNAME);
        String name = userJSON.optString(NAME);
        String sex = userJSON.optString(SEX);
        String email = userJSON.optString(EMAIL);
        Date dateOfBirth = dateFromString(userJSON.optString(DATE_OF_BIRTH));
        String status = userJSON.optString(STATUS);
        int carNumber = userJSON.optInt(CAR_NUMBER);
        int balance = userJSON.optInt(BALANCE);

        return new User(id, role, username, name, sex, email, dateOfBirth, status,
                balance, carNumber);
    }

    public static ArrayList<Transaction> makeTransactions(JSONArray transactionsJSONArray) {
        ArrayList<Transaction> transactions = new ArrayList<>();

        for (int index = 0 ; index < transactionsJSONArray.length() ; index++) {
            JSONObject transactionJSON = transactionsJSONArray.optJSONObject(index);

            JSONObject recipient  = transactionJSON.optJSONObject(TO);

            String recipientUsername = "";
            String recipientRole = "";

            if( recipient != null ) {
                recipientUsername = recipient.optString(USERNAME);
                recipientRole = recipient.optString(ROLE);
            }

            JSONObject sender  = transactionJSON.optJSONObject(FROM);
            String senderUsername = "";
            String senderRole = "";

            if (sender != null) {
                senderUsername = sender.optString(USERNAME);
                senderRole = sender.optString(ROLE);
            }

            Date date = dateFromString(transactionJSON.optString(DATE));

            String description = transactionJSON.optString(DESCRIPTION);
            String paymentType = transactionJSON.optString(PAYMENT_TYPE);
            String id = transactionJSON.optString(ID);
            String orderId = transactionJSON.optString(ORDER);
            int moneyAmount = transactionJSON.optInt(MONEY_AMOUNT);

            transactions.add(new Transaction(id, recipientUsername, recipientRole, senderUsername, senderRole,
                    date, description, paymentType, moneyAmount, orderId));
        }

        return transactions;
    }

    public static Date dateFromString(String dateString) {
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
