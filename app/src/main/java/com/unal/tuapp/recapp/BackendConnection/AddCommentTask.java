package com.unal.tuapp.recapp.BackendConnection;

import android.os.AsyncTask;

import com.unal.tuapp.recapp.backend.myApi.model.StringResult;

import java.io.IOException;

/**
 * Created by yeisondavid on 08/11/2015.
 */
public class AddCommentTask extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {
        String result = null;
        try {
            StringResult query = getAPI.getMyAPI().addComment(params[0], Long.parseLong(params[1]), params[2]).execute();
            result = (String)query.get("result");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
