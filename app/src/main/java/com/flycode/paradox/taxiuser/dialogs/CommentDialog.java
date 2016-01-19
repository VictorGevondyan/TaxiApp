package com.flycode.paradox.taxiuser.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.flycode.paradox.taxiuser.R;
import com.flycode.paradox.taxiuser.utils.TypefaceUtils;

/**
 * Created by anhaytananun on 24.12.15.
 */
public class CommentDialog extends DialogFragment implements View.OnClickListener, DialogInterface.OnKeyListener {
    private static final String COMMENT = "comment";

    private Activity activity;
    private CommentDialogListener listener;
    private EditText commentEditText;
    private KeyboardStateListener keyboardStateListener;

    public static CommentDialog initialize(String comment, CommentDialogListener listener, KeyboardStateListener keyboardStateListener) {
        Bundle arguments = new Bundle();
        arguments.putString(COMMENT, comment);

        CommentDialog commentsDialog = new CommentDialog();
        commentsDialog.setArguments(arguments);
        commentsDialog.setListener(listener);
        commentsDialog.setKeyboardStateListener(keyboardStateListener);

        return commentsDialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.dialog_comment, container, false);

        setCancelable(false);

        Typeface robotoRegularTypeface = TypefaceUtils.getTypeface(getActivity(), TypefaceUtils.AVAILABLE_FONTS.ROBOTO_REGULAR);

        Button cancelButton = (Button) view.findViewById(R.id.cancel);
        cancelButton.setOnClickListener(this);
        Button doneButton = (Button) view.findViewById(R.id.done);
        doneButton.setOnClickListener(this);
        commentEditText = (EditText) view.findViewById(R.id.comment);
        commentEditText.requestFocus();
        TextView commentTitle = (TextView) view.findViewById(R.id.comment_title);
        commentTitle.setTypeface(robotoRegularTypeface);

        Bundle arguments = getArguments();

        if (arguments != null) {
            commentEditText.setText(arguments.getString(COMMENT));
        }

        cancelButton.setTypeface(robotoRegularTypeface);
        doneButton.setTypeface(robotoRegularTypeface);
        commentEditText.setTypeface(robotoRegularTypeface);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onPause() {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(commentEditText.getWindowToken(), 0);

        super.onPause();
    }

    @Override
    public void onResume() {
        commentEditText.requestFocus();
        super.onResume();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        dialog.setOnKeyListener(this);
        return dialog;
    }

    private void done() {
        if (listener != null) {
            String comment = commentEditText.getText().toString().trim();

            listener.onCommentDone(comment);
        }

        dismiss();
    }

    /**
     * OnClickListener Methods
     */

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.cancel) {
            closeDialog();
        } else {
            done();
        }
    }

    public interface CommentDialogListener {
        void onCommentDone(String input);
        void onCommentCancel();
    }

    public void setListener(CommentDialogListener listener) {
        this.listener = listener;
    }

    private void closeDialog() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(commentEditText.getWindowToken(), 0);

        listener.onCommentCancel();
        dismiss();
    }

    /**
     * OnKeyListener Methods
     */

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if ((keyCode ==  android.view.KeyEvent.KEYCODE_BACK)) {
            closeDialog();

            return true;
        } else {
            return false;
        }
    }

    /**
     * Interface for sending keyboard state change event to OrderFragment
     */

    public interface KeyboardStateListener{
        public void onKeyboardStateChanged();
    }

    public void setKeyboardStateListener( KeyboardStateListener keyboardStateListener ){
        this.keyboardStateListener = keyboardStateListener;
    }

}
