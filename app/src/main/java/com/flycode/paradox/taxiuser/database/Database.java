package com.flycode.paradox.taxiuser.database;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;

import com.flycode.paradox.taxiuser.api.GetOrdersHandler;
import com.flycode.paradox.taxiuser.models.CarCategory;
import com.flycode.paradox.taxiuser.models.Driver;
import com.flycode.paradox.taxiuser.models.Order;
import com.flycode.paradox.taxiuser.models.Transaction;
import com.flycode.paradox.taxiuser.settings.UserData;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by anhaytananun on 28.01.16.
 */
public class Database extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private final int SELECT_BLOCK_LIMIT = 500;

    private final String TABLE_CAR_CATEGORY = "carCategory";
    private final String TABLE_TRANSACTION = "transactions";
    private final String TABLE_ORDERS = "orders";
    private final String ID = "id";
    private final String NAME = "name";
    private final String USERNAME = "username";
    private final String MINIMAL = "minimal";
    private final String KM_PRICE = "kmPrice";
    private final String DESCRIPTION = "description";
    private final String MINUTE_PRICE = "minutePrice";
    private final String RECIPIENT_USERNAME = "recipientUsername";
    private final String RECIPIENT_ROLE = "recipientRole";
    private final String SENDER_USERNAME = "senderUsername";
    private final String SENDER_ROLE = "senderRole";
    private final String MONEY_AMOUNT = "moneyAmount";
    private final String BONUS = "bonus";
    private final String PAYMENT_TYPE = "paymentType";
    private final String ORDER_ID = "orderId";
    private final String STATUS = "status";
    private final String DISTANCE = "distance";
    private final String STARTING_POINT_NAME = "startingPointName";
    private final String STARTING_POINT_LAT = "startingPointLat";
    private final String STARTING_POINT_LON = "startingPointLon";
    private final String ENDING_POINT_NAME = "endingPointName";
    private final String ENDING_POINT_LAT = "endingPointLat";
    private final String ENDING_POINT_LON = "endingPointLon";
    private final String DATE = "date";
    private final String FINISH_TIME = "finishTime";
    private final String USER_PICKUP_TIME = "userPickupTime";
    private final String ORDER_TIME = "orderTime";
    private final String CAR_CATEGORY = "carCategory";
    private final String CAR_NUMBER = "carNumber";
    private final String DRIVER = "driver";
    private final String UPDATED_TIME = "updatesTime";
    private final String HAS_FEEDBACK = "hasFeedback";
    private final String FEEDBACK_RATING = "feedbackRating";

    private static Database sharedDatabase;

    private Context context;

    public static Database sharedDatabase(Context context) {
        if (sharedDatabase == null) {
            sharedDatabase = new Database(context);
        }

        return sharedDatabase;
    }

    private Database(Context context) {
        super(context, "FlyCodeParadoxTaxiUser", null, VERSION);

        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String carCategoriesTable = "CREATE TABLE IF NOT EXISTS " + TABLE_CAR_CATEGORY + " ( "
                + ID + " TEXT NOT NULL PRIMARY KEY, "
                + NAME + " TEXT NOT NULL, "
                + DESCRIPTION + " TEXT NOT NULL, "
                + MINIMAL + " REAL NOT NULL, "
                + KM_PRICE + " REAL NOT NULL, "
                + MINUTE_PRICE + " REAL NOT NULL) ";
        String transactionTable = "CREATE TABLE IF NOT EXISTS " + TABLE_TRANSACTION + " ( "
                + ID + " TEXT NOT NULL PRIMARY KEY, "
                + USERNAME + " TEXT NOT NULL, "
                + DESCRIPTION + " TEXT NOT NULL, "
                + RECIPIENT_ROLE + " TEXT NOT NULL, "
                + RECIPIENT_USERNAME + " TEXT NOT NULL, "
                + SENDER_ROLE + " TEXT NOT NULL, "
                + SENDER_USERNAME + " TEXT NOT NULL, "
                + ORDER_ID + " TEXT NOT NULL, "
                + PAYMENT_TYPE + " TEXT NOT NULL, "
                + MONEY_AMOUNT + " REAL NOT NULL, "
                + DATE + " BIGINTEGER NOT NULL) ";
        String orderTable = "CREATE TABLE IF NOT EXISTS " + TABLE_ORDERS + " ( "
                + ID + " TEXT NOT NULL PRIMARY KEY, "
                + STATUS + " TEXT NOT NULL, "
                + STARTING_POINT_NAME + " TEXT NOT NULL, "
                + ENDING_POINT_NAME + " TEXT NOT NULL, "
                + DESCRIPTION + " TEXT NOT NULL, "
                + UPDATED_TIME + " BIGINTEGER NUT NULL, "
                + FINISH_TIME + " BIGINTEGER NUT NULL, "
                + ORDER_TIME + " BIGINTEGER NUT NULL, "
                + USER_PICKUP_TIME + " BIGINTEGER NUT NULL, "
                + MONEY_AMOUNT + " INTEGER NOT NULL, "
                + BONUS + " INTEGER NOT NULL, "
                + DISTANCE + " REAL NOT NULL, "
                + PAYMENT_TYPE + " TEXT NOT NULL, "
                + USERNAME + " TEXT NOT NULL, "
                + STARTING_POINT_LAT + " REAL NOT NULL, "
                + STARTING_POINT_LON + " REAL NOT NULL, "
                + ENDING_POINT_LAT + " REAL NOT NULL, "
                + ENDING_POINT_LON + " REAL NOT NULL, "
                + CAR_CATEGORY + " TEXT NOT NULL, "
                + CAR_NUMBER + " TEXT NOT NULL, "
                + DRIVER + " TEXT NOT NULL, "
                + FEEDBACK_RATING + " INTEGER NOT NULL, "
                + HAS_FEEDBACK + " INTEGER NOT NULL); ";

        db.execSQL(carCategoriesTable);
        db.execSQL(transactionTable);
        db.execSQL(orderTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void storeCategories(final CarCategory[] carCategories) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                SQLiteDatabase db = getWritableDatabase();
                db.delete(TABLE_CAR_CATEGORY, "", null);

                boolean multiQueryAvailable = Build.VERSION.SDK_INT > 15;

                String replaceIntoStatement = "REPLACE INTO " + TABLE_CAR_CATEGORY + " ( "
                        + ID + ","
                        + NAME + ","
                        + DESCRIPTION + ","
                        + MINIMAL + ","
                        + KM_PRICE + ","
                        + MINUTE_PRICE + " )  VALUES ";

                if (carCategories.length > 0) {
                    int blockNumber = carCategories.length / SELECT_BLOCK_LIMIT + 1;
                    int entitiesOffset = carCategories.length;
                    String[] queries = new String[blockNumber];

                    for (int blockIndex = 0; blockIndex < blockNumber; blockIndex++) {
                        StringBuilder categoriesQuery = new StringBuilder(replaceIntoStatement);

                        int limit = entitiesOffset - SELECT_BLOCK_LIMIT > 0 ? entitiesOffset
                                - SELECT_BLOCK_LIMIT
                                : 0;

                        for (int index = entitiesOffset - 1; index >= limit; index--) {
                            CarCategory carCategory = carCategories[index];

                            categoriesQuery = categoriesQuery
                                    .append("( ")
                                    .append(DatabaseUtils.sqlEscapeString(carCategory.getId())).append(",")
                                    .append(DatabaseUtils.sqlEscapeString(carCategory.getName())).append(",")
                                    .append(DatabaseUtils.sqlEscapeString(carCategory.getDescription())).append(",")
                                    .append(carCategory.getMinPrice()).append(",")
                                    .append(carCategory.getRoutePrice()).append(",")
                                    .append(carCategory.getTimePrice()).append(")");

                            if (!multiQueryAvailable) {
                                db.execSQL(categoriesQuery.append(";").toString());
                                categoriesQuery.setLength(0);
                                categoriesQuery.append(replaceIntoStatement);
                            } else if (index != limit) {
                                categoriesQuery = categoriesQuery.append(",");
                            }
                        }

                        entitiesOffset = entitiesOffset - SELECT_BLOCK_LIMIT;

                        categoriesQuery = categoriesQuery.append(";");
                        queries[blockIndex] = categoriesQuery.toString();
                    }

                    if (multiQueryAvailable) {
                        for (int block = queries.length - 1; block >= 0; block--) {
                            db.execSQL(queries[block]);
                        }
                    }
                }

                return null;
            }
        }.execute();
    }

    public void storeTransactions(final ArrayList<Transaction> transactions, final String username) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                if (transactions.size() > 0) {
                    SQLiteDatabase db = getWritableDatabase();

                    boolean multiQueryAvailable = Build.VERSION.SDK_INT > 15;

                    int blockNumber = transactions.size() / SELECT_BLOCK_LIMIT + 1;
                    int entitiesOffset = transactions.size();
                    String[] queries = new String[blockNumber];

                    String replaceIntoStatement =
                            "REPLACE INTO " + TABLE_TRANSACTION + " ( "
                                    + ID + ","
                                    + USERNAME + ","
                                    + DESCRIPTION + ","
                                    + RECIPIENT_USERNAME + ","
                                    + RECIPIENT_ROLE + ","
                                    + SENDER_USERNAME + ","
                                    + SENDER_ROLE + ","
                                    + ORDER_ID + ","
                                    + PAYMENT_TYPE + ","
                                    + DATE + ","
                                    + MONEY_AMOUNT + " )  VALUES ";

                    for (int blockIndex = 0; blockIndex < blockNumber; blockIndex++) {
                        StringBuilder categoriesQuery = new StringBuilder(replaceIntoStatement);

                        int limit = entitiesOffset - SELECT_BLOCK_LIMIT > 0 ? entitiesOffset
                                - SELECT_BLOCK_LIMIT
                                : 0;

                        for (int index = entitiesOffset - 1; index >= limit; index--) {
                            Transaction transaction = transactions.get(index);

                            categoriesQuery = categoriesQuery
                                    .append("( ")
                                    .append(DatabaseUtils.sqlEscapeString(transaction.getId())).append(",")
                                    .append(DatabaseUtils.sqlEscapeString(username)).append(",")
                                    .append(DatabaseUtils.sqlEscapeString(transaction.getDescription())).append(",")
                                    .append(DatabaseUtils.sqlEscapeString(transaction.getRecipientUsername())).append(",")
                                    .append(DatabaseUtils.sqlEscapeString(transaction.getRecipientRole())).append(",")
                                    .append(DatabaseUtils.sqlEscapeString(transaction.getSenderUsername())).append(",")
                                    .append(DatabaseUtils.sqlEscapeString(transaction.getSenderRole())).append(",")
                                    .append(DatabaseUtils.sqlEscapeString(transaction.getOrderId())).append(",")
                                    .append(DatabaseUtils.sqlEscapeString(transaction.getPaymentType())).append(",")
                                    .append(transaction.getDate().getTime()).append(",")
                                    .append(transaction.getMoneyAmount()).append(")");

                            if (!multiQueryAvailable) {
                                db.execSQL(categoriesQuery.append(";").toString());
                                categoriesQuery.setLength(0);
                                categoriesQuery.append(replaceIntoStatement);
                            } else if (index != limit) {
                                categoriesQuery = categoriesQuery.append(",");
                            }
                        }

                        entitiesOffset = entitiesOffset - SELECT_BLOCK_LIMIT;

                        categoriesQuery = categoriesQuery.append(";");
                        queries[blockIndex] = categoriesQuery.toString();
                    }

                    if (multiQueryAvailable) {
                        for (int block = queries.length - 1; block >= 0; block--) {
                            db.execSQL(queries[block]);
                        }
                    }
                }

                return null;
            }
        }.execute();
    }

    public void storeOrders(final ArrayList<Order> orders, final String[] statuses, final GetOrdersHandler listener) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                if (orders.size() > 0) {
                    SQLiteDatabase db = getWritableDatabase();

                    boolean multiQueryAvailable = Build.VERSION.SDK_INT > 15;

                    int blockNumber = orders.size() / SELECT_BLOCK_LIMIT + 1;
                    int entitiesOffset = orders.size();
                    String[] queries = new String[blockNumber];
                    String replaceIntoStatement = generateOrdersReplaceQuery().toString();

                    for (int blockIndex = 0; blockIndex < blockNumber; blockIndex++) {
                        StringBuilder categoriesQuery = new StringBuilder(replaceIntoStatement);

                        int limit = entitiesOffset - SELECT_BLOCK_LIMIT > 0 ? entitiesOffset
                                - SELECT_BLOCK_LIMIT
                                : 0;

                        for (int index = entitiesOffset - 1; index >= limit; index--) {
                            Order order = orders.get(index);

                            categoriesQuery = appendOrderValues(order, categoriesQuery);

                            if (!multiQueryAvailable) {
                                db.execSQL(categoriesQuery.append(";").toString());
                                categoriesQuery.setLength(0);
                                categoriesQuery.append(replaceIntoStatement);
                            } else if (index != limit) {
                                categoriesQuery = categoriesQuery.append(",");
                            }
                        }

                        entitiesOffset = entitiesOffset - SELECT_BLOCK_LIMIT;

                        categoriesQuery = categoriesQuery.append(";");
                        queries[blockIndex] = categoriesQuery.toString();
                    }

                    if (multiQueryAvailable) {
                        for (int block = queries.length - 1; block >= 0; block--) {
                            db.execSQL(queries[block]);
                        }
                    }
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                ArrayList<Order> dbOrders = getOrders(-1, -1, statuses, null, UserData.sharedData(context).getUsername());
                listener.onGetOrdersSuccess(dbOrders, dbOrders.size());
            }
        }.execute();
    }

    public void storeOrder(final Order order) {
        if (!Order.isRealUpdate(order, context)) {
            return;
        }

        StringBuilder ordersQuery = generateOrdersReplaceQuery();
        ordersQuery = appendOrderValues(order, ordersQuery);

        ordersQuery = ordersQuery.append(";");
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL(ordersQuery.toString());
    }

    public CarCategory[] getCarCategories() {
        String query = "SELECT * FROM " + TABLE_CAR_CATEGORY;
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        CarCategory[] carCategories = new CarCategory[cursor.getCount()];

        int idIndex = cursor.getColumnIndex(ID);
        int nameIndex = cursor.getColumnIndex(NAME);
        int descriptionIndex = cursor.getColumnIndex(DESCRIPTION);
        int minimalIndex = cursor.getColumnIndex(MINIMAL);
        int kmPriceIndex = cursor.getColumnIndex(KM_PRICE);
        int minutePriceIndex = cursor.getColumnIndex(MINUTE_PRICE);
        int index = 0;

        while (!cursor.isAfterLast()) {
            carCategories[index] = new CarCategory(
                    cursor.getString(idIndex),
                    cursor.getString(nameIndex),
                    cursor.getString(descriptionIndex),
                    cursor.getDouble(minutePriceIndex),
                    cursor.getDouble(kmPriceIndex),
                    cursor.getDouble(minimalIndex)
            );

            cursor.moveToNext();
            index++;
        }

        cursor.close();

        return carCategories;
    }

    public ArrayList<Transaction> getTransactions(int skip, int limit, String username) {
        String query = "SELECT * FROM " + TABLE_TRANSACTION
                + " WHERE " + USERNAME + " = " + DatabaseUtils.sqlEscapeString(username)
                + " ORDER BY " + DATE + " DESC ";

        //TODO: Change limit to paging some day
        if (skip < 0 && limit < 0) {
            skip = 0;
            limit = 100;
        }

        if (skip >= 0 && limit >= 0) {
            query = query + " LIMIT " + skip + " , " + limit;
        }

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        ArrayList<Transaction> transactions = new ArrayList<>();

        int idIndex = cursor.getColumnIndex(ID);
        int descriptionIndex = cursor.getColumnIndex(DESCRIPTION);
        int recipientRoleIndex = cursor.getColumnIndex(RECIPIENT_ROLE);
        int recipientUsernameIndex = cursor.getColumnIndex(RECIPIENT_USERNAME);
        int senderRoleIndex = cursor.getColumnIndex(SENDER_ROLE);
        int senderUsernameIndex = cursor.getColumnIndex(SENDER_USERNAME);
        int orderIdIndex = cursor.getColumnIndex(SENDER_USERNAME);
        int paymentType = cursor.getColumnIndex(PAYMENT_TYPE);
        int moneyAmountIndex = cursor.getColumnIndex(MONEY_AMOUNT);
        int dateIndex = cursor.getColumnIndex(DATE);

        while (!cursor.isAfterLast()) {
            transactions.add(new Transaction(
                    cursor.getString(idIndex),
                    cursor.getString(recipientUsernameIndex),
                    cursor.getString(recipientRoleIndex),
                    cursor.getString(senderUsernameIndex),
                    cursor.getString(senderRoleIndex),
                    new Date(cursor.getLong(dateIndex)),
                    cursor.getString(descriptionIndex),
                    cursor.getString(paymentType),
                    cursor.getDouble(moneyAmountIndex),
                    cursor.getString(orderIdIndex)
            ));

            cursor.moveToNext();
        }

        cursor.close();

        return transactions;
    }

    public ArrayList<Order> getOrders(int skip, int limit, String[] statuses, String orderId, String username) {
        ArrayList<Order> orders = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_ORDERS
                + " WHERE " + USERNAME + " = " + DatabaseUtils.sqlEscapeString(username);

        if (statuses.length > 0) {
            query = query + " AND ( ";

            for (int index = 0; index < statuses.length; index++) {
                query = query + STATUS + " = " + DatabaseUtils.sqlEscapeString(statuses[index]);

                if (index != statuses.length - 1) {
                    query = query + " OR ";
                }
            }

            query = query + " ) ";
        }
        if (orderId != null && !orderId.isEmpty()) {
            query = query + " AND " + ID + " = " + DatabaseUtils.sqlEscapeString(orderId);
        }

        query = query + " ORDER BY " + UPDATED_TIME + " DESC ";

        //TODO: Change limit to paging some day
        if (skip < 0 && limit < 0) {
            skip = 0;
            limit = 100;
        }

        if (skip >= 0 && limit >= 0) {
            query = query + " LIMIT " + skip + "," + limit;
        }

        Cursor cursor = getReadableDatabase().rawQuery(query, null);
        cursor.moveToFirst();

        int idIndex = cursor.getColumnIndex(ID);
        int statusIndex = cursor.getColumnIndex(STATUS);
        int startingPointNameIndex = cursor.getColumnIndex(STARTING_POINT_NAME);
        int endingPointNameIndex = cursor.getColumnIndex(ENDING_POINT_NAME);
        int descriptionIndex = cursor.getColumnIndex(DESCRIPTION);
        int finishTimeIndex = cursor.getColumnIndex(FINISH_TIME);
        int orderTimeIndex = cursor.getColumnIndex(ORDER_TIME);
        int userPickupTimeIndex = cursor.getColumnIndex(USER_PICKUP_TIME);
        int moneyAmountIndex = cursor.getColumnIndex(MONEY_AMOUNT);
        int bonusIndex = cursor.getColumnIndex(BONUS);
        int distanceIndex = cursor.getColumnIndex(DISTANCE);
        int paymentTypeIndex = cursor.getColumnIndex(PAYMENT_TYPE);
        int usernameIndex = cursor.getColumnIndex(USERNAME);
        int startingPointLatIndex = cursor.getColumnIndex(STARTING_POINT_LAT);
        int startingPointLonIndex = cursor.getColumnIndex(STARTING_POINT_LON);
        int endingPointLatIndex = cursor.getColumnIndex(ENDING_POINT_LAT);
        int endingPointLonIndex = cursor.getColumnIndex(ENDING_POINT_LON);
        int carCategoryIndex = cursor.getColumnIndex(CAR_CATEGORY);
        int carNumberIndex = cursor.getColumnIndex(CAR_NUMBER);
        int driverIndex = cursor.getColumnIndex(DRIVER);
        int updateTimeIndex = cursor.getColumnIndex(UPDATED_TIME);
        int feedbackRatingIndex = cursor.getColumnIndex(FEEDBACK_RATING);
        int hasFeedbackIndex = cursor.getColumnIndex(HAS_FEEDBACK);

        long orderTimeMillis;
        long pickupTimeMillis;
        long finishTimeMillis;

        double startingPointLat;
        double startingPointLon;
        double endingPointLat;
        double endingPointLon;

        while (!cursor.isAfterLast()) {
            orderTimeMillis = cursor.getLong(orderTimeIndex);
            pickupTimeMillis = cursor.getLong(userPickupTimeIndex);
            finishTimeMillis = cursor.getLong(finishTimeIndex);

            startingPointLat = cursor.getDouble(startingPointLatIndex);
            startingPointLon = cursor.getDouble(startingPointLonIndex);
            endingPointLat = cursor.getDouble(endingPointLatIndex);
            endingPointLon = cursor.getDouble(endingPointLonIndex);

            Location startingLocation = null;
            Location endingLocation = null;

            if (startingPointLat != 0 || startingPointLon != 0) {
                startingLocation = new Location("SQL Provider");
                startingLocation.setLatitude(startingPointLat);
                startingLocation.setLongitude(startingPointLon);
            }

            if (endingPointLat != 0 || endingPointLon != 0) {
                endingLocation = new Location("SQL Provider");
                endingLocation.setLatitude(endingPointLat);
                endingLocation.setLongitude(endingPointLon);
            }

            orders.add(new Order(
                    cursor.getString(idIndex),
                    cursor.getString(statusIndex),
                    cursor.getString(descriptionIndex),
                    cursor.getString(usernameIndex),
                    orderTimeMillis > 0 ? new Date(orderTimeMillis) : null,
                    pickupTimeMillis > 0 ? new Date(pickupTimeMillis) : null,
                    finishTimeMillis > 0 ? new Date(finishTimeMillis) : null,
                    new Date(cursor.getLong(updateTimeIndex)),
                    cursor.getString(startingPointNameIndex),
                    cursor.getString(endingPointNameIndex),
                    startingLocation,
                    endingLocation,
                    cursor.getString(paymentTypeIndex),
                    cursor.getInt(moneyAmountIndex),
                    cursor.getInt(bonusIndex),
                    cursor.getDouble(distanceIndex),
                    new Driver(
                            cursor.getString(carNumberIndex),
                            cursor.getString(driverIndex)
                    ),
                    cursor.getString(carCategoryIndex),
                    cursor.getInt(feedbackRatingIndex),
                    cursor.getInt(hasFeedbackIndex) == 1
            ));

            cursor.moveToNext();
        }

        cursor.close();

        return orders;
    }

    private StringBuilder generateOrdersReplaceQuery() {
        return new StringBuilder(
                "REPLACE INTO " + TABLE_ORDERS + " ( "
                        + ID + " , "
                        + STATUS + " , "
                        + STARTING_POINT_NAME + " , "
                        + ENDING_POINT_NAME + " , "
                        + DESCRIPTION + " , "
                        + UPDATED_TIME + " , "
                        + FINISH_TIME + " , "
                        + ORDER_TIME + " , "
                        + USER_PICKUP_TIME + " , "
                        + MONEY_AMOUNT + " , "
                        + BONUS + " , "
                        + DISTANCE + " , "
                        + PAYMENT_TYPE + " , "
                        + USERNAME + " , "
                        + STARTING_POINT_LAT + " , "
                        + STARTING_POINT_LON + " , "
                        + ENDING_POINT_LAT + " , "
                        + ENDING_POINT_LON + " , "
                        + CAR_CATEGORY + " , "
                        + CAR_NUMBER + " , "
                        + DRIVER + " , "
                        + FEEDBACK_RATING + " , "
                        + HAS_FEEDBACK + " )  VALUES ");
    }

    private StringBuilder appendOrderValues(Order order, StringBuilder query) {
        return query
            .append("( ")
            .append(DatabaseUtils.sqlEscapeString(order.getId())).append(",")
            .append(DatabaseUtils.sqlEscapeString(order.getStatus())).append(",")
            .append(DatabaseUtils.sqlEscapeString(order.getStartingPointName())).append(",")
            .append(DatabaseUtils.sqlEscapeString(order.getEndingPointName())).append(",")
            .append(DatabaseUtils.sqlEscapeString(order.getDescription())).append(",")
            .append(order.getUpdatedTime().getTime()).append(",")
            .append(order.getFinishTime() == null ? 0 : order.getFinishTime().getTime()).append(",")
            .append(order.getOrderTime() == null ? 0 : order.getOrderTime().getTime()).append(",")
            .append(order.getUserPickupTime() == null ? 0 : order.getUserPickupTime().getTime()).append(",")
            .append(order.getMoneyAmount()).append(",")
            .append(order.getBonus()).append(",")
            .append(order.getDistance()).append(",")
            .append(DatabaseUtils.sqlEscapeString(order.getPaymentType())).append(",")
            .append(DatabaseUtils.sqlEscapeString(order.getUsername())).append(",")
            .append(order.getStartingPointGeo() == null ? 0 : order.getStartingPointGeo().getLatitude()).append(",")
            .append(order.getStartingPointGeo() == null ? 0 : order.getStartingPointGeo().getLongitude()).append(",")
            .append(order.getEndingPointGeo() == null ? 0 : order.getEndingPointGeo().getLatitude()).append(",")
            .append(order.getEndingPointGeo() == null ? 0 : order.getEndingPointGeo().getLongitude()).append(",")
            .append(DatabaseUtils.sqlEscapeString(order.getCarCategory())).append(",")
            .append(DatabaseUtils.sqlEscapeString(order.getDriver() == null ? "" : order.getDriver().getCarNumber())).append(",")
            .append(DatabaseUtils.sqlEscapeString(order.getDriver() == null ? "" : order.getDriver().getUsername())).append(",")
            .append(order.getFeedbackRating()).append(",")
            .append(order.getHasFeedback() ? 1 : 0).append(")");
    }
}
