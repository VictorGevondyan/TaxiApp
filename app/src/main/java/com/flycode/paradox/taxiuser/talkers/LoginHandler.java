package com.flycode.paradox.taxiuser.talkers;

/**
 * Created by victor on 12/9/15.
 */
public interface LoginHandler {
    public void onLoginSuccess();
    public void onLoginFailure(String error);

}
