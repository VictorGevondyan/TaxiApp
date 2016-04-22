package com.flycode.paradox.taxiuser.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flycode.paradox.taxiuser.R;
import com.flycode.paradox.taxiuser.api.APITalker;
import com.flycode.paradox.taxiuser.api.OnGetTranslationResultHandler;
import com.flycode.paradox.taxiuser.database.Database;
import com.flycode.paradox.taxiuser.models.Translation;
import com.flycode.paradox.taxiuser.settings.AppSettings;
import com.flycode.paradox.taxiuser.utils.TypefaceUtils;
import com.flycode.paradox.taxiuser.views.RhombusView;

/**
 * Created by anhaytananun on 21.03.16.
 */
public class HelpFragment extends SuperFragment implements View.OnClickListener, OnGetTranslationResultHandler {
    private static final int PAGES_NUMBER = 5;

    private static final int[] IMAGES = {
            R.drawable.help_order,
            R.drawable.help_order_settings,
            R.drawable.help_confirm,
            R.drawable.help_driver_details,
            R.drawable.help_feedback
    };
    private static final String[] TRANSLATION_KEYS = {
            "help-order-info",
            "help-order-details-info",
            "help-order-confirm-info",
            "help-trip-details-info",
            "help-feedback-info"
    };

    private LinearLayout pagerControlsLinearLayout;
    private TextView infoTextView;
    private ImageView infoImageView;

    private String[] defaultInfo;

    private int currentPage = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View helpView = inflater.inflate(R.layout.fragment_help, container, false);

        Typeface typeface = TypefaceUtils.getTypeface(getActivity(), TypefaceUtils.AVAILABLE_FONTS.ROBOTO_THIN);

        infoTextView = (TextView) helpView.findViewById(R.id.info_text);
        infoTextView.setTypeface(typeface);

        infoImageView = (ImageView) helpView.findViewById(R.id.info_image);

        pagerControlsLinearLayout = (LinearLayout) helpView.findViewById(R.id.paging_controls);

        for (int index = 0 ; index < PAGES_NUMBER ; index++) {
            View pagingControl = inflater.inflate(R.layout.item_paging_control, pagerControlsLinearLayout, false);
            pagerControlsLinearLayout.addView(pagingControl);

            RhombusView outerRhombus = (RhombusView) pagingControl.findViewById(R.id.outer_rhombus);
            RhombusView innerRhombus = (RhombusView) pagingControl.findViewById(R.id.inner_rhombus);
            outerRhombus.setIsFilled(false);
            innerRhombus.setIsFilled(true);
            outerRhombus.setColor(getResources().getColor(R.color.base_grey_100));
            innerRhombus.setColor(getResources().getColor(R.color.cyan_100));

            outerRhombus.setTag(index);
            outerRhombus.setClickable(true);
            outerRhombus.setOnClickListener(this);
        }

        pagerControlsLinearLayout.requestLayout();

        defaultInfo = getResources().getStringArray(R.array.help_info);

        setPage();

        APITalker.sharedTalker().getTranslation(getActivity(), TRANSLATION_KEYS, this);

        return helpView;
    }

    @Override
    public void onClick(View view) {
        currentPage = (Integer) view.getTag();
        setPage();
    }

    private void setPage() {
        infoImageView.setImageResource(IMAGES[currentPage]);

        for (int index = 0 ; index < PAGES_NUMBER ; index++) {
            View pagingControl = pagerControlsLinearLayout.getChildAt(index);
            RhombusView outerRhombus = (RhombusView) pagingControl.findViewById(R.id.inner_rhombus);

            if (index == currentPage) {
                outerRhombus.setVisibility(View.VISIBLE);
            } else {
                outerRhombus.setVisibility(View.GONE);
            }
        }

        refreshText();
    }

    private void refreshText() {
        String info = Database.sharedDatabase(getActivity()).getTranslation(AppSettings.sharedSettings(getActivity()).getLanguage(), TRANSLATION_KEYS[currentPage]);

        if (info.isEmpty()) {
            info = defaultInfo[currentPage];
        }

        infoTextView.setText(info);
    }

    /**
     * OnGetTranslationResultHandler Methods
     */

    @Override
    public void onGetTranslationSuccess(Translation[] translations) {
        refreshText();
    }

    @Override
    public void onGetTranslationFailure(int status) {

    }
}
