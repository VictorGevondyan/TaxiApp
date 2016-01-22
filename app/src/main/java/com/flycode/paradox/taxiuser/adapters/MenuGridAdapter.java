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
import com.flycode.paradox.taxiuser.settings.UserData;
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

        if( ( MenuConstants.menuTitles[position] ) == R.string.balance ){
            TextView balanceAmount = ( TextView ) convertView.findViewById(R.id.balance_amount);
            balanceAmount.setVisibility(View.VISIBLE);
            balanceAmount.setText(String.valueOf(UserData.sharedData(context).getBalance()));
            balanceAmount.setTypeface(robotoThinTypeface);
        } else if( ( MenuConstants.menuTitles[position] ) == R.string.ongoing ){
            TextView balanceAmount = ( TextView ) convertView.findViewById(R.id.balance_amount);
            balanceAmount.setVisibility(View.VISIBLE);
            balanceAmount.setText(String.valueOf(UserData.sharedData(context).getOrderCount()));
            balanceAmount.setTypeface(robotoThinTypeface);
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return MenuConstants.menuTitles.length;
    }

    public int dpToPixel( int dpValue ){
        final float scale = context.getResources().getDisplayMetrics().density;
        int pixelValue = (int) (dpValue * scale + 0.5f);
        return pixelValue;
    }
}
