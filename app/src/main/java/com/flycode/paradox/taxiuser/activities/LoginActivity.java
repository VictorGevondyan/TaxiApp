package com.flycode.paradox.taxiuser.activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.flycode.paradox.taxiuser.R;
import com.flycode.paradox.taxiuser.api.APITalker;
import com.flycode.paradox.taxiuser.api.GetUserHandler;
import com.flycode.paradox.taxiuser.api.LoginHandler;
import com.flycode.paradox.taxiuser.api.OnReceivePasswordRequestListener;
import com.flycode.paradox.taxiuser.dialogs.LoadingDialog;
import com.flycode.paradox.taxiuser.gcm.GCMUtils;
import com.flycode.paradox.taxiuser.models.User;
import com.flycode.paradox.taxiuser.settings.AppSettings;
import com.flycode.paradox.taxiuser.settings.UserData;
import com.flycode.paradox.taxiuser.utils.LocaleUtils;
import com.flycode.paradox.taxiuser.utils.MessageHandlerUtil;
import com.flycode.paradox.taxiuser.utils.TypefaceUtils;

/**
 * Created by victor on 12/14/15.
 */
public class LoginActivity extends SuperActivity implements LoginHandler, GetUserHandler, OnReceivePasswordRequestListener {
    private TextView numberEditText;
    private TextView passwordEditText;
    private Spinner codeSpinner;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleUtils.setLocale(this, AppSettings.sharedSettings(this).getLanguage());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (AppSettings.sharedSettings(this).isUserLoggedIn()) {
            startActivity(new Intent(this, MenuActivity.class));
            finish();
        }

        loadingDialog = new LoadingDialog(this);

        Typeface icomoonTypeface = TypefaceUtils.getTypeface(this, TypefaceUtils.AVAILABLE_FONTS.ICOMOON);
        Typeface robotoThinTypeface = TypefaceUtils.getTypeface(this, TypefaceUtils.AVAILABLE_FONTS.ROBOTO_THIN);

        TextView mobileIconTextView = (TextView) findViewById(R.id.mobile_icon);
        TextView lockIconTextView = (TextView) findViewById(R.id.lock_icon);
        TextView logoIconTextView = (TextView) findViewById(R.id.logo);
        mobileIconTextView.setTypeface(icomoonTypeface);
        lockIconTextView.setTypeface(icomoonTypeface);
        logoIconTextView.setTypeface(icomoonTypeface);

//        CodeSpinnerAdapter codeSpinnerAdapter = new CodeSpinnerAdapter(this);

        numberEditText = (EditText) findViewById(R.id.number);
        passwordEditText = (EditText) findViewById(R.id.password);
//        codeSpinner = (Spinner) findViewById(R.id.phone_code);
        numberEditText.setTypeface(robotoThinTypeface);
        passwordEditText.setTypeface(robotoThinTypeface);
//        codeSpinner.setAdapter(codeSpinnerAdapter);
//        codeSpinner.setSelection(codeSpinnerAdapter.getDefaultPosition());

        String username = UserData.sharedData(this).getUsername();
        String cashedPassword = UserData.sharedData(this).getPassword();

        if (username != null && !username.isEmpty()) {
            numberEditText.setText(username.replaceFirst("374", ""));
        }

        if (cashedPassword != null && !cashedPassword.isEmpty()) {
            passwordEditText.setText(cashedPassword);
        }

        TextView phoneCodeTextView = (TextView) findViewById(R.id.phone_code);
        phoneCodeTextView.setTypeface(robotoThinTypeface);

//        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//        String number = telephonyManager.getLine1Number();
//
//        if (number != null && !number.isEmpty()) {
//            numberEditText.setText(number);
//        }
    }

    /**
     * Button Click Methods
     */

    public void onLoginClicked(View view) {
        String number = numberEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (number.isEmpty()) {
            MessageHandlerUtil.showMessage(R.string.attention, R.string.empty_phone_number, this);
            return;
        }
        if (number.length() != 8 || number.charAt(0) == '0') {
            MessageHandlerUtil.showMessage(R.string.attention, R.string.invalid_phone_number, this);
            return;
        }
        if (password.isEmpty()) {
            MessageHandlerUtil.showMessage(R.string.attention, R.string.empty_password, this);
            return;
        }

        loadingDialog.show();

        APITalker.sharedTalker().login(this, "374" + number, password, this);
    }

    public void onReceivePasswordClicked(View view) {
        String number = numberEditText.getText().toString();

        if (number.isEmpty()) {
            MessageHandlerUtil.showMessage(R.string.attention, R.string.empty_phone_number, this);
            return;
        }
        if (number.length() != 8 || number.charAt(0) == '0') {
            MessageHandlerUtil.showMessage(R.string.attention, R.string.invalid_phone_number, this);
            return;
        }

        loadingDialog.show();

        APITalker.sharedTalker().receivePassword("374" + number, this);
    }

    /**
     * LoginHandler Methods
     */

    @Override
    public void onLoginSuccess() {
        loadingDialog.dismiss();

        APITalker.sharedTalker().getUser(this, this);
    }

    @Override
    public void onLoginFailure(int statusCode) {
        loadingDialog.dismiss();

        if (statusCode == 401 || statusCode == 403 || statusCode == 404) {
            MessageHandlerUtil.showMessage(R.string.wrong_credentials, R.string.phone_pass_combination_wrong, this);
        } else {
            MessageHandlerUtil.showErrorForStatusCode(statusCode, this);
        }
    }

    /**
     * GetUserHandler Methods
     */

    @Override
    public void onGetUserSuccess(User user) {
        if (Build.VERSION.SDK_INT >= 16) {
            ActivityOptions options = ActivityOptions.makeCustomAnimation(this, 0, R.anim.slide_down_out);
            startActivity(new Intent(this, MenuActivity.class), options.toBundle());
        } else {
            startActivity(new Intent(this, MenuActivity.class));
        }

        loadingDialog.dismiss();

        finish();
        GCMUtils.removeRegistrationId(this);
        AppSettings.sharedSettings(this).setIsUserLoggedIn(true);
        UserData.sharedData(this).setPassword(passwordEditText.getText().toString());
        setUserData(user);
    }

    @Override
    public void onGetUserFailure(int statusCode) {
        loadingDialog.dismiss();

        MessageHandlerUtil.showErrorForStatusCode(statusCode, this);
    }

    private void setUserData(User user) {
        UserData userData = UserData.sharedData(this);

        userData.setId(user.getId());
        userData.setUsername(user.getUsername());
        userData.setName(user.getName());
        userData.setBalance(user.getBalance());
    }

    /**
     * OnReceivePasswordRequestListener Methods
     */

    @Override
    public void onReceivePasswordRequestSuccess() {
        loadingDialog.dismiss();

        MessageHandlerUtil.showMessage(R.string.success, R.string.you_will_receive_sms, this);
    }

    @Override
    public void onReceivePasswordRequestFail(int statusCode) {
        loadingDialog.dismiss();

        if (statusCode == 422 || statusCode == 200 || statusCode == 201) {
            MessageHandlerUtil.showMessage(R.string.success, R.string.you_will_receive_sms, this);
        } else {
            MessageHandlerUtil.showErrorForStatusCode(statusCode, this);
        }
    }
}
