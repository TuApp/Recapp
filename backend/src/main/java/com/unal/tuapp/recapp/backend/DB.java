package com.unal.tuapp.recapp.backend;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Created by yeisondavid on 30/09/2015.
 */
public class DB {
    public static boolean state = false;
    public static Map<String, UserDB> users;
    public static Map<Long , PlaceDB> places;
    public static Map<Long, CommentDB> comments;
    public static Map<Long, EventDB> events;
}
