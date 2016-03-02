package com.flycode.paradox.taxiuser.api;

/**
 * Created by anhaytananun on 05.02.16.
 */
public interface FeedbackRequestHandler {
    void onFeedbackRequestSuccess();
    void onFeedbackRequestFailure(int statusCode);
}
