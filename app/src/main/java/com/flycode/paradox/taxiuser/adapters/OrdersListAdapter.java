package com.flycode.paradox.taxiuser.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.flycode.paradox.taxiuser.R;
import com.flycode.paradox.taxiuser.models.Order;
import com.flycode.paradox.taxiuser.utils.TypefaceUtils;

import java.util.ArrayList;

/**
 * Created by victor on 12/11/15.
 */
public class OrdersListAdapter extends ArrayAdapter<Order> {

    private Context context;
    private ArrayList<Order> ordersList;
    private Typeface robotoTypeface;


    public OrdersListAdapter(Context context, int resource, ArrayList<Order> ordersList) {
        super(context, resource, ordersList);
        this.context = context;
        this.ordersList = ordersList;
        this.robotoTypeface = TypefaceUtils.getTypeface(context, TypefaceUtils.AVAILABLE_FONTS.ROBOTO_THIN);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Order order = getItem(position);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.item_order, parent, false);

            Typeface icomoonTypeface = TypefaceUtils.getTypeface(context, TypefaceUtils.AVAILABLE_FONTS.ICOMOON);
            TextView iconArrow = (TextView) convertView.findViewById(R.id.icon_arrow);
            TextView dateIconTextView = (TextView) convertView.findViewById(R.id.icon_date);
            TextView locationIconTextView = (TextView) convertView.findViewById(R.id.icon_location);
            TextView statusIconTextView = (TextView) convertView.findViewById(R.id.icon_status);

            dateIconTextView.setTypeface(icomoonTypeface);
            locationIconTextView.setTypeface(icomoonTypeface);
            statusIconTextView.setTypeface(icomoonTypeface);
            iconArrow.setTypeface(icomoonTypeface);
        }

        TextView dateTextView = (TextView) convertView.findViewById(R.id.date);
        TextView locationTextView = (TextView) convertView.findViewById(R.id.location);
        TextView statusTextView = (TextView) convertView.findViewById(R.id.status);

        TextView dateValueTextView = (TextView) convertView.findViewById(R.id.date_value);
        TextView locationValueTextView = (TextView) convertView.findViewById(R.id.location_value);
        TextView statusValueTextView = (TextView) convertView.findViewById(R.id.status_value);

        dateTextView.setTypeface(robotoTypeface);
        locationTextView.setTypeface(robotoTypeface);
        statusTextView.setTypeface(robotoTypeface);

        dateValueTextView.setTypeface(robotoTypeface);
        locationValueTextView.setTypeface(robotoTypeface);
        statusValueTextView.setTypeface(robotoTypeface);

        dateValueTextView.setText(DateUtils.formatDateTime(
                context,
                order.getOrderTime().getTime(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME
                        | DateUtils.FORMAT_ABBREV_MONTH | DateUtils.FORMAT_SHOW_YEAR
                        | DateUtils.FORMAT_24HOUR));
        locationValueTextView.setText(order.getStartingPointName());
        statusValueTextView.setText(order.getStatus());

        return convertView;
    }

    @Override
    public int getCount() {
        return ordersList.size();
    }

    @Override
    public Order getItem(int position) {
        return ordersList.get(position);
    }

    public void setItem(Order order) {
        ordersList.add(0, order);
        notifyDataSetChanged();
    }

    public void setItems(ArrayList<Order> ordersList) {
        this.ordersList = ordersList;
        notifyDataSetChanged();
    }
}
