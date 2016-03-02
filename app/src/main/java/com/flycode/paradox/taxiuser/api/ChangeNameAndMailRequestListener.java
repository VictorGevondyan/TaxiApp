package com.flycode.paradox.taxiuser.api;

/**
 * Created by victor on 12/26/15.
 */
public interface ChangeNameAndMailRequestListener {
    void onChangeNameAndMailSuccess();
    void onChangeNameAndMailFailure(int statusCode);
}
