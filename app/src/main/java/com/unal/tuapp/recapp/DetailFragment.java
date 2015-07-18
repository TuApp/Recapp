package com.unal.tuapp.recapp;

import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment {
    private ImageView favorite;
    private Button share;
    private View root;
    private NestedScrollView nestedScrollView;
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_detail, container, false);

        favorite = (ImageView) root.findViewById(R.id.card_favorite);
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drawable drawable = favorite.getDrawable();
                if(drawable.getConstantState().equals(getActivity().getResources()
                    .getDrawable(R.drawable.ic_favorites).getConstantState())) {
                    favorite.setImageResource(R.drawable.ic_favorites_color);

                }else{
                    favorite.setImageResource(R.drawable.ic_favorites);
                }
                favorite.invalidate();
            }
        });
        nestedScrollView = (NestedScrollView) root.findViewById(R.id.card_detail_scroll);
        nestedScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_container,DetailFragment.this)
                        .commit();
            }
        });
        share = (Button) root.findViewById(R.id.card_share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(getActivity().findViewById(R.id.detail_coordination),"algo",Snackbar.LENGTH_SHORT).show();
            }
        });
        return root;
    }
    @Override
    public void onPause(){
        super.onPause();
        getActivity().getSupportFragmentManager().popBackStack();
    }
}
