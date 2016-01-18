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
import android.widget.TextView;

import com.flycode.paradox.taxiuser.R;
import com.flycode.paradox.taxiuser.utils.TypefaceUtils;

public class ErrorDialog extends DialogFragment implements View.OnClickListener {

    public static final String ERROR_DIALOG_TAG = "errorDialogTag";

    private static final String TITLE = "title";
    private static final String MESSAGE = "message";

    public static ErrorDialog initialize( String title, String message ) {
        Bundle arguments = new Bundle();
        arguments.putString(TITLE, title);
        arguments.putString(MESSAGE, message);

        ErrorDialog errorDialog = new ErrorDialog();
        errorDialog.setArguments(arguments);

        return errorDialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_error, container, false);

        setCancelable(false);

        Button okButton = (Button) view.findViewById(R.id.ok);
        okButton.setOnClickListener(this);
        TextView errorTitleTextView = (TextView) view.findViewById(R.id.error_title);
        TextView errorMessageTextView = (TextView) view.findViewById(R.id.error_message);

        Typeface robotoRegularTypeface = TypefaceUtils.getTypeface(getActivity(), TypefaceUtils.AVAILABLE_FONTS.ROBOTO_REGULAR);

        okButton.setTypeface(robotoRegularTypeface);
        errorTitleTextView.setTypeface(robotoRegularTypeface);
        errorMessageTextView.setTypeface(robotoRegularTypeface);

        Bundle arguments = getArguments();

        if (arguments != null) {
            errorTitleTextView.setText(arguments.getString(TITLE));
            errorMessageTextView.setText(arguments.getString(MESSAGE));
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
    public void onClick( View view ) {
        if( view.getId() == R.id.ok ){
            dismiss();
        }
    }
}
