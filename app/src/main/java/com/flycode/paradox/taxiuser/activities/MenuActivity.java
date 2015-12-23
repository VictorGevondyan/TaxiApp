package com.flycode.paradox.taxiuser.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.flycode.paradox.taxiuser.R;
import com.flycode.paradox.taxiuser.adapters.MenuGridAdapter;
import com.flycode.paradox.taxiuser.fragments.SettingsFragment;
import com.flycode.paradox.taxiuser.layouts.SideMenuLayout;
import com.flycode.paradox.taxiuser.settings.AppSettings;
import com.flycode.paradox.taxiuser.utils.TypefaceUtils;

/**
 * Created by victor on 12/14/15.
 */
public class MenuActivity  extends Activity {

    private final int INDEX_SETTINGS = 5;
    private final int INDEX_LOGOUT = 9;

    private SideMenuLayout sideMenu;
    private MenuGridAdapter menuGridAdapter;

    private int currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sideMenu = (SideMenuLayout) getLayoutInflater().inflate(R.layout.activity_menu, null);

        setContentView(sideMenu);

        Typeface icomoonTypeface = TypefaceUtils.getTypeface(this, TypefaceUtils.AVAILABLE_FONTS.ICOMOON);
        Typeface robotoThinTypeface = TypefaceUtils.getTypeface(this, TypefaceUtils.AVAILABLE_FONTS.ROBOTO_THIN);

        Button openMenuButton = (Button) findViewById(R.id.open_menu);
        Button closeMenuButton = (Button) findViewById(R.id.close_menu);
        openMenuButton.setTypeface(icomoonTypeface);
        closeMenuButton.setTypeface(icomoonTypeface);

        TextView menuTitleTextView = (TextView) findViewById(R.id.menu_title);
        menuTitleTextView.setTypeface(robotoThinTypeface);

        menuGridAdapter = new MenuGridAdapter(this, R.layout.item_menu_grid);

        GridView menuGridView = (GridView) findViewById(R.id.menu_grid);
        menuGridView.setAdapter(menuGridAdapter);
        menuGridView.setOnItemClickListener(onMenuItemClickListener);

        changeFragment(currentPosition);
    }

    @Override
    public void onBackPressed() {

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

    AdapterView.OnItemClickListener onMenuItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            changeFragment(position);
        }
    };

    private String getStringResource(int resource) {
        return getResources().getString(resource);
    }

    public void onLogout( View view ) {
        AppSettings.sharedSettings(this).setIsUserLoggedIn(false);

        Intent loginIntent = new Intent(MenuActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    private void changeFragment( int position ) {
        if (position == currentPosition) {
            sideMenu.toggleMenu();

            return;
        }

        currentPosition = position;

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        Fragment fragment = null;

        if( position  == INDEX_SETTINGS ) {
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