package com.flycode.paradox.taxiuser.fragments;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.flycode.paradox.taxiuser.R;
import com.flycode.paradox.taxiuser.dialogs.CommentDialog;
import com.flycode.paradox.taxiuser.dialogs.PickTimeDialog;
import com.flycode.paradox.taxiuser.utils.TypefaceUtils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;

public class OrderFragment extends Fragment implements View.OnClickListener, CommentDialog.CommentDialogListener {
    private final String COMMENT_DIALOG_TAG = "commentDialogTag";
    private final String TIME_DIALOG_TAG = "timeDialogTag";

    private MapView mapView;
    private GoogleMap googleMap;

    private View isNowRombus;
    private View isLaterRombus;
    private View isCashOnlyRombus;

    private TextView locationTextView;
    private TextView commentsTextView;

    private TextView minutesTextView;
    private TextView hoursTextView;
    private TextView dayTextView;

    private EditText locationEditText;

    private boolean isLater = false;
    private boolean isCashOnly = false;

    private String day;
    private String comment = "";
    private int hour;
    private int minute;

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

        isNowRombus = orderView.findViewById(R.id.now_rombus);
        isLaterRombus = orderView.findViewById(R.id.later_rombus);
        isCashOnlyRombus = orderView.findViewById(R.id.only_cash_rombus);

        TextView isNowTextView = (TextView) orderView.findViewById(R.id.now_text);
        TextView isLaterTextView = (TextView) orderView.findViewById(R.id.later_text);
        TextView isCashOnlyTextView = (TextView) orderView.findViewById(R.id.only_cash_text);

        isNowTextView.setTypeface(robotoThinTypeface);
        isLaterTextView.setTypeface(robotoThinTypeface);
        isCashOnlyTextView.setTypeface(robotoThinTypeface);

        TextView locationTextIconTextView = (TextView) orderView.findViewById(R.id.location_text_icon);
        TextView locationIconTextView = (TextView) orderView.findViewById(R.id.location_icon);
        TextView commentTextIconTextView = (TextView) orderView.findViewById(R.id.comment_text_icon);
        Button closeIconTextView = (Button) orderView.findViewById(R.id.close_button);

        locationIconTextView.setTypeface(icomoonTypeface);
        locationTextIconTextView.setTypeface(icomoonTypeface);
        commentTextIconTextView.setTypeface(icomoonTypeface);
        closeIconTextView.setTypeface(icomoonTypeface);

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

        setupTimeRombus();
        setupCacheOnlyRombus();

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

    private void setupTimeRombus() {
        isNowRombus.setBackgroundResource(isLater ? R.drawable.rombus_white : R.drawable.rombus_green);
        isLaterRombus.setBackgroundResource(isLater ? R.drawable.rombus_green : R.drawable.rombus_white);
    }

    private void setupCacheOnlyRombus() {
        isCashOnlyRombus.setBackgroundResource(isCashOnly ? R.drawable.rombus_green : R.drawable.rombus_white);
    }

    /**
     * OnClickListener Methods
     */

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.now) {
            isLater = false;
            setupTimeRombus();
        } else if (view.getId() == R.id.later) {
            isLater = true;
            setupTimeRombus();
        } else if (view.getId() == R.id.only_cash) {
            isCashOnly = !isCashOnly;
            setupCacheOnlyRombus();
        } else if (view.getId() == R.id.comment_section) {
            CommentDialog.initialize(comment, this).show(getFragmentManager(), COMMENT_DIALOG_TAG);
        } else if (view.getId() == R.id.time_section) {
            PickTimeDialog.initialize(null).show(getFragmentManager(), TIME_DIALOG_TAG);
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
}