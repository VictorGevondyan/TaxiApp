package com.flycode.paradox.taxiuser.fragments;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flycode.paradox.taxiuser.R;
import com.flycode.paradox.taxiuser.api.APITalker;
import com.flycode.paradox.taxiuser.api.CarCategoriesListener;
import com.flycode.paradox.taxiuser.api.MakeOrderListener;
import com.flycode.paradox.taxiuser.dialogs.CommentDialog;
import com.flycode.paradox.taxiuser.dialogs.PickTimeDialog;
import com.flycode.paradox.taxiuser.models.CarCategory;
import com.flycode.paradox.taxiuser.models.Order;
import com.flycode.paradox.taxiuser.utils.GeocodeUtil;
import com.flycode.paradox.taxiuser.utils.TypefaceUtils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;

import java.util.Calendar;
import java.util.Date;

public class OrderFragment extends Fragment implements View.OnClickListener, CommentDialog.CommentDialogListener, CarCategoriesListener, GoogleMap.OnCameraChangeListener, GeocodeUtil.GeocodeListener, MakeOrderListener {
    private final String COMMENT_DIALOG_TAG = "commentDialogTag";
    private final String TIME_DIALOG_TAG = "timeDialogTag";

    private MapView mapView;
    private GoogleMap googleMap;

    private ImageView isNowRhombus;
    private ImageView isLaterRhombus;
    private ImageView isCashOnlyRhombus;

    private TextView locationTextView;
    private TextView commentsTextView;

    private TextView minutesTextView;
    private TextView hoursTextView;
    private TextView dayTextView;

    private EditText locationEditText;

    private Button closeIconButton;
    private Button orderButton;

    private LinearLayout carCategoriesSectionLinearLayout;
    private LinearLayout orderDetailsLinerLayout;

    private boolean isLater = false;
    private boolean isCashOnly = false;

    private String comment = "";
    private int hour;
    private int minute;
    private boolean isToday = true;

    private int orderStage = 0;

    private CarCategory currentCarCategory;

    private OrderFragmentListener listener;

