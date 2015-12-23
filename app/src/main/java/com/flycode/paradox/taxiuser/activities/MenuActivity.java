package com.flycode.paradox.taxiuser.activities;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.flycode.paradox.taxiuser.R;
import com.flycode.paradox.taxiuser.adapters.MenuGridAdapter;
import com.flycode.paradox.taxiuser.layouts.SideMenuLayout;
import com.flycode.paradox.taxiuser.utils.TypefaceUtils;

/**
 * Created by victor on 12/14/15.
 */
public class MenuActivity  extends Activity {
    private SideMenuLayout sideMenu;
    private MenuGridAdapter menuGridAdapter;

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
}