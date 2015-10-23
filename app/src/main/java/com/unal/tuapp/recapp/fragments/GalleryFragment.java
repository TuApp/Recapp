package com.unal.tuapp.recapp.fragments;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.unal.tuapp.recapp.R;
import com.unal.tuapp.recapp.adapters.RecyclePlaceImagesAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class GalleryFragment extends Fragment {

    private static RecyclerView recyclerView;
    private static ViewPager viewPager;
    private static RecyclePlaceImagesAdapter recyclePlaceImagesAdapter;
    private View root;
    public static onPlaceImagesListener mOnPlaceImagesListener;

    private LinearLayoutManager layoutManager;
    public static  final int NON_SELECTED_BORDER = Color.BLACK;
    public static final int SELECTED_BORDER = Color.argb(100,121,134,203);


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        root = inflater.inflate(R.layout.fragment_gallery,container,false);
        recyclerView = (RecyclerView) root.findViewById(R.id.places_images_recycle_view);
        //setHasOptionsMenu(true);

        //For now we can use this line to optimize the recycleview because we know that the size of the list won't change
        //recyclerView.setHasFixedSize(true);

        List<byte[]> placesImages = new ArrayList<>();

        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(layoutManager);
        recyclePlaceImagesAdapter = new RecyclePlaceImagesAdapter(placesImages);
        recyclePlaceImagesAdapter.setOnItemClickListener(new RecyclePlaceImagesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, long position) {
                viewPager.setCurrentItem((int) position);
                if (mOnPlaceImagesListener != null) {
                    mOnPlaceImagesListener.onPlaceImage(view, position);
                }

            }
        });

        recyclerView.setAdapter(recyclePlaceImagesAdapter);

        viewPager = (ViewPager) root.findViewById(R.id.slider_images);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setSelectedImage(position);
                int size = layoutManager.getWidth();
                layoutManager.scrollToPositionWithOffset(position,size/2);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return root;
    }

    public void setSelectedImage(int currentPosition){
        RecyclePlaceImagesAdapter adapter = (RecyclePlaceImagesAdapter)recyclerView.getAdapter();
        adapter.setCurrentSelectedPositionImage(currentPosition);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

    }

    public interface onPlaceImagesListener {
        void onPlaceImage(View view, long position);
    }


    public void setOnPlaceListener(final onPlaceImagesListener mOnPlaceImagesListener){
        this.mOnPlaceImagesListener = mOnPlaceImagesListener;
    }

    public void setData(List<byte[]> places,Cursor cursor){
        recyclePlaceImagesAdapter.swapData(places);
        recyclePlaceImagesAdapter.setPlaceImagesCursor(cursor);

        // Pass results to ViewPagerAdapter Class
        PlaceImagesViewPagerAdapter adapter = new PlaceImagesViewPagerAdapter(getActivity(), places);
        // Binds the Adapter to the ViewPager
        viewPager.setAdapter(adapter);
    }
    public void closeData(){
        recyclePlaceImagesAdapter.closeCursor();
    }

    static class PlaceImagesViewPagerAdapter extends PagerAdapter{
        Context context;
        LayoutInflater inflater;
        List<byte[]> placeImages;

        public PlaceImagesViewPagerAdapter(Context context, List<byte[]> placeImages) {
            this.context = context;
            this.placeImages = placeImages;
        }

        @Override
        public int getCount() {
            return placeImages.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((ImageView) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            ImageView imageView;

            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemView = inflater.inflate(R.layout.place_image_item, container,
                    false);

            // Locate the ImageView in viewpager_item.xml
            imageView = (ImageView) itemView.findViewById(R.id.place_image);
            // Capture position and set to the ImageView
            byte[] image = placeImages.get(position);
            Bitmap bitmap = BitmapFactory.decodeByteArray(image,0,image.length);
            imageView.setImageBitmap(bitmap);

            // Add viewpager_item.xml to ViewPager
            ((ViewPager) container).addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // Remove viewpager_item.xml from ViewPager
            ((ViewPager) container).removeView((ImageView) object);

        }

    }

}
