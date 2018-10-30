package com.sterbsociety.orarisapienza.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.sterbsociety.orarisapienza.activities.CurrentPlanActivity;
import com.sterbsociety.orarisapienza.activities.LessonTimetableActivity;
import com.sterbsociety.orarisapienza.R;
import com.sterbsociety.orarisapienza.activities.ClassListActivity;
import com.sterbsociety.orarisapienza.activities.FaqActivity;
import com.sterbsociety.orarisapienza.activities.MapsActivity;
import com.sterbsociety.orarisapienza.utils.AppUtils;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;
    private ChangeFragmentListener changeFragmentListener;
    public final static String TAG = "HOME_FRAGMENT";
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    private boolean userClicked;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HomeFragment.
     */
    public static HomeFragment newInstance(Context context) {
        HomeFragment fragment = new HomeFragment();
        mContext = context;
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        userClicked = false;

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Context mContext = inflater.getContext();
        CardView studyPlanBtn = view.findViewById(R.id.study_plan_btn);
        CardView timeTablesBtn = view.findViewById(R.id.timetables_btn);
        CardView currentPlanBtn = view.findViewById(R.id.current_plan_btn);
        CardView classListBtn = view.findViewById(R.id.class_list_btn);
        CardView faqBtn = view.findViewById(R.id.faq_btn);
        CardView contactBtn = view.findViewById(R.id.contact_btn);

        faqBtn.setOnClickListener(this);
        classListBtn.setOnClickListener(this);
        timeTablesBtn.setOnClickListener(this);
        studyPlanBtn.setOnClickListener(this);
        currentPlanBtn.setOnClickListener(this);

        ((TextView)studyPlanBtn.findViewById(R.id.txt_study_plan)).setText(AppUtils.getStringByLocal(mContext, R.string.piano_di_studi));
        ((TextView)timeTablesBtn.findViewById(R.id.txt_timetables)).setText(AppUtils.getStringByLocal(mContext, R.string.lessons_timetable));
        ((TextView)currentPlanBtn.findViewById(R.id.txt_current_plan)).setText(AppUtils.getStringByLocal(mContext, R.string.study_plan_active));
        ((TextView)classListBtn.findViewById(R.id.txt_classroom_plan)).setText(AppUtils.getStringByLocal(mContext, R.string.classrooms_list));
        ((TextView)contactBtn.findViewById(R.id.txt_contacts)).setText(AppUtils.getStringByLocal(mContext, R.string.contacts));

        contactBtn.setOnClickListener(view16 -> changeFragmentListener.onChangeFragmentLicked(ContactFragment.TAG));

        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.study_plan_btn || view.getId() == R.id.timetables_btn
                || view.getId() == R.id.current_plan_btn || view.getId() == R.id.class_list_btn) {
            if (AppUtils.isDBFullyLoaded) {
                if (!userClicked) {
                    userClicked = true;
                    switch (view.getId()) {
                        case R.id.study_plan_btn:
                            startActivity(new Intent(mContext, MapsActivity.class));
                            break;
                        case R.id.timetables_btn:
                            startActivity(new Intent(mContext, LessonTimetableActivity.class));
                            break;
                        case R.id.current_plan_btn:
                            startActivity(new Intent(mContext, CurrentPlanActivity.class));
                            break;
                        case R.id.class_list_btn:
                            startActivity(new Intent(mContext, ClassListActivity.class));
                            break;
                    }
                }
            } else {
                StyleableToast.makeText(mContext, getString(R.string.loading_data_msg),
                        Toast.LENGTH_LONG, R.style.loadingToast).show();
            }
        } else if (view.getId() == R.id.faq_btn && !userClicked) {
            userClicked = true;
            startActivity(new Intent(mContext, FaqActivity.class));
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public void setChangeFragmentListener(ChangeFragmentListener changeFragmentListener) {
        this.changeFragmentListener = changeFragmentListener;
    }
}
