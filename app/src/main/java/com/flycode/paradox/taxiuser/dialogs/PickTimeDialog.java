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
import android.widget.TextView;

import com.flycode.paradox.taxiuser.R;
import com.flycode.paradox.taxiuser.utils.TypefaceUtils;
import com.flycode.paradox.taxiuser.views.PublicNumberPicker;
import com.flycode.paradox.taxiuser.views.PublicTimePicker;

import java.util.Calendar;

/**
 * Created by anhaytananun on 24.12.15.
 */
public class PickTimeDialog extends DialogFragment implements View.OnClickListener, DialogInterface.OnKeyListener,
        NumberPicker.OnValueChangeListener {
    private static final String IS_TODAY = "today";
    private static final String HOUR= "hour";
    private static final String MINUTE = "minute";

    private Activity activity;
    private PickTimeDialogListener listener;
    private PublicTimePicker timePicker;
    private PublicNumberPicker dayPicker;

    public static PickTimeDialog initialize( boolean isToday, int hour, int minute, PickTimeDialogListener listener) {
        Bundle arguments = new Bundle();

        arguments.putBoolean(IS_TODAY, isToday);
        arguments.putInt(HOUR, hour);
        arguments.putInt(MINUTE, minute);

        PickTimeDialog pickTimeDialog = new PickTimeDialog();
        pickTimeDialog.setArguments(arguments);
        pickTimeDialog.setListener(listener);

        return pickTimeDialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_time, container, false);

        setCancelable(false);

        Typeface robotoRegularTypeface = TypefaceUtils.getTypeface(getActivity(), TypefaceUtils.AVAILABLE_FONTS.ROBOTO_REGULAR);

        TextView orderTimeTextView = (TextView) view.findViewById(R.id.order_time);
        orderTimeTextView.setTypeface(robotoRegularTypeface);

        Button cancelButton = (Button) view.findViewById(R.id.cancel);
        cancelButton.setOnClickListener(this);
        Button doneButton = (Button) view.findViewById(R.id.done);
        doneButton.setOnClickListener(this);

        cancelButton.setTypeface(robotoRegularTypeface);
        doneButton.setTypeface(robotoRegularTypeface);

        timePicker = (PublicTimePicker) view.findViewById(R.id.time_picker);
        timePicker.setIs24HourView(true);

        dayPicker = (PublicNumberPicker) view.findViewById(R.id.day_picker);
        dayPicker.setMinValue(0);
        dayPicker.setMaxValue(1);
        dayPicker.setDisplayedValues(new String[]{
                getString(R.string.today),
                getString(R.string.tomorrow)
        });
        dayPicker.setOnValueChangedListener(this);

        Bundle arguments = getArguments();
        if (arguments != null) {
            dayPicker.setValue(arguments.getBoolean(IS_TODAY) ? 0 : 1);
            timePicker.setCurrentHour(arguments.getInt(HOUR));
            timePicker.setCurrentMinute(arguments.getInt(MINUTE));
        }

        timePicker.getHourPicker().setOnValueChangedListener(timePickerHourChangeListener);
        timePicker.getMinutePicker().setOnValueChangedListener(timePickerMinuteChangeListener);

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

            listener.onTimePickDone(
                    dayPicker.getValue() == 0,
                    timePicker.getCurrentHour(),
                    timePicker.getCurrentMinute()
            );
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
        void onTimePickDone(boolean isToday, int hour, int minute);
        void onTimePickCancel();
    }

    public void setListener(PickTimeDialogListener listener) {
        this.listener = listener;
    }

    private void closeDialog() {
        listener.onTimePickCancel();
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

    NumberPicker.OnValueChangeListener timePickerHourChangeListener = new NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            if(dayPicker.getValue() == 0) {
                if (newVal < Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
                    picker.setValue(oldVal);
                    newVal = oldVal;
                }
                if (newVal == Calendar.getInstance().get(Calendar.HOUR_OF_DAY) && timePicker.getCurrentMinute() < Calendar.getInstance().get(Calendar.MINUTE)) {
                    timePicker.setCurrentMinute(Calendar.getInstance().get(Calendar.MINUTE));
                }
            }
        }
    };

    NumberPicker.OnValueChangeListener timePickerMinuteChangeListener = new NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            if(dayPicker.getValue() == 0) {
                if (timePicker.getCurrentHour() == Calendar.getInstance().get(Calendar.HOUR_OF_DAY) && newVal < Calendar.getInstance().get(Calendar.MINUTE)) {
                    picker.setValue(oldVal);
                }
            }

        }
    };

}
