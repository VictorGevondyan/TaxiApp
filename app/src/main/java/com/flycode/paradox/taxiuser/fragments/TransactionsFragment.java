package com.flycode.paradox.taxiuser.fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.flycode.paradox.taxiuser.R;
import com.flycode.paradox.taxiuser.adapters.TransactionsListAdapter;
import com.flycode.paradox.taxiuser.api.APITalker;
import com.flycode.paradox.taxiuser.api.GetOwnTransactionsHandler;
import com.flycode.paradox.taxiuser.models.Transaction;

import java.util.ArrayList;

public class TransactionsFragment extends Fragment implements GetOwnTransactionsHandler{
    private ListView transactionsListView;
    private TransactionsListAdapter transactionsListAdapter;
    private ProgressDialog progressDialog;

    private static final String TYPE = "type";

    public static TransactionsFragment initialize(  ) {
        TransactionsFragment transactionsFragment = new TransactionsFragment();
        return transactionsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View transactionsView = inflater.inflate(R.layout.fragment_transactions, container, false);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();

        APITalker.sharedTalker().getOwnTransactions(getActivity(), this);

        transactionsListView = (ListView)transactionsView.findViewById(R.id.transactions_list_view);
        transactionsListAdapter = new TransactionsListAdapter(getActivity(), R.layout.item_transaction, new ArrayList<Transaction>());
        transactionsListView.setAdapter(transactionsListAdapter);

        return transactionsView;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onGetOwnTransactionsSuccess(ArrayList<Transaction> transactionsList) {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        transactionsListAdapter.setItems(transactionsList);
    }

    @Override
    public void onGetOwnTransactionsFailure() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }


}
