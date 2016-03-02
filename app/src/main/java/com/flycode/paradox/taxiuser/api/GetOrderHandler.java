package com.flycode.paradox.taxiuser.api;

import com.flycode.paradox.taxiuser.models.Order;

/**
 * Created by anhaytananun on 26.12.15.
 */
public interface GetOrderHandler {
    void onGetOrderSuccess(Order order);
    void onGetOrderFailure(int statusCode);
}
