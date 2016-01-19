package com.flycode.paradox.taxiuser.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flycode.paradox.taxiuser.R;
import com.flycode.paradox.taxiuser.adapters.MenuGridAdapter;
import com.flycode.paradox.taxiuser.api.APITalker;
import com.flycode.paradox.taxiuser.api.GetUserHandler;
import com.flycode.paradox.taxiuser.fragments.OrderFragment;
import com.flycode.paradox.taxiuser.fragments.OrdersFragment;
import com.flycode.paradox.taxiuser.fragments.SettingsFragment;
import com.flycode.paradox.taxiuser.fragments.SuperFragment;
import com.flycode.paradox.taxiuser.fragments.TransactionsFragment;
import com.flycode.paradox.taxiuser.gcm.GCMSubscriber;
import com.flycode.paradox.taxiuser.layouts.SideMenuLayout;
import com.flycode.paradox.taxiuser.models.Order;
import com.flycode.paradox.taxiuser.models.User;
import com.flycode.paradox.taxiuser.settings.AppSettings;
import com.flycode.paradox.taxiuser.settings.UserData;
import com.flycode.paradox.taxiuser.utils.LocaleUtils;
import com.flycode.paradox.taxiuser.utils.TypefaceUtils;

import java.io.IOException;

public class MenuActivity extends Activity implements OrderFragment.OrderFragmentListener, GetUserHandler {
    private final String SAVED_CURRENT_POSITION = "savedCurrentPosition";
    private final String SAVED_CURRENT_FRAGMENT = "savedCurrentFragment";

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
    private MenuGridAdapter menuGridAdapter;

    private int currentPosition = -1;

    Fragment currentFragment;

    private boolean isKeyboardOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleUtils.setLocale(this, AppSettings.sharedSettings(this).getLanguage());

        super.onCreate(savedInstanceState);

        sideMenu = (SideMenuLayout) getLayoutInflater().inflate(R.layout.activity_menu, null);

        setContentView(sideMenu);

        Typeface icomoonTypeface = TypefaceUtils.getTypeface(this, TypefaceUtils.AVAILABLE_FONTS.ICOMOON);
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
        menuTitleTextView.setTypeface(robotoRegularTypeface);

        menuGridAdapter = new MenuGridAdapter(this, R.layout.item_menu_grid);

        GridView menuGridView = (GridView) findViewById(R.id.menu_grid);
        menuGridView.setAdapter(menuGridAdapter);
        menuGridView.setOnItemClickListener(onMenuItemClickListener);

        int newPosition = INDEX_ORDER;

        if( savedInstanceState != null ) {
            currentFragment = getFragmentManager().getFragment(savedInstanceState, SAVED_CURRENT_FRAGMENT);
            newPosition = savedInstanceState.getInt(SAVED_CURRENT_POSITION);
        }

        currentPosition = -1;
        changeFragment(newPosition, false, savedInstanceState != null);

        try {
            GCMSubscriber.registerForGcm(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // TODO: Replace this in more suitable place
        APITalker.sharedTalker().getUser(this, this);

        getWindow()
            .getDecorView()
            .getViewTreeObserver()
            .addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            sideMenu.requestLayout();
                        }
                    });
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(SAVED_CURRENT_POSITION, currentPosition);

        //Save the fragment's instance
        getFragmentManager().putFragment(outState, SAVED_CURRENT_FRAGMENT, currentFragment);
    }

    /**
     * Action Bar Button Methods
     */

    public void onActionBarLeftButtonClicked(View view) {
        sideMenu.toggleMenu();
    }

    public void onActionBarRightButtonClicked(View view) {

        if( ( currentPosition == INDEX_ONGOING ) || ( currentPosition == INDEX_HISTORY ) ){
            OrdersFragment ordersFragment = (OrdersFragment)currentFragment;
            ordersFragment.refresh();
        } else if( ( currentPosition == INDEX_BALANCE )){
            TransactionsFragment transactionsFragment = (TransactionsFragment)currentFragment;
            transactionsFragment.refresh();
        }

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
            changeFragment(position, true, false);
        }
    };

    private void changeFragment(int position, boolean needsToggle, boolean fromSavedInstanceState) {
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
            actionBarRightButton.setVisibility(View.VISIBLE);
            actionBarRightButton.setText(R.string.icon_refresh);
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
            actionBarTitleTextView.setText(R.string.history);

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

        if (!fromSavedInstanceState) {
            if (getFragmentManager().findFragmentByTag("fragment") == null) {
                fragmentTransaction.add(R.id.content_fragment, fragment, "fragment");
            } else {
                fragmentTransaction.replace(R.id.content_fragment, fragment, "fragment");
            }

            fragmentTransaction.commit();
            currentFragment = fragment;
        }

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

        setIsActionBarTransparent(false);
        actionBarRightButton.setVisibility(View.GONE);

        Fragment fragment = OrdersFragment.initialize(OrdersFragment.TYPES.ONGOING);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.content_fragment, fragment, "fragment")
                .commit();
        currentFragment = fragment;
    }

    /**
     * GetUserHandler Methods
     */

    @Override
    public void onGetUserSuccess(User user) {
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

        if (menuGridAdapter != null) {
            menuGridAdapter.notifyDataSetChanged();
        }
    }
}
