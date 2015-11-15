package com.unal.tuapp.recapp.backend;

import java.util.Hashtable;
import java.util.Map;
import java.util.TreeSet;

/**
 * Created by yeisondavid on 07/10/2015.
 */
public class StartDB {
    public static void startDB()
    {

        //PlaceDB place1 = new PlaceDB("unicentro");
        //PlaceDB place2 = new PlaceDB("gran estacion");
        //PlaceDB place3 = new PlaceDB("salitre");
        PlaceDB place1 = new PlaceDB("mi direccion", "mi descripcion", PlaceDB.getnextID(), 0.0, 0.0, "gran estacion", 5.0, new byte[]{1, 0, 1, 0}, "mi pagina web");
        PlaceDB place2 = new PlaceDB("mi direccion", "mi descripcion", PlaceDB.getnextID(), 0.0, 0.0, "salitre", 3.0, new byte[]{1, 0, 1, 0}, "mi pagina web");
        DB.users = new Hashtable<String, UserDB>();
        DB.places = new Hashtable<Long, PlaceDB>();
        DB.comments = new Hashtable<Long, CommentDB>();
        DB.events = new Hashtable<Long, EventDB>();

        DB.places.put(place1.id, place1);
        DB.places.put(place2.id, place2);
        //DB.places.add(place1);
        //DB.places.add(place2);
        //DB.places.add(place3);
    }
}
