package com.unal.tuapp.recapp.table;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.Display;
import android.view.WindowManager;

import com.unal.tuapp.recapp.R;
import com.unal.tuapp.recapp.data.Statistics;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import de.codecrafters.tableview.toolkit.TableDataRowColorizers;

/**
 * Created by andresgutierrez on 12/29/15.
 */
public class StatisticsTable extends TableView<Statistics> {
    public StatisticsTable(Context context) {
        this(context,null);
    }

    public StatisticsTable(Context context, AttributeSet attributes) {
        this(context, attributes, 0);
    }

    public StatisticsTable(Context context, AttributeSet attributes, int styleAttributes) {
        super(context, attributes, styleAttributes);
        int rowColorEven = context.getResources().getColor(R.color.table_data_row_even);
        int rowColorOdd = context.getResources().getColor(R.color.table_data_row_odd);
        switch (getResources().getConfiguration().orientation){

            case Configuration.ORIENTATION_PORTRAIT:
                SimpleTableHeaderAdapter simpleTableHeaderAdapterPortrait = new SimpleTableHeaderAdapter(context,
                        context.getResources().getString(R.string.statistics_name),
                        context.getResources().getString(R.string.statistics_email),
                        context.getResources().getString(R.string.statistics_points)
                );
                setHeaderAdapter(simpleTableHeaderAdapterPortrait);

                setDataRowColoriser(TableDataRowColorizers.alternatingRows(rowColorEven, rowColorOdd));

                setColumnWeight(0, 3);
                setColumnWeight(1, 3);
                setColumnWeight(2, 2);
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                SimpleTableHeaderAdapter simpleTableHeaderAdapter = new SimpleTableHeaderAdapter(context,
                        context.getResources().getString(R.string.statistics_image),
                        context.getResources().getString(R.string.statistics_name),
                        context.getResources().getString(R.string.statistics_email),
                        context.getResources().getString(R.string.statistics_points)
                );
                setHeaderAdapter(simpleTableHeaderAdapter);
                setDataRowColoriser(TableDataRowColorizers.alternatingRows(rowColorEven, rowColorOdd));

                setColumnWeight(0, 2);
                setColumnWeight(1, 2);
                setColumnWeight(2, 2);
                setColumnWeight(3, 2);
                break;
        }



    }

}
