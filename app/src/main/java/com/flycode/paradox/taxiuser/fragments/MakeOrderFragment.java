package com.flycode.paradox.taxiuser.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flycode.paradox.taxiuser.R;
import com.flycode.paradox.taxiuser.api.APITalker;
import com.flycode.paradox.taxiuser.api.CarCategoriesListener;
import com.flycode.paradox.taxiuser.api.MakeOrderListener;
import com.flycode.paradox.taxiuser.dialogs.CommentDialog;
import com.flycode.paradox.taxiuser.dialogs.PickTimeDialog;
import com.flycode.paradox.taxiuser.layouts.MaximalLinearLayout;
import com.flycode.paradox.taxiuser.models.CarCategory;
import com.flycode.paradox.taxiuser.models.Order;
import com.flycode.paradox.taxiuser.utils.GeocodeUtil;
import com.flycode.paradox.taxiuser.utils.TypefaceUtils;
import com.flycode.paradox.taxiuser.views.LocationPickerView;
import com.flycode.paradox.taxiuser.views.MaximalScrollView;
import com.flycode.paradox.taxiuser.views.OrderView;
import com.flycode.paradox.taxiuser.views.RhombusView;
import com.mapbox.mapboxsdk.constants.MyLocationTracking;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.views.MapView;

import java.util.Calendar;
import java.util.Date;

