package com.flycode.paradox.taxiuser.fragments;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.flycode.paradox.taxiuser.R;
import com.flycode.paradox.taxiuser.activities.OrderActivity;
import com.flycode.paradox.taxiuser.adapters.TransactionsListAdapter;
import com.flycode.paradox.taxiuser.api.APITalker;
import com.flycode.paradox.taxiuser.api.GetOrderHandler;
import com.flycode.paradox.taxiuser.api.GetOwnTransactionsHandler;
import com.flycode.paradox.taxiuser.database.Database;
import com.flycode.paradox.taxiuser.dialogs.LoadingDialog;
import com.flycode.paradox.taxiuser.models.Order;
import com.flycode.paradox.taxiuser.models.Transaction;
import com.flycode.paradox.taxiuser.settings.UserData;
import com.flycode.paradox.taxiuser.utils.MessageHandlerUtil;
import com.flycode.paradox.taxiuser.utils.TypefaceUtils;

import java.util.ArrayList;
import java.util.Date;

public class TransactionsFragment extends SuperFragment implements GetOwnTransactionsHandler, AdapterView.OnItemClickListener, GetOrderHandler {
    private ListView transactionsListView;
    private TransactionsListAdapter transactionsListAdapter;
    private TextView noTransactionTextView;
    private LoadingDialog loadingDialog;

    public static TransactionsFragment initialize(  ) {
        return new TransactionsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View transactionsView = inflater.inflate(R.layout.fragment_transactions, container, false);

        transactionsListView = (ListView)transactionsView.findViewById(R.id.transactions_list_view);
        transactionsListAdapter = new TransactionsListAdapter(
                getActivity(),
                R.layout.item_transaction,
                new ArrayList<Transaction>());
        transactionsListView.setAdapter(transactionsListAdapter);
        transactionsListView.setOnItemClickListener(this);

        noTransactionTextView = (TextView) transactionsView.findViewById(R.id.no_transaction);
        noTransactionTextView.setText(R.string.no_transaction);
        noTransactionTextView.setTypeface(TypefaceUtils.getTypeface(getActivity(), TypefaceUtils.AVAILABLE_FONTS.ROBOTO_THIN));

        loadingDialog = new LoadingDialog(getActivity());

        return transactionsView;
    }

    @Override
    public void onResume() {
        super.onResume();

        refresh();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onGetOwnTransactionsSuccess(ArrayList<Transaction> transactionsList) {
        transactionsListAdapter.addItems(transactionsList);

        if (transactionsListAdapter.getCount() == 0) {
            noTransactionTextView.setVisibility(View.VISIBLE);
        } else {
            noTransactionTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onGetOwnTransactionsFailure(int statusCode) {

    }

    public void refresh(){
        transactionsListAdapter.clear();

        ArrayList<Transaction> transactions = Database.sharedDatabase(getActivity()).getTransactions(-1, -1, UserData.sharedData(getActivity()).getUsername());

        Date fromDate = null;

        if (!transactions.isEmpty()) {
            fromDate = transactions.get(0).getDate();
        }

        transactionsListAdapter.addItems(transactions);

        if (transactionsListAdapter.getCount() == 0) {
            noTransactionTextView.setVisibility(View.VISIBLE);
        } else {
            noTransactionTextView.setVisibility(View.GONE);
        }

        APITalker.sharedTalker().getOwnTransactions(getActivity(), this, fromDate);
    }

    /**
     * OnItemClickListener Methods
     */

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Transaction transaction = transactionsListAdapter.getItem(position);

        if (!transaction.getOrderId().isEmpty()) {
            loadingDialog.show();

            APITalker.sharedTalker().getOrder(getActivity(), transaction.getOrderId(), this);
        }
    }

    /**
     * GetOrderHandler Methods
     */

    @Override
    public void onGetOrderSuccess(Order order) {
        Intent orderActivityIntent = new Intent(getActivity(), OrderActivity.class);
        orderActivityIntent.putExtra(OrderActivity.ORDER, order);

        loadingDialog.dismiss();

        if (android.os.Build.VERSION.SDK_INT >= 16) {
            startActivity(
                    orderActivityIntent,
                    ActivityOptions.makeCustomAnimation(getActivity(), R.anim.slide_left_in, R.anim.hold).toBundle());
        } else {
            startActivity(orderActivityIntent);
        }
    }

    @Override
    public void onGetOrderFailure(int statusCode) {
        loadingDialog.dismiss();

        MessageHandlerUtil.showErrorForStatusCode(statusCode, getActivity());
    }
}
