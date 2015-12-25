package com.flycode.paradox.taxiuser.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
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
import android.widget.Button;
import android.widget.NumberPicker;

import com.flycode.paradox.taxiuser.R;
import com.flycode.paradox.taxiuser.utils.TypefaceUtils;
import com.flycode.paradox.taxiuser.views.PublicTimePicker;

/**
 * Created by anhaytananun on 24.12.15.
 */
public class PickTimeDialog extends DialogFragment implements View.OnClickListener, DialogInterface.OnKeyListener, NumberPicker.OnValueChangeListener {
    private static final String COMMENT = "comment";

    private Activity activity;
    private PickTimeDialogListener listener;
    private PublicTimePicker timePicker;

    public static PickTimeDialog initialize(PickTimeDialogListener listener) {
        Bundle arguments = new Bundle();

        PickTimeDialog pickTimeDialog = new PickTimeDialog();
        pickTimeDialog.setArguments(arguments);
        pickTimeDialog.setListener(listener);

        return pickTimeDialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_time, container, false);

        setCancelable(false);

        Typeface robotoThinTypeface = TypefaceUtils.getTypeface(getActivity(), TypefaceUtils.AVAILABLE_FONTS.ROBOTO_THIN);

        Button cancelButton = (Button) view.findViewById(R.id.cancel);
        cancelButton.setOnClickListener(this);
        Button doneButton = (Button) view.findViewById(R.id.done);
        doneButton.setOnClickListener(this);

        cancelButton.setTypeface(robotoThinTypeface);
        doneButton.setTypeface(robotoThinTypeface);

        timePicker = (PublicTimePicker) view.findViewById(R.id.time_picker);
        timePicker.setIs24HourView(true);

        NumberPicker dayPicker = (NumberPicker) view.findViewById(R.id.day_picker);
        dayPicker.setMinValue(0);
        dayPicker.setMaxValue(1);
        dayPicker.setDisplayedValues(new String[]{
                getString(R.string.today),
                getString(R.string.tomorrow)
        });
        dayPicker.setOnValueChangedListener(this);

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

    public interface PickTimeDialogListener {
        void onCommentDone(String input);
        void onCommentCancel();
    }

    public void setListener(PickTimeDialogListener listener) {
        this.listener = listener;
    }

    private void closeDialog() {
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
     * NumberPicker.OnValueChangeListener Methods
     */

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        if (newVal == 0) {

        } else {

        }
    }
}
