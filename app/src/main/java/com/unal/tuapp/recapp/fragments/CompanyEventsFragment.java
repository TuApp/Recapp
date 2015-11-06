package com.unal.tuapp.recapp.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unal.tuapp.recapp.R;
import com.unal.tuapp.recapp.adapters.RecycleEventsAdapter;
import com.unal.tuapp.recapp.data.Event;
import com.unal.tuapp.recapp.data.RecappContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andresgutierrez on 11/5/15.
 */
public class CompanyEventsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private View root;
    private RecyclerView companyEventes;
    private RecycleEventsAdapter  companyEventsAdapter;
    private final int COMPANY_EVENTS = 1578;
    private String email;
    private OnEventCompanyListener onEventCompanyListener;

    public interface OnEventCompanyListener {
        void onAction(long id);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_events_company,container,false);
        if(getActivity().getIntent().getExtras()!=null){
            email = getActivity().getIntent().getExtras().getString("email");
        }
        companyEventes = (RecyclerView) root.findViewById(R.id.company_recycler_events);
        companyEventes.setLayoutManager(new LinearLayoutManager(getActivity()));
        companyEventsAdapter = new RecycleEventsAdapter(new ArrayList<Event>());
        companyEventsAdapter.setOnEventListener(new RecycleEventsAdapter.OnEventListener() {
            @Override
            public void onAction(long id) {
                if(onEventCompanyListener!=null){
                    onEventCompanyListener.onAction(id);
                }
            }
        });
        companyEventes.setAdapter(companyEventsAdapter);
        if(getLoaderManager().getLoader(COMPANY_EVENTS)==null){
            getLoaderManager().initLoader(COMPANY_EVENTS,null,this);
        }else{
            getLoaderManager().restartLoader(COMPANY_EVENTS,null,this);
        }
        return root;
    }


    public void setOnEventCompanyListener(OnEventCompanyListener onEventCompanyListener) {
        this.onEventCompanyListener = onEventCompanyListener;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                RecappContract.EventEntry.CONTENT_URI,
                null,
                RecappContract.EventEntry.COLUMN_CREATOR+ " = ? ",
                new String[]{email},
                RecappContract.EventEntry.COLUMN_DATE + " ASC "
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        List<Event> events = Event.allEvents(data);
        companyEventsAdapter.setEventsCursor(data);
        companyEventsAdapter.swapData(events);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        companyEventsAdapter.closeCursor();
    }
}
