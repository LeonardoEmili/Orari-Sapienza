package com.sterbsociety.orarisapienza;

import android.content.Context;
import android.util.AttributeSet;

import com.sterbsociety.orarisapienza.model.Course;
import com.sterbsociety.orarisapienza.utils.CourseComparator;

import androidx.core.content.ContextCompat;
import de.codecrafters.tableview.SortableTableView;
import de.codecrafters.tableview.model.TableColumnWeightModel;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import de.codecrafters.tableview.toolkit.SortStateViewProviders;
import de.codecrafters.tableview.toolkit.TableDataRowBackgroundProviders;

public class SortableCourseTableView extends SortableTableView<Course> {

    public SortableCourseTableView(final Context context) {
        this(context, null);
    }

    public SortableCourseTableView(final Context context, final AttributeSet attributes) {
        this(context, attributes, android.R.attr.listViewStyle);
    }

    public SortableCourseTableView(final Context context, final AttributeSet attributes, final int styleAttributes) {
        super(context, attributes, styleAttributes);

        final SimpleTableHeaderAdapter simpleTableHeaderAdapter = new SimpleTableHeaderAdapter(context, getContext().getString(R.string.time_course),
                getContext().getString(R.string.course), getContext().getString(R.string.classroom));
        simpleTableHeaderAdapter.setTextColor(ContextCompat.getColor(context, R.color.white));
        setHeaderAdapter(simpleTableHeaderAdapter);

        final int rowColorEven = ContextCompat.getColor(context, R.color.table_data_row_even);
        final int rowColorOdd = ContextCompat.getColor(context, R.color.table_data_row_odd);
        setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(rowColorEven, rowColorOdd));
        setHeaderSortStateViewProvider(SortStateViewProviders.brightArrows());

        final TableColumnWeightModel tableColumnWeightModel = new TableColumnWeightModel(3);
        tableColumnWeightModel.setColumnWeight(0, 9);
        tableColumnWeightModel.setColumnWeight(1, 12);
        tableColumnWeightModel.setColumnWeight(2, 9);
        setColumnModel(tableColumnWeightModel);

        setColumnComparator(0, CourseComparator.getCourseTimeComparator());
        setColumnComparator(1, CourseComparator.getCourseNameComparator());
        setColumnComparator(2, CourseComparator.getCourseClassComparator());
    }

}