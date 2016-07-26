package com.flycode.paradox.taxiuser.activities;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

import com.flycode.paradox.taxiuser.R;
import com.flycode.paradox.taxiuser.settings.AppSettings;
import com.flycode.paradox.taxiuser.utils.TypefaceUtils;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends Activity {
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Initialize

        Typeface robotoThinTypeface = TypefaceUtils.getTypeface(this, TypefaceUtils.AVAILABLE_FONTS.ROBOTO_THIN);
        Typeface icomoonTypeface = TypefaceUtils.getTypeface(this, TypefaceUtils.AVAILABLE_FONTS.ICOMOON);

        TextView poweredByTextView = (TextView) findViewById(R.id.powered_by);
        TextView logoTextView = (TextView) findViewById(R.id.logo);

        poweredByTextView.setTypeface(robotoThinTypeface);
        logoTextView.setTypeface(icomoonTypeface);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Set timer for 1.5 seconds. Open application when timer expires.

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (AppSettings.sharedSettings(SplashActivity.this).isUserLoggedIn()) {
                    // Open MenuActivity if user is already logged in.
                    Intent intent = new Intent(SplashActivity.this, MenuActivity.class);
                    startActivity(intent);
                } else {
                    // Open LoginActivity, if user needs to log in.
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);

                    if (android.os.Build.VERSION.SDK_INT >= 16) {
                        startActivity(
                                intent,
                                ActivityOptions.makeCustomAnimation(SplashActivity.this, R.anim.fade_in, R.anim.fade_out).toBundle());
                    } else {
                        startActivity(intent);
                    }
                }
            }
        }, 1500);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Cancel timer.

        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
    }
}
