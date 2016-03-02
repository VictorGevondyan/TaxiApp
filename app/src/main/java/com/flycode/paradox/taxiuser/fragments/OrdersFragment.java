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
import com.flycode.paradox.taxiuser.database.Database;
import com.flycode.paradox.taxiuser.models.Order;
import com.flycode.paradox.taxiuser.settings.UserData;
import com.flycode.paradox.taxiuser.utils.TypefaceUtils;

import java.util.ArrayList;
import java.util.Date;

public class OrdersFragment extends SuperFragment implements GetOrdersHandler {
    private static final String TYPE = "type";
    private OrdersListAdapter ordersListAdapter;
    private TextView noOrderTextView;
    private OnOngoingOrdersRefreshListener listener;

    public static OrdersFragment initialize(String type, OnOngoingOrdersRefreshListener listener) {
        Bundle arguments = new Bundle();
        arguments.putString(TYPE, type);

        OrdersFragment ordersFragment = new OrdersFragment();
        ordersFragment.setArguments(arguments);

        if (type.equals(TYPES.ONGOING)) {
            ordersFragment.setOnOngoingOrdersRefreshListener(listener);
        }

        return ordersFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View ordersView = inflater.inflate(R.layout.fragment_orders, container, false);

        String type = getArguments().getString(TYPE);

        ordersListAdapter = new OrdersListAdapter(
                getActivity(),
                R.layout.item_order,
                new ArrayList<Order>());

        ListView ordersListView = (ListView) ordersView.findViewById(R.id.orders_list_view);
        ordersListView.setAdapter(ordersListAdapter);
        ordersListView.setOnItemClickListener(onOrderListClickListener);

        noOrderTextView = (TextView) ordersView.findViewById(R.id.no_order);
        noOrderTextView.setText(type.equals(TYPES.HISTORY) ? R.string.no_history : R.string.no_ongoing_orders);
        noOrderTextView.setTypeface(TypefaceUtils.getTypeface(getActivity(), TypefaceUtils.AVAILABLE_FONTS.ROBOTO_THIN));

        return ordersView;
    }

    @Override
    public void onResume() {
        super.onResume();

        refresh();
    }

    @Override
    public void onOrderUpdated(Order order) {
        super.onOrderUpdated(order);

        String type = getArguments().getString(TYPE);

        ordersListAdapter.setItems(Database
                .sharedDatabase(getActivity())
                .getOrders(
                        -1,
                        -1,
                        type.equals(TYPES.HISTORY) ? TYPES.HISTORY_STATUSES : TYPES.ONGOING_STATUSES,
                        null,
                        UserData.sharedData(getActivity()).getUsername()));
    }

    /**
     * GetOrdersHandler Methods
     */

    public void onGetOrdersSuccess(ArrayList<Order> ordersList, int ordersCount) {
        ordersListAdapter.setItems(ordersList);

        if (ordersList.isEmpty()) {
            noOrderTextView.setVisibility(View.VISIBLE);
        } else {
            noOrderTextView.setVisibility(View.GONE);
        }

        if (getArguments().getString(TYPE).equals(TYPES.ONGOING)) {
            UserData.sharedData(getActivity()).setOrderCount(ordersCount);

            if (listener != null) {
                listener.onOngoingOrdersRefresh();
            }
        }
    }

    @Override
    public void onGetOrdersFailure(int statusCode) {

    }

    public void refresh() {
        String type = getArguments().getString(TYPE);

        ordersListAdapter.setItems(Database
                .sharedDatabase(getActivity())
                .getOrders(
                        -1,
                        -1,
                        type.equals(TYPES.HISTORY) ? TYPES.HISTORY_STATUSES : TYPES.ONGOING_STATUSES,
                        null,
                        UserData.sharedData(getActivity()).getUsername()));

        ArrayList<Order> orders = Database.sharedDatabase(getActivity()).getOrders(
                0,
                1,
                new String[0],
                null,
                UserData.sharedData(getActivity()).getUsername());

        Date fromDate = null;

        if (orders.size() > 0) {
            fromDate = orders.get(0).getUpdatedTime();
        }

        APITalker.sharedTalker().getOwnOrders(
                getActivity(),
                type.equals(TYPES.HISTORY) ? TYPES.HISTORY_STATUSES : TYPES.ONGOING_STATUSES,
                fromDate,
                this);
    }

    public void setOnOngoingOrdersRefreshListener(OnOngoingOrdersRefreshListener listener) {
        this.listener = listener;
    }

    private AdapterView.OnItemClickListener onOrderListClickListener = new AdapterView.OnItemClickListener() {
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

    public interface OnOngoingOrdersRefreshListener {
        void onOngoingOrdersRefresh();
    }

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
                OrderStatusConstants.WAITING,
                OrderStatusConstants.STARTED
        };
    }
}