public class MakeOrderFragment extends SuperFragment implements View.OnClickListener, CommentDialog.CommentDialogListener, CarCategoriesListener,
        GeocodeUtil.GeocodeListener, MakeOrderListener, MapView.OnMyLocationChangeListener, MapView.OnMapChangedListener,
        PickTimeDialog.PickTimeDialogListener, CommentDialog.KeyboardStateListener {
    private static final Object LOCKER = new Object();
    private static final String COMMENT_DIALOG_TAG = "commentDialogTag";
    private static final String TIME_DIALOG_TAG = "timeDialogTag";

    private static final String SAVED_ORDER_STAGE = "savedOrderStage";
    private static final String SAVED_CAR_CATEGORY_INDEX = "savedCarCategoryIndex";
    private static final String SAVED_COMMENT = "savedComment";
    private static final String SAVED_TIME_RHOMBUS_STATE = "savedTimeRhombusState";
    private static final String SAVED_CASH_ONLY_STATE = "savedCashOnlyState";
    private static final String SAVED_LOCATION = "savedLocation";
    private static final String SAVED_ADDRESS = "savedAddress";

    //    private MapView mapView;
//    private GoogleMap googleMap;
    private MapView mapView;
    private boolean hasMyLocationDetermined;

    private View orderFragmentView;

    private RhombusView isNowRhombus;
    private RhombusView isLaterRhombus;
    private RhombusView isCashOnlyRhombus;

    private TextView locationTextView;
    private TextView commentsTextView;

    private TextView minutesTextView;
    private TextView hoursTextView;
    private TextView dayTextView;

    private TextView locationTopTextView;

    private Button closeIconButton;
    private OrderView orderButton;

    private LinearLayout carCategoriesSectionLinearLayout;
    private LinearLayout orderDetailsLinerLayout;

    private boolean isLater = false;
    private boolean isCashOnly = false;
    private boolean isFromSaveInstanceState = false;

    private String comment = "";
    private int hour;
    private int minute;
    private boolean isToday = true;

    private int orderStage = 0;
    private boolean needsRelayout;

    private int savedCarCategoryIndex = 0;

    private CarCategory currentCarCategory;
    private OrderFragmentListener listener;

    public static MakeOrderFragment initialize(OrderFragmentListener listener) {
        MakeOrderFragment orderFragment = new MakeOrderFragment();
        orderFragment.setListener(listener);

        return orderFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        orderFragmentView = inflater.inflate(R.layout.fragment_order, container, false);

        Typeface robotoThinTypeface = TypefaceUtils.getTypeface(getActivity(), TypefaceUtils.AVAILABLE_FONTS.ROBOTO_THIN);
        Typeface robotoRegularTypeface = TypefaceUtils.getTypeface(getActivity(), TypefaceUtils.AVAILABLE_FONTS.ROBOTO_REGULAR);
        Typeface icomoonTypeface = TypefaceUtils.getTypeface(getActivity(), TypefaceUtils.AVAILABLE_FONTS.ICOMOON);

//        mapView = (MapView) orderView.findViewById(R.id.map_view);
//        mapView.onCreate(savedInstanceState);
//        MapsInitializer.initialize(getActivity());
//
//        googleMap = mapView.getMap();
//        googleMap.setMyLocationEnabled(true);
//        googleMap.setOnCameraChangeListener(this);

        needsRelayout = true;

        Calendar calendar = Calendar.getInstance();
        minute = calendar.get(Calendar.MINUTE);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        hasMyLocationDetermined = false;

        LatLng location = null;
        String savedAddress = "";

        if (savedInstanceState != null) {
            isFromSaveInstanceState = true;
            orderStage = savedInstanceState.getInt(SAVED_ORDER_STAGE, 0);
            savedCarCategoryIndex = savedInstanceState.getInt(SAVED_CAR_CATEGORY_INDEX);

            comment = savedInstanceState.getString(SAVED_COMMENT);

            isLater = savedInstanceState.getBoolean(SAVED_TIME_RHOMBUS_STATE);
            isCashOnly = savedInstanceState.getBoolean(SAVED_CASH_ONLY_STATE);

            location = savedInstanceState.getParcelable(SAVED_LOCATION);
            hasMyLocationDetermined = true;

            savedAddress = savedInstanceState.getString(SAVED_ADDRESS, "");
        }

        mapView = (MapView) orderFragmentView.findViewById(R.id.map_view);
        mapView.setStyleUrl(Style.LIGHT);
        mapView.setZoomLevel(15);
        mapView.setZoomControlsEnabled(false);
        mapView.setCompassEnabled(false);
        mapView.setMyLocationEnabled(true);
        mapView.setMyLocationTrackingMode(MyLocationTracking.TRACKING_NONE);
        mapView.setOnMyLocationChangeListener(this);

        if (hasMyLocationDetermined) {
            mapView.setCenterCoordinate(location);
        } else {
            mapView.setCenterCoordinate(new LatLng(40.177570, 44.512549));
            mapView.addOnMapChangedListener(this);
        }

        mapView.onCreate(savedInstanceState);

        isNowRhombus = (RhombusView) orderFragmentView.findViewById(R.id.now_rhombus);
        isLaterRhombus = (RhombusView) orderFragmentView.findViewById(R.id.later_rhombus);
        isCashOnlyRhombus = (RhombusView) orderFragmentView.findViewById(R.id.only_cash_rhombus);

        isNowRhombus.setIsFilled(true);
        isLaterRhombus.setIsFilled(true);
        isCashOnlyRhombus.setIsFilled(true);

        TextView isNowTextView = (TextView) orderFragmentView.findViewById(R.id.now_text);
        TextView isLaterTextView = (TextView) orderFragmentView.findViewById(R.id.later_text);
        TextView isCashOnlyTextView = (TextView) orderFragmentView.findViewById(R.id.only_cash_text);

        isNowTextView.setTypeface(robotoThinTypeface);
        isLaterTextView.setTypeface(robotoThinTypeface);
        isCashOnlyTextView.setTypeface(robotoThinTypeface);

        TextView locationTextIconTextView = (TextView) orderFragmentView.findViewById(R.id.location_text_icon);
        TextView locationIconTextView = (TextView) orderFragmentView.findViewById(R.id.location_icon);
        TextView commentTextIconTextView = (TextView) orderFragmentView.findViewById(R.id.comment_text_icon);

        locationIconTextView.setTypeface(icomoonTypeface);
        locationTextIconTextView.setTypeface(icomoonTypeface);
        commentTextIconTextView.setTypeface(icomoonTypeface);

        orderFragmentView.findViewById(R.id.time_section).setOnClickListener(this);
        orderFragmentView.findViewById(R.id.comment_section).setOnClickListener(this);
        orderFragmentView.findViewById(R.id.now).setOnClickListener(this);
        orderFragmentView.findViewById(R.id.later).setOnClickListener(this);
        orderFragmentView.findViewById(R.id.only_cash).setOnClickListener(this);

        dayTextView = (TextView) orderFragmentView.findViewById(R.id.day);
        hoursTextView = (TextView) orderFragmentView.findViewById(R.id.hours);
        minutesTextView = (TextView) orderFragmentView.findViewById(R.id.minutes);

        dayTextView.setTypeface(robotoRegularTypeface);
        hoursTextView.setTypeface(robotoRegularTypeface);
        minutesTextView.setTypeface(robotoRegularTypeface);

        locationTextView = (TextView) orderFragmentView.findViewById(R.id.location_text);
        commentsTextView = (TextView) orderFragmentView.findViewById(R.id.comment_text);
        locationTopTextView = (TextView) orderFragmentView.findViewById(R.id.location);

        locationTextView.setTypeface(robotoThinTypeface);
        commentsTextView.setTypeface(robotoThinTypeface);
        locationTopTextView.setTypeface(robotoThinTypeface);

        locationTopTextView.setText(savedAddress);
        locationTextView.setText(savedAddress);

        setupTimeRhombus();
        setupCacheOnlyRhombus();

        Fragment commentDialogCandidate = getFragmentManager().findFragmentByTag(COMMENT_DIALOG_TAG);
        Fragment timeDialogCandidate = getFragmentManager().findFragmentByTag(TIME_DIALOG_TAG);

        if (commentDialogCandidate != null) {
            CommentDialog commentsDialog = (CommentDialog) commentDialogCandidate;
            commentsDialog.setListener(this);
            commentsDialog.setKeyboardStateListener(this);
        }
        if (timeDialogCandidate != null) {
            PickTimeDialog timeDialog = (PickTimeDialog) timeDialogCandidate;
            timeDialog.setListener(this);
        }

        orderButton = (OrderView) orderFragmentView.findViewById(R.id.order_button);
        orderButton.setClickable(true);
        orderButton.setOnClickListener(this);
        orderButton.setOrderStage(orderStage);

        closeIconButton = (Button) orderFragmentView.findViewById(R.id.close_button);
        closeIconButton.setTypeface(icomoonTypeface);
        closeIconButton.setOnClickListener(this);

        carCategoriesSectionLinearLayout = (LinearLayout) orderFragmentView.findViewById(R.id.car_categories_container);
        orderDetailsLinerLayout = (LinearLayout) orderFragmentView.findViewById(R.id.order_details);

        dayTextView.setText(isToday ? R.string.today : R.string.tomorrow);

        String formattedMinuteString = String.format("%02d",  minute);
        String formattedHourString = String.format("%02d",  hour);

        minutesTextView.setText(formattedMinuteString);
        hoursTextView.setText(formattedHourString);

        if (orderStage == 1) {
            orderDetailsLinerLayout.setVisibility(View.VISIBLE);
            closeIconButton.setVisibility(View.VISIBLE);
        }

        commentsTextView.setText(comment);

        return orderFragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();

        Calendar calendar = Calendar.getInstance();
        int currentMinute = calendar.get(Calendar.MINUTE);
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);

        if (isToday) {
            if (((hour <= currentHour) && (minute < currentMinute))) {
                hour = currentHour;
                minute = currentMinute;
            }
        }
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        APITalker.sharedTalker().getCarCategories(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mapView.onSaveInstanceState(outState);
        outState.putInt(SAVED_ORDER_STAGE, orderStage);
        outState.putInt(SAVED_CAR_CATEGORY_INDEX, savedCarCategoryIndex);
        outState.putString(SAVED_COMMENT, comment);
        outState.putBoolean(SAVED_TIME_RHOMBUS_STATE, isLater);
        outState.putBoolean(SAVED_CASH_ONLY_STATE, isCashOnly);
        outState.putParcelable(SAVED_LOCATION, mapView.getCenterCoordinate());
        outState.putString(SAVED_ADDRESS, locationTopTextView.getText().toString());
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus && needsRelayout && orderStage == 1) {
            needsRelayout = false;

            setOrderDetailsVisible();
        }
    }

    /**
     * Rhombus Methods
     */

    private void setupTimeRhombus() {
        isNowRhombus.setColor(getResources().getColor(isLater ? R.color.white_100 : R.color.cyan));
        isLaterRhombus.setColor(getResources().getColor(isLater ? R.color.cyan : R.color.white_100));
    }

    private void setupCacheOnlyRhombus() {
        isCashOnlyRhombus.setColor(getResources().getColor(isCashOnly ? R.color.cyan: R.color.white_100));
    }

    private void setupCarCategoryRhombus(View chosenSection) {
        for (int index = 0; index < carCategoriesSectionLinearLayout.getChildCount(); index++) {
            View carCategorySection = carCategoriesSectionLinearLayout.getChildAt(index);
            RhombusView rhombus = (RhombusView) carCategorySection.findViewById(R.id.rhombus);
            TextView carCategoryInfoTextView = (TextView) carCategorySection.findViewById(R.id.info);
            TextView carCategoryTitleTextView = (TextView) carCategorySection.findViewById(R.id.text);

            if (chosenSection.equals(carCategorySection)) {
                savedCarCategoryIndex = index;
                CarCategory carCategory = (CarCategory) chosenSection.getTag();
                currentCarCategory = carCategory;
                rhombus.setColor(getResources().getColor(R.color.cyan));
                carCategoryTitleTextView.setTextColor(getResources().getColor(R.color.cyan));
                carCategoryInfoTextView.setText(getString(R.string.min) + carCategory.getMinPrice() + " " + getString(R.string.one_km) + carCategory.getRoutePrice());
            } else {
                rhombus.setColor(getResources().getColor(R.color.white_100));
                carCategoryInfoTextView.setText("");
                carCategoryTitleTextView.setTextColor(getResources().getColor(R.color.white_100));
            }
        }
    }

    /**
     * OnClickListener Methods
     */

    @Override
    public void onClick(View view) {
        if (view.getTag() instanceof CarCategory) {
            setupCarCategoryRhombus(view);
        } else if (view.getId() == R.id.now) {
            isLater = false;
            setupTimeRhombus();
        } else if (view.getId() == R.id.later) {
            isLater = true;
            setupTimeRhombus();
        } else if (view.getId() == R.id.only_cash) {
            isCashOnly = !isCashOnly;
            setupCacheOnlyRhombus();
        } else if (view.getId() == R.id.comment_section) {
            CommentDialog.initialize(comment, this, this).show(getFragmentManager(), COMMENT_DIALOG_TAG);
        } else if (view.getId() == R.id.time_section) {
            if (!isLater) {
                return;
            }
            PickTimeDialog.initialize(isToday, hour, minute, this).show(getFragmentManager(), TIME_DIALOG_TAG);
        } else if (view.getId() == R.id.order_button) {
            if (orderStage == 0) {
                orderStage++;
                orderDetailsLinerLayout.setVisibility(View.VISIBLE);
                closeIconButton.setVisibility(View.VISIBLE);
                locationTextView.setText(locationTopTextView.getText());
                mapView.setAllGesturesEnabled(false);
                orderButton.setOrderStage(orderStage);

                setOrderDetailsVisible();
            } else {
                APITalker.sharedTalker().makeOrder(
                        getActivity(),
                        locationTextView.getText().toString(),
                        mapView.getCenterCoordinate().getLatitude(),
                        mapView.getCenterCoordinate().getLongitude(),
                        new Date(),
                        currentCarCategory.getId(),
                        comment,
                        this
                );
            }
        } else if (view.getId() == R.id.close_button) {
            orderStage--;
            orderDetailsLinerLayout.setVisibility(View.GONE);
            closeIconButton.setVisibility(View.GONE);
            mapView.setAllGesturesEnabled(true);
            orderButton.setOrderStage(orderStage);

            MaximalLinearLayout headerPanelView = (MaximalLinearLayout) orderFragmentView.findViewById(R.id.header_panel);
            headerPanelView.findViewById(R.id.header_panel_location).setVisibility(View.VISIBLE);

            LocationPickerView locationPickerView = (LocationPickerView) orderFragmentView.findViewById(R.id.picker_view);

            mapView.setY(0);
            locationPickerView.setY((mapView.getMeasuredHeight() - locationPickerView.getMeasuredHeight()) / 2);
        }
    }

    private void setOrderDetailsVisible() {
        MaximalLinearLayout headerPanelView = (MaximalLinearLayout) orderFragmentView.findViewById(R.id.header_panel);
        MaximalScrollView orderDetailsView = (MaximalScrollView) orderFragmentView.findViewById(R.id.order_details_container);
        LocationPickerView locationPickerView = (LocationPickerView) orderFragmentView.findViewById(R.id.picker_view);

        float headerBottom = headerPanelView.getBottom() - locationTopTextView.getMeasuredHeight();
        float maxOrderDetailsHeight = mapView.getMeasuredHeight() - headerBottom
                - 3 * locationPickerView.getMeasuredHeight() / 2
                - carCategoriesSectionLinearLayout.getMeasuredHeight() - closeIconButton.getMeasuredHeight();
        float offset = (mapView.getMeasuredHeight() - headerBottom - carCategoriesSectionLinearLayout.getMeasuredHeight()
                - closeIconButton.getMeasuredHeight() - maxOrderDetailsHeight) / 2;

        mapView.setTranslationY(-offset);
        locationPickerView.setTranslationY(-offset);

        orderDetailsView.setMaxHeight((int) maxOrderDetailsHeight);

        headerPanelView.findViewById(R.id.header_panel_location).setVisibility(View.GONE);
    }

    /**
     * CommentDialogListener Methods
     */

    @Override
    public void onCommentDone(String comment) {
        this.comment = comment;
        commentsTextView.setText(comment);
    }

    @Override
    public void onCommentCancel() {

    }

    /**
     * CarCategoriesListener Methods
     */

    @Override
    public void onGetCarCategoriesSuccess(CarCategory[] carCategories) {
        if (getActivity() == null) {
            // TODO: Investigate Reason
            return;
        }

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        carCategoriesSectionLinearLayout.removeAllViews();
        carCategoriesSectionLinearLayout.setWeightSum(carCategories.length);

        for (int index = 0; index < carCategories.length; index++) {
            CarCategory carCategory = carCategories[index];
            View carCategoryView = inflater.inflate(R.layout.item_car_category, carCategoriesSectionLinearLayout, false);
            LinearLayout carCategorySectionLinearLayout = (LinearLayout) carCategoryView.findViewById(R.id.section);
            carCategorySectionLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
            carCategoryView.setTag(carCategory);
            carCategoryView.setClickable(true);
            carCategoryView.setOnClickListener(this);

            TextView carCategoryInfoTextView = (TextView) carCategoryView.findViewById(R.id.info);
            carCategoryInfoTextView.setTypeface(TypefaceUtils.getTypeface(getActivity(), TypefaceUtils.AVAILABLE_FONTS.ROBOTO_THIN));

            TextView carCategoryTitleTextView = (TextView) carCategoryView.findViewById(R.id.text);
            carCategoryTitleTextView.setText(carCategory.getName().toUpperCase());
            carCategoryTitleTextView.setTypeface(TypefaceUtils.getTypeface(getActivity(), TypefaceUtils.AVAILABLE_FONTS.ROBOTO_THIN));

            RhombusView carCategoryRhombus = (RhombusView) carCategoryView.findViewById(R.id.rhombus);
            carCategoryRhombus.setIsFilled(true);

            if (index == 0) {
                currentCarCategory = carCategory;
                carCategoryRhombus.setColor(getResources().getColor(R.color.cyan));
                carCategoryInfoTextView.setText(getString(R.string.min) + carCategory.getMinPrice() + " " + getString(R.string.one_km) + carCategory.getRoutePrice());
                carCategoryTitleTextView.setTextColor(getResources().getColor(R.color.cyan));
            } else {
                carCategoryRhombus.setColor(getResources().getColor(R.color.white_100));
                carCategoryTitleTextView.setTextColor(getResources().getColor(R.color.white_100));
            }

            carCategoriesSectionLinearLayout.addView(carCategoryView);
        }

        if (isFromSaveInstanceState) {
            for (int index = 0; index < carCategoriesSectionLinearLayout.getChildCount(); index++) {
                if (savedCarCategoryIndex == index) {
                    View carCategorySection = carCategoriesSectionLinearLayout.getChildAt(savedCarCategoryIndex);
                    setupCarCategoryRhombus(carCategorySection);
                }
            }
        }
    }

    @Override
    public void onGetCarCategoriesFail() {

    }

    /**
     * GeocodeUtil.GeocodeListener Methods
     */

    @Override
    public void onGeocodeSuccess(String address, LatLng location) {
        if (location == null) {
            return;
        }

        locationTopTextView.setTextColor(Color.BLACK);
        locationTopTextView.setText(address);
    }

    /**
     * MakeOrderListener Methods
     */

    @Override
    public void onMakeOrderSuccess(Order order) {
        if (listener == null) {
            return;
        }

        listener.onOrderMade(order);
    }

    @Override
    public void onMakeOrderFail() {
    }

    /**
     * MapView.OnMyLocationChangeListener
     */

    @Override
    public void onMyLocationChange(Location location) {
        if (!hasMyLocationDetermined) {
            hasMyLocationDetermined = true;
            LatLng center = new LatLng(location.getLatitude(), location.getLongitude());
            mapView.setCenterCoordinate(center, false);
            GeocodeUtil.geocode(getActivity(), center, this);
        }
    }

    /**
     * MapView.OnMapChangedListener
     */

    @Override
    public void onMapChanged(int change) {
        if (change == MapView.REGION_DID_CHANGE_ANIMATED
                || change == MapView.REGION_DID_CHANGE) {
            synchronized (LOCKER) {
                try {
                    locationTopTextView.setText("");
                    mapInteractionHandler.removeCallbacks(mapInteractionRunnable);
                    mapInteractionHandler.postDelayed(mapInteractionRunnable, 500);
                } catch (Exception e) {
                    // TODO: Investigate Reason
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Map Helpers
     */

    private Handler mapInteractionHandler = new Handler(Looper.getMainLooper());

    private Runnable mapInteractionRunnable = new Runnable() {
        @Override
        public void run() {
            synchronized (LOCKER) {
                try {
                    GeocodeUtil.geocode(getActivity(), mapView.getCenterCoordinate(), MakeOrderFragment.this);
                } catch (Exception e) {
                    // TODO: Investigate Reason
                    e.printStackTrace();
                }
            }
        }
    };

    /**
     * MakeOrderFragment Listener
     */

    public void setListener(OrderFragmentListener listener) {
        this.listener = listener;
    }

    @Override
    public void onTimePickDone(boolean isToday, int hour, int minute) {
        this.isToday = isToday;
        this.hour = hour;
        this.minute = minute;
        dayTextView.setText(isToday ? R.string.today : R.string.tomorrow);

        String formattedMinuteString = String.format("%02d",  minute);
        String formattedHourString = String.format("%02d",  hour);

        minutesTextView.setText(formattedMinuteString);
        hoursTextView.setText(formattedHourString);
    }

    @Override
    public void onTimePickCancel() {

    }

    @Override
    public void onKeyboardStateChanged() {
        orderFragmentView.findViewById(R.id.order_fragment_layout).requestLayout();
    }

    public interface OrderFragmentListener {
        void onOrderMade(Order order);
    }

}
