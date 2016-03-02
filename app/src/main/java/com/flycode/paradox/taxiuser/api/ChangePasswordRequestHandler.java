package com.flycode.paradox.taxiuser.api;

/**
 * Created by anhaytananun on 06.02.16.
 */
public interface ChangePasswordRequestHandler {
    void onChangePasswordRequestSuccess();
    void onChangePasswordRequestFailure(int statusCode);
}
