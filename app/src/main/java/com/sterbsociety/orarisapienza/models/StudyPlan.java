package com.sterbsociety.orarisapienza.models;

import java.util.List;

/**
 * This object class holds a strong reference to its TimeModel elements and two
 * strings which may be useful to check if the StudyPlan saved by the user is outdated or
 * to display additional info.
 */
public class StudyPlan {

    private List<TimeLineModel> mDataList;
    private String startRequestDate, endRequestDate;

    public List<TimeLineModel> getDataList() {
        return mDataList;
    }

    public void setDataList(List<TimeLineModel> mDataList) {
        this.mDataList = mDataList;
    }

    public String getStartRequestDate() {
        return startRequestDate;
    }

    public void setRequestDates(String startRequestDate, String endRequestDate) {
        this.startRequestDate = startRequestDate;
        this.endRequestDate = endRequestDate;
    }

    public String getEndRequestDate() {
        return endRequestDate;
    }
}
