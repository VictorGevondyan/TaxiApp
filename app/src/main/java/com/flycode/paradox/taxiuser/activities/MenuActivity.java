package com.flycode.paradox.taxiuser.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.flycode.paradox.taxiuser.R;
import com.flycode.paradox.taxiuser.adapters.MenuGridAdapter;
import com.flycode.paradox.taxiuser.fragments.OrderFragment;
import com.flycode.paradox.taxiuser.fragments.SettingsFragment;
import com.flycode.paradox.taxiuser.layouts.SideMenuLayout;
import com.flycode.paradox.taxiuser.settings.AppSettings;
import com.flycode.paradox.taxiuser.utils.LocaleUtils;
import com.flycode.paradox.taxiuser.utils.TypefaceUtils;

/**
 * Created by victor on 12/14/15.
 */
public class MenuActivity  extends Activity {

    private final int INDEX_ORDER = 2;
    private final int INDEX_SETTINGS = 5;
    private final int INDEX_LOGOUT = 9;

    private SideMenuLayout sideMenu;
    private MenuGridAdapter menuGridAdapter;

    private TextView actionBarTitleTextView;
    private Button actionBarRightButton;

    private int currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleUtils.setLocale(this, AppSettings.sharedSettings(this).getLanguage());

        super.onCreate(savedInstanceState);

        sideMenu = (SideMenuLayout) getLayoutInflater().inflate(R.layout.activity_menu, null);

        setContentView(sideMenu);

        Typeface icomoonTypeface = TypefaceUtils.getTypeface(this, TypefaceUtils.AVAILABLE_FONTS.ICOMOON);
        Typeface robotoThinTypeface = TypefaceUtils.getTypeface(this, TypefaceUtils.AVAILABLE_FONTS.ROBOTO_THIN);

        Button actionBarLeftButton = (Button) findViewById(R.id.action_bar_left_button);
        actionBarRightButton = (Button) findViewById(R.id.action_bar_right_button);
        Button closeMenuButton = (Button) findViewById(R.id.close_menu);
        actionBarLeftButton.setTypeface(icomoonTypeface);
        actionBarRightButton.setTypeface(icomoonTypeface);
        closeMenuButton.setTypeface(icomoonTypeface);

        actionBarTitleTextView = (TextView) findViewById(R.id.title_text);
        actionBarTitleTextView.setTypeface(robotoThinTypeface);

        TextView menuTitleTextView = (TextView) findViewById(R.id.menu_title);
        menuTitleTextView.setTypeface(robotoThinTypeface);

        menuGridAdapter = new MenuGridAdapter(this, R.layout.item_menu_grid);

        GridView menuGridView = (GridView) findViewById(R.id.menu_grid);
        menuGridView.setAdapter(menuGridAdapter);
        menuGridView.setOnItemClickListener(onMenuItemClickListener);
    }

    @Override
    public void onBackPressed() {
        if (sideMenu.isMenuShown()) {
            sideMenu.toggleMenu();
            return;
        }

        super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        recreate();
        super.onConfigurationChanged(newConfig);
    }

    /**
     * Action Bar Button Methods
     */

    public void onActionBarLeftButtonClicked(View view) {
        sideMenu.toggleMenu();
    }

    public void onActionBarRightButtonClicked(View view) {
    }

    /**
     * Side Menu Methods
     */

    public void onCloseMenuClicked(View view) {
        sideMenu.toggleMenu();
    }

    AdapterView.OnItemClickListener onMenuItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            changeFragment(position);
        }
    };

    private void changeFragment( int position ) {
        if (position == currentPosition) {
            sideMenu.toggleMenu();

            return;
        }

        actionBarRightButton.setVisibility(View.GONE);

        currentPosition = position;

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        Fragment fragment = null;

        if (position == INDEX_ORDER) {
            actionBarTitleTextView.setText(R.string.order);
            actionBarRightButton.setVisibility(View.VISIBLE);

            OrderFragment orderFragment = new OrderFragment();
            fragment = orderFragment;
        } else if( position  == INDEX_SETTINGS ) {
            actionBarTitleTextView.setText(R.string.settings);

            SettingsFragment settingsFragment = new SettingsFragment();
            fragment = settingsFragment;
        } else if ( position == INDEX_LOGOUT ) {
            AppSettings.sharedSettings(this).setIsUserLoggedIn(false);
            AppSettings.sharedSettings(this).setToken(null);
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        if (getFragmentManager().findFragmentByTag("fragment") == null) {
            fragmentTransaction.add(R.id.content_fragment, fragment, "fragment");
        } else {
            fragmentTransaction.replace(R.id.content_fragment, fragment, "fragment");
        }

        fragmentTransaction.commit();

        sideMenu.toggleMenu();
    }
}