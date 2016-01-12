package com.unal.tuapp.recapp.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.unal.tuapp.recapp.R;
import com.unal.tuapp.recapp.data.Statistics;

import java.util.List;

import de.codecrafters.tableview.TableDataAdapter;

/**
 * Created by andresgutierrez on 12/29/15.
 */
public class StatisticsTableAdapter extends TableDataAdapter<Statistics> {
    public StatisticsTableAdapter(Context context, List<Statistics> data) {
        super(context, data);

    }

    @Override
    public View getCellView(int rowIndex, int columnIndex, ViewGroup parentView) {
        Statistics statistics = getRowData(rowIndex);
        View viewRender = null;
        switch (columnIndex){
            case 0:
                viewRender = renderColumnImage(statistics, parentView);
                break;
            case 1:
                viewRender = renderColumnText(1, statistics, parentView);
                break;
            case 2:
                viewRender = renderColumnText(2,statistics,parentView);
                break;
            case 3:
                viewRender = renderColumnText(3,statistics,parentView);
                break;

        }
        return viewRender;
    }
    private View renderColumnImage(Statistics statistics,ViewGroup parentView){
        View view = getLayoutInflater().inflate(R.layout.table_layout_image, parentView, false);
        de.hdodenhof.circleimageview.CircleImageView imageView = (de.hdodenhof.circleimageview.CircleImageView) view.findViewById(R.id.image_table);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 3;
        imageView.setImageBitmap(
                BitmapFactory.decodeByteArray(statistics.getImage(),0,statistics.getImage().length,options)
        );
        return view;

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
