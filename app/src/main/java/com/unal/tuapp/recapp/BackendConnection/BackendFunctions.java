package com.unal.tuapp.recapp.BackendConnection;

import com.unal.tuapp.recapp.backend.myApi.MyApi;

/**
 * Created by yeisondavid on 25/10/2015.
 */
public class BackendFunctions {
    public static MyApi myApi = getAPI.getMyAPI();
    public static String addUser(String name, String lastName, String email, String profileImage)
    {
        return "hola";
    }
}
