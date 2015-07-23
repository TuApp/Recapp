package com.unal.tuapp.recapp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment {
    private ImageView favorite;
    private Button share;
    private View root;
    private NestedScrollView nestedScrollView;
    private RecyclerView comment;
    private Button commentButton;
    private EditText commentText;
    private RatingBar commentRating;
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_detail, container, false);
        favorite = (ImageView) root.findViewById(R.id.card_favorite);
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drawable drawable = favorite.getDrawable();
                if (drawable.getConstantState().equals(getActivity().getResources()
                        .getDrawable(R.drawable.ic_favorites).getConstantState())) {
                    favorite.setImageResource(R.drawable.ic_favorites_color);

                } else {
                    favorite.setImageResource(R.drawable.ic_favorites);
                }
                favorite.invalidate();
            }
        });
        final Comment comment1 = new Comment();
        comment1.setRating(2.5f);
        comment1.setComment("algo");
        Comment comment2 = new Comment();
        comment2.setRating(3);
        comment2.setComment("algo1");
        Comment comment3 = new Comment();
        comment3.setRating(3);
        comment3.setComment("algo2");
        Comment [] commentsArray ={comment1,comment1,comment2};
        final ArrayList<Comment> comments = new ArrayList<>(Arrays.asList(commentsArray));
        commentText = (EditText) root.findViewById(R.id.comment_text);

        commentRating = (RatingBar) root.findViewById(R.id.comment_rating);
        commentButton = (Button) root.findViewById(R.id.comment_button);

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
                Snackbar.make(getActivity().findViewById(R.id.detail_coordination), "algo", Snackbar.LENGTH_SHORT).show();
            }
        });
        comment = (RecyclerView) root.findViewById(R.id.comment_list);



        LinearLayoutManager linearLayout = new LinearLayoutManager(getActivity());
        comment.setLayoutManager(linearLayout);
        final RecycleCommentsAdapter commentsAdapter = new RecycleCommentsAdapter(comments);
        comment.setAdapter(commentsAdapter);

        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("algo", commentText.getText().toString());
                Log.e("algo", "" + commentRating.getRating());
                if (!commentText.getText().toString().equals("")) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    Comment commentTemp = new Comment();
                    commentTemp.setRating(commentRating.getRating());
                    commentTemp.setComment(commentText.getText().toString());
                    comments.add(0, commentTemp);
                    commentText.setText("");
                    commentRating.setRating(0);
                    commentsAdapter.notifyItemInserted(0);
                    comment.scrollToPosition(0);

                }
            }
        });

        return root;
    }


}
