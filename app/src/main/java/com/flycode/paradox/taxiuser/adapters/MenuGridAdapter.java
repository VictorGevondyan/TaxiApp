package com.flycode.paradox.taxiuser.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.flycode.paradox.taxiuser.R;
import com.flycode.paradox.taxiuser.models.MenuItem;
import com.flycode.paradox.taxiuser.utils.TypefaceUtils;

import java.util.ArrayList;

/**
 * Created by victor on 12/11/15.
 */
public class MenuGridAdapter extends ArrayAdapter<MenuItem> {

    private Context context;
    private ArrayList<MenuItem> menuItemsList;


    public MenuGridAdapter(Context context, int resource, ArrayList<MenuItem> menuItemsList) {
        super(context, resource, menuItemsList);
        this.context = context;
        this.menuItemsList = menuItemsList;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MenuItem menuItem = getItem(position);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.item_menu_grid, parent, false);
        }

        Typeface icomoonTypeface = TypefaceUtils.getTypeface(context, TypefaceUtils.AVAILABLE_FONTS.ICOMOON);
        Typeface robotoThinTypeface = TypefaceUtils.getTypeface(context, TypefaceUtils.AVAILABLE_FONTS.ROBOTO_THIN);

        TextView menuIconTextView = (TextView) convertView.findViewById(R.id.icon);
        TextView menuTitleTextView = (TextView) convertView.findViewById(R.id.title);

        menuIconTextView.setTypeface(icomoonTypeface);
        menuTitleTextView.setTypeface(robotoThinTypeface);

        menuIconTextView.setText(menuItem.getIcon());
        menuTitleTextView.setText(menuItem.getTitle());

        return convertView;
    }

    @Override
    public int getCount() {
        return menuItemsList.size();
    }

    @Override
    public MenuItem getItem(int position) {
        return menuItemsList.get(position);
    }

    public void setItem( MenuItem menuItem) {
        menuItemsList.add(0, menuItem);
        notifyDataSetChanged();
    }

    public void setItems(ArrayList<MenuItem> menuItemsList) {
        this.menuItemsList = menuItemsList;
        notifyDataSetChanged();
    }


}
