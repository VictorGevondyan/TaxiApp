package com.flycode.paradox.taxiuser.api;

import com.flycode.paradox.taxiuser.models.Transaction;

import java.util.ArrayList;

/**
 * Created by victor on 12/12/15.
 */
public interface GetOwnTransactionsHandler {
    public void onGetOwnTransactionsSuccess(ArrayList<Transaction> ordersList);
    public void onGetOwnTransactionsFailure(int statusCode);
}
