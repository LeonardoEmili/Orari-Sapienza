package com.sterbsociety.orarisapienza.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sterbsociety.orarisapienza.models.Lesson;

import java.util.List;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.LongPressAwareTableDataAdapter;

public class CourseTableDataAdapter extends LongPressAwareTableDataAdapter<Lesson> {

    private static final int TEXT_SIZE = 15;

    public CourseTableDataAdapter(final Context context, final List<Lesson> data, final TableView<Lesson> tableView) {
        super(context, data, tableView);
    }

    @Override
    public View getDefaultCellView(int rowIndex, int columnIndex, ViewGroup parentView) {
        final Lesson lesson = getRowData(rowIndex);
        View renderedView = null;

        switch (columnIndex) {
            case 0:
                renderedView = renderString(lesson.getStartLesson());
                break;
            case 1:
                renderedView = renderString(lesson.getSubjectName());
                break;
            case 2:
                renderedView = renderString(lesson.getClassRoom());
                break;
        }
        return renderedView;
    }

    @Override
    public View getLongPressCellView(int rowIndex, int columnIndex, ViewGroup parentView) {
        return null;
    }

    private View renderString(final String value) {
        final TextView textView = new TextView(getContext());
        textView.setText(value);
        textView.setPadding(30, 20, 20, 20);
        textView.setTextSize(TEXT_SIZE);
        return textView;
    }
}