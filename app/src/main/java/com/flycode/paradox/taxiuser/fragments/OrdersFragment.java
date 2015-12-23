package com.flycode.paradox.taxiuser.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.flycode.paradox.taxiuser.R;
import com.flycode.paradox.taxiuser.adapters.OrdersListAdapter;
import com.flycode.paradox.taxiuser.constants.OrderStatusConstants;
import com.flycode.paradox.taxiuser.models.Order;
import com.flycode.paradox.taxiuser.talkers.APITalker;
import com.flycode.paradox.taxiuser.talkers.GetOrdersHandler;

import java.util.ArrayList;

public class OrdersFragment extends Fragment implements GetOrdersHandler {
    private OrdersListAdapter ordersListAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View ordersView = inflater.inflate(R.layout.fragment_orders, container, false);

        ListView ordersListView = (ListView) ordersView.findViewById(R.id.orders_list_view);
        ordersListAdapter = new OrdersListAdapter(getActivity(), R.layout.item_order, new ArrayList<Order>());
        ordersListView.setAdapter(ordersListAdapter);
        ordersListView.setOnItemClickListener(onOrderListClickListener);

        APITalker.sharedTalker().getOwnOrders(getActivity(), OrderStatusConstants.FINISHED, this);

        return  ordersView;
    }

    AdapterView.OnItemClickListener onOrderListClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    };

    public void onGetOrdersSuccess( ArrayList<Order> ordersList ) {
        ordersListAdapter.setItems(ordersList);
    }

    @Override
    public void onGetOrdersFailure() {

    }

}
