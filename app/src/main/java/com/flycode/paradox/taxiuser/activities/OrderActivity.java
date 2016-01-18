package com.flycode.paradox.taxiuser.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.flycode.paradox.taxiuser.R;
import com.flycode.paradox.taxiuser.api.APITalker;
import com.flycode.paradox.taxiuser.api.GetOrderHandler;
import com.flycode.paradox.taxiuser.constants.OrderStatusConstants;
import com.flycode.paradox.taxiuser.dialogs.ErrorDialog;
import com.flycode.paradox.taxiuser.models.Order;
import com.flycode.paradox.taxiuser.settings.AppSettings;
import com.flycode.paradox.taxiuser.utils.LocaleUtils;
import com.flycode.paradox.taxiuser.utils.TypefaceUtils;
import com.flycode.paradox.taxiuser.views.MaximalScrollView;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.constants.MyLocationTracking;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.views.MapView;

import java.util.Timer;
import java.util.TimerTask;

public class OrderActivity extends Activity implements GetOrderHandler {
    public static final String ORDER = "order";

    private MapView mapView;
    private Order order;

    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleUtils.setLocale(this, AppSettings.sharedSettings(this).getLanguage());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        Typeface icomoonTypeface = TypefaceUtils.getTypeface(this, TypefaceUtils.AVAILABLE_FONTS.ICOMOON);
        Typeface robotoRegularTypeface = TypefaceUtils.getTypeface(this, TypefaceUtils.AVAILABLE_FONTS.ROBOTO_REGULAR);

        TextView actionBarTitleTextView = (TextView) findViewById(R.id.title_text);
        actionBarTitleTextView.setTypeface(robotoRegularTypeface);

        Button actionBarLeftButton = (Button) findViewById(R.id.action_bar_left_button);
        Button actionBarRightButton = (Button) findViewById(R.id.action_bar_right_button);
        actionBarLeftButton.setTypeface(icomoonTypeface);
        actionBarRightButton.setTypeface(icomoonTypeface);

        Intent orderIntent = getIntent();
        order = orderIntent.getParcelableExtra(ORDER);

        mapView = (MapView) findViewById(R.id.map_view);
        mapView.setStyleUrl(Style.LIGHT);
        mapView.setZoomLevel(15);
        mapView.setZoomControlsEnabled(false);
        mapView.setMyLocationEnabled(true);
        mapView.setCompassEnabled(false);
        mapView.setMyLocationTrackingMode(MyLocationTracking.TRACKING_NONE);
        mapView.setLogoVisibility(View.GONE);
        mapView.setAttributionVisibility(View.GONE);
        mapView.onCreate(savedInstanceState);

