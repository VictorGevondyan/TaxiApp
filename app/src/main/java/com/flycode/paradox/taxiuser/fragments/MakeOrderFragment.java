package com.flycode.paradox.taxiuser.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flycode.paradox.taxiuser.R;
import com.flycode.paradox.taxiuser.api.APITalker;
import com.flycode.paradox.taxiuser.api.CarCategoriesListener;
import com.flycode.paradox.taxiuser.api.MakeOrderListener;
import com.flycode.paradox.taxiuser.database.Database;
import com.flycode.paradox.taxiuser.dialogs.CommentDialog;
import com.flycode.paradox.taxiuser.dialogs.LoadingDialog;
import com.flycode.paradox.taxiuser.dialogs.MessageDialog;
import com.flycode.paradox.taxiuser.dialogs.PickTimeDialog;
import com.flycode.paradox.taxiuser.layouts.MaximalLinearLayout;
import com.flycode.paradox.taxiuser.models.CarCategory;
import com.flycode.paradox.taxiuser.models.Order;
import com.flycode.paradox.taxiuser.settings.UserData;
import com.flycode.paradox.taxiuser.utils.GeocodeUtil;
import com.flycode.paradox.taxiuser.utils.HardwareAccessibilityUtil;
import com.flycode.paradox.taxiuser.utils.MessageHandlerUtil;
import com.flycode.paradox.taxiuser.utils.TypefaceUtils;
import com.flycode.paradox.taxiuser.views.LocationPickerView;
import com.flycode.paradox.taxiuser.views.MaximalScrollView;
import com.flycode.paradox.taxiuser.views.OrderView;
import com.flycode.paradox.taxiuser.views.RhombusView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;

