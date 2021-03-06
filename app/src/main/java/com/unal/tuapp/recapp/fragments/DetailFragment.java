package com.unal.tuapp.recapp.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.plus.PlusShare;
import com.unal.tuapp.recapp.R;
import com.unal.tuapp.recapp.adapters.RecycleCommentsAdapter;
import com.unal.tuapp.recapp.backend.model.userByPlaceApi.model.UserByPlace;
import com.unal.tuapp.recapp.data.Comment;
import com.unal.tuapp.recapp.data.Place;
import com.unal.tuapp.recapp.data.RecappContract;
import com.unal.tuapp.recapp.data.User;
import com.unal.tuapp.recapp.others.Utility;
import com.unal.tuapp.recapp.servicesAndAsyncTasks.CommentEndPoint;
import com.unal.tuapp.recapp.servicesAndAsyncTasks.PlaceEndPoint;
import com.unal.tuapp.recapp.servicesAndAsyncTasks.UserByPlaceEndPoint;

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
    private ImageButton share;
    private ImageButton shareFacebook;
    private View root;
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
                    UserByPlace userByPlaceBackend = new UserByPlace();
                    userByPlaceBackend.setUserId(user.getId());
                    userByPlaceBackend.setPlaceId(id);
                    userByPlaceBackend.setId(System.currentTimeMillis());
                    //favorite.setImageResource(R.drawable.ic_favorites_color);
                    ContentValues userByPlace = new ContentValues();
                    userByPlace.put(RecappContract.UserByPlaceEntry.COLUMN_USER_KEY, user.getId());
                    userByPlace.put(RecappContract.UserByPlaceEntry.COLUMN_PLACE_KEY, id);
                    userByPlace.put(RecappContract.UserByPlaceEntry._ID, userByPlaceBackend.getId());
                    getActivity().getContentResolver().insert(
                            RecappContract.UserByPlaceEntry.CONTENT_URI,
                            userByPlace
                    );
                    if(Utility.isNetworkAvailable(getContext())) {
                        Pair<Context, Pair<UserByPlace, String>> pairUserByPlace = new Pair<>(getContext(), new Pair<>(userByPlaceBackend, "addUserByPlace"));
                        new UserByPlaceEndPoint().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, pairUserByPlace);
                    }

                } else {
                    //favorite.setImageResource(R.drawable.ic_favorites);
                    if(Utility.isNetworkAvailable(getContext())) {
                        UserByPlace userByPlaceBackend = new UserByPlace();
                        String selection = RecappContract.UserByPlaceEntry.COLUMN_USER_KEY + " = ? AND " +
                                RecappContract.UserByPlaceEntry.COLUMN_PLACE_KEY + " = ? ";

                        Cursor cursor = getActivity().getContentResolver().query(
                                RecappContract.UserByPlaceEntry.CONTENT_URI,
                                new String[]{RecappContract.UserByPlaceEntry._ID},
                                selection,
                                new String[]{"" + user.getId(), "" + id},
                                null
                        );
                        if (cursor.moveToFirst()) {
                            userByPlaceBackend.setId(cursor.getLong(0));
                        }
                        getActivity().getContentResolver().delete(
                                RecappContract.UserByPlaceEntry.CONTENT_URI,
                                selection,
                                new String[]{"" + user.getId(), "" + id}
                        );
                        Pair<Context, Pair<UserByPlace, String>> pairUserByPlace = new Pair<>(getContext(), new Pair<>(userByPlaceBackend, "deleteUserByPlace"));
                        new UserByPlaceEndPoint().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, pairUserByPlace);
                    }else{
                        new AlertDialog.Builder(getContext())
                                .setCancelable(true)
                                .setTitle(getResources().getString(R.string.internet))
                                .setMessage(getResources().getString(R.string.need_internet))
                                .show();
                    }


                }
                //favorite.invalidate();
            }
        });


        commentText = (EditText) root.findViewById(R.id.comment_text);
        commentRating = (RatingBar) root.findViewById(R.id.comment_rating);
        commentButton = (Button) root.findViewById(R.id.comment_button);

        /*nestedScrollView = (NestedScrollView) root.findViewById(R.id.card_detail_scroll);
        nestedScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_container,DetailFragment.this)
                        .commit();
            }
        });*/
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
        share = (ImageButton) root.findViewById(R.id.card_share);
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
        shareFacebook = (ImageButton) root.findViewById(R.id.card_share_facebook);
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
        commentsAdapter = new RecycleCommentsAdapter(comments,getContext());
        comment.setAdapter(commentsAdapter);

        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!(commentText.getText().toString()).equals("")) {
                    com.unal.tuapp.recapp.backend.model.commentApi.model.Comment commentApi = new com.unal.tuapp.recapp.backend.model.commentApi.model.Comment();
                    commentApi.setId(System.currentTimeMillis());
                    commentApi.setDate(System.currentTimeMillis());
                    commentApi.setDescription(commentText.getText().toString());
                    commentApi.setRating(commentRating.getRating());
                    commentApi.setPlaceId(id);
                    commentApi.setUserId(user.getId());
                    ContentValues values = new ContentValues();
                    values.put(RecappContract.CommentEntry.COLUMN_DESCRIPTION, commentApi.getDescription());
                    values.put(RecappContract.CommentEntry.COLUMN_RATING, commentApi.getRating());
                    values.put(RecappContract.CommentEntry.COLUMN_DATE, commentApi.getDate());
                    values.put(RecappContract.CommentEntry.COLUMN_USER_KEY, commentApi.getUserId());
                    values.put(RecappContract.CommentEntry.COLUMN_PLACE_KEY, commentApi.getPlaceId());
                    values.put(RecappContract.CommentEntry._ID, commentApi.getId());
                    getActivity().getContentResolver().insert(
                            RecappContract.CommentEntry.CONTENT_URI,
                            values
                    );
                    if(Utility.isNetworkAvailable(getContext())) {
                        Pair<Pair<Context, Pair<Long, Long>>, Pair<com.unal.tuapp.recapp.backend.model.commentApi.model.Comment, String>> pair =
                                new Pair<>(new Pair<>(getContext(), new Pair<>(user.getId(), id)), new Pair<>(commentApi, "addComment"));
                        new CommentEndPoint().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, pair);
                    }

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
                                RecappContract.CommentEntry.COLUMN_PLACE_KEY,
                                RecappContract.CommentEntry.COLUMN_USER_KEY},
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
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 3;
                    card_image.setImageBitmap(BitmapFactory.decodeByteArray(
                            place.getImageFavorite(), 0, place.getImageFavorite().length,options
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
                if(Utility.isNetworkAvailable(getContext())) {
                    com.unal.tuapp.recapp.backend.model.placeApi.model.Place place = new com.unal.tuapp.recapp.backend.model.placeApi.model.Place();
                    place.setId(id);
                    place.setRating((float) rating);
                    Pair<Context, Pair<com.unal.tuapp.recapp.backend.model.placeApi.model.Place, String>> pairPlace = new Pair<>(getContext(), new Pair<>(place, "updatePlaceRating"));
                    new PlaceEndPoint().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, pairPlace);
                }

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
            }

            @Override
            public void onError(FacebookException e) {
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    public void sharePlace(){

        if(!place.getWeb().startsWith("http://") || !place.getWeb().startsWith("https://")){
            place.setWeb("http://"+place.getWeb());
        }
        ShareOpenGraphObject object = new ShareOpenGraphObject.Builder()
                .putString("og:type", "unalrecycle:recycle")
                .putString("og:title", "Name: " + place.getName())
                .putString("og:description","Description: " + place.getDescription())
                .putString("og:url", place.getWeb())
                .putString("place:location:latitude",""+place.getLat())
                .putString("place:location:longitude", "" + place.getLog())
                .putString("unalrecyce:hastag","#recycle")
                .putString("og:image","https://scontent-mia1-1.xx.fbcdn.net/hphotos-xtf1/v/t1.0-9/12347998_10207330643760879_1614176967930939591_n.jpg?oh=498fbe834b4a7de3adf9d7d5f2cffc3f&oe=571C63E5" )
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
                    Log.e("algo","algo");

                }

                @Override
                public void onError(FacebookException e) {
                    Log.e("algo","algo");
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
