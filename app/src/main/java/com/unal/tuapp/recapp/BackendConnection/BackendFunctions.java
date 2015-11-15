package com.unal.tuapp.recapp.BackendConnection;

import android.os.AsyncTask;

import com.unal.tuapp.recapp.backend.myApi.MyApi;
import com.unal.tuapp.recapp.backend.myApi.model.StringResult;

/**
 * Created by yeisondavid on 25/10/2015.
 */
public class BackendFunctions {
    public static String startDB(String password)
    {
        String result = "bad";
        try {
            result =  new StartDBTask().execute(password).get();
        }
        catch(Exception e){}
        return result;
    }
    public static String addUser(String name, String lastName, String email, String profileImage)
    {
        String result = "bad";
        try
        {
            result = new AddUserTask().execute(new String[]{name, lastName, email, profileImage}).get();
        }catch(Exception e){}
        return result;
    }
    public static String addComment(String emailUser, Long idPlace, String content)
    {
        String result = "bad";
        try
        {
            result = new AddCommentTask().execute(new String[]{emailUser, idPlace+"", content}).get();
        }catch(Exception e){}
        return result;
    }
}
