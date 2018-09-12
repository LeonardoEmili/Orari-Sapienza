package com.sterbsociety.orarisapienza.adapters;

import android.content.Context;
import android.view.LayoutInflater;

import com.sterbsociety.orarisapienza.models.Classroom;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseClassListAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private final LayoutInflater mInflater;

    BaseClassListAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
    }

    LayoutInflater getInflater() {
        return mInflater;
    }

    public abstract void notifyDataSetChanged(List<Classroom> dataList);

}