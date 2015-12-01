package com.unal.tuapp.recapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.unal.tuapp.recapp.R;
import com.unal.tuapp.recapp.activities.Recapp;

/**
 * Created by andresgutierrez on 11/30/15.
 */
public class ImageFragment  extends Fragment{

    private Button skip;
    private TextView text;
    private ImageView image;


    public static ImageFragment newInstance(int imageId,String text){
        ImageFragment imageFragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putInt("image", imageId);
        args.putString("text", text);
        imageFragment.setArguments(args);

        return imageFragment;
    }
    private View root;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_image,container,false);
        image = (ImageView) root.findViewById(R.id.image_main);
        text = (TextView) root.findViewById(R.id.text_main);
        skip = (Button) root.findViewById(R.id.button_carrousel);
        int imageId = getArguments().getInt("image");
        String description = getArguments().getString("text");
        image.setImageResource(imageId);
        text.setText(description);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Recapp.carrousel.setCurrentItem(3);
            }
        });
        return root;
    }
}
