package com.flycode.paradox.taxiuser.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flycode.paradox.taxiuser.R;
import com.flycode.paradox.taxiuser.api.APITalker;
import com.flycode.paradox.taxiuser.api.OnGetTranslationResultHandler;
import com.flycode.paradox.taxiuser.database.Database;
import com.flycode.paradox.taxiuser.layouts.RhombusLayout;
import com.flycode.paradox.taxiuser.models.Translation;
import com.flycode.paradox.taxiuser.settings.AppSettings;
import com.flycode.paradox.taxiuser.utils.TypefaceUtils;
import com.flycode.paradox.taxiuser.views.RhombusView;

/**
 * Created by anhaytananun on 22.03.16.
 */
public class ServicesFragment extends SuperFragment implements View.OnClickListener, OnGetTranslationResultHandler {
    private static final String[] TRANSLATION_KEYS = {
            "business-info",
            "standard-info",
            "minivan-info",
            "delivery-info"
    };

    private int currentService = 0;

    private RhombusLayout rhombusLayout;
    private TextView infoTextView;
    private String[] defaultInfo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View servicesView = inflater.inflate(R.layout.fragment_services, container, false);

        infoTextView = (TextView) servicesView.findViewById(R.id.info_text);
        infoTextView.setTypeface(TypefaceUtils.getTypeface(getActivity(), TypefaceUtils.AVAILABLE_FONTS.ROBOTO_THIN));

        rhombusLayout = (RhombusLayout) servicesView.findViewById(R.id.control);

        for (int index = 0 ; index < rhombusLayout.getChildCount() ; index++) {
            RhombusView rhombusView = (RhombusView) rhombusLayout.getChildAt(index);
            rhombusView.setTag(index);
            rhombusView.setIsFilled(false);
            rhombusView.setColor(Color.parseColor("#1FBAD6"));
            rhombusView.setTextColor(getResources().getColor(R.color.black_100));
            rhombusView.setOnClickListener(this);
        }

        defaultInfo = getResources().getStringArray(R.array.services_info);

        setupService();

        APITalker.sharedTalker().getTranslation(getActivity(), TRANSLATION_KEYS, this);

        return servicesView;
    }

    /**
     * View.OnClickListener Methods
     */

    @Override
    public void onClick(View view) {
        int index = (Integer)view.getTag();

        if (currentService == index) {
            return;
        }

        currentService = index;

        setupService();
    }

    private void setupService() {
        for (int index = 0 ; index < rhombusLayout.getChildCount() ; index++) {
            boolean isSelected = index == currentService;

            RhombusView rhombusView = (RhombusView) rhombusLayout.getChildAt(index);
            rhombusView.setIsFilled(!isSelected);
            rhombusView.setTextColor(getResources().getColor(isSelected ? R.color.black_100 : R.color.white_100));
        }

        refreshText();
    }

    private void refreshText() {
        String info = Database.sharedDatabase(getActivity()).getTranslation(AppSettings.sharedSettings(getActivity()).getLanguage(), TRANSLATION_KEYS[currentService]);

        if (info.isEmpty()) {
            info = defaultInfo[currentService];
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
