package com.sterbsociety.orarisapienza.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sterbsociety.orarisapienza.R;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import static com.sterbsociety.orarisapienza.utils.AppUtils.getStringByLocal;

public class SliderAdapter extends PagerAdapter {

    private String[] slideHeadings, slideDescriptions;

    private int[] slideImages = new int[] {
            R.drawable.stopwatch,
            R.drawable.oval,
            R.drawable.desk
    };

    public SliderAdapter(Context context) {
        slideDescriptions = new String[] {
                getStringByLocal(context, R.string.updated_timetables_desc),
                getStringByLocal(context, R.string.study_plan_desc),
                getStringByLocal(context, R.string.class_info_desc)
        };
        slideHeadings = new String[] {
                getStringByLocal(context, R.string.updated_timetables_title),
                getStringByLocal(context, R.string.study_plan_title),
                getStringByLocal(context, R.string.class_info_title)
        };
    }

    @Override
    public int getCount() {
        return slideHeadings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater layoutInflater = (LayoutInflater) container.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;
        View view = layoutInflater.inflate(R.layout.slide_layout, container, false);
        ImageView slideImageView = view.findViewById(R.id.slide_image);
        TextView slideHeading = view.findViewById(R.id.slide_heading);
        TextView slideDescription = view.findViewById(R.id.slide_desc);
        slideImageView.setImageResource(slideImages[position]);
        slideHeading.setText(slideHeadings[position]);
        slideDescription.setText(slideDescriptions[position]);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout)object);
    }
}
