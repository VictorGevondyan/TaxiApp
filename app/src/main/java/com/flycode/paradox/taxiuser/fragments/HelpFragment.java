package com.flycode.paradox.taxiuser.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flycode.paradox.taxiuser.R;
import com.flycode.paradox.taxiuser.utils.TypefaceUtils;
import com.flycode.paradox.taxiuser.views.RhombusView;

/**
 * Created by anhaytananun on 21.03.16.
 */
public class HelpFragment extends SuperFragment implements View.OnClickListener {
    private static final int PAGES_NUMBER = 5;

    private static final int[] IMAGES = {
            R.drawable.help_order,
            R.drawable.help_order_settings,
            R.drawable.help_confirm,
            R.drawable.help_driver_details,
            R.drawable.help_feedback
    };

    private LinearLayout pagerControlsLinearLayout;
    private TextView infoTextView;
    private ImageView infoImageView;

    private int currentPage = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View helpView = inflater.inflate(R.layout.fragment_help, container, false);

        Typeface typeface = TypefaceUtils.getTypeface(getActivity(), TypefaceUtils.AVAILABLE_FONTS.ROBOTO_THIN);

        infoTextView = (TextView) helpView.findViewById(R.id.info_text);
        infoTextView.setTypeface(typeface);

        infoImageView = (ImageView) helpView.findViewById(R.id.info_image);

        pagerControlsLinearLayout = (LinearLayout) helpView.findViewById(R.id.paging_controls);

        for (int index = 0 ; index < PAGES_NUMBER ; index++) {
            View pagingControl = inflater.inflate(R.layout.item_paging_control, pagerControlsLinearLayout, false);
            pagerControlsLinearLayout.addView(pagingControl);

            RhombusView outerRhombus = (RhombusView) pagingControl.findViewById(R.id.outer_rhombus);
            RhombusView innerRhombus = (RhombusView) pagingControl.findViewById(R.id.inner_rhombus);
            outerRhombus.setIsFilled(false);
            innerRhombus.setIsFilled(true);
            outerRhombus.setColor(getResources().getColor(R.color.base_grey_100));
            innerRhombus.setColor(getResources().getColor(R.color.cyan_100));

            outerRhombus.setTag(index);
            outerRhombus.setClickable(true);
            outerRhombus.setOnClickListener(this);
        }

        pagerControlsLinearLayout.requestLayout();

        setPage();

        return helpView;
    }

    @Override
    public void onClick(View view) {
        currentPage = (Integer) view.getTag();
        setPage();
    }

    private void setPage() {
        infoImageView.setImageResource(IMAGES[currentPage]);

        for (int index = 0 ; index < PAGES_NUMBER ; index++) {
            View pagingControl = pagerControlsLinearLayout.getChildAt(index);
            RhombusView outerRhombus = (RhombusView) pagingControl.findViewById(R.id.inner_rhombus);

            if (index == currentPage) {
                outerRhombus.setVisibility(View.VISIBLE);
            } else {
                outerRhombus.setVisibility(View.GONE);
            }
        }

        infoTextView.setText("lly unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.\n" +
                "\n" +
                "It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using 'Content here, content here', making it look like readable English. Many desktop publishing packages and web page editors now use Lorem Ipsum as their default model text, and a search for 'lorem ipsum' will uncover many web sites still in their infancy. Various versions have evolved over the years, sometimes by accident, sometimes on purpose (injected humour and the like).\n" +
                "\n" +
                " \n" +
                "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of \"de Finibus Bonorum et Malorum\" (The Extremes of Good and Evil) by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular during the Renaissance. The first line of Lorem Ipsum, \"Lorem ipsum dolor sit amet..\", comes from a line in section 1.10.32.\n" +
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
                "ipsum dolor sit amet...'Generate Lorem Ipsum\n");
    }
}
