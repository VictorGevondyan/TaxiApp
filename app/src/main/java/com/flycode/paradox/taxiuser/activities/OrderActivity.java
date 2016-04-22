package com.flycode.paradox.taxiuser.activities;

import android.Manifest;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flycode.paradox.taxiuser.R;
import com.flycode.paradox.taxiuser.api.APITalker;
import com.flycode.paradox.taxiuser.api.FeedbackRequestHandler;
import com.flycode.paradox.taxiuser.api.GetOrderHandler;
import com.flycode.paradox.taxiuser.api.PointsForOrderListener;
import com.flycode.paradox.taxiuser.api.UpdateOrderStatusHandler;
import com.flycode.paradox.taxiuser.constants.DriverStatusConstants;
import com.flycode.paradox.taxiuser.constants.OrderStatusConstants;
import com.flycode.paradox.taxiuser.dialogs.FeedbackDialog;
import com.flycode.paradox.taxiuser.dialogs.LoadingDialog;
import com.flycode.paradox.taxiuser.dialogs.MessageDialog;
import com.flycode.paradox.taxiuser.models.Order;
import com.flycode.paradox.taxiuser.settings.AppSettings;
import com.flycode.paradox.taxiuser.utils.HardwareAccessibilityUtil;
import com.flycode.paradox.taxiuser.utils.LocaleUtils;
import com.flycode.paradox.taxiuser.utils.MessageHandlerUtil;
import com.flycode.paradox.taxiuser.utils.TypefaceUtils;
import com.flycode.paradox.taxiuser.views.MaximalScrollView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mapbox.mapboxsdk.geometry.BoundingBox;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class OrderActivity extends SuperActivity implements GetOrderHandler, PointsForOrderListener, OnMapReadyCallback, FeedbackDialog.FeedbackDialogListener, FeedbackRequestHandler, UpdateOrderStatusHandler {
    public static final String ORDER = "order";
    public static final String RETRY = "retry";
    public static final String CAN_RETRY = "carRetry";

    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 0;
    private static final String TAG_FEEDBACK_DIALOG = "tagFeedbackDialog";

    private static final int STAR_COUNT = 5;
    private static final String CANCEL_DIALOG_TAG = "cancelDialogTag";

    private MapView mapView;
    private GoogleMap googleMap;
    private LinearLayout leaveFeedbackLinearLayout;
    private LinearLayout feedbackStarsLinearLayout;
    private RelativeLayout feedbackResultRelativeLayout;

    private LoadingDialog loadingDialog;
    private Order order;

    private Timer timer;

    private String driverPhoneNumber;
    private Intent callIntent;

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

        loadingDialog = new LoadingDialog(this);

        mapView = (MapView) findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        MapsInitializer.initialize(this);

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

        if (orderIntent.getBooleanExtra(CAN_RETRY, false)
                && order.getStatus().equals(OrderStatusConstants.CANCELED)) {
            super.onOrderUpdated(order);
        }

        if (savedInstanceState != null) {
            order = savedInstanceState.getParcelable(ORDER);
        }

        Fragment fragment = getFragmentManager().findFragmentByTag(CANCEL_DIALOG_TAG);

        if (fragment instanceof MessageDialog) {
            MessageDialog messageDialog = (MessageDialog) fragment;
            messageDialog.setListener(cancelDialogListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();

        if (order.getStatus().equals(OrderStatusConstants.NOT_TAKEN)
                || order.getStatus().equals(OrderStatusConstants.TAKEN)
                || order.getStatus().equals(OrderStatusConstants.WAITING)
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
            }, 10000, 10000);
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
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        mapView.onSaveInstanceState(outState);

        outState.putParcelable(ORDER, order);

        super.onSaveInstanceState(outState);
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
        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity(callIntent);
            } else {
            }

            return;
        }
    }

    @Override
    protected void onOrderUpdated(Order order) {
        super.onOrderUpdated(order);

        this.order = order;
        init(order);
    }

    private void setupPhoneIcon() {
        String orderStatus = order.getStatus();
        boolean setPhoneButtonVisible = (order.getDriver() != null)
                && (orderStatus.equals(OrderStatusConstants.STARTED)
                || orderStatus.equals(OrderStatusConstants.TAKEN)
                || orderStatus.equals(OrderStatusConstants.WAITING));
        findViewById(R.id.action_bar_right_button).setVisibility(setPhoneButtonVisible ? View.VISIBLE : View.GONE);
    }

    private void setupCancelButton() {
        String orderStatus = order.getStatus();
        boolean setCancelButtonVisible = orderStatus.equals(OrderStatusConstants.NOT_TAKEN)
                || orderStatus.equals(OrderStatusConstants.TAKEN)
                || orderStatus.equals(OrderStatusConstants.WAITING);
        findViewById(R.id.cancel_button).setVisibility(setCancelButtonVisible ? View.VISIBLE : View.GONE);
    }

    public void init(Order order) {
        if (order.getStatus().equals(OrderStatusConstants.TAKEN)
                || order.getStatus().equals(OrderStatusConstants.NOT_TAKEN)
                || order.getStatus().equals(OrderStatusConstants.WAITING)) {
            if (order.getStartingPointGeo() == null) {
                return;
            }

            LatLng pickupCoordinate = new LatLng(
                    order.getStartingPointGeo().getLatitude(),
                    order.getStartingPointGeo().getLongitude());

            MarkerOptions pickupLocationMarkerOptions = new MarkerOptions();
            pickupLocationMarkerOptions.position(pickupCoordinate);
            pickupLocationMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.tile_user));
            pickupLocationMarkerOptions.anchor(0.5f, 0.5f);

            if (HardwareAccessibilityUtil.checkIfHasPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                googleMap.setMyLocationEnabled(true);
            }

            if (userMarker != null) {
                userMarker.remove();
            } else {
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(pickupCoordinate));
            }

            userMarker = googleMap.addMarker(pickupLocationMarkerOptions);
        }

        if (order.getStatus().equals(OrderStatusConstants.TAKEN)
                || order.getStatus().equals(OrderStatusConstants.WAITING)) {
            APITalker.sharedTalker().getPointsForOrder(order.getId(), DriverStatusConstants.GOING_TO_ORDER, 1, this);
        } else if (order.getStatus().equals(OrderStatusConstants.STARTED)
                || order.getStatus().equals(OrderStatusConstants.FINISHED)) {
            APITalker.sharedTalker().getPointsForOrder(order.getId(), DriverStatusConstants.IN_ORDER, 0, this);
        }

        if (order.getStatus().equals(OrderStatusConstants.NOT_TAKEN)
                || order.getStatus().equals(OrderStatusConstants.TAKEN)
                || order.getStatus().equals(OrderStatusConstants.STARTED)
                || order.getStatus().equals(OrderStatusConstants.WAITING)) {
            initDriverDetails(order);
        } else {
            initOrderDetails(order);
        }

        setupPhoneIcon();
        setupCancelButton();
    }

    public void initOrderDetails(Order order) {
        View orderInfoView = findViewById(R.id.order_info);
        orderInfoView.setVisibility(View.VISIBLE);
        View driverInfoView = findViewById(R.id.driver_info);
        driverInfoView.setVisibility(View.GONE);

        Typeface icomoonTypeface = TypefaceUtils.getTypeface(this, TypefaceUtils.AVAILABLE_FONTS.ICOMOON);
        Typeface robotoThinTypeface = TypefaceUtils.getTypeface(this, TypefaceUtils.AVAILABLE_FONTS.ROBOTO_THIN);
        Typeface robotoRegularTypeface = TypefaceUtils.getTypeface(this, TypefaceUtils.AVAILABLE_FONTS.ROBOTO_REGULAR);

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

        dateTextView.setTypeface(robotoThinTypeface);
        locationTextView.setTypeface(robotoThinTypeface);
        costTextView.setTypeface(robotoThinTypeface);
        distanceTextView.setTypeface(robotoThinTypeface);
        statusTextView.setTypeface(robotoThinTypeface);

        dateValueTextView.setTypeface(robotoThinTypeface);

        startLocationValueTextView.setTypeface(robotoThinTypeface);
        endLocationValueTextView.setTypeface(robotoThinTypeface);
        costValueTextView.setTypeface(robotoThinTypeface);
        distanceValueTextView.setTypeface(robotoThinTypeface);
        statusValueTextView.setTypeface(robotoThinTypeface);

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

        dateValueTextView.setText(DateUtils.formatDateTime(
                this,
                order.getOrderTime().getTime(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME
                        | DateUtils.FORMAT_ABBREV_MONTH | DateUtils.FORMAT_SHOW_YEAR
                        | DateUtils.FORMAT_24HOUR));

        if (order.getStartingPointName().isEmpty()) {
            startLocationValueTextView.setVisibility(View.GONE);
        } else {
            startLocationValueTextView.setText(order.getStartingPointName());
        }
        if (order.getEndingPointName().isEmpty()) {
            endLocationValueTextView.setVisibility(View.GONE);
        } else {
            endLocationValueTextView.setText(order.getEndingPointName());
        }

        String distanceString = String.format("%.2f", order.getDistance() / 1000) + getString(R.string.km);

        costValueTextView.setText(order.getMoneyAmount() + " / " + order.getPaymentType() + " / " + order.getBonus());
        distanceValueTextView.setText(distanceString + " - " + durationString);

        if (order.getStatus().equals(OrderStatusConstants.CANCELED)) {
            statusValueTextView.setText(R.string.canceled);
        } else if (order.getStatus().equals(OrderStatusConstants.CANCELED)) {
            statusValueTextView.setText(R.string.finished);
        }

        leaveFeedbackLinearLayout = (LinearLayout) findViewById(R.id.feedback_section);
        feedbackResultRelativeLayout = (RelativeLayout) findViewById(R.id.feedback_result);
        feedbackStarsLinearLayout = (LinearLayout) findViewById(R.id.feedback_result_stars);

        Button leaveFeedbackButton = (Button) leaveFeedbackLinearLayout.findViewById(R.id.feedback_button);
        leaveFeedbackButton.setTypeface(robotoThinTypeface);
        TextView feedbackTextView = (TextView) feedbackResultRelativeLayout.findViewById(R.id.feedback_text);
        feedbackTextView.setTypeface(robotoThinTypeface);

        if (order.getStatus().equals(OrderStatusConstants.CANCELED)) {
            leaveFeedbackLinearLayout.setVisibility(View.GONE);
            feedbackResultRelativeLayout.setVisibility(View.GONE);
        } else {
            if (!order.getHasFeedback()) {
                leaveFeedbackLinearLayout.setVisibility(View.VISIBLE);
            } else {
                feedbackResultRelativeLayout.setVisibility(View.VISIBLE);
                initFeedbackStars();
                setFeedbackResult();
            }
        }
    }

    private void initFeedbackStars() {
        Typeface icomoonTypeface = TypefaceUtils.getTypeface(this, TypefaceUtils.AVAILABLE_FONTS.ICOMOON);

        int index;
        TextView feedbackStar;
        for (index = 0; index < STAR_COUNT; index++) {
            feedbackStar = (TextView) feedbackStarsLinearLayout.getChildAt(index);
            feedbackStar.setTypeface(icomoonTypeface);
        }
    }

    public void initDriverDetails(Order order) {
        Typeface robotoThinTypeface = TypefaceUtils.getTypeface(this, TypefaceUtils.AVAILABLE_FONTS.ROBOTO_THIN);
        Typeface robotoRegularTypeface = TypefaceUtils.getTypeface(this, TypefaceUtils.AVAILABLE_FONTS.ROBOTO_REGULAR);

        View driverInfoView = findViewById(R.id.driver_info);
        driverInfoView.setVisibility(View.VISIBLE);
        View orderInfoView = findViewById(R.id.order_info);
        orderInfoView.setVisibility(View.GONE);

        if ((order.getStatus().equals(OrderStatusConstants.TAKEN)
                || order.getStatus().equals(OrderStatusConstants.STARTED)
                || order.getStatus().equals(OrderStatusConstants.WAITING))
                && order.getDriver() != null) {
            TextView carNumberTextView = (TextView) driverInfoView.findViewById(R.id.car_number);
            TextView carDescriptionTextView = (TextView) driverInfoView.findViewById(R.id.car_description);

            carNumberTextView.setText(order.getDriver().getCarNumber());

            carNumberTextView.setTypeface(robotoRegularTypeface);
            carDescriptionTextView.setTypeface(robotoRegularTypeface);
        }

        TextView orderStatusTextView = (TextView) driverInfoView.findViewById(R.id.order_status);
        TextView typeLabelTextView = (TextView) driverInfoView.findViewById(R.id.type_label);
        TextView orderStatusLabelTextView = (TextView) driverInfoView.findViewById(R.id.order_status_label);
        TextView typeValueTextView = (TextView) driverInfoView.findViewById(R.id.type_value);

        orderStatusTextView.setTypeface(robotoRegularTypeface);
        typeValueTextView.setTypeface(robotoRegularTypeface);
        orderStatusLabelTextView.setTypeface(robotoThinTypeface);
        typeLabelTextView.setTypeface(robotoThinTypeface);

        String orderStatus = order.getStatus();
        if (orderStatus.equals(OrderStatusConstants.TAKEN)) {
            orderStatusTextView.setText(getString(R.string.taken));
        } else if (orderStatus.equals(OrderStatusConstants.NOT_TAKEN)) {
            orderStatusTextView.setText(getString(R.string.not_taken));
        } else if (orderStatus.equals(OrderStatusConstants.STARTED)) {
            orderStatusTextView.setText(getString(R.string.started));
        } else if (orderStatus.equals(OrderStatusConstants.WAITING)) {
            orderStatusTextView.setText(getString(R.string.waiting));
        }

        typeValueTextView.setText(order.getCarCategory());
    }

    public void onActionBarLeftButtonClicked(View view) {
        finish();
    }

    public void onActionBarRightButtonClicked(View view) {
        driverPhoneNumber = "+" + order.getDriver().getUsername();
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

    public void onLeaveFeedback(View view) {
        FeedbackDialog
                .initialize(this)
                .show(getFragmentManager(), TAG_FEEDBACK_DIALOG);
    }

    public void onCancelOrder(View view) {
        MessageDialog.initialize(
                getString(R.string.attention),
                getString(R.string.sure_to_cancel),
                getString(android.R.string.no),
                getString(android.R.string.yes)
        )
        .setListener(cancelDialogListener)
        .show(getFragmentManager(), CANCEL_DIALOG_TAG);
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
    public void onGetOrderFailure(int statusCode) {
//        MessageHandlerUtil.showErrorForStatusCode(statusCode, this);
    }

    /**
     * PointsForOrderListener Methods
     */

    @Override
    public void onGetPointsForOrderFail(int statusCode) {

    }

    @Override
    public void onGetPointsForOrderSuccess(JSONArray coordinates) {
        if (order.getStatus().equals(OrderStatusConstants.STARTED)
                || order.getStatus().equals(OrderStatusConstants.FINISHED)) {
            drawPath(coordinates);
        } else if (order.getStatus().equals(OrderStatusConstants.TAKEN)
                || order.getStatus().equals(OrderStatusConstants.WAITING)) {
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

        if (north - south < 0.001) {
            north = north + 0.001;
            south = south - 0.001;
        }
        if (east - west < 0.001) {
            east = east + 0.001;
            west = west - 0.001;
        }

        if (taxiMarker != null) {
            taxiMarker.remove();
        } else {
            View actionBarView = findViewById(R.id.action_bar);
            View orderInfoView = findViewById(R.id.order_info);

            LatLngBounds bounds = new LatLngBounds(new LatLng(south, west), new LatLng(north, east));

            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
                    bounds,
                    mapView.getMeasuredWidth(),
                    mapView.getMeasuredHeight() - orderInfoView.getMeasuredHeight() -
                            actionBarView.getMeasuredHeight() + actionBarView.getMeasuredHeight(),
                    0
            ));
        }

        MarkerOptions taxiLocationMarkerOptions = new MarkerOptions();
        taxiLocationMarkerOptions.position(new LatLng(latitude, longitude));
        taxiLocationMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.tile_taxi));
        taxiLocationMarkerOptions.anchor(0.5f, 0.5f);

        taxiMarker = googleMap.addMarker(taxiLocationMarkerOptions);
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
            pathPolyline.remove();
        } else {
            BoundingBox zoomLevel = new BoundingBox(north, east, south, west);
            LatLngBounds bounds = new LatLngBounds(new LatLng(south, west), new LatLng(north, east));

            if (zoomLevel.isValid()) {
                View actionBarView = findViewById(R.id.action_bar);
                int infoHeight = 0;

                if (order.getStatus().equals(OrderStatusConstants.STARTED)) {
                    View driverInfoView = findViewById(R.id.driver_info);
                    infoHeight = driverInfoView.getMeasuredHeight();
                } else {
                    View orderInfoView = findViewById(R.id.order_info_scroll_view);
                    infoHeight = orderInfoView.getMeasuredHeight();
                }

                googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(
                        bounds,
                        mapView.getMeasuredWidth(),
                        mapView.getMeasuredHeight() - infoHeight -
                                actionBarView.getMeasuredHeight() + actionBarView.getMeasuredHeight(),
                        actionBarView.getMeasuredHeight()
                ));
                googleMap.moveCamera(CameraUpdateFactory.scrollBy(0, mapView.getMeasuredHeight() / 4 - actionBarView.getMeasuredHeight()));
            }
        }

        pathPolyline = googleMap.addPolyline(pathPolylineOptions);

        if (userMarker != null) {
            userMarker.remove();
            userMarker = null;
        }

        if (taxiMarker != null) {
            taxiMarker.remove();
            taxiMarker = null;
        }

        if (startMarker != null) {
            startMarker.remove();
        }

        if (HardwareAccessibilityUtil.checkIfHasPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            googleMap.setMyLocationEnabled(false);
        }

        MarkerOptions startLocationMarkerOptions = new MarkerOptions();
        startLocationMarkerOptions.position(pathPolylineOptions.getPoints().get(0));
        startLocationMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.tile_start));
        startLocationMarkerOptions.anchor(0.5f, 0.5f);

        startMarker = googleMap.addMarker(startLocationMarkerOptions);

        if (order.getStatus().equals(OrderStatusConstants.STARTED)) {
            if (locationMarker != null) {
                locationMarker.remove();
            }

            MarkerOptions locationMarkerOptions = new MarkerOptions();
            locationMarkerOptions.position(pathPolylineOptions.getPoints().get(pathPolylineOptions.getPoints().size() - 1));
            locationMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.tile_location));
            locationMarkerOptions.anchor(0.5f, 0.5f);

            locationMarker = googleMap.addMarker(locationMarkerOptions);
        } else if (order.getStatus().equals(OrderStatusConstants.FINISHED)) {
            if (timer != null) {
                timer.cancel();
                timer.purge();
            }

            if (locationMarker != null) {
                locationMarker.remove();
                locationMarker = null;
            }

            if (finishMarker != null) {
                finishMarker.remove();
            }

            MarkerOptions finishLocationMarkerOptions = new MarkerOptions();
            finishLocationMarkerOptions.position(pathPolylineOptions.getPoints().get(pathPolylineOptions.getPoints().size() - 1));
            finishLocationMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.tile_finish));
            finishLocationMarkerOptions.anchor(0.5f, 0.5f);

            finishMarker = googleMap.addMarker(finishLocationMarkerOptions);
        }
    }

    /**
     * OnMapReadyCallback Methods
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setCompassEnabled(false);

        googleMap.moveCamera(CameraUpdateFactory.zoomTo(15));

        if (HardwareAccessibilityUtil.checkIfHasPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            googleMap.setMyLocationEnabled(true);
        }

        this.googleMap = googleMap;

        init(order);
    }

    /**
     * FeedbackDialogListener Methods
     */

    @Override
    public void onFeedbackDone(String comment, int rating) {
        loadingDialog.show();
        order.setFeedbackRating(rating);
        APITalker.sharedTalker().sendFeedback(this, comment, rating, order.getId(), this);
    }

    @Override
    public void onFeedbackCancel() {

    }

    /**
     * FeedbackRequestHandler
     */

    @Override
    public void onFeedbackRequestSuccess() {
        loadingDialog.dismiss();

        order.setHasFeedback(true);
        MessageHandlerUtil.showMessage(R.string.success, R.string.thank_you_for_feedback, this);
        leaveFeedbackLinearLayout.setVisibility(View.GONE);
        feedbackResultRelativeLayout.setVisibility(View.VISIBLE);

        initFeedbackStars();
        setFeedbackResult();
    }

    public void setFeedbackResult() {
        feedbackStarsLinearLayout.setVisibility(View.VISIBLE);

        int index;
        TextView feedbackStar;

        for (index = 0; index < order.getFeedbackRating(); index++) {
            feedbackStar = (TextView) feedbackStarsLinearLayout.getChildAt(index);
            feedbackStar.setText(R.string.icon_star_filled);
        }

        for (index = order.getFeedbackRating(); index < STAR_COUNT; index++) {
            feedbackStar = (TextView) feedbackStarsLinearLayout.getChildAt(index);
            feedbackStar.setText(R.string.icon_favorites);
        }
    }

    @Override
    public void onFeedbackRequestFailure(int statusCode) {
        loadingDialog.dismiss();

        MessageHandlerUtil.showErrorForStatusCode(statusCode, this);
    }

    /**
     * UpdateOrderStatusHandler Methods
     */

    @Override
    public void onUpdateOrderStatusSuccess(Order order) {
        loadingDialog.dismiss();

        this.order = order;

        init(order);
    }

    @Override
    public void onUpdateOrderStatusFailure(int statusCode) {
        loadingDialog.dismiss();

        MessageHandlerUtil.showErrorForStatusCode(statusCode, this);
    }

    /**
     * MessageDialogListener
     */

    MessageDialog.MessageDialogListener cancelDialogListener = new MessageDialog.MessageDialogListener() {
        @Override
        public void onNegativeClicked(MessageDialog messageDialog) {

        }

        @Override
        public void onPositiveClicked(MessageDialog messageDialog) {
            loadingDialog.show();
            APITalker.sharedTalker().updateOrderStatus(OrderActivity.this, order.getId(), OrderStatusConstants.CANCELED, OrderActivity.this);
        }
    };
}
