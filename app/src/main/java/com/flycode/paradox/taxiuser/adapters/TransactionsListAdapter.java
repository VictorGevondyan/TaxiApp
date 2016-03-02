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
import com.flycode.paradox.taxiuser.models.Transaction;
import com.flycode.paradox.taxiuser.utils.TypefaceUtils;

import java.util.ArrayList;

/**
 * Created by victor on 12/12/15.
 */
public class TransactionsListAdapter extends ArrayAdapter<Transaction> {
    private Context context;
    private ArrayList<Transaction> transactionsList;
    private Typeface robotoTypeface;

    public TransactionsListAdapter(Context context, int resource, ArrayList<Transaction> transactionsList) {
        super(context, resource, transactionsList);
        this.context = context;
        this.transactionsList = transactionsList;
        this.robotoTypeface = TypefaceUtils.getTypeface(context, TypefaceUtils.AVAILABLE_FONTS.ROBOTO_THIN);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Transaction transaction = transactionsList.get(position);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.item_transaction, parent, false);

            Typeface icomoonTypeface = TypefaceUtils.getTypeface(context, TypefaceUtils.AVAILABLE_FONTS.ICOMOON);
            TextView timeIconTextView = (TextView) convertView.findViewById(R.id.time_icon);
            TextView costIconTextView = (TextView) convertView.findViewById(R.id.cost_icon);
            TextView userIconTextView = (TextView) convertView.findViewById(R.id.user_icon);
            TextView commentIconTextView = (TextView) convertView.findViewById(R.id.comment_icon);
            timeIconTextView.setTypeface(icomoonTypeface);
            costIconTextView.setTypeface(icomoonTypeface);
            userIconTextView.setTypeface(icomoonTypeface);
            commentIconTextView.setTypeface(icomoonTypeface);

            TextView toTextView = (TextView) convertView.findViewById(R.id.to);
            toTextView.setTypeface(robotoTypeface);
        }

        TextView recipientUsername = (TextView) convertView.findViewById(R.id.recipient_username);
        TextView transactionDate  = (TextView) convertView.findViewById(R.id.transaction_date);
        TextView transactionDescription = (TextView) convertView.findViewById(R.id.transaction_description);
        TextView paymentType = (TextView) convertView.findViewById(R.id.payment_type);
        TextView moneyAmount = (TextView) convertView.findViewById(R.id.money_amount);

        recipientUsername.setTypeface(robotoTypeface);
        transactionDate.setTypeface(robotoTypeface);
        transactionDescription.setTypeface(robotoTypeface);
        paymentType.setTypeface(robotoTypeface);
        moneyAmount.setTypeface(robotoTypeface);

        if (transaction.getRecipientUsername().isEmpty()) {
            convertView.findViewById(R.id.recipient_username_section).setVisibility(View.GONE);
        } else {
            convertView.findViewById(R.id.recipient_username_section).setVisibility(View.VISIBLE);

            if (transaction.getRecipientRole().equals("user")) {
                recipientUsername.setText(R.string.me);
            } else if (transaction.getRecipientRole().equals("driver")) {
                recipientUsername.setText(context.getString(R.string.driver) + " (" + transaction.getRecipientUsername() + ")");
            }
        }

        transactionDate.setText(DateUtils.formatDateTime(
                context,
                transaction.getDate().getTime(),
                DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
                        | DateUtils.FORMAT_ABBREV_MONTH | DateUtils.FORMAT_SHOW_YEAR
                        | DateUtils.FORMAT_24HOUR));

        String description = transaction.getDescription().replaceFirst("[0-9a-z]{24}", "").trim();
        transactionDescription.setText(description);

        if (description.isEmpty()) {
            convertView.findViewById(R.id.transactions_description_section).setVisibility(View.GONE);
        } else {
            convertView.findViewById(R.id.transactions_description_section).setVisibility(View.VISIBLE);
        }

        paymentType.setText(transaction.getPaymentType());
        moneyAmount.setText(String.format("%.0f", transaction.getMoneyAmount()));

        return convertView;
    }

    @Override
    public int getCount() {
        return transactionsList.size();
    }

    @Override
    public void clear() {
        transactionsList.clear();
        notifyDataSetChanged();
    }

    @Override
    public Transaction getItem(int position) {
        return transactionsList.get(position);
    }

    public void addItems(ArrayList<Transaction> transactionsList) {
        this.transactionsList.addAll(transactionsList);
        notifyDataSetChanged();
    }
}
