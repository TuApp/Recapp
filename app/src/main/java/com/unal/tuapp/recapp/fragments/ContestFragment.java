package com.unal.tuapp.recapp.fragments;


import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import com.unal.tuapp.recapp.R;
import com.unal.tuapp.recapp.adapters.StatisticsTableAdapter;
import com.unal.tuapp.recapp.adapters.StatisticsTableAdapterPortrait;
import com.unal.tuapp.recapp.data.RecappContract;
import com.unal.tuapp.recapp.data.Statistics;
import com.unal.tuapp.recapp.others.Utility;
import com.unal.tuapp.recapp.table.StatisticsTable;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import de.codecrafters.tableview.TableDataAdapter;
import de.codecrafters.tableview.TableView;

/**
 * Created by andresgutierrez on 12/29/15.
 */
public class ContestFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private View root;
    private TableView statistics;
    private TableDataAdapter statisticsTableAdapter;
    private static List<Statistics> data;
    private Button week;
    private Button month;
    private Button year;
    private Long date[];
    private static int STATISTICS = 1199;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_contest,container,false);
        statistics = (TableView) root.findViewById(R.id.table);
        date = new Long[2];
        date = Utility.getStatisticsWeek();
        week = (Button) root.findViewById(R.id.table_week);
        month = (Button) root.findViewById(R.id.table_month);
        year =  (Button) root.findViewById(R.id.table_year);
        if (getLoaderManager().getLoader(STATISTICS) == null) {
            getLoaderManager().initLoader(STATISTICS, null, this);
        } else {
            getLoaderManager().restartLoader(STATISTICS, null, this);
        }
        week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                week.setBackgroundColor(Color.parseColor("#ecbbed"));
                month.setBackgroundColor(Color.parseColor("#64010101"));
                year.setBackgroundColor(Color.parseColor("#64010101"));
                date = Utility.getStatisticsWeek();
                if (getLoaderManager().getLoader(STATISTICS) == null) {
                    getLoaderManager().initLoader(STATISTICS, null, ContestFragment.this);
                } else {
                    getLoaderManager().restartLoader(STATISTICS, null, ContestFragment.this);
                }

            }
        });
        month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                month.setBackgroundColor(Color.parseColor("#ecbbed"));
                week.setBackgroundColor(Color.parseColor("#64010101"));
                year.setBackgroundColor(Color.parseColor("#64010101"));
                date = Utility.getStatisticsMonth();

                if (getLoaderManager().getLoader(STATISTICS) == null) {
                    getLoaderManager().initLoader(STATISTICS, null, ContestFragment.this);
                } else {
                    getLoaderManager().restartLoader(STATISTICS, null, ContestFragment.this);
                }
            }
        });
        year.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                year.setBackgroundColor(Color.parseColor("#ecbbed"));
                month.setBackgroundColor(Color.parseColor("#64010101"));
                week.setBackgroundColor(Color.parseColor("#64010101"));
                date = Utility.getStatisticsYear();
                if (getLoaderManager().getLoader(STATISTICS) == null) {
                    getLoaderManager().initLoader(STATISTICS, null, ContestFragment.this);
                } else {
                    getLoaderManager().restartLoader(STATISTICS, null, ContestFragment.this);
                }
            }
        });


        return root;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(id == STATISTICS) {
            return new CursorLoader(
                    getContext(),
                    RecappContract.StatisticsEntry.buildStatisticsByUser(),
                    new String[]{
                            RecappContract.UserEntry.TABLE_NAME+"."+ RecappContract.UserEntry.COLUMN_USER_NAME,
                            RecappContract.UserEntry.TABLE_NAME+"."+ RecappContract.UserEntry.COLUMN_USER_LASTNAME,
                            RecappContract.UserEntry.TABLE_NAME+"."+ RecappContract.UserEntry.COLUMN_EMAIL,
                            RecappContract.UserEntry.TABLE_NAME+"."+ RecappContract.UserEntry.COLUMN_USER_IMAGE,
                            "SUM(" + RecappContract.StatisticsEntry.TABLE_NAME+"."+ RecappContract.StatisticsEntry.COLUMN_POINT+" ) "
                    },
                    RecappContract.StatisticsEntry.COLUMN_DATE + " BETWEEN ? AND ?",
                    new String[]{"" + date[0], "" + date[1]},
                    RecappContract.StatisticsEntry.COLUMN_POINT + " DESC "
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        List<Statistics> statisticsList = Statistics.allStatistics(data);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            statisticsTableAdapter = new StatisticsTableAdapterPortrait(getContext(), statisticsList);
            statistics.setDataAdapter(statisticsTableAdapter);
        }else{
            statisticsTableAdapter = new StatisticsTableAdapter(getContext(), statisticsList);
            statistics.setDataAdapter(statisticsTableAdapter);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
