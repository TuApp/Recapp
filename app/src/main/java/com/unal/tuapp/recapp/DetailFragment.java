package com.unal.tuapp.recapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
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
import android.util.Log;
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


import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareOpenGraphAction;
import com.facebook.share.model.ShareOpenGraphContent;
import com.facebook.share.model.ShareOpenGraphObject;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.plus.PlusShare;
import com.unal.tuapp.recapp.data.Comment;
import com.unal.tuapp.recapp.data.Place;
import com.unal.tuapp.recapp.data.RecappContract;
import com.unal.tuapp.recapp.data.User;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private User user;
    private long id;
    private ImageView favorite;
    private Button share;
    private Button shareFacebook;
    private View root;
    private NestedScrollView nestedScrollView;
    private RecyclerView comment;
    private RecycleCommentsAdapter commentsAdapter;
    private Button commentButton;
    private EditText commentText;
    private RatingBar commentRating;
    private Place place;

    private Cursor favoriteCursor;
    private Cursor placeCursor;
    private Cursor ratingCursor;

    private TextView card_title;
    private TextView card_description;
    private TextView card_address;
    private ImageView card_image;


    public int count;
    public static final int COMMENT_BY_PLACE = 1;
    public static final int PLACE = 2;
    public static final int RATING = 3;
    public static final int USER_BY_PLACE = 4;

    private CallbackManager callbackManager;
    private ShareDialog shareDialog;

    public static onPlaceImagesListener mOnPlaceImagesListener;


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_detail, container, false);
        count = 0;
        FacebookSdk.sdkInitialize(getActivity());
        initCallbackManager();
        shareDialog = new ShareDialog(this);

        Bundle extras = getActivity().getIntent().getExtras();
        if(extras!=null){
            id = extras.getLong("id");
            user = extras.getParcelable("user");
        }
        favorite = (ImageView) root.findViewById(R.id.card_favorite);
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (count == 0) {
                    //favorite.setImageResource(R.drawable.ic_favorites_color);
                    ContentValues userByPlace = new ContentValues();
                    userByPlace.put(RecappContract.UserByPlaceEntry.COLUMN_USER_KEY,user.getId());
                    userByPlace.put(RecappContract.UserByPlaceEntry.COLUMN_PLACE_KEY,id);
                    getActivity().getContentResolver().insert(
                            RecappContract.UserByPlaceEntry.CONTENT_URI,
                            userByPlace
                    );
                } else {
                    //favorite.setImageResource(R.drawable.ic_favorites);
                    String selection = RecappContract.UserByPlaceEntry.COLUMN_USER_KEY+" = ? AND " +
                            RecappContract.UserByPlaceEntry.COLUMN_PLACE_KEY+" = ? ";
                    getActivity().getContentResolver().delete(
                            RecappContract.UserByPlaceEntry.CONTENT_URI,
                            selection,
                            new String[]{"" + user.getId(), "" + id}
                    );
                }
                //favorite.invalidate();
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
        card_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mOnPlaceImagesListener.onPlaceImages(view, 0);


            }
        });
        share = (Button) root.findViewById(R.id.card_share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlusShare.Builder builder = new PlusShare.Builder(getActivity());

                builder.addCallToAction("VIEW", Uri.parse(place.getWeb()), "/place/" + place.getId())
                        .setContentUrl(Uri.parse(place.getWeb()))
                        .setContentDeepLinkId("/place/" + place.getId(),null,null,null)
                        .setText("This is a excellent place to recycle \n "+
                        "Name: "+place.getName()+"\n"+
                        "Address: "+place.getAddress()+"\n"+
                        "#recycle");


                startActivityForResult(builder.getIntent(), 0);

            }
        });
        shareFacebook = (Button) root.findViewById(R.id.card_share_facebook);
        shareFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(AccessToken.getCurrentAccessToken()!=null) { //If the user is logged we should log him/her out
                    sharePlace();
                }else {
                    LoginManager.getInstance().logInWithPublishPermissions(DetailFragment.this, Arrays.asList("publish_actions"));
                }


            }
        });
        comment = (RecyclerView) root.findViewById(R.id.comment_list);
        List<Comment> comments = new ArrayList<>();


        LinearLayoutManager linearLayout = new LinearLayoutManager(getActivity());
        comment.setLayoutManager(linearLayout);
        commentsAdapter = new RecycleCommentsAdapter(comments);
        comment.setAdapter(commentsAdapter);

        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!(commentText.getText().toString()).equals("")) {
                    ContentValues values = new ContentValues();
                    values.put(RecappContract.CommentEntry.COLUMN_DESCRIPTION, commentText.getText().toString());
                    values.put(RecappContract.CommentEntry.COLUMN_RATING, commentRating.getRating());
                    values.put(RecappContract.CommentEntry.COLUMN_DATE, System.currentTimeMillis());
                    values.put(RecappContract.CommentEntry.COLUMN_USER_KEY, user.getId());
                    values.put(RecappContract.CommentEntry.COLUMN_PLACE_KEY, id);
                    getActivity().getContentResolver().insert(
                            RecappContract.CommentEntry.CONTENT_URI,
                            values
                    );


                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
        if(getLoaderManager().getLoader(COMMENT_BY_PLACE)==null) {
            getLoaderManager().initLoader(COMMENT_BY_PLACE, null, this);
        }else{
            getLoaderManager().restartLoader(COMMENT_BY_PLACE, null, this);
        }
        if(getLoaderManager().getLoader(PLACE)==null) {
            getLoaderManager().initLoader(PLACE, null, this);
        }else{
            getLoaderManager().restartLoader(PLACE, null, this);
        }
        if (getLoaderManager().getLoader(USER_BY_PLACE) == null) {
            getLoaderManager().initLoader(USER_BY_PLACE,null,this);
        }else{
            getLoaderManager().restartLoader(USER_BY_PLACE,null,this);
        }
        if(getLoaderManager().getLoader(RATING)==null) {
            getLoaderManager().initLoader(RATING, null, this);
        }else{
            getLoaderManager().restartLoader(RATING, null, this);
        }

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id){
            case COMMENT_BY_PLACE:
                String sortOrderComment = RecappContract.CommentEntry.COLUMN_DATE + " DESC ";
                return  new CursorLoader(
                    getActivity(),
                    RecappContract.CommentEntry.buildCommentPlaceUserUri(this.id),
                        new String[]{RecappContract.CommentEntry.TABLE_NAME+"."+RecappContract.CommentEntry._ID,
                                RecappContract.CommentEntry.TABLE_NAME+"."+ RecappContract.CommentEntry.COLUMN_DATE,
                                RecappContract.CommentEntry.TABLE_NAME + "." + RecappContract.CommentEntry.COLUMN_DESCRIPTION,
                                RecappContract.CommentEntry.TABLE_NAME + "." + RecappContract.CommentEntry.COLUMN_RATING,
                                RecappContract.UserEntry.COLUMN_USER_IMAGE,
                                RecappContract.CommentEntry.COLUMN_PLACE_KEY},
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
            case RATING:
                return  new CursorLoader(
                        getActivity(),
                        RecappContract.CommentEntry.buildCommentPlaceUri(this.id),
                        new String[]{"AVG(" + RecappContract.CommentEntry.TABLE_NAME + "." + RecappContract.CommentEntry.COLUMN_RATING + ")"},
                        RecappContract.PlaceEntry.TABLE_NAME+"."+ RecappContract.PlaceEntry._ID,
                        null,
                        null
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
                //comment.setAdapter(commentsAdapter);
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
                            data.getBlob(data.getColumnIndexOrThrow(RecappContract.PlaceEntry.COLUMN_IMAGE_FAVORITE)),
                            data.getString(data.getColumnIndexOrThrow(RecappContract.PlaceEntry.COLUMN_WEB)));
                    card_title.setText(place.getName());
                    card_description.setText(place.getDescription());
                    card_address.setText(place.getAddress());
                    card_image.setImageBitmap(BitmapFactory.decodeByteArray(
                            place.getImageFavorite(), 0, place.getImageFavorite().length
                    ));

                    placeCursor = data;



                }
                break;
            case RATING:
                double rating = 0;
                if (data.moveToFirst()) {
                    rating =data.getDouble(0);
                }
                ContentValues values = new ContentValues();
                values.put(RecappContract.PlaceEntry.COLUMN_RATING, rating);
                getActivity().getContentResolver().update(
                        RecappContract.PlaceEntry.CONTENT_URI,
                        values,
                        RecappContract.PlaceEntry._ID + " = ? ",
                        new String[]{"" + this.id}
                );
                ratingCursor = data;
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
                favoriteCursor = data;

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()){
            case COMMENT_BY_PLACE:
                commentsAdapter.closeCursor();
                break;
            case PLACE:
                placeCursor.close();
                break;
            case RATING:
                ratingCursor.close();
                break;
            case USER_BY_PLACE:
                favoriteCursor.close();
                break;

        }


    }
    public void initCallbackManager(){

        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                sharePlace();
            }

            @Override
            public void onCancel() {
                Log.e("algo", "cancel");
            }

            @Override
            public void onError(FacebookException e) {
                Log.e("algo", e.toString());
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    public void sharePlace(){

        ShareOpenGraphObject object = new ShareOpenGraphObject.Builder()
                .putString("og:type", "unalrecycle:recycle")
                .putString("og:title", "Name: " + place.getName())
                .putString("og:description","Description: " + place.getDescription())
                .putString("og:url", place.getWeb())
                .putString("place:location:latitude",""+place.getLat())
                .putString("place:location:longitude",""+place.getLog())
                .putString("unalrecyce:hastag","#recycle")
                .putString("og:image","https://scontent-mia1-1.xx.fbcdn.net/hphotos-xtp1/v/t34.0-12/11922061_10206791589044848_1016804803_n.jpg?oh=e3900090e3ada42e02a62eadff90c700&oe=55FA5CE6")
                .build();

        ShareOpenGraphAction action = new ShareOpenGraphAction.Builder()
                .setActionType("unalrecycle:be")
                .putObject("unalrecycle:recycle", object)
                .build();

        ShareOpenGraphContent content = new ShareOpenGraphContent.Builder()
                .setPreviewPropertyName("unalrecycle:recycle")
                .setAction(action)
                .build();


        if(ShareDialog.canShow(ShareOpenGraphContent.class)){
            shareDialog.show(content);
        }else {
            ShareApi.share(content, new FacebookCallback<Sharer.Result>() {
                @Override
                public void onSuccess(Sharer.Result result) {
                    Snackbar.make(root.findViewById(R.id.fragment_detail), "successful", Snackbar.LENGTH_SHORT).show();
                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onError(FacebookException e) {

                }
            });
        }
    }

    public interface onPlaceImagesListener{
        void onPlaceImages(View view,long position);
    }


    public void setOnPlaceImagesListener(final onPlaceImagesListener mOnPlaceListener){
        this.mOnPlaceImagesListener= mOnPlaceListener;
    }

}
