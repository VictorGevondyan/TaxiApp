package com.flycode.paradox.taxiuser.api;

import com.flycode.paradox.taxiuser.models.User;

/**
 * Created by victor on 12/25/15.
 */
public interface GetUserHandler {
    void onGetDriverSuccess(User user);
    void onGetDriverFailure();
}
