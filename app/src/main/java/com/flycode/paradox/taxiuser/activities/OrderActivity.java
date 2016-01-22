package com.flycode.paradox.taxiuser.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.flycode.paradox.taxiuser.R;
import com.flycode.paradox.taxiuser.api.APITalker;
import com.flycode.paradox.taxiuser.api.GetOrderHandler;
import com.flycode.paradox.taxiuser.api.PointsForOrderListener;
import com.flycode.paradox.taxiuser.constants.DriverStatusConstants;
import com.flycode.paradox.taxiuser.constants.OrderStatusConstants;
import com.flycode.paradox.taxiuser.dialogs.MessageDialog;
import com.flycode.paradox.taxiuser.models.Order;
import com.flycode.paradox.taxiuser.settings.AppSettings;
import com.flycode.paradox.taxiuser.utils.LocaleUtils;
import com.flycode.paradox.taxiuser.utils.TypefaceUtils;
import com.flycode.paradox.taxiuser.views.MaximalScrollView;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.constants.MyLocationTracking;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.BoundingBox;
import com.mapbox.mapboxsdk.geometry.CoordinateBounds;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.views.MapView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class OrderActivity extends Activity implements GetOrderHandler, PointsForOrderListener {
    public static final String ORDER = "order";

    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 0;

    private MapView mapView;
    private Order order;

    private Timer timer;

    private String driverPhoneNumber;
    private Intent callIntent;

    private boolean hasCenteredOnes = false;

    private Marker userMarker;
    private Marker taxiMarker;
    private Marker startMarker;
    private Marker finishMarker;
    private Marker locationMarker;
    private Polyline pathPolyline;

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

        String orderStatus = order.getStatus();
        boolean setPhoneButtonVisible = (order.getDriver() != null) && (orderStatus.equals(OrderStatusConstants.STARTED) ||
                orderStatus.equals(OrderStatusConstants.TAKEN));
        if (setPhoneButtonVisible) {
            actionBarRightButton.setVisibility(View.VISIBLE);
        }

        mapView = (MapView) findViewById(R.id.map_view);
        mapView.setStyleUrl(Style.LIGHT);
        mapView.setZoomLevel(15);
        mapView.setZoomControlsEnabled(false);
        mapView.setCompassEnabled(false);
        mapView.setMyLocationTrackingMode(MyLocationTracking.TRACKING_NONE);
        mapView.onCreate(savedInstanceState);

        init(order);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();

        if (order.getStatus().equals(OrderStatusConstants.NOT_TAKEN)
                || order.getStatus().equals(OrderStatusConstants.NOT_TAKEN)
                || order.getStatus().equals(OrderStatusConstants.STARTED)) {
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();

        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if ( requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE ) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity(callIntent);
            } else {
            }

            return;
        }
    }

    public void init(Order order) {
        if (order.getStatus().equals(OrderStatusConstants.TAKEN)
                || order.getStatus().equals(OrderStatusConstants.NOT_TAKEN)) {
            LatLng pickupCoordinate = new LatLng(
                    order.getStartingPointGeo().getLatitude(),
                    order.getStartingPointGeo().getLongitude());

            MarkerOptions pickupLocationMarkerOptions = new MarkerOptions();
            pickupLocationMarkerOptions.position(pickupCoordinate);
            pickupLocationMarkerOptions.icon(IconFactory.getInstance(this).fromResource(R.drawable.tile_user));

            mapView.setMyLocationEnabled(true);

            if (userMarker != null) {
                mapView.removeAnnotation(userMarker);
            } else {
                mapView.setCenterCoordinate(pickupCoordinate, false);
            }

            userMarker = mapView.addMarker(pickupLocationMarkerOptions);
        }

        if (order.getStatus().equals(OrderStatusConstants.TAKEN)) {
            APITalker.sharedTalker().getPointsForOrder(order.getId(), DriverStatusConstants.GOING_TO_ORDER, 1, this);
        } else if (order.getStatus().equals(OrderStatusConstants.STARTED)
                || order.getStatus().equals(OrderStatusConstants.FINISHED)) {
            APITalker.sharedTalker().getPointsForOrder(order.getId(), DriverStatusConstants.IN_ORDER, 0, this);
        }

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

        if (order.getFinishTime() != null
                && order.getUserPickupTime() != null) {
            long durationMillis = order.getFinishTime().getTime() - order.getUserPickupTime().getTime();
            long seconds = durationMillis / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;

            if (hours > 0) {
                durationString = durationString + hours + getString(R.string.hour);
            }
            if (minutes > 0) {
                durationString = durationString + " " + (minutes - hours * 60) + getString(R.string.minute);
            }

            durationString = durationString + " " + (seconds - minutes * 60) + getString(R.string.second);
        }

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

        if (order.getStatus().equals(OrderStatusConstants.TAKEN)
                && order.getDriver() != null) {
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

        String orderStatus = order.getStatus();
        if (orderStatus.equals(OrderStatusConstants.TAKEN)) {
            orderStatusTextView.setText(getString(R.string.taken));
        } else if (orderStatus.equals(OrderStatusConstants.NOT_TAKEN)) {
            orderStatusTextView.setText(getString(R.string.not_taken));
        }

        typeValueTextView.setText(order.getCarCategory());

        cancelButton.setText(getString(R.string.cancel).toUpperCase());
    }

    public void onActionBarLeftButtonClicked(View view) {
        finish();
    }

    public void onActionBarRightButtonClicked(View view) {
        driverPhoneNumber = order.getDriver().getUsername();
        callIntent = new Intent(Intent.ACTION_CALL);

        callIntent.setData(Uri.parse("tel:" + driverPhoneNumber));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE);
            return;
        }

        startActivity(callIntent);
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
        MessageDialog.initialize("Error", "GET ORDER ERROR").show(getFragmentManager(), MessageDialog.ERROR_DIALOG_TAG);
    }

    /**
     * PointsForOrderListener Methods
     */

    @Override
    public void onGetPointsForOrderFail() {

    }

    @Override
    public void onGetPointsForOrderSuccess(JSONArray coordinates) {
        if (order.getStatus().equals(OrderStatusConstants.STARTED)
                || order.getStatus().equals(OrderStatusConstants.FINISHED)) {
            drawPath(coordinates);
        } else if (order.getStatus().equals(OrderStatusConstants.TAKEN)) {
            showTaxiLocation(coordinates);
        }
    }

    private void showTaxiLocation(JSONArray coordinates) {
        if (coordinates.length() == 0) {
            return;
        }

        JSONObject coordinate = coordinates.optJSONObject(0);
        JSONArray geo = coordinate.optJSONArray("geo");
        double latitude = geo.optDouble(0);
        double longitude = geo.optDouble(1);

        double south = order.getStartingPointGeo().getLatitude();
        double north = order.getStartingPointGeo().getLatitude();
        double east = order.getStartingPointGeo().getLongitude();
        double west = order.getStartingPointGeo().getLongitude();

        if (latitude < south) {
            south = latitude;
        }
        if (latitude > north) {
            north = latitude;
        }
        if (longitude < west) {
            west = longitude;
        }
        if (longitude > east) {
            east = longitude;
        }

        if (north - south < 0.0005) {
            north = north + 0.0005;
            south = south - 0.0005;
        }
        if (east - west < 0.0005) {
            east = east + 0.0005;
            west = west - 0.0005;
        }

        if (taxiMarker != null) {
            mapView.removeAnnotation(taxiMarker);
        } else {
            CoordinateBounds coordinateBounds = new CoordinateBounds(new LatLng(north, east), new LatLng(south, west));
            View actionBarView = findViewById(R.id.action_bar);
            View orderInfoView = findViewById(R.id.order_info);

            mapView.setVisibleCoordinateBounds(
                coordinateBounds,
                new RectF(
                        actionBarView.getMeasuredHeight() / 2,
                        actionBarView.getMeasuredHeight(),
                        actionBarView.getMeasuredHeight() / 2,
                        orderInfoView.getMeasuredHeight()),
                        true);
        }

        MarkerOptions taxiLocationMarkerOptions = new MarkerOptions();
        taxiLocationMarkerOptions.position(new LatLng(latitude, longitude));
        taxiLocationMarkerOptions.icon(IconFactory.getInstance(this).fromResource(R.drawable.tile_taxi));

        taxiMarker = mapView.addMarker(taxiLocationMarkerOptions);
    }

    private void drawPath(JSONArray coordinates) {
        double south = Double.MAX_VALUE;
        double north = Double.MIN_VALUE;
        double east = Double.MIN_VALUE;
        double west = Double.MAX_VALUE;

        PolylineOptions pathPolylineOptions = new PolylineOptions();
        pathPolylineOptions.color(getResources().getColor(R.color.path_grey));
        pathPolylineOptions.width(5);

        if (coordinates.length() > 0) {
            for (int index = 0; index < coordinates.length(); index++) {
                JSONObject coordinate = coordinates.optJSONObject(index);
                JSONArray geo = coordinate.optJSONArray("geo");
                double latitude = geo.optDouble(0);
                double longitude = geo.optDouble(1);

                pathPolylineOptions.add(new LatLng(latitude, longitude));

                if (latitude < south) {
                    south = latitude;
                }
                if (latitude > north) {
                    north = latitude;
                }
                if (longitude < west) {
                    west = longitude;
                }
                if (longitude > east) {
                    east = longitude;
                }
            }
        } else {
            return;
        }

        if (north - south < 0.0005) {
            north = north + 0.0005;
            south = south - 0.0005;
        }
        if (east - west < 0.0005) {
            east = east + 0.0005;
            west = west - 0.0005;
        }

        if (pathPolyline != null) {
            mapView.removeAnnotation(pathPolyline);
        } else {
            BoundingBox zoomLevel = new BoundingBox(north, east, south, west);
            CoordinateBounds coordinateBounds = new CoordinateBounds(new LatLng(north, east), new LatLng(south, west));

            if (zoomLevel.isValid()) {
                View actionBarView = findViewById(R.id.action_bar);
                int infoHeight = 0;

                if (order.getStatus().equals(OrderStatusConstants.STARTED)) {
                    View driverInfoView = findViewById(R.id.driver_info);
                    infoHeight = driverInfoView.getMeasuredHeight();
                } else {
                    View orderInfoView = findViewById(R.id.order_info);
                    infoHeight = orderInfoView.getMeasuredHeight();
                }

                mapView.setVisibleCoordinateBounds(
                        coordinateBounds,
                        new RectF(
                                actionBarView.getMeasuredHeight() / 2,
                                actionBarView.getMeasuredHeight(),
                                actionBarView.getMeasuredHeight() / 2,
                                infoHeight),
                        false);
            }
        }

        pathPolyline = mapView.addPolyline(pathPolylineOptions);

        if (userMarker != null) {
            mapView.removeAnnotation(userMarker);
            userMarker = null;
        }

        if (taxiMarker != null) {
            mapView.removeAnnotation(taxiMarker);
            taxiMarker = null;
        }

        if (startMarker != null) {
            mapView.removeAnnotation(startMarker);
        }

        MarkerOptions startLocationMarkerOptions = new MarkerOptions();
        startLocationMarkerOptions.position(pathPolylineOptions.getPoints().get(pathPolylineOptions.getPoints().size() - 1));
        startLocationMarkerOptions.icon(IconFactory.getInstance(this).fromResource(R.drawable.tile_start));

        startMarker = mapView.addMarker(startLocationMarkerOptions);

        if (order.getStatus().equals(OrderStatusConstants.STARTED)) {
            if (locationMarker != null) {
                mapView.removeAnnotation(locationMarker);
            }

            MarkerOptions locationMarkerOptions = new MarkerOptions();
            locationMarkerOptions.position(pathPolylineOptions.getPoints().get(0));
            locationMarkerOptions.icon(IconFactory.getInstance(this).fromResource(R.drawable.tile_location));

            locationMarker = mapView.addMarker(locationMarkerOptions);
        } else if (order.getStatus().equals(OrderStatusConstants.FINISHED)) {
            if (locationMarker != null) {
                mapView.removeAnnotation(locationMarker);
                locationMarker = null;
            }

            if (finishMarker != null) {
                mapView.removeAnnotation(finishMarker);
            }

            MarkerOptions finishLocationMarkerOptions = new MarkerOptions();
            finishLocationMarkerOptions.position(pathPolylineOptions.getPoints().get(0));
            finishLocationMarkerOptions.icon(IconFactory.getInstance(this).fromResource(R.drawable.tile_finish));

            finishMarker = mapView.addMarker(finishLocationMarkerOptions);
        }
    }
}
