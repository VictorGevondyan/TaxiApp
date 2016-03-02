package com.flycode.paradox.taxiuser.api;

import org.json.JSONArray;

/**
 * Created by anhaytananun on 26.12.15.
 */
public interface PointsForOrderListener {
    void onGetPointsForOrderFail(int statusCode);
    void onGetPointsForOrderSuccess(JSONArray response);
}
