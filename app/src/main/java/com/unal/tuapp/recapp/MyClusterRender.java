package com.unal.tuapp.recapp;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.unal.tuapp.recapp.data.Place;

/**
 * Created by andresgutierrez on 8/15/15.
 */
public class MyClusterRender extends DefaultClusterRenderer<Place> {
    public MyClusterRender(Context context, GoogleMap map, ClusterManager<Place> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<Place> cluster) {
        return cluster.getSize()>1;//Always render like cluster
    }
}
