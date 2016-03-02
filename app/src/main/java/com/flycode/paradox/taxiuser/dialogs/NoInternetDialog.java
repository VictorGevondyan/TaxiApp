package com.flycode.paradox.taxiuser.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.flycode.paradox.taxiuser.R;
import com.flycode.paradox.taxiuser.utils.TypefaceUtils;

public class NoInternetDialog extends DialogFragment {
    private ImageView darkerInternetCar;
    private ImageView lighterInternetCar;

    public static NoInternetDialog initialize() {
        return new NoInternetDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_no_internet, container, false);

        setCancelable(false);

        TextView titleTextView = (TextView) view.findViewById(R.id.title);
        TextView messageTextView = (TextView) view.findViewById(R.id.message);
        TextView buttonTextView = (TextView) view.findViewById(R.id.button_text);

        Typeface robotoRegularTypeface = TypefaceUtils.getTypeface(getActivity(), TypefaceUtils.AVAILABLE_FONTS.ROBOTO_THIN);

        titleTextView.setTypeface(robotoRegularTypeface);
        messageTextView.setTypeface(robotoRegularTypeface);
        buttonTextView.setTypeface(robotoRegularTypeface);

        darkerInternetCar = (ImageView) view.findViewById(R.id.darker);
        lighterInternetCar = (ImageView) view.findViewById(R.id.lighter);

        view.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

        AlphaAnimation inAlphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        inAlphaAnimation.setRepeatMode(Animation.REVERSE);
        inAlphaAnimation.setRepeatCount(Animation.INFINITE);
        inAlphaAnimation.setDuration(1500);
        AlphaAnimation outAlphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        outAlphaAnimation.setRepeatMode(Animation.REVERSE);
        outAlphaAnimation.setRepeatCount(Animation.INFINITE);
        outAlphaAnimation.setDuration(1500);

        darkerInternetCar.startAnimation(inAlphaAnimation);
        lighterInternetCar.startAnimation(outAlphaAnimation);

    }

    @Override
    public void onPause() {
        super.onPause();

        darkerInternetCar.clearAnimation();
        lighterInternetCar.clearAnimation();
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
}
