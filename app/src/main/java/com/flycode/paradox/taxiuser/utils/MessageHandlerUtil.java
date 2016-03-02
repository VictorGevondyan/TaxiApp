package com.flycode.paradox.taxiuser.utils;

import android.app.Activity;
import android.content.Intent;

import com.flycode.paradox.taxiuser.R;
import com.flycode.paradox.taxiuser.activities.LoginActivity;
import com.flycode.paradox.taxiuser.dialogs.MessageDialog;
import com.flycode.paradox.taxiuser.settings.AppSettings;

/**
 * Created by anhaytananun on 02.02.16.
 */
public class MessageHandlerUtil {
    public static void showErrorForStatusCode(int statusCode, Activity activity) {
        if (statusCode == 0) {
            showMessage(R.string.cannot_complete_request_title, R.string.cannot_complete_request_message, activity);
        } else if (statusCode == 500) {
            showMessage(R.string.server_error_title, R.string.server_error_message, activity);
        } else if (statusCode == 422) {
            if (activity instanceof LoginActivity) {
                showMessage(R.string.wrong_credentials, R.string.phone_pass_combination_wrong, activity);
            } else {
                showMessage(R.string.invalid_data_title, R.string.invalid_data_message, activity);
            }
        } else if (statusCode == 401) {
            if (activity instanceof LoginActivity) {
                showMessage(R.string.wrong_credentials, R.string.phone_pass_combination_wrong, activity);
            } else {
                AppSettings.sharedSettings(activity).setIsUserLoggedIn(false);
                Intent intent = new Intent(activity, LoginActivity.class);
                activity.startActivity(intent);
                activity.finish();
            }
        } else {
            showMessage(
                    activity.getString(R.string.unknown_error_title),
                    activity.getString(R.string.unknown_error_message) + " " + statusCode,
                    activity);
        }
    }

    public static void showMessage(int titleId, int messageId, Activity activity) {
        MessageDialog
                .initialize(
                        activity.getString(titleId),
                        activity.getString(messageId),
                        "",
                        activity.getString(R.string.ok)
                )
                .show(activity.getFragmentManager(), "ErrorDialog");
    }

    public static void showMessage(String title, String message, Activity activity) {
        MessageDialog
                .initialize(
                        title,
                        message,
                        "",
                        activity.getString(R.string.ok)
                )
                .show(activity.getFragmentManager(), "ErrorDialog");
    }
}
