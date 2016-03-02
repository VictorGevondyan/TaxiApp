package com.flycode.paradox.taxiuser.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.flycode.paradox.taxiuser.R;
import com.flycode.paradox.taxiuser.utils.CountryCodesUtil;
import com.flycode.paradox.taxiuser.utils.TypefaceUtils;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by anhaytananun on 27.01.16.
 */
public class CodeSpinnerAdapter extends ArrayAdapter {
    private Typeface robotoThinTypeface;
    private Context context;
    private JSONArray cityCodesJSON;

    public CodeSpinnerAdapter(Context context) {
        super(context, R.layout.item_code_view);

        this.robotoThinTypeface = TypefaceUtils.getTypeface(context, TypefaceUtils.AVAILABLE_FONTS.ROBOTO_THIN);
        this.context = context;
        this.cityCodesJSON = CountryCodesUtil.getCityCodes();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.item_code_view, parent, false);
        }

        JSONObject data = cityCodesJSON.optJSONObject(position);

        TextView codeTextView = (TextView) convertView.findViewById(R.id.code);
        codeTextView.setTypeface(robotoThinTypeface);
//        codeTextView.setText(data.optString("dial_code"));
        codeTextView.setText("+374");

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.item_code_dropdown, parent, false);
        }

        JSONObject data = cityCodesJSON.optJSONObject(position);

        TextView countryTextView = (TextView) convertView.findViewById(R.id.country);
        TextView codeTextView = (TextView) convertView.findViewById(R.id.code);

        countryTextView.setTypeface(robotoThinTypeface);
        codeTextView.setTypeface(robotoThinTypeface);

//        countryTextView.setText(data.optString("name"));
//        codeTextView.setText(data.optString("dial_code"));
        countryTextView.setText("Armenia");
        codeTextView.setText("+374");

        return convertView;
    }

    @Override
    public int getCount() {
//        return cityCodesJSON.length();
        return 1;
    }

    @Override
    public JSONObject getItem(int position) {
        return cityCodesJSON.optJSONObject(position);
    }

    public int getDefaultPosition() {
//        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//        String cityCode = telephonyManager.getSimCountryIso();
//
//        if (cityCode.isEmpty()) {
            String cityCode = "AM";
//        }

        for (int index = 0 ; index < cityCodesJSON.length() ; index++) {
            if (cityCodesJSON.optJSONObject(index).optString("code").equals(cityCode)) {
                return index;
            }
        }

        return 0;
    }
}
