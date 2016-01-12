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
import com.flycode.paradox.taxiuser.api.APITalker;
import com.flycode.paradox.taxiuser.api.ChangeNameAndMailHandler;
import com.flycode.paradox.taxiuser.settings.AppSettings;
import com.flycode.paradox.taxiuser.settings.UserData;
import com.flycode.paradox.taxiuser.utils.LocaleUtils;
import com.flycode.paradox.taxiuser.utils.TypefaceUtils;
import com.flycode.paradox.taxiuser.views.GenericTriangleView;

/**
 * Created by victor on 12/22/15.
 */
public class SettingsFragment extends Fragment implements View.OnClickListener, ChangeNameAndMailHandler {
    private EditText lastNameEditText;
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText oldPasswordEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;

    private TextView phoneNumberTextView;

    private View armenianRhombusView;
    private View russianRhombusView;
    private View englishRhombusView;

    private UserData userData;

    private  String newFullName;
    private String newEmail;
    private String newPassword;

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
        oldPasswordEditText = (EditText) settingsView.findViewById(R.id.old_password);
        passwordEditText = (EditText) settingsView.findViewById(R.id.enter_password);
        confirmPasswordEditText = (EditText) settingsView.findViewById(R.id.confirm_password);
        phoneNumberTextView = (TextView) settingsView.findViewById(R.id.phone_number);

        nameEditText.setText(UserData.sharedData(getActivity()).getName());
        emailEditText.setText(UserData.sharedData(getActivity()).getEmail());
        phoneNumberTextView.setText(UserData.sharedData(getActivity()).getUsername());

        nameEditText.setTypeface(robotoTypeface);
        lastNameEditText.setTypeface(robotoTypeface);
        emailEditText.setTypeface(robotoTypeface);
        oldPasswordEditText.setTypeface(robotoTypeface);
        passwordEditText.setTypeface(robotoTypeface);
        confirmPasswordEditText.setTypeface(robotoTypeface);
        phoneNumberTextView.setTypeface(robotoTypeface);

        armenianRhombusView = settingsView.findViewById(R.id.armenian_rhombus);
        englishRhombusView = settingsView.findViewById(R.id.english_rhombus);
        russianRhombusView = settingsView.findViewById(R.id.russian_rhombus);

        View armenianSection = settingsView.findViewById(R.id.armenian);
        View russianSection = settingsView.findViewById(R.id.russian);
        View englishSection = settingsView.findViewById(R.id.english);

        armenianSection.setOnClickListener(this);
        russianSection.setOnClickListener(this);
        englishSection.setOnClickListener(this);

        GenericTriangleView submitPasswordTriangleView = (GenericTriangleView) settingsView.findViewById(R.id.submit_password);
        submitPasswordTriangleView.setClickable(true);
        submitPasswordTriangleView.setOnClickListener(this);

        GenericTriangleView submitAccountTriangleView = (GenericTriangleView) settingsView.findViewById(R.id.submit_account);
        submitAccountTriangleView.setClickable(true);
        submitAccountTriangleView.setOnClickListener(this);

        setupLanguageRhombus();

        userData = UserData.sharedData(getActivity());

        return settingsView;
    }

    private void setupLanguageRhombus() {
        String language = AppSettings.sharedSettings(getActivity()).getLanguage();

        armenianRhombusView.setBackgroundResource(
                language.equals(AppSettings.LANGUAGES.HY) ? R.drawable.rhombus_green : R.drawable.rhombus_white);
        russianRhombusView.setBackgroundResource(
                language.equals(AppSettings.LANGUAGES.RU) ? R.drawable.rhombus_green : R.drawable.rhombus_white);
        englishRhombusView.setBackgroundResource(
                language.equals(AppSettings.LANGUAGES.EN) ? R.drawable.rhombus_green : R.drawable.rhombus_white);
    }

    /**
     * View.OnClickListener Methods
     */

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.submit_account) {
            newFullName = nameEditText.getText().toString();
            newEmail = emailEditText.getText().toString();

            APITalker.sharedTalker().changeNameAndMail(getActivity(), newFullName, newEmail,  this);

        } else if (view.getId() == R.id.submit_password) {

            String oldPassword = oldPasswordEditText.getText().toString();
            newPassword = passwordEditText.getText().toString();
            APITalker.sharedTalker().changeUserPassword(getActivity(), oldPassword, newPassword);

        } else if (view.getId() == R.id.russian) {
            AppSettings.sharedSettings(getActivity()).setLanguage(AppSettings.LANGUAGES.RU);
            setupLanguageRhombus();
            LocaleUtils.setLocale(getActivity(), AppSettings.LANGUAGES.RU);
            getActivity().recreate();
        } else if (view.getId() == R.id.armenian) {
            AppSettings.sharedSettings(getActivity()).setLanguage(AppSettings.LANGUAGES.HY);
            setupLanguageRhombus();
            LocaleUtils.setLocale(getActivity(), AppSettings.LANGUAGES.HY);
            getActivity().recreate();
        } else if (view.getId() == R.id.english) {
            AppSettings.sharedSettings(getActivity()).setLanguage(AppSettings.LANGUAGES.EN);
            setupLanguageRhombus();
            LocaleUtils.setLocale(getActivity(), AppSettings.LANGUAGES.EN);
            getActivity().recreate();
        }
    }

    @Override
    public void onChangeNameAndMailSuccess() {
        userData.setName(newFullName);
        userData.setEmail(newEmail);

    }

    @Override
    public void onChangeNameAndMailFailure() {

    }
}
