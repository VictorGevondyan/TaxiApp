package com.flycode.paradox.taxiuser.activities;

import android.app.Activity;
import android.os.Bundle;

import com.flycode.paradox.taxiuser.R;
import com.flycode.paradox.taxiuser.settings.AppSettings;
import com.flycode.paradox.taxiuser.utils.LocaleUtils;

public class OrderActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleUtils.setLocale(this, AppSettings.sharedSettings(this).getLanguage());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
    }
}
