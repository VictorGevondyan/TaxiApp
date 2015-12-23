package com.flycode.paradox.taxiuser.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.flycode.paradox.taxiuser.R;
import com.flycode.paradox.taxiuser.constants.MenuConstants;
import com.flycode.paradox.taxiuser.models.MenuItem;
import com.flycode.paradox.taxiuser.utils.TypefaceUtils;

/**
 * Created by victor on 12/11/15.
 */
public class MenuGridAdapter extends ArrayAdapter<MenuItem> {

    private Context context;

    public MenuGridAdapter(Context context, int resource) {
        super(context, resource);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.item_menu_grid, parent, false);
        }

        if (position < MenuConstants.menuTitles.length - 2) {
            convertView.findViewById(R.id.horizontal_space).setVisibility(View.VISIBLE);
        } else {
            convertView.findViewById(R.id.horizontal_space).setVisibility(View.GONE);
        }

        if (position % 2 == 1) {
            convertView.findViewById(R.id.vertical_space).setVisibility(View.VISIBLE);
        } else {
            convertView.findViewById(R.id.vertical_space).setVisibility(View.GONE);
        }

        Typeface icomoonTypeface = TypefaceUtils.getTypeface(context, TypefaceUtils.AVAILABLE_FONTS.ICOMOON);
        Typeface robotoThinTypeface = TypefaceUtils.getTypeface(context, TypefaceUtils.AVAILABLE_FONTS.ROBOTO_THIN);

        TextView menuIconTextView = (TextView) convertView.findViewById(R.id.icon);
        TextView menuTitleTextView = (TextView) convertView.findViewById(R.id.title);

        menuIconTextView.setTypeface(icomoonTypeface);
        menuTitleTextView.setTypeface(robotoThinTypeface);

        menuIconTextView.setText(MenuConstants.menuIcons[position]);
        menuTitleTextView.setText(MenuConstants.menuTitles[position]);

        return convertView;
    }

    @Override
    public int getCount() {
        return MenuConstants.menuTitles.length;
    }
}