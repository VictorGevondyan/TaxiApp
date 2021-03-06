package com.flycode.paradox.taxiuser.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.flycode.paradox.taxiuser.R;
import com.flycode.paradox.taxiuser.api.APITalker;
import com.flycode.paradox.taxiuser.api.ChangeNameAndMailRequestListener;
import com.flycode.paradox.taxiuser.api.ChangePasswordRequestHandler;
import com.flycode.paradox.taxiuser.dialogs.LoadingDialog;
import com.flycode.paradox.taxiuser.settings.AppSettings;
import com.flycode.paradox.taxiuser.settings.UserData;
import com.flycode.paradox.taxiuser.utils.LocaleUtils;
import com.flycode.paradox.taxiuser.utils.MessageHandlerUtil;
import com.flycode.paradox.taxiuser.utils.TypefaceUtils;
import com.flycode.paradox.taxiuser.views.GenericTriangleView;
import com.flycode.paradox.taxiuser.views.RhombusView;

/**
 * Created by victor on 12/22/15.
 */
public class SettingsFragment extends SuperFragment implements View.OnClickListener, ChangeNameAndMailRequestListener, ChangePasswordRequestHandler {

    private EditText lastNameEditText;
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText oldPasswordEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;

    private String lastName;
    private String name;
    private String email;
    private String oldPassword;
    private String password;
    private String confirmPassword;

    private RhombusView armenianRhombusView;
    private RhombusView russianRhombusView;
    private RhombusView englishRhombusView;

    private TextView armenianTextView;
    private TextView russianTextView;
    private TextView englishTextView;

    private LoadingDialog loadingDialog;

    private UserData userData;

    private String newFullName;
    private String newEmail;

    private final String SAVED_LAST_NAME = "savedLastName";
    private final String SAVED_NAME = "savedName";
    private final String SAVED_EMAIL = "savedEmail";
    private final String SAVED_OLD_PASSWORD = "savedOldPassword";
    private final String SAVED_PASSWORD = "savedPassword";
    private final String SAVED_CONFIRM_PASSWORD = "savedConfirmPassword";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        View settingsView = inflater.inflate(R.layout.fragment_settings, container, false);

        loadingDialog = new LoadingDialog(getActivity());

        Typeface icomoonTypeface = TypefaceUtils.getTypeface(getActivity(), TypefaceUtils.AVAILABLE_FONTS.ICOMOON);
        Typeface robotoTypeface = TypefaceUtils.getTypeface(getActivity(), TypefaceUtils.AVAILABLE_FONTS.ROBOTO_THIN);

        userData = UserData.sharedData(getActivity());

        TextView nameIconBigTextView = (TextView) settingsView.findViewById(R.id.name_icon_big);
        TextView nameIconTextView = (TextView) settingsView.findViewById(R.id.name_icon);
        TextView lastNameIconTextView = (TextView) settingsView.findViewById(R.id.last_name_icon);
        TextView emailIconTextView = (TextView) settingsView.findViewById(R.id.email_icon);
        TextView oldPasswordIconTextView = (TextView) settingsView.findViewById(R.id.old_password_icon);
        TextView enterPasswordIconTextView = (TextView) settingsView.findViewById(R.id.enter_password_icon);
        TextView confirmPasswordIconTextView = (TextView) settingsView.findViewById(R.id.confirm_password_icon);

        nameIconBigTextView.setTypeface(icomoonTypeface);
        nameIconTextView.setTypeface(icomoonTypeface);
        lastNameIconTextView.setTypeface(icomoonTypeface);
        emailIconTextView.setTypeface(icomoonTypeface);
        oldPasswordIconTextView.setTypeface(icomoonTypeface);
        enterPasswordIconTextView.setTypeface(icomoonTypeface);
        confirmPasswordIconTextView.setTypeface(icomoonTypeface);

        TextView changePasswordTextView = (TextView) settingsView.findViewById(R.id.change_password);
        TextView languageTextView = (TextView) settingsView.findViewById(R.id.language);

        changePasswordTextView.setTypeface(robotoTypeface);
        languageTextView.setTypeface(robotoTypeface);

        nameEditText = (EditText) settingsView.findViewById(R.id.name);
        lastNameEditText = (EditText) settingsView.findViewById(R.id.last_name);
        emailEditText = (EditText) settingsView.findViewById(R.id.email);
        oldPasswordEditText = (EditText) settingsView.findViewById(R.id.old_password);
        passwordEditText = (EditText) settingsView.findViewById(R.id.enter_password);
        confirmPasswordEditText = (EditText) settingsView.findViewById(R.id.confirm_password);
        TextView phoneNumberTextView = (TextView) settingsView.findViewById(R.id.phone_number);

        if (savedInstanceState != null) {
            lastName = savedInstanceState.getString(SAVED_LAST_NAME);
            name = savedInstanceState.getString(SAVED_NAME);
            email = savedInstanceState.getString(SAVED_EMAIL);
            oldPassword = savedInstanceState.getString(SAVED_OLD_PASSWORD);
            password = savedInstanceState.getString(SAVED_PASSWORD);
            confirmPassword = savedInstanceState.getString(SAVED_CONFIRM_PASSWORD);

            lastNameEditText.setText(lastName);
            nameEditText.setText(name);
            emailEditText.setText(email);
            oldPasswordEditText.setText(oldPassword);
            passwordEditText.setText(password);
            confirmPasswordEditText.setText(confirmPassword);
        }

