package com.flycode.paradox.taxiuser.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flycode.paradox.taxiuser.R;
import com.flycode.paradox.taxiuser.layouts.RhombusLayout;
import com.flycode.paradox.taxiuser.utils.TypefaceUtils;
import com.flycode.paradox.taxiuser.views.RhombusView;

/**
 * Created by anhaytananun on 22.03.16.
 */
public class ServicesFragment extends SuperFragment implements View.OnClickListener {
    private int currentService = 0;

    private RhombusLayout rhombusLayout;
    private TextView infoTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View servicesView = inflater.inflate(R.layout.fragment_services, container, false);

        infoTextView = (TextView) servicesView.findViewById(R.id.info_text);
        infoTextView.setTypeface(TypefaceUtils.getTypeface(getActivity(), TypefaceUtils.AVAILABLE_FONTS.ROBOTO_THIN));

        rhombusLayout = (RhombusLayout) servicesView.findViewById(R.id.control);

        for (int index = 0 ; index < rhombusLayout.getChildCount() ; index++) {
            RhombusView rhombusView = (RhombusView) rhombusLayout.getChildAt(index);
            rhombusView.setTag(index);
            rhombusView.setIsFilled(false);
            rhombusView.setColor(Color.parseColor("#1FBAD6"));
            rhombusView.setTextColor(getResources().getColor(R.color.black_100));
            rhombusView.setOnClickListener(this);
        }

        setupService();

        return servicesView;
    }

    /**
     * View.OnClickListener Methods
     */

    @Override
    public void onClick(View view) {
        int index = (Integer)view.getTag();
        currentService = index;

        setupService();
    }

    private void setupService() {
        for (int index = 0 ; index < rhombusLayout.getChildCount() ; index++) {
            boolean isSelected = index == currentService;

            RhombusView rhombusView = (RhombusView) rhombusLayout.getChildAt(index);
            rhombusView.setIsFilled(!isSelected);
            rhombusView.setTextColor(getResources().getColor(isSelected ? R.color.black_100 : R.color.white_100));
        }

        infoTextView.setText("e of classical Latin literature from 45 BC, making it over 2000 years old. Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of \"de Finibus Bonorum et Malorum\" (The Extremes of Good and Evil) by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular during the Renaissance. The first line of Lorem Ipsum, \"Lorem ipsum dolor sit amet..\", comes from a line in section 1.10.32.\n" +
                "\n" +
                "The standard chunk of Lorem Ipsum used since the 1500s is reproduced below for those interested. Sections 1.10.32 and 1.10.33 from \"de Finibus Bonorum et Malorum\" by Cicero are also reproduced in their exact original form, accompanied by English versions from the 1914 translation by H. Rackham.\n" +
                "\n" +
                "There are many variations of passages of Lorem Ipsum available, but the majority have suffered alteration in some form, by injected humour, or randomised words which don't look even slightly believable. If you are going to use a passage of Lorem Ipsum, you need to be sure there isn't anything embarrassing hidden in the middle of text. All the Lorem Ipsum generators on the Internet tend to repeat predefined chunks as necessary, making this the first true generator on the Internet. It uses a dictionary of over 200 Latin words, combined with a handful of model sentence structures, to generate Lorem Ipsum which looks reasonable. The generated Lorem Ipsum is therefore always free from repetition, injected humour, or non-characteristic words etc.\n" +
                "\n" +
                "\n" +
                "5\n" +
                "\tparagraphs\n" +
                "\twords\n" +
                "\tbytes\n" +
                "\tlists\n" +
                "Start with 'Lorem\n" +
                "ipsum dolor sit amet...'Generate Lorem Ipsum\n" +
                " \n");
    }
}