        init(order);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                new Handler(getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        APITalker.sharedTalker().getOrder(OrderActivity.this, order.getId(), OrderActivity.this);
                    }
                });
            }
        }, 30000, 30000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();

        timer.cancel();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        MaximalScrollView orderInfoScrollView = (MaximalScrollView) findViewById(R.id.order_info_scroll_view);
        View actionBar = findViewById(R.id.action_bar);
        orderInfoScrollView.setMaxHeight((mapView.getMeasuredHeight() - actionBar.getMeasuredHeight()) / 2);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.hold, R.anim.slide_right_out);
    }

    public void init(Order order) {
        LatLng startingPoint = new LatLng(order.getStartingPointGeo().getLatitude(), order.getStartingPointGeo().getLongitude());

        MarkerOptions userLocationMarkerOptions = new MarkerOptions();
        userLocationMarkerOptions.position(startingPoint);
        userLocationMarkerOptions.icon(IconFactory.getInstance(this).fromResource(R.drawable.people));

        mapView.addMarker(userLocationMarkerOptions);
        mapView.setCenterCoordinate(startingPoint);

        if (order.getStatus().equals(OrderStatusConstants.NOT_TAKEN)
                || order.getStatus().equals(OrderStatusConstants.TAKEN)
                || order.getStatus().equals(OrderStatusConstants.STARTED)) {
            initDriverDetails(order);
        } else {
            Button actionBarRightButton = (Button) findViewById(R.id.action_bar_right_button);
            actionBarRightButton.setVisibility(View.GONE);
            initOrderDetails(order);
        }
    }

    public void initOrderDetails(Order order) {
        View orderInfoView = findViewById(R.id.order_info);
        orderInfoView.setVisibility(View.VISIBLE);
        View driverInfoView = findViewById(R.id.driver_info);
        driverInfoView.setVisibility(View.GONE);

        Typeface icomoonTypeface = TypefaceUtils.getTypeface(this, TypefaceUtils.AVAILABLE_FONTS.ICOMOON);
        Typeface robotoTypeface = TypefaceUtils.getTypeface(this, TypefaceUtils.AVAILABLE_FONTS.ROBOTO_THIN);

        TextView dateIconTextView = (TextView) orderInfoView.findViewById(R.id.icon_date);
        TextView locationIconTextView = (TextView) orderInfoView.findViewById(R.id.icon_location);
        TextView costIconTextView = (TextView) orderInfoView.findViewById(R.id.icon_cost);
        TextView distanceIconTextView = (TextView) orderInfoView.findViewById(R.id.icon_distance);
        TextView statusIconTextView = (TextView) orderInfoView.findViewById(R.id.icon_status);

        dateIconTextView.setTypeface(icomoonTypeface);
        locationIconTextView.setTypeface(icomoonTypeface);
        costIconTextView.setTypeface(icomoonTypeface);
        distanceIconTextView.setTypeface(icomoonTypeface);
        statusIconTextView.setTypeface(icomoonTypeface);

        TextView dateTextView = (TextView) orderInfoView.findViewById(R.id.date);
        TextView locationTextView = (TextView) orderInfoView.findViewById(R.id.location);
        TextView costTextView = (TextView) orderInfoView.findViewById(R.id.cost);
        TextView distanceTextView = (TextView) orderInfoView.findViewById(R.id.distance);
        TextView statusTextView = (TextView) orderInfoView.findViewById(R.id.status_label);

        TextView dateValueTextView = (TextView) orderInfoView.findViewById(R.id.date_value);
        TextView startLocationValueTextView = (TextView) orderInfoView.findViewById(R.id.start_location_value);
        TextView endLocationValueTextView = (TextView) orderInfoView.findViewById(R.id.end_location_value);
        TextView costValueTextView = (TextView) orderInfoView.findViewById(R.id.cost_value);
        TextView distanceValueTextView = (TextView) orderInfoView.findViewById(R.id.distance_value);
        TextView statusValueTextView = (TextView) orderInfoView.findViewById(R.id.status_value);

        dateTextView.setTypeface(robotoTypeface);
        locationTextView.setTypeface(robotoTypeface);
        costTextView.setTypeface(robotoTypeface);
        distanceTextView.setTypeface(robotoTypeface);
        statusTextView.setTypeface(robotoTypeface);

        dateValueTextView.setTypeface(robotoTypeface);
        startLocationValueTextView.setTypeface(robotoTypeface);
        endLocationValueTextView.setTypeface(robotoTypeface);
        costValueTextView.setTypeface(robotoTypeface);
        distanceValueTextView.setTypeface(robotoTypeface);
        statusValueTextView.setTypeface(robotoTypeface);

        String durationString = "";
        long durationMillis = order.getFinishTime().getTime() - order.getUserPickupTime().getTime();
        long seconds = durationMillis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        if (hours > 0) {
            durationString = durationString + hours + getString(R.string.hour);
        }
        if (minutes > 0) {
            durationString = durationString +  " " + (minutes - hours * 60) + getString(R.string.minute);
        }

        durationString = durationString + " " + (seconds - minutes * 60) + getString(R.string.second);

        int bonus = order.getMoneyAmount() * 20 / 100;

        dateValueTextView.setText(DateUtils.formatDateTime(
                this,
                order.getOrderTime().getTime(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME
                        | DateUtils.FORMAT_ABBREV_MONTH | DateUtils.FORMAT_SHOW_YEAR
                        | DateUtils.FORMAT_24HOUR));
        startLocationValueTextView.setText(order.getStartingPointName());
        endLocationValueTextView.setText(order.getEndingPointName());
        costValueTextView.setText(order.getMoneyAmount() + " / " + order.getPaymentType() + " / " + bonus);
        distanceValueTextView.setText(order.getDistance() + " - " + durationString);
        statusValueTextView.setText(order.getStatus());
    }

    public void initDriverDetails(Order order) {
        Typeface robotoThinTypeface = TypefaceUtils.getTypeface(this, TypefaceUtils.AVAILABLE_FONTS.ROBOTO_THIN);
        Typeface robotoRegularTypeface = TypefaceUtils.getTypeface(this, TypefaceUtils.AVAILABLE_FONTS.ROBOTO_REGULAR);

        View driverInfoView = findViewById(R.id.driver_info);
        driverInfoView.setVisibility(View.VISIBLE);
        View orderInfoView = findViewById(R.id.order_info);
        orderInfoView.setVisibility(View.GONE);

        if (order.getStatus().equals(OrderStatusConstants.TAKEN)) {
            TextView carNumberTextView = (TextView) driverInfoView.findViewById(R.id.car_number);
            TextView carDescriptionTextView = (TextView) driverInfoView.findViewById(R.id.car_description);

            carNumberTextView.setText(order.getDriver().getCarNumber());

            carNumberTextView.setTypeface(robotoRegularTypeface);
            carDescriptionTextView.setTypeface(robotoRegularTypeface);
        }

        TextView orderStatusTextView = (TextView) driverInfoView.findViewById(R.id.order_status);
        TextView typeLabelTextView = (TextView) driverInfoView.findViewById(R.id.type_label);
        TextView typeValueTextView = (TextView) driverInfoView.findViewById(R.id.type_value);
        Button cancelButton = (Button) driverInfoView.findViewById(R.id.cancel_button);

        orderStatusTextView.setTypeface(robotoRegularTypeface);
        typeValueTextView.setTypeface(robotoRegularTypeface);
        typeLabelTextView.setTypeface(robotoThinTypeface);
        cancelButton.setTypeface(robotoThinTypeface);

        if (order.getStatus().equals(OrderStatusConstants.TAKEN)) {
            orderStatusTextView.setText(getString(R.string.taken));
        } else if (order.getStatus().equals(OrderStatusConstants.NOT_TAKEN)) {
            orderStatusTextView.setText(getString(R.string.not_taken));
        }

        typeValueTextView.setText("Standard");
        cancelButton.setText(getString(R.string.cancel).toUpperCase());
    }

    public void onActionBarLeftButtonClicked(View view) {
        finish();
    }

    public void onActionBarRightButtonClicked(View view) {
    }

    /**
     * GetOrderHandler Methods
     */

    @Override
    public void onGetOrderSuccess(Order order) {
        this.order = order;
        init(order);
    }

    @Override
    public void onGetOrderFailure() {
        ErrorDialog.initialize("Error", "GET ORDER ERROR").show(getFragmentManager(), ErrorDialog.ERROR_DIALOG_TAG);
    }
}
