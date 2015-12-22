package com.flycode.paradox.taxiuser.fragments;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flycode.paradox.taxiuser.R;
import com.flycode.paradox.taxiuser.utils.TypefaceUtils;

/**
 * Created by victor on 12/22/15.
 */
public class SettingsFragment extends Fragment{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View settingsView = inflater.inflate(R.layout.fragment_settings, container, false);

        Typeface icomoonTypeface = TypefaceUtils.getTypeface(getActivity(), TypefaceUtils.AVAILABLE_FONTS.ICOMOON);

        TextView nameIconBigTextView = ( TextView )settingsView.findViewById(R.id.name_icon_big);
        TextView nameIconTextView = ( TextView )settingsView.findViewById(R.id.name_icon);
        TextView lastNameIconTextView = ( TextView )settingsView.findViewById(R.id.last_name_icon);
        TextView emailIconTextView = ( TextView )settingsView.findViewById(R.id.email_icon);
        TextView enterPasswordIconTextView = ( TextView )settingsView.findViewById(R.id.enter_password_icon);
        TextView confirmPasswordIconTextView = ( TextView )settingsView.findViewById(R.id.confirm_password_icon);

        nameIconBigTextView.setTypeface(icomoonTypeface);
        nameIconTextView.setTypeface(icomoonTypeface);
        lastNameIconTextView.setTypeface(icomoonTypeface);
        emailIconTextView.setTypeface(icomoonTypeface);
        enterPasswordIconTextView.setTypeface(icomoonTypeface);
        confirmPasswordIconTextView.setTypeface(icomoonTypeface);

        return settingsView;

    }


}
