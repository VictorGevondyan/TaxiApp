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

        handleHardwareState();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Register for network status updates

        IntentFilter networkFilter = new IntentFilter();
        networkFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkStateReceiver, networkFilter);

        // Register for order status updates

        IntentFilter orderUpdateFilter = new IntentFilter();
        orderUpdateFilter.addAction(SuperActivity.ACTION_ORDER_STATUS_UPDATED);
        registerReceiver(orderUpdateBroadcastReceiver, orderUpdateFilter);

        handleHardwareState();
    }

    @Override
    protected void onPause() {
        // Unregister from events

        unregisterReceiver(networkStateReceiver);
        unregisterReceiver(orderUpdateBroadcastReceiver);

        // Remove location updates. Check for permission to avoid no permission error.

        if (HardwareAccessibilityUtil.checkIfHasPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            locationManager.removeUpdates(this);
        }

        super.onPause();
    }

    public void handleHardwareState(){
        if (HardwareAccessibilityUtil.checkIfHasPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

            // Register for location updates.

            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 1000, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 1000, this);

            // Show appropriate dialog if GPS is not enabled.

            onGPSStatusChanged(HardwareAccessibilityUtil.isGPSEnabled(SuperActivity.this));
        } else {
            // No GPS permissions. Show appropriate dialog.

            onGPSPermissionChanged(false);
        }

        if (!HardwareAccessibilityUtil.isNetworkEnabled(this)) {
            // No network. Show appropriate dialog.

            onNetworkStateChanged(false);
        }
    }

    /**
     * Show appropriate dialog if GPS is not enabled. Remove shown dialog, if GPS access has been restored.
     * @param isGPSEnabled true if GPS is enabled, false otherwise
     */
    protected void onGPSStatusChanged(boolean isGPSEnabled) {
        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(NO_GPS_TAG);

        if (isGPSEnabled) {
            // GPS is enabled, dismiss dialog if it is presented.

            if (fragment instanceof NoGPSDialog) {
                noGpsDialogsShown--;

                NoGPSDialog noGPSDialog = (NoGPSDialog) fragment;
                noGPSDialog.dismiss();
            }
        } else {
            // GPS is not enabled.
            // Check if dialog is already presented.

            if (noGpsDialogsShown > 0) {
                return;
            }

            if (fragment instanceof NoGPSDialog) {
                return;
            }

            noGpsDialogsShown++;

            // Present dialog.

            NoGPSDialog
                    .initialize(NoGPSDialog.ISSUE_DISABLED)
                    .show(fragmentManager, NO_GPS_TAG);
        }
    }

    /**
     * Show appropriate dialog if GPS usage is not permitted. Remove shown dialog, if GPS usage permission has been restored.
     * @param isGPSEnabled true if GPS usage is not permitted, false otherwise
     */
    protected void onGPSPermissionChanged(boolean isGPSPermitted) {
        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(NO_GPS_TAG);

        if (isGPSPermitted) {
            // Have GPS permission, dismiss dialog if it is presented.

            if (fragment instanceof NoGPSDialog) {
                noGpsDialogsShown--;

                NoGPSDialog noGPSDialog = (NoGPSDialog) fragment;
                noGPSDialog.dismiss();
            }
        } else {
            // No GPS Permission.
            // Check if dialog is already presented.

            if (noGpsDialogsShown > 0) {
                return;
            }

            if (fragment instanceof NoGPSDialog) {
                return;
            }

            noGpsDialogsShown++;

            // Present dialog.

            NoGPSDialog
                    .initialize(NoGPSDialog.ISSUE_PERMISSION)
                    .show(fragmentManager, NO_GPS_TAG);
        }
    }

    /**
     * Called on location change
     * @param latitude New latitude
     * @param longitude New longitude
     */
    protected void onLocationChange(double latitude, double longitude) {

    }

    /**
     * Called on network state change. Shows appropriate dialog if there is not network connected, dismisses shown dialog if network has been restored.
     * @param isConnected true if connected to network, false otherwise
     */
    protected void onNetworkStateChanged(boolean isConnected) {
        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(NO_NETWORK_TAG);

        if (isConnected) {
            // Have connection. Dismiss dialog if shown.

            if (fragment instanceof NoInternetDialog) {
                NoInternetDialog noInternetDialog = (NoInternetDialog) fragment;
                noInternetDialog.dismiss();
            }
        } else {
            // No connection.
            // Check if dialog is already shown

            if (fragment instanceof NoInternetDialog) {
                return;
            }

            // Show dialog.

            NoInternetDialog
                    .initialize()
                    .show(fragmentManager, NO_NETWORK_TAG);
        }
    }

    /**
     * Called when order has been updated. Show appropriate message if order was canceled.
     * @param order
     */
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
            // User chooses to retry order.

            Order order = messageDialog.getAdditionalInfo().getParcelable(OrderActivity.ORDER);

            // Remove notification from notification center.

            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(order.getId(), 0);

            // Reopen MenuActivity with appropriate extras.

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
