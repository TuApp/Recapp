package com.unal.tuapp.recapp;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.unal.tuapp.recapp.data.Place;


/**
 * Created by andresgutierrez on 8/15/15.
 */
public class MyClusterRender extends DefaultClusterRenderer<Place> {
    Context context;
    public MyClusterRender(Context context, GoogleMap map, ClusterManager<Place> clusterManager) {
        super(context, map, clusterManager);
        this.context = context;
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<Place> cluster) {
        return cluster.getSize()>1;//Always render like cluster
    }

    @Override
    protected void onBeforeClusterItemRendered(Place item, MarkerOptions markerOptions) {
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation((float) item.getRating()/3);
        ColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.marker);
        Bitmap scaled = Bitmap.createScaledBitmap(bitmap,45,80,false);

        Paint paint = new Paint();
        paint.setColorFilter(colorFilter);
        Canvas canvas = new Canvas(scaled);
        canvas.drawBitmap(scaled,0,0,paint);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(scaled));

    }
}
