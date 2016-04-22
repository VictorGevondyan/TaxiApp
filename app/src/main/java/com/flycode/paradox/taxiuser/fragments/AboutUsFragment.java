package com.flycode.paradox.taxiuser.fragments;

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
import com.flycode.paradox.taxiuser.models.Translation;
import com.flycode.paradox.taxiuser.settings.AppSettings;
import com.flycode.paradox.taxiuser.utils.TypefaceUtils;

/**
 * Created by anhaytananun on 21.03.16.
 */
public class AboutUsFragment extends SuperFragment implements OnGetTranslationResultHandler {
    private TextView infoTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View aboutUsView = inflater.inflate(R.layout.fragment_about_us, container, false);

        infoTextView = (TextView) aboutUsView.findViewById(R.id.info);
        infoTextView.setTypeface(TypefaceUtils.getTypeface(getActivity(), TypefaceUtils.AVAILABLE_FONTS.ROBOTO_THIN));

        setInfo();

        APITalker.sharedTalker().getTranslation(getActivity(), new String[]{"about-us-info"}, this);

        return aboutUsView;
    }

    private void setInfo() {
        String info = Database.sharedDatabase(getActivity()).getTranslation(AppSettings.sharedSettings(getActivity()).getLanguage(), "about-us-info");

        if (info.isEmpty()) {
            info = getString(R.string.about_us_info);
        }

        infoTextView.setText(info);
    }

    /**
     * OnGetTranslationResultHandler Methods
     */

    @Override
    public void onGetTranslationSuccess(Translation[] translations) {
        setInfo();
    }

    @Override
    public void onGetTranslationFailure(int status) {

    }
}
