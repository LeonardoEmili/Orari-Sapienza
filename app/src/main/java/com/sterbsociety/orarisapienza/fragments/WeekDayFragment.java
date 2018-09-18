package com.sterbsociety.orarisapienza.fragments;

import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import de.codecrafters.tableview.listeners.TableDataClickListener;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sterbsociety.orarisapienza.R;
import com.sterbsociety.orarisapienza.views.SortableCourseTableView;
import com.sterbsociety.orarisapienza.activities.LessonTimetableActivity;
import com.sterbsociety.orarisapienza.adapters.CourseTableDataAdapter;
import com.sterbsociety.orarisapienza.models.Lesson;
import com.sterbsociety.orarisapienza.utils.AppUtils;

import java.util.List;

public abstract class WeekDayFragment extends Fragment {

    private final static String SPAN_PLACEHOLDER = "x";
    private TextView mTextView;
    private SortableCourseTableView sortableCourseTableView;
    private FrameLayout frameLayout;

    public WeekDayFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_week_day, container, false);
        frameLayout = rootView.findViewById(R.id.frame_layout);
        sortableCourseTableView = rootView.findViewById(R.id.tableView);

        initFragment(rootView);

        return rootView;
    }


    private void initFragment(View rootView) {

        // We don't need to set locale inside fragments if this has already done by it's "parent" activity.
        final Drawable mIcon = getResources().getDrawable(R.drawable.ic_search_cool_black_24dp);

        // GUI stuff for improving the UX
        if (!AppUtils.isDarkTheme()) {
            frameLayout.setBackgroundColor(getResources().getColor(R.color.whiteSmoke));
        } else {
            sortableCourseTableView.setHeaderBackgroundColor(AppUtils.getToolbarColor());
            mIcon.setTint(getResources().getColor(android.R.color.white));
        }

        mTextView = rootView.findViewById(R.id.search_help_hint);

        final String mText = getResources().getString(R.string.timetables_help_msg);
        final int startIndex = mText.indexOf(SPAN_PLACEHOLDER);

        final SpannableString spannableString = new SpannableString(mText);

        final Paint textPaint = new Paint();
        textPaint.setTextSize(mTextView.getTextSize());
        Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
        mIcon.setBounds(0, fontMetrics.ascent, mTextView.getLineHeight(), fontMetrics.bottom);

        spannableString.setSpan(new ImageSpan(mIcon, ImageSpan.ALIGN_BASELINE), startIndex, startIndex + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTextView.setText(spannableString);

        // We set up an itemOnClickListener
        sortableCourseTableView.addDataClickListener(new CourseClickListener());

        if (AppUtils.isTableVisible) {
            displayTableView();
        }
    }

    /**
     * This is an utility method to show the tableView when a course is selected.
     */
    public void displayTableView() {

        mTextView.setVisibility(View.GONE);

        // Here we have to retrieve the course data by getting the name
        List<Lesson> lessonList = LessonTimetableActivity.getScheduledLessonsForDay(getWeekDayIndex());

        final CourseTableDataAdapter carTableDataAdapter = new CourseTableDataAdapter(getContext(), lessonList, sortableCourseTableView);
        sortableCourseTableView.setDataAdapter(carTableDataAdapter);
    }

    private class CourseClickListener implements TableDataClickListener<Lesson> {

        @Override
        public void onDataClicked(final int rowIndex, final Lesson clickedData) {
            final String carString = "Click: " + clickedData.getCourseName() + " " + clickedData.getProfessor();
            Toast.makeText(getActivity(), carString, Toast.LENGTH_SHORT).show();
        }
    }

    abstract protected int getWeekDayIndex();
}