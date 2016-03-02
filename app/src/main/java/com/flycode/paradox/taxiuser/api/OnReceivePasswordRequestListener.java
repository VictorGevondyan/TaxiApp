package com.flycode.paradox.taxiuser.api;

/**
 * Created by anhaytananun on 31.01.16.
 */
public interface OnReceivePasswordRequestListener {
    void onReceivePasswordRequestSuccess();
    void onReceivePasswordRequestFail(int statusCode);
}