    public static OrderFragment initialize(OrderFragmentListener listener) {
        OrderFragment orderFragment = new OrderFragment();
        orderFragment.setListener(listener);

        return orderFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View orderView = inflater.inflate(R.layout.fragment_order, container, false);

        Typeface robotoThinTypeface = TypefaceUtils.getTypeface(getActivity(), TypefaceUtils.AVAILABLE_FONTS.ROBOTO_THIN);
        Typeface icomoonTypeface = TypefaceUtils.getTypeface(getActivity(), TypefaceUtils.AVAILABLE_FONTS.ICOMOON);

        mapView = (MapView) orderView.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        MapsInitializer.initialize(getActivity());

        googleMap = mapView.getMap();
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnCameraChangeListener(this);

        isNowRhombus = (ImageView) orderView.findViewById(R.id.now_rombus);
        isLaterRhombus = (ImageView) orderView.findViewById(R.id.later_rombus);
        isCashOnlyRhombus = (ImageView) orderView.findViewById(R.id.only_cash_rombus);

        TextView isNowTextView = (TextView) orderView.findViewById(R.id.now_text);
        TextView isLaterTextView = (TextView) orderView.findViewById(R.id.later_text);
        TextView isCashOnlyTextView = (TextView) orderView.findViewById(R.id.only_cash_text);

        isNowTextView.setTypeface(robotoThinTypeface);
        isLaterTextView.setTypeface(robotoThinTypeface);
        isCashOnlyTextView.setTypeface(robotoThinTypeface);

        TextView locationTextIconTextView = (TextView) orderView.findViewById(R.id.location_text_icon);
        TextView locationIconTextView = (TextView) orderView.findViewById(R.id.location_icon);
        TextView commentTextIconTextView = (TextView) orderView.findViewById(R.id.comment_text_icon);

        locationIconTextView.setTypeface(icomoonTypeface);
        locationTextIconTextView.setTypeface(icomoonTypeface);
        commentTextIconTextView.setTypeface(icomoonTypeface);

        orderView.findViewById(R.id.time_section).setOnClickListener(this);
        orderView.findViewById(R.id.comment_section).setOnClickListener(this);
        orderView.findViewById(R.id.now).setOnClickListener(this);
        orderView.findViewById(R.id.later).setOnClickListener(this);
        orderView.findViewById(R.id.only_cash).setOnClickListener(this);

        dayTextView = (TextView) orderView.findViewById(R.id.day);
        hoursTextView = (TextView) orderView.findViewById(R.id.hours);
        minutesTextView = (TextView) orderView.findViewById(R.id.minutes);

        dayTextView.setTypeface(robotoThinTypeface);
        hoursTextView.setTypeface(robotoThinTypeface);
        minutesTextView.setTypeface(robotoThinTypeface);

        locationTextView = (TextView) orderView.findViewById(R.id.location_text);
        commentsTextView = (TextView) orderView.findViewById(R.id.comment_text);
        locationEditText = (EditText) orderView.findViewById(R.id.location);

        locationTextView.setTypeface(robotoThinTypeface);
        commentsTextView.setTypeface(robotoThinTypeface);
        locationEditText.setTypeface(robotoThinTypeface);

        setupTimeRhombus();
        setupCacheOnlyRhombus();

        Fragment commentDialogCandidate = getFragmentManager().findFragmentByTag(COMMENT_DIALOG_TAG);
        Fragment timeDialogCandidate = getFragmentManager().findFragmentByTag(TIME_DIALOG_TAG);

        if (commentDialogCandidate != null) {
            CommentDialog commentsDialog = (CommentDialog) commentDialogCandidate;
            commentsDialog.setListener(this);
        }
        if (timeDialogCandidate != null) {
            PickTimeDialog timeDialog = (PickTimeDialog) timeDialogCandidate;
            timeDialog.setListener(null);
        }

        orderButton = (Button) orderView.findViewById(R.id.order_button);
        orderButton.setTypeface(robotoThinTypeface);
        orderButton.setText(R.string.order);
        orderButton.setOnClickListener(this);

        closeIconButton = (Button) orderView.findViewById(R.id.close_button);
        closeIconButton.setTypeface(icomoonTypeface);
        closeIconButton.setTypeface(robotoThinTypeface);
        closeIconButton.setOnClickListener(this);

        carCategoriesSectionLinearLayout = (LinearLayout) orderView.findViewById(R.id.car_categories_container);
        orderDetailsLinerLayout = (LinearLayout) orderView.findViewById(R.id.order_details);

        Calendar calendar = Calendar.getInstance();
        minute = calendar.get(Calendar.MINUTE);
        hour = calendar.get(Calendar.HOUR_OF_DAY);

        dayTextView.setText(isToday ? R.string.today : R.string.tomorrow);
        minutesTextView.setText(String.valueOf(minute));
        hoursTextView.setText(String.valueOf(hour));

        APITalker.sharedTalker().getCarCategories(this);

        return orderView;
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

    /**
     * Rombus Methods
     */

    private void setupTimeRhombus() {
        isNowRhombus.setImageResource(isLater ? R.drawable.rhombus_white : R.drawable.rhombus_green);
        isLaterRhombus.setImageResource(isLater ? R.drawable.rhombus_green : R.drawable.rhombus_white);
    }

    private void setupCacheOnlyRhombus() {
        isCashOnlyRhombus.setImageResource(isCashOnly ? R.drawable.rhombus_green : R.drawable.rhombus_white);
    }

    private void setupCarCategoryRhombus(View chosenSection) {
        for (int index = 0 ; index < carCategoriesSectionLinearLayout.getChildCount() ; index++) {
            View carCategorySection = carCategoriesSectionLinearLayout.getChildAt(index);
            ImageView rhombus = (ImageView) carCategorySection.findViewById(R.id.rhombus);
            TextView carCategoryInfoTextView = (TextView) carCategorySection.findViewById(R.id.info);

            if (chosenSection.equals(carCategorySection)) {
                CarCategory carCategory = (CarCategory) chosenSection.getTag();
                currentCarCategory = carCategory;
                rhombus.setImageResource(R.drawable.rhombus_green);
//                rhombus.setBackgroundResource(R.drawable.rhombus_green);
                carCategoryInfoTextView.setText(getString(R.string.min) + carCategory.getMinPrice() + " " + getString(R.string.one_km) + carCategory.getRoutePrice());
            } else {
                rhombus.setImageResource(R.drawable.rhombus_white);
                carCategoryInfoTextView.setText("");
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
            PickTimeDialog.initialize(null).show(getFragmentManager(), TIME_DIALOG_TAG);
        } else if (view.getId() == R.id.order_button) {
            if (orderStage == 0) {
                orderButton.setText(R.string.confirm);
                orderDetailsLinerLayout.setVisibility(View.VISIBLE);
                closeIconButton.setVisibility(View.VISIBLE);
                googleMap.getUiSettings().setAllGesturesEnabled(false);
                locationTextView.setText(locationEditText.getText());
                orderStage++;
            } else {
                APITalker.sharedTalker().makeOrder(
                        getActivity(),
                        locationTextView.getText().toString(),
                        googleMap.getCameraPosition().target,
                        new Date(),
                        currentCarCategory.getId(),
                        comment,
                        this
                );
            }
        } else if (view.getId() == R.id.close_button) {
            orderButton.setText(R.string.order);
            orderDetailsLinerLayout.setVisibility(View.GONE);
            closeIconButton.setVisibility(View.GONE);
            googleMap.getUiSettings().setAllGesturesEnabled(true);
            orderStage--;
        }
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
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        carCategoriesSectionLinearLayout.removeAllViews();
        carCategoriesSectionLinearLayout.setWeightSum(carCategories.length);

        for (int index = 0 ; index < carCategories.length ; index++) {
            CarCategory carCategory = carCategories[index];
            View carCategoryView = inflater.inflate(R.layout.item_car_category, carCategoriesSectionLinearLayout, false);
            LinearLayout carCategorySectionLinearLayout = (LinearLayout) carCategoryView.findViewById(R.id.section);
            carCategorySectionLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
            carCategoryView.setTag(carCategory);
            carCategoryView.setClickable(true);
            carCategoryView.setOnClickListener(this);

            TextView carCategoryInfoTextView = (TextView) carCategoryView.findViewById(R.id.info);
            carCategoryInfoTextView.setTypeface(TypefaceUtils.getTypeface(getActivity(), TypefaceUtils.AVAILABLE_FONTS.ROBOTO_THIN));

            if (index == 0) {
                currentCarCategory = carCategory;
                ImageView carCategoryRhombus = (ImageView) carCategoryView.findViewById(R.id.rhombus);
                carCategoryRhombus.setImageResource(R.drawable.rhombus_green);
                carCategoryInfoTextView.setText(getString(R.string.min) + carCategory.getMinPrice() + " " + getString(R.string.one_km) + carCategory.getRoutePrice());
            } else {
                ImageView carCategoryRhombus = (ImageView) carCategoryView.findViewById(R.id.rhombus);
                carCategoryRhombus.setImageResource(R.drawable.rhombus_white);
            }

            TextView carCategoryNameTextView = (TextView) carCategoryView.findViewById(R.id.text);
            carCategoryNameTextView.setText(carCategory.getName().toUpperCase());
            carCategoryNameTextView.setTypeface(TypefaceUtils.getTypeface(getActivity(), TypefaceUtils.AVAILABLE_FONTS.ROBOTO_THIN));

            carCategoriesSectionLinearLayout.addView(carCategoryView);
        }
    }

    @Override
    public void onGetCarCategoriesFail() {

    }

    /**
     * GoogleMap.OnCameraChangeListener Methods
     */

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        locationEditText.setText("");
        GeocodeUtil.geocode(getActivity(), cameraPosition.target, this);
    }

    /**
     * GeocodeUtil.GeocodeListener Methods
     */

    @Override
    public void onGeocodeSuccess(String address) {
        locationEditText.setText(address);
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
     * OrderFragment Listener
     */

    public void setListener(OrderFragmentListener listener) {
        this.listener = listener;
    }

    public interface OrderFragmentListener {
        void onOrderMade(Order order);
    }
}
