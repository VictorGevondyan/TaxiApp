package com.flycode.paradox.taxiuser.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.flycode.paradox.taxiuser.R;
import com.flycode.paradox.taxiuser.activities.OrderActivity;
import com.flycode.paradox.taxiuser.activities.SuperActivity;
import com.flycode.paradox.taxiuser.api.APITalker;
import com.flycode.paradox.taxiuser.api.GetOrderHandler;
import com.flycode.paradox.taxiuser.constants.OrderStatusConstants;
import com.flycode.paradox.taxiuser.models.Order;

import org.json.JSONException;
import org.json.JSONObject;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    private static final String MESSAGE = "message";
    private static final String TYPE = "type";
    private static final String ORDER = "order";
    private static final String ARRIVAL_TIME = "arrivalTime";

    private static int idCounter = 0;

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

        if (messageObject.has(ORDER)) {
            APITalker.sharedTalker().getOrder(context, messageObject.optString(ORDER), new GetOrderHandler() {
                @Override
                public void onGetOrderSuccess(Order order) {
                    if (Order.isOldVersion(order, context)) {
                        return;
                    }

                    int arrivalTime = messageObject.optInt(ARRIVAL_TIME, -1);

                    generateUserNotification(context, order, arrivalTime, messageObject.optString(TYPE));
                }

                @Override
                public void onGetOrderFailure(int statusCode) {

                }
            });
        }
    }

    private void generateUserNotification(final Context context, final Order order, final int arrivalTime, final String type) {
        Intent notificationIntent = new Intent(Intent.ACTION_MAIN);
        notificationIntent.setClass(context.getApplicationContext(), OrderActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationIntent.putExtra(OrderActivity.ORDER, order);

        if (type.equals(OrderStatusConstants.CANCELED)) {
            notificationIntent.putExtra(OrderActivity.CAN_RETRY, true);
        }

        PendingIntent contentIntent = PendingIntent.getActivity(context,
                (int) System.currentTimeMillis(),
                notificationIntent, 0
                /*PendingIntent.FLAG_CANCEL_CURRENT*/);

        int title = R.string.something_happens;

        if (type.equals(OrderStatusConstants.FINISHED)) {
            title = R.string.your_order_finished;
        } else if (type.equals(OrderStatusConstants.TAKEN)) {
            title = R.string.your_order_taken;
        } else if (type.equals(OrderStatusConstants.STARTED)) {
            title = R.string.your_order_started;
        } else if (type.equals(OrderStatusConstants.WAITING)) {
            title = R.string.your_driver_waiting;
        } else if (type.equals(OrderStatusConstants.CANCELED)) {
            title = R.string.your_order_not_taken;
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
        notificationBuilder
                .setContentTitle(context.getString(title))
                .setVibrate(new long[]{1000, 1000})
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(contentIntent)
                .setTicker(context.getString(title))
                .setPriority(NotificationCompat.PRIORITY_MAX);

        if (type.equals(OrderStatusConstants.CANCELED)) {
            Intent cancelButtonIntent = new Intent(context, ActionBroadcastReceiver.class);
            cancelButtonIntent.putExtra(OrderActivity.ORDER, order);
            cancelButtonIntent.putExtra(ActionBroadcastReceiver.ACTION_TYPE, ActionBroadcastReceiver.ACTION_TYPE_CANCEL);
            Intent retryButtonIntent = new Intent(context, ActionBroadcastReceiver.class);
            retryButtonIntent.putExtra(OrderActivity.ORDER, order);
            retryButtonIntent.putExtra(ActionBroadcastReceiver.ACTION_TYPE, ActionBroadcastReceiver.ACTION_TYPE_RETRY);

            PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(context, idCounter++, cancelButtonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent retryPendingIntent = PendingIntent.getBroadcast(context, idCounter++, retryButtonIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            notificationBuilder
                    .setContentText(context.getString(R.string.no_car_for_you))
                    .addAction(0, context.getString(R.string.cancel), cancelPendingIntent)
                    .addAction(0, context.getString(R.string.retry), retryPendingIntent);
        }

        if (type.equals(OrderStatusConstants.TAKEN) && arrivalTime > 0) {
            notificationBuilder
                    .setContentText(context.getString(R.string.your_car_will_be_in_minutes, arrivalTime));
        }

        Notification notification = notificationBuilder.build();

        notification.flags |= NotificationCompat.FLAG_AUTO_CANCEL;
        notification.flags |= NotificationCompat.FLAG_SHOW_LIGHTS;

        notification.defaults |= NotificationCompat.DEFAULT_LIGHTS | NotificationCompat.DEFAULT_VIBRATE | NotificationCompat.DEFAULT_SOUND;

        notificationManager.notify(order.getId(), 0, notification);

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(SuperActivity.ACTION_ORDER_STATUS_UPDATED);
        broadcastIntent.putExtra(OrderActivity.ORDER, order);

        context.sendBroadcast(broadcastIntent);
    }
}
