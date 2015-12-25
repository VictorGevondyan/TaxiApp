package com.flycode.paradox.taxiuser.api;

import com.flycode.paradox.taxiuser.models.Order;

/**
 * Created by anhaytananun on 25.12.15.
 */
public interface MakeOrderListener {
    void onMakeOrderSuccess(Order order);
    void onMakeOrderFail();
}
