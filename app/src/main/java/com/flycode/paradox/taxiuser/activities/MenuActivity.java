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

import com.flycode.paradox.taxiuser.R;
import com.flycode.paradox.taxiuser.fragments.SettingsFragment;
import com.flycode.paradox.taxiuser.adapters.MenuGridAdapter;
import com.flycode.paradox.taxiuser.layouts.SideMenuLayout;
import com.flycode.paradox.taxiuser.menu_resources.MenuResources;
import com.flycode.paradox.taxiuser.models.MenuItem;
import com.flycode.paradox.taxiuser.settings.AppSettings;
import com.flycode.paradox.taxiuser.utils.TypefaceUtils;

import java.util.ArrayList;

/**
 * Created by victor on 12/14/15.
 */
public class MenuActivity  extends Activity {

    private final int INDEX_SETTINGS = 6;

    private SideMenuLayout sideMenu;
    private MenuGridAdapter menuGridAdapter;
    ArrayList<MenuItem> menuItemsList;
    private int currentPosition = INDEX_SETTINGS;

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

        menuItemsList = new ArrayList<>();
        initializeMenuItems();
        menuGridAdapter = new MenuGridAdapter(this, R.layout.item_menu_grid, menuItemsList);

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


        }
    };
    
    private String getStringResource( int resource){
        return getResources().getString(resource);
    }

    public void initializeMenuItems(){
        int i;
        MenuItem menuItem;
        for( i = 0; i< MenuResources.menuIcons.length; i++){
            menuItem = new MenuItem(MenuResources.menuIcons[i], MenuResources.menuTitles[i]);
            menuItemsList.add(menuItem);
        }
    }

    public void onLogout( View view ){
        AppSettings.sharedSettings(this).setIsUserLoggedIn(false);

        Intent loginIntent = new Intent(MenuActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    private void changeFragment( int position ){
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        Fragment fragment = null;

        if( position  == 6 ){
            SettingsFragment settingsFragment = new SettingsFragment();
            fragment = settingsFragment;
        }

        fragmentTransaction.replace(R.id.content_fragment, fragment, "fragment");
        fragmentTransaction.commit();
    }

}
