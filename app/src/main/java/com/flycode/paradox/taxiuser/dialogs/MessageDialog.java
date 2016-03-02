package com.flycode.paradox.taxiuser.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flycode.paradox.taxiuser.R;
import com.flycode.paradox.taxiuser.utils.TypefaceUtils;

public class MessageDialog extends DialogFragment implements View.OnClickListener {
    public static final String ERROR_DIALOG_TAG = "errorDialogTag";

    private static final String TITLE = "title";
    private static final String MESSAGE = "message";
    private static final String NEGATIVE = "negative";
    private static final String POSITIVE = "positive";
    private static final String ADDITIONAL = "additional";

    private MessageDialogListener listener;

    public static MessageDialog initialize(String title, String message, String negative, String positive) {
        Bundle arguments = new Bundle();
        arguments.putString(TITLE, title);
        arguments.putString(MESSAGE, message);
        arguments.putString(NEGATIVE, negative);
        arguments.putString(POSITIVE, positive);

        MessageDialog messageDialog = new MessageDialog();
        messageDialog.setArguments(arguments);

        return messageDialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_message, container, false);

        setCancelable(false);

        Button positiveButton = (Button) view.findViewById(R.id.positive);
        positiveButton.setOnClickListener(this);
        Button negativeButton = (Button) view.findViewById(R.id.negative);
        negativeButton.setOnClickListener(this);

        TextView errorTitleTextView = (TextView) view.findViewById(R.id.error_title);
        TextView errorMessageTextView = (TextView) view.findViewById(R.id.error_message);

        Typeface robotoRegularTypeface = TypefaceUtils.getTypeface(getActivity(), TypefaceUtils.AVAILABLE_FONTS.ROBOTO_REGULAR);

        positiveButton.setTypeface(robotoRegularTypeface);
        negativeButton.setTypeface(robotoRegularTypeface);
        errorTitleTextView.setTypeface(robotoRegularTypeface);
        errorMessageTextView.setTypeface(robotoRegularTypeface);

        Bundle arguments = getArguments();

        errorTitleTextView.setText(arguments.getString(TITLE));
        errorMessageTextView.setText(arguments.getString(MESSAGE));

        String positive = arguments.getString(POSITIVE);
        String negative = arguments.getString(NEGATIVE);

        if (positive.isEmpty()) {
            positiveButton.setVisibility(View.GONE);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) negativeButton.getLayoutParams();
            params.weight = 2;
            negativeButton.setLayoutParams(params);
        } else {
            positiveButton.setText(positive);
        }

        if (negative.isEmpty()) {
            negativeButton.setVisibility(View.GONE);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) positiveButton.getLayoutParams();
            params.weight = 2;
            positiveButton.setLayoutParams(params);
        } else {
            negativeButton.setText(negative);
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

        return dialog;
    }

    @Override
    public void onClick(View view) {
        if (listener != null) {
            if (view.getId() == R.id.positive) {
                listener.onPositiveClicked(this);
            } else if (view.getId() == R.id.negative) {
                listener.onNegativeClicked(this);
            }
        }

        dismiss();
    }

    public MessageDialog setAdditionalInfo(Bundle additionalInfo) {
        getArguments().putBundle(ADDITIONAL, additionalInfo);

        return this;
    }

    public Bundle getAdditionalInfo() {
        return getArguments().getBundle(ADDITIONAL);
    }

    public MessageDialog setListener(MessageDialogListener listener) {
        this.listener = listener;

        return this;
    }

    public interface MessageDialogListener {
        void onNegativeClicked(MessageDialog messageDialog);
        void onPositiveClicked(MessageDialog messageDialog);
    }
}
