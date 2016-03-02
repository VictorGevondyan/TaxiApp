package com.flycode.paradox.taxiuser.api;

import com.flycode.paradox.taxiuser.models.Order;

/**
 * Created by anhaytananun on 13.02.16.
 */
public interface UpdateOrderStatusHandler {
    void onUpdateOrderStatusSuccess(Order order);
    void onUpdateOrderStatusFailure(int statusCode);
}
