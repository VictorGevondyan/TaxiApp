package com.flycode.paradox.taxiuser.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.flycode.paradox.taxiuser.R;
import com.flycode.paradox.taxiuser.activities.OrderActivity;
import com.flycode.paradox.taxiuser.api.APITalker;
import com.flycode.paradox.taxiuser.api.GetOrderHandler;
import com.flycode.paradox.taxiuser.constants.OrderStatusConstants;
import com.flycode.paradox.taxiuser.models.Order;

import org.json.JSONException;
import org.json.JSONObject;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    private final String MESSAGE = "message";
    private final String TYPE = "type";
    private final String ORDER = "order";

    @Override
    public void onReceive(final Context context, Intent intent) {
        for (String key : intent.getExtras().keySet()) {
            Object value =intent.getExtras().get(key);
            Log.d("ARSENIUM23", String.format("%s %s (%s)", key,
                    value.toString(), value.getClass().getName()));
        }

        final JSONObject messageObject;

        try {
            messageObject = new JSONObject(intent.getExtras().getString(MESSAGE));
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        APITalker.sharedTalker().getOrder(context, messageObject.optString(ORDER), new GetOrderHandler() {
            @Override
            public void onGetOrderSuccess(Order order) {
                generateUserNotification(context, order, messageObject.optString(TYPE));
            }

            @Override
            public void onGetOrderFailure() {

            }
        });

        Toast.makeText(context.getApplicationContext(), "gfsdhsdfhsdhgdhsdfhgdhsdhsdhdghdsghsdhsdfgsfgfsgafsgsdfgfsd", Toast.LENGTH_LONG).show();
    }

    private void generateUserNotification(final Context context, final Order order, final String type) {
        Intent notificationIntent = new Intent(Intent.ACTION_MAIN);
        notificationIntent.setClass(context.getApplicationContext(), OrderActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationIntent.putExtra(OrderActivity.ORDER, order);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                (int) System.currentTimeMillis(),
                notificationIntent, 0
                /*PendingIntent.FLAG_CANCEL_CURRENT*/);

        int title = R.string.something_happens;

        if (type.equals(OrderStatusConstants.FINISHED)) {
            title = R.string.your_order_finished;
        } else if (type.equals(OrderStatusConstants.TAKEN)) {
            title = R.string.your_order_taken;
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(
                Context.NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(context)
                .setContentTitle(context.getString(title))
//                .setContentText(message.getText())
                .setVibrate(new long[]{1000, 1000})
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(contentIntent)
                .setTicker(context.getString(title))
//                .setStyle(style)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .build();

        notification.flags |= NotificationCompat.FLAG_AUTO_CANCEL;
        notification.flags |= NotificationCompat.FLAG_SHOW_LIGHTS;

        notification.defaults |= NotificationCompat.DEFAULT_LIGHTS | NotificationCompat.DEFAULT_VIBRATE | NotificationCompat.DEFAULT_SOUND;

        notificationManager.notify(order.getId(), 0, notification);
    }
}
