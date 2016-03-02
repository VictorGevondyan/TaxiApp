package com.flycode.paradox.taxiuser.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;

import com.flycode.paradox.taxiuser.R;
import com.flycode.paradox.taxiuser.constants.OrderStatusConstants;
import com.flycode.paradox.taxiuser.dialogs.MessageDialog;
import com.flycode.paradox.taxiuser.dialogs.NoGPSDialog;
import com.flycode.paradox.taxiuser.dialogs.NoInternetDialog;
import com.flycode.paradox.taxiuser.models.Order;
import com.flycode.paradox.taxiuser.utils.HardwareAccessibilityUtil;

/**
 * Created by anhaytananun on 25.01.16.
 */
public class SuperActivity extends Activity implements LocationListener {
    public static final String ACTION_ORDER_STATUS_UPDATED = "actionOrderStatusUpdated";

    private static final String NO_NETWORK_TAG = "noNetwork";
    private static final String NO_GPS_TAG = "noGpsTag";
    private static final String ORDER_CANCELED_DIALOG_TAG = "orderCanceledDialogTag";

    private int noGpsDialogsShown = 0;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (!hasFocus) {
            return;
        }

        if (HardwareAccessibilityUtil.checkIfHasPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 1000, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 1000, this);

            onGPSStatusChanged(HardwareAccessibilityUtil.isGPSEnabled(SuperActivity.this));
        } else {
            onGPSPermissionChanged(false);
        }

        if (!HardwareAccessibilityUtil.isNetworkEnabled(this)) {
            onNetworkStateChanged(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter networkFilter = new IntentFilter();
        networkFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkStateReceiver, networkFilter);

        IntentFilter orderUpdateFilter = new IntentFilter();
        orderUpdateFilter.addAction(SuperActivity.ACTION_ORDER_STATUS_UPDATED);
        registerReceiver(orderUpdateBroadcastReceiver, orderUpdateFilter);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(networkStateReceiver);
        unregisterReceiver(orderUpdateBroadcastReceiver);

        if (HardwareAccessibilityUtil.checkIfHasPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            locationManager.removeUpdates(this);
        }

        super.onPause();
    }

    protected void onGPSStatusChanged(boolean isGPSEnabled) {
        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(NO_GPS_TAG);

        if (isGPSEnabled) {
            if (fragment instanceof NoGPSDialog) {
                noGpsDialogsShown--;

                NoGPSDialog noGPSDialog = (NoGPSDialog) fragment;
                noGPSDialog.dismiss();
            }
        } else {
            if (noGpsDialogsShown > 0) {
                return;
            }

            if (fragment instanceof NoGPSDialog) {
                return;
            }

            noGpsDialogsShown++;

            NoGPSDialog
                    .initialize(NoGPSDialog.ISSUE_DISABLED)
                    .show(fragmentManager, NO_GPS_TAG);
        }
    }

    protected void onGPSPermissionChanged(boolean isGPSPermitted) {
        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(NO_GPS_TAG);

        if (isGPSPermitted) {
            if (fragment instanceof NoGPSDialog) {
                noGpsDialogsShown--;

                NoGPSDialog noGPSDialog = (NoGPSDialog) fragment;
                noGPSDialog.dismiss();
            }
        } else {
            if (noGpsDialogsShown > 0) {
                return;
            }

            if (fragment instanceof NoGPSDialog) {
                return;
            }

            noGpsDialogsShown++;

            NoGPSDialog
                    .initialize(NoGPSDialog.ISSUE_PERMISSION)
                    .show(fragmentManager, NO_GPS_TAG);
        }
    }

    protected void onLocationChange(double latitude, double longitude) {

    }

    protected void onNetworkStateChanged(boolean isConnected) {
        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(NO_NETWORK_TAG);

        if (isConnected) {
            if (fragment instanceof NoInternetDialog) {
                NoInternetDialog noInternetDialog = (NoInternetDialog) fragment;
                noInternetDialog.dismiss();
            }
        } else {
            if (fragment instanceof NoInternetDialog) {
                return;
            }

            NoInternetDialog
                    .initialize()
                    .show(fragmentManager, NO_NETWORK_TAG);
        }
    }

    protected void onOrderUpdated(Order order) {
        if (order.getStatus().equals(OrderStatusConstants.CANCELED)) {
            Bundle additionalInfo = new Bundle();
            additionalInfo.putParcelable(OrderActivity.ORDER, order);

            MessageDialog
                    .initialize(
                            getString(R.string.your_order_not_taken),
                            getString(R.string.no_car_for_you),
                            getString(R.string.cancel),
                            getString(R.string.retry)
                    )
            .setAdditionalInfo(additionalInfo)
            .setListener(canceledDialogListener)
            .show(getFragmentManager(), ORDER_CANCELED_DIALOG_TAG);
        }
    }

    /**
     * LocationListener Methods
     */

    @Override
    public void onLocationChanged(Location location) {
        onLocationChange(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        onGPSStatusChanged(HardwareAccessibilityUtil.isGPSEnabled(SuperActivity.this));
    }

    @Override
    public void onProviderDisabled(String provider) {
        onGPSStatusChanged(HardwareAccessibilityUtil.isGPSEnabled(SuperActivity.this));
    }

    private BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onNetworkStateChanged(HardwareAccessibilityUtil.isNetworkEnabled(SuperActivity.this));
        }
    };

    private BroadcastReceiver orderUpdateBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Order order = intent.getParcelableExtra(OrderActivity.ORDER);
            onOrderUpdated(order);
        }
    };

    private MessageDialog.MessageDialogListener canceledDialogListener = new MessageDialog.MessageDialogListener() {
        @Override
        public void onNegativeClicked(MessageDialog messageDialog) {

        }

        @Override
        public void onPositiveClicked(MessageDialog messageDialog) {
            Order order = messageDialog.getAdditionalInfo().getParcelable(OrderActivity.ORDER);

            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(order.getId(), 0);

            Intent menuActivityIntent = new Intent(getApplicationContext(), MenuActivity.class);
            menuActivityIntent.putExtra(OrderActivity.ORDER, order);
            menuActivityIntent.putExtra(OrderActivity.RETRY, true);
            menuActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            menuActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            menuActivityIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

            getApplicationContext().startActivity(menuActivityIntent);
        }
    };
}
