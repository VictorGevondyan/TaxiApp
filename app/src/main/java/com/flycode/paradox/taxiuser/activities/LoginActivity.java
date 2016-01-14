package com.flycode.paradox.taxiuser.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.flycode.paradox.taxiuser.R;
import com.flycode.paradox.taxiuser.api.APITalker;
import com.flycode.paradox.taxiuser.api.GetUserHandler;
import com.flycode.paradox.taxiuser.api.LoginHandler;
import com.flycode.paradox.taxiuser.models.User;
import com.flycode.paradox.taxiuser.settings.AppSettings;
import com.flycode.paradox.taxiuser.settings.UserData;
import com.flycode.paradox.taxiuser.utils.LocaleUtils;
import com.flycode.paradox.taxiuser.utils.TypefaceUtils;

/**
 * Created by victor on 12/14/15.
 */
public class LoginActivity  extends Activity implements LoginHandler, GetUserHandler {
    private TextView numberTextView;
    private TextView passwordTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleUtils.setLocale(this, AppSettings.sharedSettings(this).getLanguage());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(AppSettings.sharedSettings(this).isUserLoggedIn()){
                startActivity(new Intent(this, MenuActivity.class));
        }

        Typeface icomoonTypeface = TypefaceUtils.getTypeface(this, TypefaceUtils.AVAILABLE_FONTS.ICOMOON);
        Typeface robotoThinTypeface = TypefaceUtils.getTypeface(this, TypefaceUtils.AVAILABLE_FONTS.ROBOTO_THIN);

        TextView mobileIconTextView = (TextView) findViewById(R.id.mobile_icon);
        TextView lockIconTextView = (TextView) findViewById(R.id.lock_icon);
        mobileIconTextView.setTypeface(icomoonTypeface);
        lockIconTextView.setTypeface(icomoonTypeface);

        numberTextView = (TextView) findViewById(R.id.number);
        passwordTextView = (TextView) findViewById(R.id.password);
        numberTextView.setTypeface(robotoThinTypeface);
        passwordTextView.setTypeface(robotoThinTypeface);
    }

    /**
     * Button Click Methods
     */

    public void onLoginClicked(View view) {
        EditText numberEditText = (EditText) findViewById(R.id.number);
        EditText passwordEditText = (EditText) findViewById(R.id.password);

        String number = numberEditText.getText().toString();
        String passwordText = passwordEditText.getText().toString();

        APITalker.sharedTalker().login(this, number, passwordText, this);

    }

    public void onReceivePasswordClicked(View view) {

    }

    /**
     * LoginHandler Methods
     */

    @Override
    public void onLoginSuccess() {
        APITalker.sharedTalker().getUser(this, this);
    }

    @Override
    public void onLoginFailure(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    /**
     * GetUserHandler Methods
     */

    @Override
    public void onGetUserSuccess(User user) {
        startActivity(new Intent(this, MenuActivity.class));
        finish();
        AppSettings.sharedSettings(this).setIsUserLoggedIn(true);
        setUserData(user);
    }

    @Override
    public void onGetUserFailure() {
        Toast.makeText(this, "NE POVEZLO :(", Toast.LENGTH_SHORT).show();
    }

    private void setUserData(User user){
        UserData userData = UserData.sharedData(this);

        userData.setId(user.getId());
        userData.setRole(user.getRole());
        userData.setUsername(user.getUsername());
        userData.setName(user.getName());
        userData.setSex(user.getSex());
//        userData.setDateOfBirth(user.getDateOfBirth());
        userData.setStatus(user.getStatus());
        userData.setBalance(user.getBalance());
    }
}
