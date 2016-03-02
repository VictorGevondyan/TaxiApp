package com.flycode.paradox.taxiuser.dialogs;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.TextView;

import com.flycode.paradox.taxiuser.R;
import com.flycode.paradox.taxiuser.utils.TypefaceUtils;

public class LoadingDialog extends ProgressDialog {
	private RotateAnimation animation;
	private Context context;

    public LoadingDialog(Context context) {
		super(context);

		this.context = context;

		animation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF,
				 0.5f,  Animation.RELATIVE_TO_SELF, 0.5f);
		animation.setFillAfter(true);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(2000);
		animation.setRepeatCount(Animation.INFINITE);

		setCancelable(false);
		setCanceledOnTouchOutside(false);
	}

	@Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.dialog_loading);

    	TextView dialogMessageTextView = (TextView) findViewById(R.id.message);
		dialogMessageTextView.setTypeface(TypefaceUtils.getTypeface(context, TypefaceUtils.AVAILABLE_FONTS.ROBOTO_THIN));
		dialogMessageTextView.setShadowLayer(10.0f, 0.0f, 0.0f, Color.parseColor("#6FCCDD"));
	}

	@Override
	public void show() {
		try{
			if (isShowing()) {
				super.dismiss();
			}

			super.show();

			View spinner = findViewById(R.id.spinner);
			spinner.startAnimation(animation);
		}catch( Exception e){
			e.printStackTrace();
		}

	}

	@Override
	public void dismiss() {
		try {
			super.dismiss();

			animation.cancel();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