        nameEditText.setText(userData.getName());
        emailEditText.setText(userData.getEmail());
        phoneNumberTextView.setText(userData.getUsername());

        nameEditText.setTypeface(robotoTypeface);
        lastNameEditText.setTypeface(robotoTypeface);
        emailEditText.setTypeface(robotoTypeface);
        oldPasswordEditText.setTypeface(robotoTypeface);
        passwordEditText.setTypeface(robotoTypeface);
        confirmPasswordEditText.setTypeface(robotoTypeface);
        phoneNumberTextView.setTypeface(robotoTypeface);

        armenianRhombusView = (RhombusView) settingsView.findViewById(R.id.armenian_rhombus);
        englishRhombusView = (RhombusView) settingsView.findViewById(R.id.english_rhombus);
        russianRhombusView = (RhombusView) settingsView.findViewById(R.id.russian_rhombus);

        armenianTextView = (TextView) settingsView.findViewById(R.id.armenian_text);
        englishTextView = (TextView) settingsView.findViewById(R.id.english_text);
        russianTextView = (TextView) settingsView.findViewById(R.id.russian_text);

        armenianTextView.setTypeface(robotoTypeface);
        russianTextView.setTypeface(robotoTypeface);
        englishTextView.setTypeface(robotoTypeface);

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

        return settingsView;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    private void setupLanguageRhombus() {
        String language = AppSettings.sharedSettings(getActivity()).getLanguage();

        armenianRhombusView.setColor(
                getResources().getColor(language.equals(AppSettings.LANGUAGES.HY) ? R.color.yellow : R.color.cyan_100));
        russianRhombusView.setColor(
                getResources().getColor(language.equals(AppSettings.LANGUAGES.RU) ? R.color.yellow : R.color.cyan_100));
        englishRhombusView.setColor(
                getResources().getColor(language.equals(AppSettings.LANGUAGES.EN) ? R.color.yellow : R.color.cyan_100));
        armenianRhombusView.setIsFilled(language.equals(AppSettings.LANGUAGES.HY));
        russianRhombusView.setIsFilled(language.equals(AppSettings.LANGUAGES.RU));
        englishRhombusView.setIsFilled(language.equals(AppSettings.LANGUAGES.EN));

        armenianTextView.setTextColor(getResources().getColor(
                language.equals(AppSettings.LANGUAGES.HY) ? R.color.yellow : R.color.white_100));
        russianTextView.setTextColor(getResources().getColor(
                language.equals(AppSettings.LANGUAGES.RU) ? R.color.yellow : R.color.white_100));
        englishTextView.setTextColor(getResources().getColor(
                language.equals(AppSettings.LANGUAGES.EN) ? R.color.yellow : R.color.white_100));
    }

    /**
     * View.OnClickListener Methods
     */

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.submit_account) {
            newFullName = nameEditText.getText().toString();
            newEmail = emailEditText.getText().toString();

            loadingDialog.show();

            APITalker.sharedTalker().changeNameAndMail(getActivity(), newFullName, newEmail, this);
        } else if (view.getId() == R.id.submit_password) {
            String oldPassword = oldPasswordEditText.getText().toString();
            String newPassword = passwordEditText.getText().toString();
            String confirmPassword = confirmPasswordEditText.getText().toString();

            if (newPassword.isEmpty() || oldPassword.isEmpty() || confirmPassword.isEmpty()) {
                MessageHandlerUtil.showMessage(R.string.app_name, R.string.empty_password, getActivity());
                return;
            }
            if (!newPassword.equals(confirmPassword)) {
                MessageHandlerUtil.showMessage(R.string.attention, R.string.password_does_not_match, getActivity());
                return;
            }

            loadingDialog.show();

            APITalker.sharedTalker().changeUserPassword(getActivity(), oldPassword, newPassword, this);
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        lastName = lastNameEditText.getText().toString();
        name = nameEditText.getText().toString();
        email = emailEditText.getText().toString();
        oldPassword = oldPasswordEditText.getText().toString();
        password = passwordEditText.getText().toString();
        confirmPassword = confirmPasswordEditText.getText().toString();

        outState.putString(SAVED_LAST_NAME, lastName);
        outState.putString(SAVED_NAME, name);
        outState.putString(SAVED_EMAIL, email);
        outState.putString(SAVED_OLD_PASSWORD, oldPassword);
        outState.putString(SAVED_PASSWORD, password);
        outState.putString(SAVED_CONFIRM_PASSWORD, confirmPassword);
    }

    /**
     * ChangeNameAndMailHandler Methods
     */

    @Override
    public void onChangeNameAndMailSuccess() {
        loadingDialog.dismiss();

        userData.setName(newFullName);
        userData.setEmail(newEmail);
        MessageHandlerUtil.showMessage(R.string.congratulation, R.string.info_changed, getActivity());
    }

    @Override
    public void onChangeNameAndMailFailure(int statusCode) {
        loadingDialog.dismiss();

        MessageHandlerUtil.showErrorForStatusCode(statusCode, getActivity());
    }

    /**
     * ChangePasswordRequestHandler
     */

    @Override
    public void onChangePasswordRequestSuccess() {
        loadingDialog.dismiss();

        UserData.sharedData(getActivity()).setPassword(passwordEditText.getText().toString());

        MessageHandlerUtil.showMessage(R.string.congratulation, R.string.password_changed, getActivity());
    }

    @Override
    public void onChangePasswordRequestFailure(int statusCode) {
        loadingDialog.dismiss();

        MessageHandlerUtil.showErrorForStatusCode(statusCode, getActivity());
    }
}
