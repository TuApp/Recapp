package com.unal.tuapp.recapp;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by andresgutierrez on 8/11/15.
 */
public class Utility {
    public static String getDate(long time){
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        return format.format(date);
    }
}
