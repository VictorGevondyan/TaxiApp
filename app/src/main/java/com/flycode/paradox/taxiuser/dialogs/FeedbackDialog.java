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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flycode.paradox.taxiuser.R;
import com.flycode.paradox.taxiuser.utils.TypefaceUtils;

/**
 * Created by anhaytananun on 24.12.15.
 */
public class FeedbackDialog extends DialogFragment implements View.OnClickListener, DialogInterface.OnKeyListener {
    private static final String COMMENT = "comment";

    private Activity activity;
    private FeedbackDialogListener listener;
    private EditText commentEditText;
    private LinearLayout ratingBar;

    private int rating = 5;

    public static FeedbackDialog initialize(FeedbackDialogListener listener) {
        FeedbackDialog commentsDialog = new FeedbackDialog();
        commentsDialog.setListener(listener);

        return commentsDialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.dialog_feedback, container, false);

        setCancelable(false);

        Typeface robotoRegularTypeface = TypefaceUtils.getTypeface(getActivity(), TypefaceUtils.AVAILABLE_FONTS.ROBOTO_REGULAR);

        Button cancelButton = (Button) view.findViewById(R.id.cancel);
        cancelButton.setOnClickListener(this);
        Button doneButton = (Button) view.findViewById(R.id.done);
        doneButton.setOnClickListener(this);
        commentEditText = (EditText) view.findViewById(R.id.comment);

        ratingBar = (LinearLayout) view.findViewById(R.id.feedback_rating);
        setupRatingBar();

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

    public void setupRatingBar(){
        int i;
        TextView ratingStar;
        Typeface icomoonTypeface = TypefaceUtils.getTypeface(getActivity(), TypefaceUtils.AVAILABLE_FONTS.ICOMOON);
        int childCount = ratingBar.getChildCount();
        for( i = 0; i < childCount; i++ ){
            ratingStar = (TextView) ratingBar.getChildAt(i);
            ratingStar.setOnClickListener(ratingBarStarClickListener);
            ratingStar.setTypeface(icomoonTypeface);
        }
    }

    View.OnClickListener ratingBarStarClickListener =new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            //

            ViewGroup parentView = (ViewGroup)view.getParent();
            int clickedViewIndex = ratingBar.indexOfChild(view);
            int childCount = ratingBar.getChildCount();
            rating = clickedViewIndex + 1;

            int index;
            TextView child;
            for( index = 0; index <= clickedViewIndex; index++ ){
                child = (TextView)ratingBar.getChildAt(index);
                child.setText(R.string.icon_star_filled);
            }

            for ( index = clickedViewIndex +1 ; index < childCount; index++){
                child = (TextView)ratingBar.getChildAt(index);
                child.setText(R.string.icon_favorites);
            }
        }
    };

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setOnKeyListener(this);
        return dialog;
    }

    private void done() {
        if (listener != null) {
            String comment = commentEditText.getText().toString().trim();

            listener.onFeedbackDone(comment, rating);
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

    public interface FeedbackDialogListener {
        void onFeedbackDone(String comment, int rating);
        void onFeedbackCancel();
    }

    public void setListener(FeedbackDialogListener listener) {
        this.listener = listener;
    }

    private void closeDialog() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(commentEditText.getWindowToken(), 0);

        listener.onFeedbackCancel();
        dismiss();
    }

    /**
     * OnKeyListener Methods
     */

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if ((keyCode ==  KeyEvent.KEYCODE_BACK)) {
            closeDialog();

            return true;
        } else {
            return false;
        }
    }
}
