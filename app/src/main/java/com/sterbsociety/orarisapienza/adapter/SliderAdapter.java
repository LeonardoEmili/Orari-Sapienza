package com.sterbsociety.orarisapienza.adapter;

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

public class SliderAdapter extends PagerAdapter {

    Context mContext;
    LayoutInflater layoutInflater;

    private int[] slideImages = new int[] {

            R.drawable.stopwatch,
            R.drawable.oval,
            R.drawable.desk
    };

    private final String[] slideHeadings = new String[] {

            "Orari aggiornati",
            "Piani di studio",
            "Dettaglio aule"
    };


    private String[] slideDescs = new String[] {

            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam tempor, est sed pulvinar tincidunt, risus orci iaculis massa, nec gravida magna orci id nulla.",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam tempor, est sed pulvinar tincidunt, risus orci iaculis massa, nec gravida magna orci id nulla.",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam tempor, est sed pulvinar tincidunt, risus orci iaculis massa, nec gravida magna orci id nulla."
    };

    public SliderAdapter(Context context) {
        mContext = context;
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

        layoutInflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;
        View view = layoutInflater.inflate(R.layout.slide_layout, container, false);

        ImageView slideImageView = view.findViewById(R.id.slide_image);
        TextView slideHeading = view.findViewById(R.id.slide_heading);
        TextView slideDescription = view.findViewById(R.id.slide_desc);

        slideImageView.setImageResource(slideImages[position]);
        slideHeading.setText(slideHeadings[position]);
        slideDescription.setText(slideDescs[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((RelativeLayout)object);
    }
}
