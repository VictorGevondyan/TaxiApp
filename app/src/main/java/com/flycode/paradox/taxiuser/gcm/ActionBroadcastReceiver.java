package com.flycode.paradox.taxiuser.gcm;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.flycode.paradox.taxiuser.activities.MenuActivity;
import com.flycode.paradox.taxiuser.activities.OrderActivity;
import com.flycode.paradox.taxiuser.models.Order;

/**
 * Created by anhaytananun on 13.02.16.
 */
public class ActionBroadcastReceiver extends BroadcastReceiver {
    public static final String ACTION_TYPE = "actionType";
    public static final String ACTION_TYPE_CANCEL = "actionTypeCancel";
    public static final String ACTION_TYPE_RETRY = "actionTypeRetry";

    @Override
    public void onReceive(Context context, Intent intent) {
        Order order = intent.getParcelableExtra(OrderActivity.ORDER);

        if (ACTION_TYPE_RETRY.equals(intent.getStringExtra(ACTION_TYPE))) {
            Intent closeIntent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            context.sendBroadcast(closeIntent);

            Intent menuActivityIntent = new Intent(context, MenuActivity.class);
            menuActivityIntent.putExtra(OrderActivity.ORDER, order);
            menuActivityIntent.putExtra(OrderActivity.RETRY, true);
            menuActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            menuActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            menuActivityIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

            context.getApplicationContext().startActivity(menuActivityIntent);
        }

        NotificationManager notificationManager = (NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(order.getId(), 0);
    }

}
