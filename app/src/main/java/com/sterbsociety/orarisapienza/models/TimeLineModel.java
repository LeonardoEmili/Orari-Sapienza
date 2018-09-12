package com.sterbsociety.orarisapienza.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by HP-HP on 05-12-2015.
 */
public class TimeLineModel implements Parcelable {

    private String mStartDate, mEndDate;
    private Classroom mClassroom;

    public TimeLineModel(String startDate, String endDate, Classroom classroom) {
        mStartDate = startDate;
        mEndDate = endDate;
        mClassroom = classroom;
    }

    public String getStartDate() {
        return mStartDate;
    }

    public void setStartDate(String startDate) {
        mStartDate = startDate;
    }

    public String getEndDate() {
        return mEndDate;
    }

    public void setEndDate(String endDate) {
        mEndDate = endDate;
    }

    public Classroom getClassroom() {
        return mClassroom;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mStartDate);
        dest.writeString(mEndDate);
        dest.writeParcelable(mClassroom, flags);
    }

    private TimeLineModel(Parcel in) {
        mStartDate = in.readString();
        mEndDate = in.readString();
        mClassroom = in.readParcelable(Classroom.class.getClassLoader());
    }

    public static final Parcelable.Creator<TimeLineModel> CREATOR = new Parcelable.Creator<TimeLineModel>() {
        @Override
        public TimeLineModel createFromParcel(Parcel source) {
            return new TimeLineModel(source);
        }

        @Override
        public TimeLineModel[] newArray(int size) {
            return new TimeLineModel[size];
        }
    };
}
