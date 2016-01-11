package com.flycode.paradox.taxiuser.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.flycode.paradox.taxiuser.R;
import com.flycode.paradox.taxiuser.activities.OrderActivity;
import com.flycode.paradox.taxiuser.adapters.OrdersListAdapter;
import com.flycode.paradox.taxiuser.constants.OrderStatusConstants;
import com.flycode.paradox.taxiuser.models.Order;
import com.flycode.paradox.taxiuser.api.APITalker;
import com.flycode.paradox.taxiuser.api.GetOrdersHandler;

import java.util.ArrayList;

public class OrdersFragment extends Fragment implements GetOrdersHandler {
    public static class TYPES {
        public static final String ONGOING = "ongoing";
        public static final String HISTORY = "history";

        public static final String[] HISTORY_STATUSES = {
                OrderStatusConstants.FINISHED,
                OrderStatusConstants.CANCELED
        };

        public static final String[] ONGOING_STATUSES = {
                OrderStatusConstants.TAKEN,
                OrderStatusConstants.NOT_TAKEN
        };
    }

    private static final String TYPE = "type";

    private OrdersListAdapter ordersListAdapter;

    public static OrdersFragment initialize(String type) {
        Bundle arguments = new Bundle();
        arguments.putString(TYPE, type);

        OrdersFragment ordersFragment = new OrdersFragment();
        ordersFragment.setArguments(arguments);

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

        APITalker.sharedTalker().getOwnOrders(
                getActivity(),
                type.equals(TYPES.HISTORY) ? TYPES.HISTORY_STATUSES : TYPES.ONGOING_STATUSES,
                this);

        return  ordersView;
    }

    AdapterView.OnItemClickListener onOrderListClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Order order = ordersListAdapter.getItem(position);

            Intent orderActivityIntent = new Intent(getActivity(), OrderActivity.class);
            orderActivityIntent.putExtra(OrderActivity.ORDER, order);
            startActivity(orderActivityIntent);
        }
    };

    /**
     * GetOrdersHandler Methods
     */

    public void onGetOrdersSuccess( ArrayList<Order> ordersList ) {
        ordersListAdapter.setItems(ordersList);
    }

    @Override
    public void onGetOrdersFailure() {

    }
}
