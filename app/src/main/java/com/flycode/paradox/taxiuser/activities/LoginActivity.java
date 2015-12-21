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
import com.flycode.paradox.taxiuser.settings.AppSettings;
import com.flycode.paradox.taxiuser.talkers.APITalker;
import com.flycode.paradox.taxiuser.talkers.LoginHandler;
import com.flycode.paradox.taxiuser.utils.TypefaceUtils;

/**
 * Created by victor on 12/14/15.
 */
public class LoginActivity  extends Activity implements LoginHandler {
    private TextView numberTextView;
    private TextView passwordTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

    @Override
    public void onLoginSuccess() {
        startActivity(new Intent(this, MenuActivity.class));
        finish();
        AppSettings.sharedSettings(this).setIsUserLoggedIn(true);
    }

    @Override
    public void onLoginFailure(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }
}
