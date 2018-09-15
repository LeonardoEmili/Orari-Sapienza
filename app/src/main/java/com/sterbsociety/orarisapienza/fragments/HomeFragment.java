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

import com.sterbsociety.orarisapienza.activities.CurrentPlanActivity;
import com.sterbsociety.orarisapienza.activities.LessonTimetableActivity;
import com.sterbsociety.orarisapienza.R;
import com.sterbsociety.orarisapienza.activities.ClassListActivity;
import com.sterbsociety.orarisapienza.activities.FaqActivity;
import com.sterbsociety.orarisapienza.activities.MapsActivity;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private ChangeFragmentListener changeFragmentListener;
    public final static String TAG = "HOME_FRAGMENT";
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        CardView studyPlanBtn = view.findViewById(R.id.study_plan_btn);
        CardView timeTablesBtn = view.findViewById(R.id.timetables_btn);
        CardView currentPlanBtn = view.findViewById(R.id.current_plan_btn);
        CardView classListBtn = view.findViewById(R.id.class_list_btn);
        CardView faqBtn = view.findViewById(R.id.faq_btn);
        CardView contactBtn = view.findViewById(R.id.contact_btn);

        contactBtn.setOnClickListener(view16 -> changeFragmentListener.onChangeFragmentLicked("CONTACT_FRAGMENT"));
        faqBtn.setOnClickListener(view15 -> startActivity(new Intent(mContext, FaqActivity.class)));
        classListBtn.setOnClickListener(view14 -> startActivity(new Intent(mContext, ClassListActivity.class)));
        timeTablesBtn.setOnClickListener(view13 -> startActivity(new Intent(mContext, LessonTimetableActivity.class)));
        studyPlanBtn.setOnClickListener(view12 -> startActivity(new Intent(mContext, MapsActivity.class)));
        currentPlanBtn.setOnClickListener(view1 -> startActivity(new Intent(mContext, CurrentPlanActivity.class)));

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
