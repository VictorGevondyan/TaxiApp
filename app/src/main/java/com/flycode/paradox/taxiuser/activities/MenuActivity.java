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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flycode.paradox.taxiuser.R;
import com.flycode.paradox.taxiuser.adapters.MenuGridAdapter;
import com.flycode.paradox.taxiuser.fragments.OrderFragment;
import com.flycode.paradox.taxiuser.fragments.OrdersFragment;
import com.flycode.paradox.taxiuser.fragments.SettingsFragment;
import com.flycode.paradox.taxiuser.fragments.SuperFragment;
import com.flycode.paradox.taxiuser.fragments.TransactionsFragment;
import com.flycode.paradox.taxiuser.gcm.GCMSubscriber;
import com.flycode.paradox.taxiuser.layouts.SideMenuLayout;
import com.flycode.paradox.taxiuser.models.Order;
import com.flycode.paradox.taxiuser.settings.AppSettings;
import com.flycode.paradox.taxiuser.utils.LocaleUtils;
import com.flycode.paradox.taxiuser.utils.TypefaceUtils;

import java.io.IOException;

public class MenuActivity extends Activity implements OrderFragment.OrderFragmentListener {
    private final int INDEX_ONGOING = 0;
    private final int INDEX_BALANCE = 1;
    private final int INDEX_ORDER = 2;
    private final int INDEX_HISTORY = 3;
    private final int INDEX_SETTINGS = 5;
    private final int INDEX_LOGOUT = 9;

    private SideMenuLayout sideMenu;

    private TextView actionBarTitleTextView;
    private Button actionBarRightButton;
    private View contentView;
    private View actionBarView;
    private View actionBarOverlayView;

    private int currentPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleUtils.setLocale(this, AppSettings.sharedSettings(this).getLanguage());

        super.onCreate(savedInstanceState);

        sideMenu = (SideMenuLayout) getLayoutInflater().inflate(R.layout.activity_menu, null);

        setContentView(sideMenu);

        Typeface icomoonTypeface = TypefaceUtils.getTypeface(this, TypefaceUtils.AVAILABLE_FONTS.ICOMOON);
        Typeface robotoThinTypeface = TypefaceUtils.getTypeface(this, TypefaceUtils.AVAILABLE_FONTS.ROBOTO_THIN);
        Typeface robotoRegularTypeface = TypefaceUtils.getTypeface(this, TypefaceUtils.AVAILABLE_FONTS.ROBOTO_REGULAR);

        Button actionBarLeftButton = (Button) findViewById(R.id.action_bar_left_button);
        actionBarRightButton = (Button) findViewById(R.id.action_bar_right_button);
        Button closeMenuButton = (Button) findViewById(R.id.close_menu);
        actionBarLeftButton.setTypeface(icomoonTypeface);
        actionBarRightButton.setTypeface(icomoonTypeface);
        closeMenuButton.setTypeface(icomoonTypeface);

        actionBarTitleTextView = (TextView) findViewById(R.id.title_text);
        actionBarTitleTextView.setTypeface(robotoRegularTypeface);

        contentView = findViewById(R.id.content_fragment);
        actionBarView = findViewById(R.id.action_bar);
        actionBarOverlayView = actionBarView.findViewById(R.id.action_bar_overlay);
        actionBarView.bringToFront();

        TextView menuTitleTextView = (TextView) findViewById(R.id.menu_title);
        menuTitleTextView.setTypeface(robotoThinTypeface);

        MenuGridAdapter menuGridAdapter = new MenuGridAdapter(this, R.layout.item_menu_grid);

        GridView menuGridView = (GridView) findViewById(R.id.menu_grid);
        menuGridView.setAdapter(menuGridAdapter);
        menuGridView.setOnItemClickListener(onMenuItemClickListener);

        changeFragment(INDEX_ORDER, false);

        try {
            GCMSubscriber.registerForGcm(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        SuperFragment fragment = (SuperFragment) getFragmentManager().findFragmentById(R.id.content_fragment);
        fragment.onWindowFocusChanged(hasFocus);
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
            changeFragment(position, true);
        }
    };

    private void changeFragment(int position, boolean needsToggle) {
        if (position == currentPosition) {
            if (needsToggle) {
                sideMenu.toggleMenu();
            }

            return;
        }

        actionBarRightButton.setVisibility(View.GONE);
        actionBarOverlayView.setVisibility(View.GONE);
        setIsActionBarTransparent(false);

        currentPosition = position;

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        Fragment fragment = null;

        if (position == INDEX_BALANCE) {
            actionBarTitleTextView.setText(R.string.transactions);

            fragment = TransactionsFragment.initialize();
        } else if (position == INDEX_ORDER) {
            setIsActionBarTransparent(true);

            actionBarRightButton.setVisibility(View.VISIBLE);
            actionBarTitleTextView.setText(R.string.order);
            actionBarRightButton.setText(R.string.icon_phone);

            fragment = OrderFragment.initialize(this);
        } else if (position == INDEX_ONGOING) {
            actionBarRightButton.setVisibility(View.VISIBLE);
            actionBarRightButton.setText(R.string.icon_refresh);
            actionBarTitleTextView.setText(R.string.ongoing);

            fragment = OrdersFragment.initialize(OrdersFragment.TYPES.ONGOING);
        } else if (position == INDEX_HISTORY) {
            actionBarRightButton.setVisibility(View.VISIBLE);
            actionBarRightButton.setText(R.string.icon_refresh);
            actionBarTitleTextView.setText(R.string.ongoing);

            fragment = OrdersFragment.initialize(OrdersFragment.TYPES.HISTORY);
        } else if (position == INDEX_SETTINGS) {
            actionBarOverlayView.setVisibility(View.VISIBLE);
            actionBarTitleTextView.setText(R.string.settings);

            fragment = new SettingsFragment();
        } else if (position == INDEX_LOGOUT) {
            AppSettings.sharedSettings(this).setIsUserLoggedIn(false);
            AppSettings.sharedSettings(this).setToken(null);
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        } else {
            return;
        }

        if (getFragmentManager().findFragmentByTag("fragment") == null) {
            fragmentTransaction.add(R.id.content_fragment, fragment, "fragment");
        } else {
            fragmentTransaction.replace(R.id.content_fragment, fragment, "fragment");
        }

        fragmentTransaction.commit();

        if (needsToggle) {
            sideMenu.toggleMenu();
        }
    }

    private void setIsActionBarTransparent(boolean isTransparent) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) contentView.getLayoutParams();

        if (isTransparent) {
            layoutParams.addRule(RelativeLayout.BELOW, 0);
            actionBarView.setBackgroundColor(getResources().getColor(R.color.base_grey_90));
        } else {
            layoutParams.addRule(RelativeLayout.BELOW, R.id.action_bar);
            actionBarView.setBackgroundColor(getResources().getColor(R.color.base_grey_100));
        }

        contentView.setLayoutParams(layoutParams);
    }

    /**
     * OrderFragment.OrderFragmentListener Methods
     */

    @Override
    public void onOrderMade(Order order) {
        currentPosition = INDEX_ONGOING;

        Fragment fragment = OrdersFragment.initialize(OrdersFragment.TYPES.ONGOING);
        getFragmentManager().beginTransaction().replace(R.id.content_fragment, fragment, "fragment").commit();
    }
}
