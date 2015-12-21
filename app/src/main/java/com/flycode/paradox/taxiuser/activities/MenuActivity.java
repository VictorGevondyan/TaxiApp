package com.flycode.paradox.taxiuser.activities;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.flycode.paradox.taxiuser.R;
import com.flycode.paradox.taxiuser.layouts.SideMenuLayout;
import com.flycode.paradox.taxiuser.utils.TypefaceUtils;

/**
 * Created by victor on 12/14/15.
 */
public class MenuActivity  extends Activity {
    private SideMenuLayout sideMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sideMenu = (SideMenuLayout) getLayoutInflater().inflate(R.layout.activity_menu, null);

        setContentView(sideMenu);

        Typeface icomoonTypeface = TypefaceUtils.getTypeface(this, TypefaceUtils.AVAILABLE_FONTS.ICOMOON);

        Button openMenuButton = (Button) findViewById(R.id.open_menu);
        Button closeMenuButton = (Button) findViewById(R.id.close_menu);
        openMenuButton.setTypeface(icomoonTypeface);
        closeMenuButton.setTypeface(icomoonTypeface);
    }

    /**
     * Action Bar Button Methods
     */

    public void onOpenMenuClicked(View view) {
        sideMenu.toggleMenu();
    }

    public void onCloseMenuClicked(View view) {
        sideMenu.toggleMenu();
    }
}
