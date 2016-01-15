package com.flycode.paradox.taxiuser.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.flycode.paradox.taxiuser.R;
import com.flycode.paradox.taxiuser.constants.OrderStatusConstants;
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

public class OrderActivity extends Activity {
    public static final String ORDER = "order";

    private MapView mapView;
    private Order order;

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

//        LatLng startingPoint = new LatLng(order.getStartingPointGeo().getLatitude(), order.getStartingPointGeo().getLongitude());
        LatLng startingPoint = new LatLng(40.177570, 44.512549);

        MarkerOptions userLocationMarkerOptions = new MarkerOptions();
        userLocationMarkerOptions.position(startingPoint);
        userLocationMarkerOptions.icon(IconFactory.getInstance(this).fromResource(R.drawable.people));

        mapView = (MapView) findViewById(R.id.map_view);
        mapView.setStyleUrl(Style.LIGHT);
        mapView.setCenterCoordinate(startingPoint);
        mapView.setZoomLevel(15);
        mapView.setZoomControlsEnabled(false);
        mapView.setMyLocationEnabled(true);
        mapView.setCompassEnabled(false);
        mapView.setMyLocationTrackingMode(MyLocationTracking.TRACKING_NONE);
        mapView.setLogoVisibility(View.GONE);
        mapView.setAttributionVisibility(View.GONE);
        mapView.onCreate(savedInstanceState);
        mapView.addMarker(userLocationMarkerOptions);

        if (order.getStatus().equals(OrderStatusConstants.NOT_TAKEN)
                || order.getStatus().equals(OrderStatusConstants.TAKEN)) {
            initDriverDetails(order);
        } else {
            actionBarRightButton.setVisibility(View.GONE);
            initOrderDetails(order);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
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

    public void initOrderDetails(Order order) {
        findViewById(R.id.order_info).setVisibility(View.VISIBLE);

        Typeface icomoonTypeface = TypefaceUtils.getTypeface(this, TypefaceUtils.AVAILABLE_FONTS.ICOMOON);
        Typeface robotoTypeface = TypefaceUtils.getTypeface(this, TypefaceUtils.AVAILABLE_FONTS.ROBOTO_THIN);

        TextView dateIconTextView = (TextView) findViewById(R.id.icon_date);
        TextView locationIconTextView = (TextView) findViewById(R.id.icon_location);
        TextView costIconTextView = (TextView) findViewById(R.id.icon_cost);
        TextView distanceIconTextView = (TextView) findViewById(R.id.icon_distance);
        TextView statusIconTextView = (TextView) findViewById(R.id.icon_status);

        dateIconTextView.setTypeface(icomoonTypeface);
        locationIconTextView.setTypeface(icomoonTypeface);
        costIconTextView.setTypeface(icomoonTypeface);
        distanceIconTextView.setTypeface(icomoonTypeface);
        statusIconTextView.setTypeface(icomoonTypeface);

        TextView dateTextView = (TextView) findViewById(R.id.date);
        TextView locationTextView = (TextView) findViewById(R.id.location);
        TextView costTextView = (TextView) findViewById(R.id.cost);
        TextView distanceTextView = (TextView) findViewById(R.id.distance);
        TextView statusTextView = (TextView) findViewById(R.id.status_label);

        TextView dateValueTextView = (TextView) findViewById(R.id.date_value);
        TextView startLocationValueTextView = (TextView) findViewById(R.id.start_location_value);
        TextView endLocationValueTextView = (TextView) findViewById(R.id.end_location_value);
        TextView costValueTextView = (TextView) findViewById(R.id.cost_value);
        TextView distanceValueTextView = (TextView) findViewById(R.id.distance_value);
        TextView statusValueTextView = (TextView) findViewById(R.id.status_value);

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

        dateValueTextView.setText(DateUtils.formatDateTime(
                this,
                order.getOrderTime().getTime(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME
                        | DateUtils.FORMAT_ABBREV_MONTH | DateUtils.FORMAT_SHOW_YEAR
                        | DateUtils.FORMAT_24HOUR));
        startLocationValueTextView.setText(order.getStartingPointName());
        endLocationValueTextView.setText(order.getEndingPointName());
        costValueTextView.setText(order.getMoneyAmount() + " / " + order.getPaymentType());
        distanceValueTextView.setText("0");
        statusValueTextView.setText(order.getStatus());
    }

    public void initDriverDetails(Order order) {
        findViewById(R.id.driver_info).setVisibility(View.VISIBLE);
    }

    public void onActionBarLeftButtonClicked(View view) {
        finish();
    }

    public void onActionBarRightButtonClicked(View view) {
    }
}
