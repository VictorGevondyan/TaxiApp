package com.flycode.paradox.taxiuser.gcm;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GCMUtils {
    public final static String SENDER_ID = "251424049357";
    private final static String PROPERTY_REG_ID = "propertyRegId";
    private final static String PROPERTY_APP_VERSION = "propertyAppVersion";
    private final static String GCM_PREFERENCES = "GCM_PREFERENCES";

    public static GoogleCloudMessaging gcm;

    public static boolean checkPlayServices(Activity activity) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
                        9000).show();
            } else {
                Log.i("BAD GCM MESSAGE", "This device is not supported.");
                activity.finish();
            }
            return false;
        }
        return true;
    }

    public static String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");

        if (registrationId.isEmpty()) {
            return "";
        }

        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);

        if (registeredVersion != currentVersion) {
            return "";
        }
        return registrationId;
    }

    public static SharedPreferences getGCMPreferences(Context context) {
        return context.getSharedPreferences(GCM_PREFERENCES, Context.MODE_PRIVATE);
    }

    public static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    public static void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    public static void removeRegistrationId(Context context) {
        SharedPreferences.Editor edit = getGCMPreferences(context).edit();
        edit.remove(PROPERTY_REG_ID);
        edit.commit();
    }
}