public class MakeOrderFragment extends SuperFragment implements View.OnClickListener, CommentDialog.CommentDialogListener, CarCategoriesListener,
        GeocodeUtil.GeocodeListener, MakeOrderListener,
        PickTimeDialog.PickTimeDialogListener, OnMapReadyCallback, GoogleMap.OnCameraChangeListener, GoogleMap.OnMyLocationChangeListener, TextWatcher, MessageDialog.MessageDialogListener {
    private static final Object LOCKER = new Object();
    private static final String COMMENT_DIALOG_TAG = "commentDialogTag";
    private static final String TIME_DIALOG_TAG = "timeDialogTag";
    private static final String HAVE_ORDERS_DIALOG_TAG = "haveOrdersDialogTag";

    private static final String SAVED_ORDER_STAGE = "savedOrderStage";
    private static final String SAVED_CAR_CATEGORY_INDEX = "savedCarCategoryIndex";
    private static final String SAVED_COMMENT = "savedComment";
    private static final String SAVED_TIME_RHOMBUS_STATE = "savedTimeRhombusState";
    private static final String SAVED_CASH_ONLY_STATE = "savedCashOnlyState";
    private static final String SAVED_LOCATION = "savedLocation";
    private static final String SAVED_ADDRESS = "savedAddress";
    private static final String SAVED_QUERY = "savedQuery";
    private static final String SAVED_RESULT_ADDRESSES = "savedResultAddresses";
    private static final String SAVED_RESULT_PLACE_IDS = "savedPlaceIds";
    private static final String SAVED_MANUAL_QUERY_VISIBILITY = "savedManualQueryVisibility";

    private MapView mapView;
    private GoogleMap googleMap;
    private boolean hasMyLocationDetermined;
    private LatLng location;
    private LatLng myLocation;

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
    private TextView noSearchResultsTextView;

    private EditText locationEditText;

    private Button cancelButton;
    private Button closeIconButton;
    private OrderView orderButton;

    private LinearLayout carCategoriesSectionLinearLayout;
    private LinearLayout orderDetailsLinerLayout;
    private LinearLayout mapControlsLinearLayout;
    private LinearLayout searchResultContainerLinearLayout;

    private RelativeLayout searchResultsRelativeLayout;

    private LoadingDialog loadingDialog;

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
    private String[] resultPlaceIds;
    private String[] resultAddresses;
    private OrderFragmentListener listener;

    public static MakeOrderFragment initialize(OrderFragmentListener listener) {
        MakeOrderFragment orderFragment = new MakeOrderFragment();
        orderFragment.setListener(listener);

        return orderFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        super.onCreate(savedInstanceState);

        loadingDialog = new LoadingDialog(getActivity());

        orderFragmentView = inflater.inflate(R.layout.fragment_make_order, container, false);

        Typeface robotoThinTypeface = TypefaceUtils.getTypeface(getActivity(), TypefaceUtils.AVAILABLE_FONTS.ROBOTO_THIN);
        Typeface robotoRegularTypeface = TypefaceUtils.getTypeface(getActivity(), TypefaceUtils.AVAILABLE_FONTS.ROBOTO_REGULAR);
        Typeface icomoonTypeface = TypefaceUtils.getTypeface(getActivity(), TypefaceUtils.AVAILABLE_FONTS.ICOMOON);

        mapView = (MapView) orderFragmentView.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        MapsInitializer.initialize(getActivity());

        needsRelayout = true;

        Calendar calendar = Calendar.getInstance();
        minute = calendar.get(Calendar.MINUTE);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        hasMyLocationDetermined = false;

        String savedAddress = "";
        String savedQuery = "";
        boolean isManualQueryVisible = false;

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
            savedQuery = savedInstanceState.getString(SAVED_QUERY, "");
            resultAddresses = savedInstanceState.getStringArray(SAVED_RESULT_ADDRESSES);
            resultPlaceIds = savedInstanceState.getStringArray(SAVED_RESULT_PLACE_IDS);
            isManualQueryVisible = savedInstanceState.getBoolean(SAVED_MANUAL_QUERY_VISIBILITY, false);
        }

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

        locationTopTextView.setOnClickListener(this);

        setupTimeRhombus();
        setupCacheOnlyRhombus();

        Fragment commentDialogCandidate = getFragmentManager().findFragmentByTag(COMMENT_DIALOG_TAG);
        Fragment timeDialogCandidate = getFragmentManager().findFragmentByTag(TIME_DIALOG_TAG);
        Fragment haveOrdersDialogCandidate = getFragmentManager().findFragmentByTag(HAVE_ORDERS_DIALOG_TAG);

        if (commentDialogCandidate instanceof CommentDialog) {
            CommentDialog commentsDialog = (CommentDialog) commentDialogCandidate;
            commentsDialog.setListener(this);
        }
        if (timeDialogCandidate instanceof PickTimeDialog) {
            PickTimeDialog timeDialog = (PickTimeDialog) timeDialogCandidate;
            timeDialog.setListener(this);
        }
        if (haveOrdersDialogCandidate instanceof MessageDialog) {
            MessageDialog messageDialog = (MessageDialog) haveOrdersDialogCandidate;
            messageDialog.setListener(this);
        }

        orderButton = (OrderView) orderFragmentView.findViewById(R.id.order_button);
        orderButton.setClickable(true);
        orderButton.setOnClickListener(this);
        orderButton.setOrderStage(orderStage);

        closeIconButton = (Button) orderFragmentView.findViewById(R.id.close_button);
        closeIconButton.setTypeface(icomoonTypeface);
        closeIconButton.setOnClickListener(this);

        locationEditText = (EditText) orderFragmentView.findViewById(R.id.location_search);
        noSearchResultsTextView = (TextView) orderFragmentView.findViewById(R.id.no_search_results);
        cancelButton = (Button) orderFragmentView.findViewById(R.id.cancel_search_button);
        searchResultsRelativeLayout = (RelativeLayout) orderFragmentView.findViewById(R.id.search_results_section);
        searchResultContainerLinearLayout = (LinearLayout) orderFragmentView.findViewById(R.id.search_results_container);

        locationEditText.setTypeface(robotoThinTypeface);
        cancelButton.setTypeface(robotoThinTypeface);
        noSearchResultsTextView.setTypeface(robotoThinTypeface);

        if (isManualQueryVisible) {
            onClick(locationTopTextView);
            locationEditText.setText(savedQuery);
            onReverseGeocodeSuccess(savedQuery, resultAddresses, resultPlaceIds);
        }

        locationEditText.addTextChangedListener(this);
        cancelButton.setOnClickListener(this);

        Button showMyLocationButton = (Button) orderFragmentView.findViewById(R.id.my_location_button);
        showMyLocationButton.setTypeface(icomoonTypeface);
        showMyLocationButton.setOnClickListener(this);

        Button zoomInButton = (Button) orderFragmentView.findViewById(R.id.zoom_in_button);
        zoomInButton.setTypeface(icomoonTypeface);
        zoomInButton.setOnClickListener(this);

        Button zoomOutButton = (Button) orderFragmentView.findViewById(R.id.zoom_out_button);
        zoomOutButton.setTypeface(icomoonTypeface);
        zoomOutButton.setOnClickListener(this);

        mapControlsLinearLayout = (LinearLayout) orderFragmentView.findViewById(R.id.map_controls);
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
            mapControlsLinearLayout.setVisibility(View.GONE);
        }

        commentsTextView.setText(comment);

        initCarCategories(Database.sharedDatabase(getActivity()).getCarCategories());

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
    }

    @Override
    public void onStop() {
        super.onStop();
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
        outState.putParcelable(SAVED_LOCATION, googleMap.getCameraPosition().target);
        outState.putString(SAVED_ADDRESS, locationTopTextView.getText().toString());
        outState.putString(SAVED_QUERY, locationEditText.getText().toString());
        outState.putStringArray(SAVED_RESULT_ADDRESSES, resultAddresses);
        outState.putStringArray(SAVED_RESULT_PLACE_IDS, resultPlaceIds);
        outState.putBoolean(SAVED_MANUAL_QUERY_VISIBILITY, locationEditText.getVisibility() == View.VISIBLE);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus && needsRelayout && orderStage == 1) {
            needsRelayout = false;

            setOrderDetailsVisible();
        }
    }

    @Override
    public boolean onBackButtonPressed() {
        if (orderStage == 1) {
            onClick(closeIconButton);
            return true;
        }

        return false;
    }

    @Override
    public void onConnectionChanged(boolean isConnected) {
        if (Database.sharedDatabase(getActivity()).getCarCategories().length == 0) {
            APITalker.sharedTalker().getCarCategories(this);
        }
    }

    @Override
    public void onGPSPermissionChanged(boolean isGPSPermitted) {
        if (googleMap != null && isGPSPermitted) {
            try {
                googleMap.setMyLocationEnabled(true);
                googleMap.setOnMyLocationChangeListener(this);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Rhombus Methods
     */

    private void setupTimeRhombus() {
        isNowRhombus.setColor(getResources().getColor(isLater ? R.color.white_100 : R.color.cyan_100));
        isLaterRhombus.setColor(getResources().getColor(isLater ? R.color.cyan_100 : R.color.white_100));

        orderFragmentView.findViewById(R.id.time_section).setVisibility(isLater ? View.VISIBLE : View.GONE);
    }

    private void setupCacheOnlyRhombus() {
        isCashOnlyRhombus.setColor(getResources().getColor(isCashOnly ? R.color.cyan_100 : R.color.white_100));
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
                rhombus.setColor(getResources().getColor(R.color.cyan_100));
                carCategoryTitleTextView.setTextColor(getResources().getColor(R.color.cyan_100));
                carCategoryInfoTextView.setText(
                        getString(R.string.min) + String.format("%.0f", carCategory.getMinPrice()) +
                        " " + getString(R.string.one_km) + String.format("%.0f", carCategory.getRoutePrice()));
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
            CommentDialog.initialize(comment, this).show(getFragmentManager(), COMMENT_DIALOG_TAG);
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
                mapControlsLinearLayout.setVisibility(View.GONE);
                locationTextView.setText(locationTopTextView.getText());
                orderButton.setOrderStage(orderStage);

                setOrderDetailsVisible();

                if (googleMap != null) {
                    googleMap.getUiSettings().setAllGesturesEnabled(false);
                }
            } else {
                if (UserData.sharedData(getActivity()).getOrderCount() == 0) {
                    onMakeOrder();
                } else {
                    MessageDialog
                            .initialize(
                                    getString(R.string.hey),
                                    getString(R.string.already_have_orders, UserData.sharedData(getActivity()).getOrderCount()),
                                    getString(android.R.string.no),
                                    getString(android.R.string.yes)
                            )
                            .setListener(this)
                            .show(getFragmentManager(), HAVE_ORDERS_DIALOG_TAG);
                }
            }
        } else if (view.getId() == R.id.close_button) {
            orderStage--;
            orderDetailsLinerLayout.setVisibility(View.GONE);
            closeIconButton.setVisibility(View.GONE);
            mapControlsLinearLayout.setVisibility(View.VISIBLE);
            orderButton.setOrderStage(orderStage);

            MaximalLinearLayout headerPanelView = (MaximalLinearLayout) orderFragmentView.findViewById(R.id.header_panel);
            headerPanelView.findViewById(R.id.header_panel_location).setVisibility(View.VISIBLE);

            LocationPickerView locationPickerView = (LocationPickerView) orderFragmentView.findViewById(R.id.picker_view);

            mapView.setY(0);
            locationPickerView.setY((mapView.getMeasuredHeight() - locationPickerView.getMeasuredHeight()) / 2);

            if (googleMap != null) {
                googleMap.getUiSettings().setAllGesturesEnabled(true);
            }
        } else if (view.getId() == R.id.my_location_button) {
            if (myLocation != null) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(myLocation));
            }
        } else if (view.getId() == R.id.zoom_in_button) {
            float currentZoom = googleMap.getCameraPosition().zoom;

            if (currentZoom < googleMap.getMaxZoomLevel() - 1) {
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(currentZoom + 1));
            }
        } else if (view.getId() == R.id.zoom_out_button) {
            float currentZoom = googleMap.getCameraPosition().zoom;

            if (currentZoom > googleMap.getMinZoomLevel() + 1) {
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(currentZoom - 1));
            }
        } else if (view.getId() == R.id.location) {
            locationTopTextView.setVisibility(View.GONE);
            locationEditText.setVisibility(View.VISIBLE);
            noSearchResultsTextView.setVisibility(View.VISIBLE);
            cancelButton.setVisibility(View.VISIBLE);
            searchResultsRelativeLayout.setVisibility(View.VISIBLE);
            orderButton.setVisibility(View.GONE);

            if (googleMap != null) {
                googleMap.getUiSettings().setAllGesturesEnabled(false);
            }

            locationEditText.requestFocusFromTouch();
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(locationEditText, InputMethodManager.SHOW_IMPLICIT);
        } else if (view.getId() == R.id.cancel_search_button) {
            resultAddresses = new String[0];
            resultPlaceIds = new String[0];
            locationEditText.setText("");
            locationTopTextView.setVisibility(View.VISIBLE);
            locationEditText.setVisibility(View.GONE);
            noSearchResultsTextView.setVisibility(View.INVISIBLE);
            cancelButton.setVisibility(View.GONE);
            searchResultsRelativeLayout.setVisibility(View.GONE);
            orderButton.setVisibility(View.VISIBLE);

            if (googleMap != null) {
                googleMap.getUiSettings().setAllGesturesEnabled(true);
            }

            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(locationEditText.getWindowToken(), 0);
        }
    }

    private void onMakeOrder() {
        loadingDialog.show();

        Calendar calendar = Calendar.getInstance();

        if (isLater) {
            if (!isToday) {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }

            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
        }

        APITalker.sharedTalker().makeOrder(
                getActivity(),
                locationTextView.getText().toString(),
                googleMap.getCameraPosition().target.latitude,
                googleMap.getCameraPosition().target.longitude,
                calendar.getTime(),
                currentCarCategory.getId(),
                comment,
                this
        );
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

        initCarCategories(carCategories);

        Database.sharedDatabase(getActivity()).storeCategories(carCategories);
    }

    @Override
    public void onGetCarCategoriesFail(int statusCode) {

    }

    private void initCarCategories(CarCategory[] carCategories) {
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
                carCategoryRhombus.setColor(getResources().getColor(R.color.cyan_100));
                carCategoryInfoTextView.setText(
                        getString(R.string.min) + String.format("%.0f", carCategory.getMinPrice()) +
                                " " + getString(R.string.one_km) + String.format("%.0f", carCategory.getRoutePrice()));
                carCategoryTitleTextView.setTextColor(getResources().getColor(R.color.cyan_100));
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

    /**
     * GeocodeUtil.GeocodeListener Methods
     */

    @Override
    public void onGeocodeSuccess(String address, double latitude, double longitude) {
        locationTopTextView.setText(address);
    }

    @Override
    public void onPlaceLocationDetermined(String address, LatLng placeLocation) {
        locationTopTextView.setText(address);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(placeLocation));
        onClick(cancelButton);
    }

    @Override
    public void onReverseGeocodeSuccess(String searchedAddress, String[] addresses, String[] placeIds) {
        if (!searchedAddress.equals(locationEditText.getText().toString())) {
            return;
        }

        searchResultContainerLinearLayout.removeAllViews();

        this.resultAddresses = addresses;
        this.resultPlaceIds = placeIds;

        if (addresses == null || addresses.length == 0) {
            noSearchResultsTextView.setVisibility(View.VISIBLE);
            searchResultsRelativeLayout.requestLayout();

            return;
        }

        Typeface robotoThinTypeface = (TypefaceUtils.getTypeface(getActivity(), TypefaceUtils.AVAILABLE_FONTS.ROBOTO_THIN));

        noSearchResultsTextView.setVisibility(View.INVISIBLE);

        for (int index = 0 ; index < addresses.length ; index++) {
            LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View addressView = layoutInflater.inflate(R.layout.item_address, searchResultContainerLinearLayout, false);
            TextView addressTextView = (TextView) addressView.findViewById(R.id.address);
            addressTextView.setTypeface(robotoThinTypeface);
            addressTextView.setText(addresses[index]);
            addressView.setOnClickListener(new AddressItemClickListener(placeIds[index]));

            searchResultContainerLinearLayout.addView(addressView);
        }

        searchResultsRelativeLayout.requestLayout();
    }

    /**
     * MakeOrderListener Methods
     */

    @Override
    public void onMakeOrderSuccess(Order order) {
        if (listener != null) {
            listener.onOrderMade(order);
        }

        loadingDialog.dismiss();
    }

    @Override
    public void onMakeOrderFail(int statusCode) {
        loadingDialog.dismiss();

        MessageHandlerUtil.showErrorForStatusCode(statusCode, getActivity());
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
                    GeocodeUtil.geocode(
                            getActivity(),
                            googleMap.getCameraPosition().target.latitude,
                            googleMap.getCameraPosition().target.longitude,
                            MakeOrderFragment.this);
                } catch (Exception e) {
                    // TODO: Investigate Reason
                    e.printStackTrace();
                }
            }
        }
    };

    private Runnable searchInteractionRunnable = new Runnable() {
        @Override
        public void run() {
            synchronized (LOCKER) {
                try {
                    GeocodeUtil.reverseGeocode(
                            getActivity(),
                            locationEditText.getText().toString(),
                            MakeOrderFragment.this);
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

    /**
     * OnMapReadyCallback Methods
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (HardwareAccessibilityUtil.checkIfHasPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            googleMap.setOnMyLocationChangeListener(this);
            googleMap.setMyLocationEnabled(true);
        }

        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setCompassEnabled(false);

        googleMap.moveCamera(CameraUpdateFactory.zoomTo(15));

        if (hasMyLocationDetermined) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        } else {
            if (myLocation != null) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
            } else {
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(40.177570, 44.512549)));
            }
        }

        googleMap.setOnCameraChangeListener(this);

        if (HardwareAccessibilityUtil.checkIfHasPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            googleMap.setMyLocationEnabled(true);
        }

        if (locationEditText.getVisibility() == View.VISIBLE
                || closeIconButton.getVisibility() == View.VISIBLE) {
            googleMap.getUiSettings().setAllGesturesEnabled(false);
        }

        this.googleMap = googleMap;
    }

    /**
     * OnCameraChangeListener Methods
     */

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        synchronized (LOCKER) {
            mapInteractionHandler.removeCallbacks(mapInteractionRunnable);
            mapInteractionHandler.postDelayed(mapInteractionRunnable, 500);
        }
    }

    /**
     * OnMyLocationChangeListener
     */

    @Override
    public void onMyLocationChange(Location location) {
        myLocation = new LatLng(location.getLatitude(), location.getLongitude());

        if (hasMyLocationDetermined || googleMap == null) {
            return;
        }

        hasMyLocationDetermined = true;
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
    }

    /**
     * TextChangedListener Methods
     */

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        synchronized (LOCKER) {
            if (s.length() > 2) {
                mapInteractionHandler.removeCallbacks(searchInteractionRunnable);
                mapInteractionHandler.postDelayed(searchInteractionRunnable, 500);
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    /**
     * MessageDialogListener Methods
     */

    @Override
    public void onNegativeClicked(MessageDialog messageDialog) {

    }

    @Override
    public void onPositiveClicked(MessageDialog messageDialog) {
        onMakeOrder();
    }

    public interface OrderFragmentListener {
        void onOrderMade(Order order);
    }

    public class AddressItemClickListener implements View.OnClickListener {
        String placeId;

        public AddressItemClickListener(String placeId) {
            this.placeId = placeId;
        }

        @Override
        public void onClick(View view) {
            GeocodeUtil.getPlaceDetailsByPlaceId(placeId, MakeOrderFragment.this);
        }
    }
}
