package com.flycode.paradox.taxiuser.activities;

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

import com.flycode.paradox.taxiuser.R;
import com.flycode.paradox.taxiuser.adapters.MenuGridAdapter;
import com.flycode.paradox.taxiuser.api.APITalker;
import com.flycode.paradox.taxiuser.api.GetOrdersHandler;
import com.flycode.paradox.taxiuser.api.GetUserHandler;
import com.flycode.paradox.taxiuser.api.MakeOrderListener;
import com.flycode.paradox.taxiuser.constants.OrderStatusConstants;
import com.flycode.paradox.taxiuser.database.Database;
import com.flycode.paradox.taxiuser.dialogs.LoadingDialog;
import com.flycode.paradox.taxiuser.fragments.AboutUsFragment;
import com.flycode.paradox.taxiuser.fragments.HelpFragment;
import com.flycode.paradox.taxiuser.fragments.MakeOrderFragment;
import com.flycode.paradox.taxiuser.fragments.OrdersFragment;
import com.flycode.paradox.taxiuser.fragments.ServicesFragment;
import com.flycode.paradox.taxiuser.fragments.SettingsFragment;
import com.flycode.paradox.taxiuser.fragments.SuperFragment;
import com.flycode.paradox.taxiuser.fragments.TransactionsFragment;
import com.flycode.paradox.taxiuser.gcm.GCMSubscriber;
import com.flycode.paradox.taxiuser.layouts.SideMenuLayout;
import com.flycode.paradox.taxiuser.models.CarCategory;
import com.flycode.paradox.taxiuser.models.Order;
import com.flycode.paradox.taxiuser.models.User;
import com.flycode.paradox.taxiuser.settings.AppSettings;
import com.flycode.paradox.taxiuser.settings.UserData;
import com.flycode.paradox.taxiuser.utils.LocaleUtils;
import com.flycode.paradox.taxiuser.utils.MessageHandlerUtil;
import com.flycode.paradox.taxiuser.utils.TypefaceUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class MenuActivity extends SuperActivity implements MakeOrderFragment.OrderFragmentListener, GetUserHandler, GetOrdersHandler,
        OrdersFragment.OnOngoingOrdersRefreshListener {
    private final String SAVED_CURRENT_POSITION = "savedCurrentPosition";
    private final String SAVED_CURRENT_FRAGMENT = "savedCurrentFragment";

    private final int INDEX_ONGOING = 0;
    private final int INDEX_BALANCE = 1;
    private final int INDEX_ORDER = 2;
    private final int INDEX_HISTORY = 3;
    private final int INDEX_FAVORITES = 4;
    private final int INDEX_SETTINGS = 5;
    private final int INDEX_ABOUT_US = 6;
    private final int INDEX_SERVIVES = 7;
    private final int INDEX_HELP = 8;
    private final int INDEX_LOGOUT = 9;

    private SideMenuLayout sideMenu;

    private TextView actionBarTitleTextView;
    private Button actionBarRightButton;
    private View contentView;
    private View actionBarView;
    private View actionBarOverlayView;
    private MenuGridAdapter menuGridAdapter;
    private LoadingDialog loadingDialog;

    private int currentPosition = -1;

    SuperFragment currentFragment;

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

        if (savedInstanceState != null) {
            currentFragment = (SuperFragment) getFragmentManager().getFragment(savedInstanceState, SAVED_CURRENT_FRAGMENT);
            newPosition = savedInstanceState.getInt(SAVED_CURRENT_POSITION);
        }

        currentPosition = -1;
        changeFragment(newPosition, false, savedInstanceState != null);

        if (currentFragment instanceof MakeOrderFragment) {
            MakeOrderFragment makeOrderFragment = (MakeOrderFragment) currentFragment;
            makeOrderFragment.setListener(this);
        }

        if (currentFragment instanceof OrdersFragment) {
            OrdersFragment ordersFragment = (OrdersFragment) currentFragment;
            ordersFragment.setOnOngoingOrdersRefreshListener(this);
        }

        try {
            GCMSubscriber.registerForGcm(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        loadingDialog = new LoadingDialog(this);

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

        if (getIntent().getBooleanExtra(OrderActivity.RETRY, false)) {
            Order order = getIntent().getParcelableExtra(OrderActivity.ORDER);

            CarCategory[] carCategories = Database.sharedDatabase(this).getCarCategories();
            String carCategoryId = "";

            for (CarCategory carCategory : carCategories) {
                if (carCategory.getName().equals(order.getCarCategory())) {
                    carCategoryId = carCategory.getId();
                }
            }

            loadingDialog.show();
            APITalker.sharedTalker().makeOrder(
                    this,
                    order.getStartingPointName(),
                    order.getStartingPointGeo().getLatitude(),
                    order.getStartingPointGeo().getLongitude(),
                    order.getOrderTime(),
                    carCategoryId,
                    order.getDescription(),
                    order.getCashOnly(),
                    new MakeOrderListener() {
                        @Override
                        public void onMakeOrderSuccess(Order order) {
                            loadingDialog.dismiss();

                            onOrderMade(order);
                        }

                        @Override
                        public void onMakeOrderFail(int statusCode) {
                            loadingDialog.dismiss();

                            MessageHandlerUtil.showErrorForStatusCode(statusCode, MenuActivity.this);
                        }
                    }
            );
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        APITalker.sharedTalker().getUser(this, this);

        ArrayList<Order> orders = Database.sharedDatabase(this).getOrders(
                0,
                1,
                new String[0],
                null,
                UserData.sharedData(this).getUsername());

        Date fromDate = null;

        if (orders.size() > 0) {
            fromDate = orders.get(0).getUpdatedTime();
        }

        String[] statusesArray = { OrderStatusConstants.NOT_TAKEN, OrderStatusConstants.STARTED, OrderStatusConstants.TAKEN };

        APITalker.sharedTalker().getOwnOrders(this, statusesArray, fromDate, this);
    }

    @Override
    public void onBackPressed() {
        if (currentFragment.onBackButtonPressed()) {
            return;
        }

        sideMenu.toggleMenu();
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

        getFragmentManager().putFragment(outState, SAVED_CURRENT_FRAGMENT, currentFragment);
    }

    @Override
    protected void onNetworkStateChanged(boolean isConnected) {
        super.onNetworkStateChanged(isConnected);

        currentFragment.onConnectionChanged(isConnected);
    }

    @Override
    protected void onGPSPermissionChanged(boolean isGPSPermitted) {
        super.onGPSPermissionChanged(isGPSPermitted);

        currentFragment.onGPSPermissionChanged(isGPSPermitted);
    }

    @Override
    protected void onLocationChange(double latitude, double longitude) {
        super.onLocationChange(latitude, longitude);
    }

    @Override
    protected void onOrderUpdated(Order order) {
        super.onOrderUpdated(order);

        currentFragment.onOrderUpdated(order);
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
        actionBarOverlayView.setVisibility(View.VISIBLE);
        setIsActionBarTransparent(false);

        currentPosition = position;

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        SuperFragment fragment;

        if (position == INDEX_BALANCE) {
            actionBarRightButton.setVisibility(View.VISIBLE);
            actionBarRightButton.setText(R.string.icon_refresh);
            actionBarTitleTextView.setText(R.string.transactions);

            fragment = TransactionsFragment.initialize();
        } else if (position == INDEX_ORDER) {
            setIsActionBarTransparent(true);

            actionBarOverlayView.setVisibility(View.GONE);
            actionBarTitleTextView.setText(R.string.order);

            fragment = MakeOrderFragment.initialize(this);
        } else if (position == INDEX_ONGOING) {
            actionBarRightButton.setVisibility(View.VISIBLE);
            actionBarRightButton.setText(R.string.icon_refresh);
            actionBarTitleTextView.setText(R.string.ongoing);

            fragment = OrdersFragment.initialize(OrdersFragment.TYPES.ONGOING, this);
        } else if (position == INDEX_HISTORY) {
            actionBarRightButton.setVisibility(View.VISIBLE);
            actionBarRightButton.setText(R.string.icon_refresh);
            actionBarTitleTextView.setText(R.string.history);

            fragment = OrdersFragment.initialize(OrdersFragment.TYPES.HISTORY, this);
        } else if (position == INDEX_SETTINGS) {
            actionBarTitleTextView.setText(R.string.settings);

            fragment = new SettingsFragment();
        } else if (position == INDEX_ABOUT_US) {
            actionBarTitleTextView.setText(R.string.about_us);

            fragment = new AboutUsFragment();
        } else if (position == INDEX_SERVIVES) {
            actionBarTitleTextView.setText(R.string.services);

            fragment = new ServicesFragment();
        } else if (position == INDEX_HELP) {
            actionBarTitleTextView.setText(R.string.help);

            fragment = new HelpFragment();
        } else if (position == INDEX_LOGOUT) {
            AppSettings.sharedSettings(this).setIsUserLoggedIn(false);
            AppSettings.sharedSettings(this).setToken(null);
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        } else if (position == INDEX_FAVORITES) {
            MessageHandlerUtil.showMessage(R.string.favorites, R.string.coming_soon, this);

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
     * MakeOrderFragment.OrderFragmentListener Methods
     */

    @Override
    public void onOrderMade(Order order) {
        currentPosition = INDEX_ONGOING;

        setIsActionBarTransparent(false);

        actionBarRightButton.setText(R.string.icon_refresh);
        actionBarRightButton.setVisibility(View.VISIBLE);
        actionBarOverlayView.setVisibility(View.VISIBLE);
        actionBarTitleTextView.setText(R.string.ongoing);

        SuperFragment fragment = OrdersFragment.initialize(OrdersFragment.TYPES.ONGOING, this);
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
    public void onGetUserFailure(int statusCode) {

    }

    private void setUserData(User user){
        UserData userData = UserData.sharedData(this);

        userData.setId(user.getId());
        userData.setUsername(user.getUsername());
        userData.setName(user.getName());
        userData.setBalance(user.getBalance());

        if (menuGridAdapter != null) {
            menuGridAdapter.notifyDataSetChanged();
        }
    }

    /**
     * GetOrdersHandler Methods
     */

    @Override
    public void onGetOrdersSuccess(ArrayList<Order> ordersList, int ordersCount) {
        UserData.sharedData(this).setOrderCount(ordersCount);

        if (menuGridAdapter != null) {
            menuGridAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onGetOrdersFailure(int statusCode) {

    }

    @Override
    public void onOngoingOrdersRefresh() {
        if (menuGridAdapter != null) {
            menuGridAdapter.notifyDataSetChanged();
        }
    }
}
