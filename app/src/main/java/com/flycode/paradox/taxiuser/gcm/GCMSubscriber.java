package com.flycode.paradox.taxiuser.gcm;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.provider.Settings;

import com.flycode.paradox.taxiuser.api.APITalker;
import com.flycode.paradox.taxiuser.api.OnGCMTokenRegisteredListener;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

public class GCMSubscriber {
    private static final String TOKEN = "token";
    private static final String OWNER = "owner";
    private static final String DEVICE = "device";
    private static final String ANDROID = "android";

    public static void registerForGcm(final Activity activity) throws IOException {
        if (!GCMUtils.checkPlayServices(activity)) {
            return;
        }

        GCMUtils.gcm = GoogleCloudMessaging.getInstance(activity.getApplicationContext());

        String registrationId = GCMUtils.getRegistrationId(activity);

        if (registrationId.isEmpty()) {
            registerBackground(activity);
        }
    }

    private static void registerBackground(final Context context) {
        new AsyncTask<Void, String, String>() {
            @Override
            protected String doInBackground(Void... asyncParams) {
                String registrationId;
                try {
                    GCMUtils.gcm.unregister();
                    registrationId = GCMUtils.gcm.register(GCMUtils.SENDER_ID);
                } catch (IOException e) {
                    e.printStackTrace();
                    return "wtf";
                }

                return registrationId;
            }

            @Override
            protected void onPostExecute(final String registrationId) {
                if (registrationId.equals("wtf")) {
                    return;
                }

                sendingToServer(registrationId, context);
            }
        }.execute(null, null, null);
    }

    public static void sendingToServer(final String registrationId, final Context context) {
        String androidDeviceId = Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.ANDROID_ID
        );

        APITalker.sharedTalker().registerGCMToken(
                context,
                registrationId,
                androidDeviceId,
                new OnGCMTokenRegisteredListener() {
                    @Override
                    public void onGCMTokenRegistrationSuccess(String registrationId) {
                        GCMUtils.storeRegistrationId(context, registrationId);
                    }

                    @Override
                    public void onGCMTokenRegistrationFailure() {

                    }
        });
    }
}
