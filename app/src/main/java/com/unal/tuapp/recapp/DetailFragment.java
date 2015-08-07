package com.unal.tuapp.recapp;

import android.graphics.BitmapFactory;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.CursorLoader;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.unal.tuapp.recapp.data.Comment;
import com.unal.tuapp.recapp.data.Place;
import com.unal.tuapp.recapp.data.PlaceImages;
import com.unal.tuapp.recapp.data.RecappContract;
import com.unal.tuapp.recapp.data.User;

import java.util.ArrayList;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private User user;
    private long id;
    private ImageView favorite;
    private Button share;
    private View root;
    private NestedScrollView nestedScrollView;
    private RecyclerView comment;
    private RecycleCommentsAdapter commentsAdapter;
    private Button commentButton;
    private EditText commentText;
    private RatingBar commentRating;
    private Place place;
    private PlaceImages placeImages;

    private TextView card_title;
    private TextView card_description;
    private TextView card_address;
    private ImageView card_image;


    public int count;
    public static final int COMMENT_BY_PLACE = 1;
    public static final int PLACE = 2;
    public static final int PLACE_IMAGES = 3;
    public static final int USER_BY_PLACE = 4;


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_detail, container, false);
        count = 0;
        Bundle extras = getActivity().getIntent().getExtras();
        if(extras!=null){
            id = extras.getLong("id");
            user = new User();
            long userId = ((User)extras.getParcelable("user")).getId();
            user.setId((userId));
            user.setProfileImage(((User) extras.getParcelable("user")).getProfileImage());
        }
        favorite = (ImageView) root.findViewById(R.id.card_favorite);
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (count % 2 == 0) {
                    favorite.setImageResource(R.drawable.ic_favorites_color);
                    ContentValues userByPlace = new ContentValues();
                    userByPlace.put(RecappContract.UserByPlaceEntry.COLUMN_USER_KEY,user.getId());
                    userByPlace.put(RecappContract.UserByPlaceEntry.COLUMN_PLACE_KEY,id);
                    getActivity().getContentResolver().insert(
                            RecappContract.UserByPlaceEntry.CONTENT_URI,
                            userByPlace
                    );
                } else {
                    favorite.setImageResource(R.drawable.ic_favorites);
                    String selection = RecappContract.UserByPlaceEntry.COLUMN_USER_KEY+" = ? AND " +
                            RecappContract.UserByPlaceEntry.COLUMN_PLACE_KEY+" = ? ";
                    getActivity().getContentResolver().delete(
                            RecappContract.UserByPlaceEntry.CONTENT_URI,
                            selection,
                            new String[]{"" + user.getId(), "" + id}
                    );
                }
                favorite.invalidate();
            }
        });

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
        card_title = (TextView) root.findViewById(R.id.card_title);
        card_description = (TextView) root.findViewById(R.id.card_description);
        card_address = (TextView) root.findViewById(R.id.card_address);
        card_image = (ImageView) root.findViewById(R.id.card_image);
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
        ArrayList<Comment> comments = new ArrayList<>();
        commentsAdapter = new RecycleCommentsAdapter(comments);
        comment.setAdapter(commentsAdapter);

        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!commentText.getText().toString().equals("")) {
                    ContentValues values = new ContentValues();
                    values.put(RecappContract.CommentEntry.COLUMN_DESCRIPTION, commentText.getText().toString());
                    values.put(RecappContract.CommentEntry.COLUMN_RATING, commentRating.getRating());
                    values.put(RecappContract.CommentEntry.COLUMN_IMAGE, user.getProfileImage());
                    values.put(RecappContract.CommentEntry.COLUMN_DATE,System.currentTimeMillis());
                    values.put(RecappContract.CommentEntry.COLUMN_USER_KEY, user.getId());
                    values.put(RecappContract.CommentEntry.COLUMN_PLACE_KEY, id);
                    getActivity().getContentResolver().insert(
                            RecappContract.CommentEntry.CONTENT_URI,
                            values

                    );
                    //We pass the groupBy  in the argument the selection
                    Cursor cursorRating = getActivity().getContentResolver().query(
                            RecappContract.CommentEntry.buildCommentPlaceUri(id),
                            new String[]{"AVG(" + RecappContract.CommentEntry.TABLE_NAME + "." + RecappContract.CommentEntry.COLUMN_RATING + ")"},
                            RecappContract.CommentEntry.TABLE_NAME+"."+ RecappContract.CommentEntry.COLUMN_RATING,
                            null,
                            null
                    );
                    double rating = 0;
                    if (cursorRating.moveToFirst()) {
                        rating =cursorRating.getDouble(0);
                    }
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    ContentValues values1 = new ContentValues();
                    values1.put(RecappContract.PlaceEntry.COLUMN_RATING, rating);
                    getActivity().getContentResolver().update(
                            RecappContract.PlaceEntry.CONTENT_URI,
                            values1,
                            RecappContract.PlaceEntry._ID + " = ? ",
                            new String[]{"" + id}
                    );



                    commentText.setText("");
                    commentRating.setRating(0);
                    comment.scrollToPosition(0);

                }
            }
        });

        return root;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        getLoaderManager().initLoader(COMMENT_BY_PLACE,null,this);
        getLoaderManager().initLoader(PLACE,null,this);
        getLoaderManager().initLoader(USER_BY_PLACE,null,this);
        getLoaderManager().initLoader(PLACE_IMAGES,null,this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id){
            case COMMENT_BY_PLACE:
                String sortOrderComment = RecappContract.CommentEntry.COLUMN_DATE + " DESC ";
                return  new CursorLoader(
                    getActivity(),
                    RecappContract.CommentEntry.buildCommentPlaceUri(this.id),
                    new String[]{RecappContract.CommentEntry.TABLE_NAME + "." + RecappContract.CommentEntry.COLUMN_DESCRIPTION,
                            RecappContract.CommentEntry.TABLE_NAME + "." + RecappContract.CommentEntry.COLUMN_RATING,
                            RecappContract.CommentEntry.TABLE_NAME+"."+ RecappContract.CommentEntry.COLUMN_IMAGE},
                    null,
                    null,
                    sortOrderComment

                );
            case PLACE:
                return  new CursorLoader(
                        getActivity(),
                        RecappContract.PlaceEntry.buildPlaceUri(this.id),
                        null,
                        null,
                        null,
                        null

                );
            case PLACE_IMAGES:
                String sortOrder = RecappContract.PlaceImageEntry.COLUMN_WORTH + " DESC ";
                return  new CursorLoader(
                        getActivity(),
                        RecappContract.PlaceImageEntry.buildPlaceImagePlaceUri(this.id),
                        new String[]{RecappContract.PlaceImageEntry.TABLE_NAME+"."+
                                RecappContract.PlaceImageEntry.COLUMN_IMAGE},
                        null,
                        null,
                        sortOrder
                );
            case USER_BY_PLACE:
                String selection = RecappContract.UserByPlaceEntry.COLUMN_USER_KEY+" = ? AND "+
                        RecappContract.UserByPlaceEntry.COLUMN_PLACE_KEY +" = ? ";
                return new CursorLoader(
                        getActivity(),
                        RecappContract.UserByPlaceEntry.CONTENT_URI,
                        new String[]{RecappContract.UserByPlaceEntry._ID},
                        selection,
                        new String[]{""+user.getId(),""+this.id},
                        null
                );
            default:
                return null;
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()){
            case COMMENT_BY_PLACE:
                List<Comment> comments = Comment.allComment(data);
                commentsAdapter.swapData(comments);
                commentsAdapter.setCommentCursor(data);
                break;
            case PLACE:
                if(data.moveToFirst()) {
                    place = new Place(data.getString(data.getColumnIndexOrThrow(RecappContract.PlaceEntry.COLUMN_ADDRESS)),
                            data.getString(data.getColumnIndexOrThrow(RecappContract.PlaceEntry.COLUMN_DESCRIPTION)),
                            data.getLong(data.getColumnIndexOrThrow(RecappContract.PlaceEntry._ID)),
                            data.getDouble(data.getColumnIndexOrThrow(RecappContract.PlaceEntry.COLUMN_LOG)),
                            data.getDouble(data.getColumnIndexOrThrow(RecappContract.PlaceEntry.COLUMN_LAT)),
                            data.getString(data.getColumnIndexOrThrow(RecappContract.PlaceEntry.COLUMN_NAME)),
                            data.getDouble(data.getColumnIndexOrThrow(RecappContract.PlaceEntry.COLUMN_RATING)),
                            data.getBlob(data.getColumnIndexOrThrow(RecappContract.PlaceEntry.COLUMN_IMAGE_FAVORITE)));
                    card_title.setText(place.getName());
                    card_description.setText(place.getDescription());
                    card_address.setText(place.getAddress());



                }
                break;
            case PLACE_IMAGES:
                placeImages = new PlaceImages();
                placeImages.setImages(data);
                List<byte[]> images = placeImages.getImages();
                if(images.size()>0){
                    card_image.setImageBitmap(BitmapFactory.decodeByteArray(
                            images.get(0),0,images.get(0).length
                    ));
                }else if(place!=null){
                    card_image.setImageBitmap(BitmapFactory.decodeByteArray(
                            place.getImageFavorite(),0,place.getImageFavorite().length
                    ));
                }
                break;
            case USER_BY_PLACE:
                if(data.moveToFirst()){
                    count=1;
                    favorite.setImageResource(R.drawable.ic_favorites_color);
                }else{
                    count=0;
                    favorite.setImageResource(R.drawable.ic_favorites);
                }
                favorite.invalidate();

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        switch (loader.getId()){
            case COMMENT_BY_PLACE:
                List<Comment> comments = new ArrayList<>();
                commentsAdapter.swapData(comments);
                commentsAdapter.closeCursor();
                break;


        }

    }
}
