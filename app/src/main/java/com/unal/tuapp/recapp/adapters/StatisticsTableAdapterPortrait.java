package com.unal.tuapp.recapp.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.unal.tuapp.recapp.R;
import com.unal.tuapp.recapp.data.Statistics;

import java.util.List;

import de.codecrafters.tableview.TableDataAdapter;

/**
 * Created by andresgutierrez on 12/30/15.
 */
public class StatisticsTableAdapterPortrait extends TableDataAdapter<Statistics>{
        public StatisticsTableAdapterPortrait(Context context, List<Statistics> data) {
            super(context, data);

        }

        @Override
        public View getCellView(int rowIndex, int columnIndex, ViewGroup parentView) {
            Statistics statistics = getRowData(rowIndex);
            View viewRender = null;
            switch (columnIndex){
                case 0:
                    viewRender = renderColumnText(1, statistics, parentView);
                    break;
                case 1:
                    viewRender = renderColumnText(2,statistics,parentView);
                    break;
                case 2:
                    viewRender = renderColumnText(3,statistics,parentView);
                    break;

            }
            return viewRender;
        }
        private View renderColumnText(int column,Statistics statistics,ViewGroup parentView){
            View view = getLayoutInflater().inflate(R.layout.table_layout_text, parentView, false);
            TextView textView = (TextView) view.findViewById(R.id.text_table);
            switch (column){
                case 1:
                    textView.setText(statistics.getName());
                    break;
                case 2:
                    textView.setText(statistics.getEmail());
                    break;
                case 3:
                    textView.setText(""+statistics.getPoints());
                    break;
            }
            return view;

        }
}
