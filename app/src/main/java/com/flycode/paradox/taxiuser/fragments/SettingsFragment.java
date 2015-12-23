package com.flycode.paradox.taxiuser.fragments;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.flycode.paradox.taxiuser.R;
import com.flycode.paradox.taxiuser.utils.TypefaceUtils;
import com.flycode.paradox.taxiuser.views.GenericTriangleView;

/**
 * Created by victor on 12/22/15.
 */
public class SettingsFragment extends Fragment implements View.OnClickListener {
    private EditText lastNameEditText;
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;

    private TextView phoneNumberTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View settingsView = inflater.inflate(R.layout.fragment_settings, container, false);

        Typeface icomoonTypeface = TypefaceUtils.getTypeface(getActivity(), TypefaceUtils.AVAILABLE_FONTS.ICOMOON);
        Typeface robotoTypeface = TypefaceUtils.getTypeface(getActivity(), TypefaceUtils.AVAILABLE_FONTS.ROBOTO_THIN);

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

        TextView changePasswordTextView = (TextView) settingsView.findViewById(R.id.change_password);
        TextView languageTextView = (TextView) settingsView.findViewById(R.id.language);
        TextView armenianTextView = (TextView) settingsView.findViewById(R.id.armenian_text);
        TextView russianTextView = (TextView) settingsView.findViewById(R.id.russian_text);
        TextView englishTextView = (TextView) settingsView.findViewById(R.id.english_text);

        changePasswordTextView.setTypeface(robotoTypeface);
        languageTextView.setTypeface(robotoTypeface);
        armenianTextView.setTypeface(robotoTypeface);
        russianTextView.setTypeface(robotoTypeface);
        englishTextView.setTypeface(robotoTypeface);

        nameEditText = (EditText) settingsView.findViewById(R.id.name);
        lastNameEditText = (EditText) settingsView.findViewById(R.id.last_name);
        emailEditText = (EditText) settingsView.findViewById(R.id.email);
        passwordEditText = (EditText) settingsView.findViewById(R.id.password);
        confirmPasswordEditText = (EditText) settingsView.findViewById(R.id.confirm_password);

        phoneNumberTextView = (TextView) settingsView.findViewById(R.id.phone_number);

        nameEditText.setTypeface(robotoTypeface);
        lastNameEditText.setTypeface(robotoTypeface);
        emailEditText.setTypeface(robotoTypeface);
        passwordEditText.setTypeface(robotoTypeface);
        confirmPasswordEditText.setTypeface(robotoTypeface);

        phoneNumberTextView.setTypeface(robotoTypeface);

        GenericTriangleView submitPasswordTriangleView = (GenericTriangleView) settingsView.findViewById(R.id.submit_password);
        submitPasswordTriangleView.setClickable(true);
        submitPasswordTriangleView.setOnClickListener(this);

        GenericTriangleView submitAccountTriangleView = (GenericTriangleView) settingsView.findViewById(R.id.submit_account);
        submitAccountTriangleView.setClickable(true);
        submitAccountTriangleView.setOnClickListener(this);

        return settingsView;
    }

    /**
     * View.OnClickListener Methods
     */

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.submit_account) {

        } else if (view.getId() == R.id.submit_password) {

        }
    }
}