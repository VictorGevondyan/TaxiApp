package com.flycode.paradox.taxiuser.api;

import com.flycode.paradox.taxiuser.models.Order;

import java.util.ArrayList;

/**
 * Created by victor on 12/15/15.
 */
public interface GetOrdersHandler {
    public void onGetOrdersSuccess( ArrayList<Order> ordersList, int ordersCount );
    public void onGetOrdersFailure(int statusCode);
}
