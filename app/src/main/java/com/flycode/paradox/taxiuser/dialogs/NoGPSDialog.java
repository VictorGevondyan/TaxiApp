package com.flycode.paradox.taxiuser.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.flycode.paradox.taxiuser.R;
import com.flycode.paradox.taxiuser.utils.TypefaceUtils;

public class NoGPSDialog extends DialogFragment {
    public static String ISSUE_PERMISSION = "permission";
    public static String ISSUE_DISABLED = "disabled";
    private static String ISSUE = "issue";

    private ImageView slaqImageView;

    public static NoGPSDialog initialize(String issue) {
        Bundle arguments = new Bundle();
        arguments.putString(ISSUE, issue);

        NoGPSDialog noGPSDialog = new NoGPSDialog();
        noGPSDialog.setArguments(arguments);

        return noGPSDialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_no_gps, container, false);

        setCancelable(false);

        String issue = getArguments().getString(ISSUE);

        TextView titleTextView = (TextView) view.findViewById(R.id.title);
        TextView messageTextView = (TextView) view.findViewById(R.id.message);
        TextView buttonTextView = (TextView) view.findViewById(R.id.button_text);

        titleTextView.setText(issue.equals(ISSUE_DISABLED) ? R.string.gps_disabled_title : R.string.gps_not_permitted_title);
        messageTextView.setText(issue.equals(ISSUE_DISABLED) ? R.string.gps_disabled_message : R.string.gps_not_permitted_message);
        buttonTextView.setText(issue.equals(ISSUE_DISABLED) ? R.string.enable : R.string.permit);

        Typeface robotoRegularTypeface = TypefaceUtils.getTypeface(getActivity(), TypefaceUtils.AVAILABLE_FONTS.ROBOTO_THIN);

        titleTextView.setTypeface(robotoRegularTypeface);
        messageTextView.setTypeface(robotoRegularTypeface);
        buttonTextView.setTypeface(robotoRegularTypeface);

        view.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getArguments().getString(ISSUE).equals(ISSUE_DISABLED)) {
                    startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
                } else {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + getActivity().getApplicationContext().getPackageName()));
                    startActivityForResult(intent, 0);
                }
            }
        });

        Typeface icomoonTypeface = TypefaceUtils.getTypeface(getActivity(), TypefaceUtils.AVAILABLE_FONTS.ICOMOON);

        TextView gpsTextView = (TextView) view.findViewById(R.id.gps);
        gpsTextView.setTypeface(icomoonTypeface);
        TextView noGpsTextView = (TextView) view.findViewById(R.id.no_gps);
        noGpsTextView.setTypeface(icomoonTypeface);
        TextView gazTextView = (TextView) view.findViewById(R.id.gaz);
        gazTextView.setTypeface(icomoonTypeface);

        slaqImageView = (ImageView) view.findViewById(R.id.slaq);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

        RotateAnimation rotateAnimation = new RotateAnimation(
                60, -60,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 1.0f);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setDuration(2000);
        rotateAnimation.setInterpolator(new DecelerateInterpolator());

        slaqImageView.startAnimation(rotateAnimation);
    }

    @Override
    public void onPause() {
        super.onPause();
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
