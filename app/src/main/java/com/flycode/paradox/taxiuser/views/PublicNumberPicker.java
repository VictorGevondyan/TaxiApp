package com.flycode.paradox.taxiuser.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.NumberPicker;

import com.flycode.paradox.taxiuser.R;

import java.lang.reflect.Field;

/**
 * Created by anhaytananun on 24.12.15.
 */
public class PublicNumberPicker extends NumberPicker {
    public PublicNumberPicker(Context context) {
        super(context);
        process();
    }

    public PublicNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        process();
    }

    public PublicNumberPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        process();
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
            selectionDivider.set(this, getResources().getDrawable(R.drawable.line_white));

            setNumberPickerTextColor(this, R.color.white_100);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
