package com.sterbsociety.orarisapienza.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import com.labo.kaji.fragmentanimations.CubeAnimation;
import com.sterbsociety.orarisapienza.R;
import com.sterbsociety.orarisapienza.activities.BugReportActivity;
import com.sterbsociety.orarisapienza.activities.FeedbackActivity;
import com.sterbsociety.orarisapienza.activities.MainActivity;
import com.sterbsociety.orarisapienza.utils.AppUtils;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ContactFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ContactFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactFragment extends Fragment {


    private OnFragmentInteractionListener mListener;
    private ChangeFragmentListener changeFragmentListener;
    private static ActionBar mActionbar;
    public final static String TAG = "CONTACT_FRAGMENT";


    public ContactFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ContactFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContactFragment newInstance(ActionBar actionbar) {
        mActionbar = actionbar;
        ContactFragment fragment = new ContactFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        view.findViewById(R.id.feedback_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), FeedbackActivity.class));
            }
        });
        view.findViewById(R.id.bug_report_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), BugReportActivity.class));
            }
        });
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.isVisible()) {
            mActionbar.setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden)
            mActionbar.setDisplayHomeAsUpEnabled(false);
        else
            mActionbar.setDisplayHomeAsUpEnabled(true);
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public ContactFragment setChangeFragmentListener(ChangeFragmentListener changeFragmentListener) {
        this.changeFragmentListener = changeFragmentListener;
        return this;
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (AppUtils.areAnimationsAllowed()) {
            return CubeAnimation.create(CubeAnimation.RIGHT, enter, 500);
        }
        return null;
    }
}