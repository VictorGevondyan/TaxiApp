package com.flycode.paradox.taxiuser.fragments;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.flycode.paradox.taxiuser.R;
import com.flycode.paradox.taxiuser.activities.OrderActivity;
import com.flycode.paradox.taxiuser.adapters.OrdersListAdapter;
import com.flycode.paradox.taxiuser.api.APITalker;
import com.flycode.paradox.taxiuser.api.GetOrdersHandler;
import com.flycode.paradox.taxiuser.constants.OrderStatusConstants;
import com.flycode.paradox.taxiuser.dialogs.MessageDialog;
import com.flycode.paradox.taxiuser.models.Order;
import com.flycode.paradox.taxiuser.settings.UserData;
import com.flycode.paradox.taxiuser.utils.TypefaceUtils;

import java.util.ArrayList;

public class OrdersFragment extends SuperFragment implements GetOrdersHandler {
    public static class TYPES {
        public static final String ONGOING = "ongoing";
        public static final String HISTORY = "history";

        public static final String[] HISTORY_STATUSES = {
                OrderStatusConstants.FINISHED,
                OrderStatusConstants.CANCELED
        };

        public static final String[] ONGOING_STATUSES = {
                OrderStatusConstants.TAKEN,
                OrderStatusConstants.NOT_TAKEN,
                OrderStatusConstants.STARTED
        };
    }

    private static final String TYPE = "type";

    private OrdersListAdapter ordersListAdapter;
    private TextView noOrderTextView;
    private OnOngoingOrdersRefreshListener listener;

    public static OrdersFragment initialize(String type, OnOngoingOrdersRefreshListener listener) {
        Bundle arguments = new Bundle();
        arguments.putString(TYPE, type);

        OrdersFragment ordersFragment = new OrdersFragment();
        ordersFragment.setArguments(arguments);

        if(type.equals(TYPES.ONGOING)) {
            ordersFragment.setOnOngoingOrdersRefreshListener(listener);
        }
        return ordersFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View ordersView = inflater.inflate(R.layout.fragment_orders, container, false);

        ListView ordersListView = (ListView) ordersView.findViewById(R.id.orders_list_view);
        ordersListAdapter = new OrdersListAdapter(getActivity(), R.layout.item_order, new ArrayList<Order>());
        ordersListView.setAdapter(ordersListAdapter);
        ordersListView.setOnItemClickListener(onOrderListClickListener);

        String type = getArguments().getString(TYPE);

        noOrderTextView = (TextView) ordersView.findViewById(R.id.no_order);
        noOrderTextView.setText(type.equals(TYPES.HISTORY) ? R.string.no_ongoing_orders : R.string.no_history);
        noOrderTextView.setTypeface(TypefaceUtils.getTypeface(getActivity(), TypefaceUtils.AVAILABLE_FONTS.ROBOTO_THIN));

        return  ordersView;
    }

    @Override
    public void onResume() {
        super.onResume();

        String type = getArguments().getString(TYPE);

        APITalker.sharedTalker().getOwnOrders(
                getActivity(),
                type.equals(TYPES.HISTORY) ? TYPES.HISTORY_STATUSES : TYPES.ONGOING_STATUSES, false,
                this);
    }

    AdapterView.OnItemClickListener onOrderListClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Order order = ordersListAdapter.getItem(position);

            Intent orderActivityIntent = new Intent(getActivity(), OrderActivity.class);
            orderActivityIntent.putExtra(OrderActivity.ORDER, order);

            if (android.os.Build.VERSION.SDK_INT >= 16) {
                startActivity(
                        orderActivityIntent,
                        ActivityOptions.makeCustomAnimation(getActivity(), R.anim.slide_left_in, R.anim.hold).toBundle());
            } else {
                startActivity(orderActivityIntent);
            }
        }
    };

    /**
     * GetOrdersHandler Methods
     */

    public void onGetOrdersSuccess( ArrayList<Order> ordersList, int ordersCount ) {
        ordersListAdapter.setItems(ordersList);

        UserData.sharedData(getActivity()).setOrderCount(ordersCount);

        if (ordersList.isEmpty()) {
            noOrderTextView.setVisibility(View.VISIBLE);
        } else {
            noOrderTextView.setVisibility(View.GONE);
        }

        if( listener !=null && getArguments().getString(TYPE).equals(TYPES.ONGOING) ) {
            listener.onOngoingOrdersRefresh();
        }
    }

    @Override
    public void onGetOrdersFailure() {
        MessageDialog.initialize("Error", "GET ORDERS ERROR").show(getFragmentManager(), MessageDialog.ERROR_DIALOG_TAG);
    }

    public void refresh(){

        ordersListAdapter.clear();

        String type = getArguments().getString(TYPE);

        APITalker.sharedTalker().getOwnOrders(
                getActivity(),
                type.equals(TYPES.HISTORY) ? TYPES.HISTORY_STATUSES : TYPES.ONGOING_STATUSES, false,
                this);
    }

    public interface OnOngoingOrdersRefreshListener{
        void onOngoingOrdersRefresh();
    }

    public void setOnOngoingOrdersRefreshListener( OnOngoingOrdersRefreshListener listener ){
        this.listener = listener;
    }
}
