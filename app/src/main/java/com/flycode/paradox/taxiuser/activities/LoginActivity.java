package com.flycode.paradox.taxiuser.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.flycode.paradox.taxiuser.R;
import com.flycode.paradox.taxiuser.utils.TypefaceUtils;

/**
 * Created by victor on 12/14/15.
 */
public class LoginActivity  extends Activity {
    private TextView numberTextView;
    private TextView passwordTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
        // TODO: Login User First
        startActivity(new Intent(this, MenuActivity.class));
    }

    public void onReceivePasswordClicked(View view) {

    }
}
