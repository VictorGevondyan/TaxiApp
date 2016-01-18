package com.flycode.paradox.taxiuser.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import com.flycode.paradox.taxiuser.R;

import java.lang.reflect.Field;

/**
 * Created by anhaytananun on 24.12.15.
 */
public class PublicTimePicker extends TimePicker {
    public PublicTimePicker(Context context) {
        super(context);
        process();
    }

    public PublicTimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        process();
    }

    public PublicTimePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        process();
    }

    private Class<?> getInternalRID() {
        Class<?> internalRID = null;
        try {
            internalRID = Class.forName("com.android.internal.R$id");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        for (Field field : internalRID.getDeclaredFields()) {
            if (field.getName().contains("min")) {
                Log.d("NAME FIELD", field.getName());
            }
        }

        return internalRID;
    }

    private void process() {
        Class<?> numberPickerClass = null;
        try {
            numberPickerClass = Class.forName("android.widget.NumberPicker");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Field selectionDivider = null;
        try {
            selectionDivider = numberPickerClass.getDeclaredField("mSelectionDivider");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            selectionDivider.setAccessible(true);
            selectionDivider.set(getMinutePicker(), getResources().getDrawable(R.drawable.line_white));
            selectionDivider.set(getHourPicker(), getResources().getDrawable(R.drawable.line_white));

            setNumberPickerTextColor(getMinutePicker(), R.color.white_100);
            setNumberPickerTextColor(getHourPicker(), R.color.white_100);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public NumberPicker getMinutePicker() {
        Field minute = null;
        try {
            minute = getInternalRID().getField("minute");
        } catch (Exception e) {
            e.printStackTrace();
        }

        NumberPicker npMinute = null;
        try {
            npMinute = (NumberPicker) findViewById(minute.getInt(null));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return npMinute;
    }

    public NumberPicker getHourPicker() {
        Field hour = null;
        try {
            hour = getInternalRID().getField("hour");
        } catch (Exception e) {
            e.printStackTrace();
        }

        NumberPicker npHour = null;
        try {
            npHour = (NumberPicker) findViewById(hour.getInt(null));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return npHour;
    }

    public static boolean setNumberPickerTextColor(NumberPicker numberPicker, int color) {
//        final int count = numberPicker.getChildCount();
//        for(int i = 0; i < count; i++){
//            View child = numberPicker.getChildAt(i);
//            if(child instanceof EditText){
//                try{
//                    Field selectorWheelPaintField = numberPicker.getClass()
//                            .getDeclaredField("mSelectorWheelPaint");
//                    selectorWheelPaintField.setAccessible(true);
//                    ((Paint)selectorWheelPaintField.get(numberPicker)).setColor(color);
//                    ((EditText)child).setTextColor(color);
//                    numberPicker.invalidate();
//                    return true;
//                }
//                catch(Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
        return false;
    }
}
