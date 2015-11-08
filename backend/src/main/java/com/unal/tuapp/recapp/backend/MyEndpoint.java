/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package com.unal.tuapp.recapp.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;


import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;


import sun.security.util.BitArray;

/**
 * An endpoint class we are exposing
 */


//TODO
    /*
    delete user
    update user


     */
@Api(name = "myApi", version = "v1", namespace = @ApiNamespace(ownerDomain = "backend.recapp.tuapp.unal.com", ownerName = "backend.recapp.tuapp.unal.com", packagePath = ""))
public class MyEndpoint {



    @ApiMethod(name="getImage")
    public StringResult getImage() throws IOException {
        byte data[] = UtilDB.getImageByteOfPath();
        String result = "";
        for( int i = 0; i < Math.min(data.length, 50); i++ )
            result += data[i];
        return new StringResult(result);
    }

    @ApiMethod(name = "AddComment") //OK1
    public StringResult AddComment(@Named("emailUser") String emailUser,@Named("idPlace") long idPlace, @Named("content") String content) {
        UserDB myUser = DB.users.get(emailUser);
        PlaceDB myPlace = DB.places.get(idPlace);
        CommentDB myComent = new CommentDB(CommentDB.getnextID(), emailUser, idPlace, content);
        DB.comments.put(myComent.id, myComent);
        myUser.myComments.add(myComent.id);
        myPlace.comments.put(myComent.id, myComent);
        return new StringResult(myComent.id+"");
    }
    @ApiMethod(name = "AddUser") //OK1
    public StringResult  AddUser(@Named("name") String name, @Named("lastName") String lastName, @Named("email") String email, @Named("profileImage") String profileImage)
    {
        StringResult result;
        try {
            UserDB myUser = new UserDB(name, lastName, email, profileImage);
            DB.users.put(myUser.email, myUser);
            result = new StringResult("success");
        }catch(Exception e)
        {
            result = new  StringResult("wrong");
        }
        return result;
    }

    @ApiMethod(name = "AddEvent") //OK1
    public StringResult Event(@Named("emailUser") String emailUser, @Named("content") String content)
    {
        UserDB myUser = DB.users.get(emailUser);
        EventDB myEvent = new EventDB(EventDB.getnextID(), emailUser, content);
        DB.events.put(myEvent.id, myEvent);
        myUser.myEvents.add(myEvent.id);
        return new StringResult(myEvent.id+"");
    }

    @ApiMethod(name = "attendToEvent")
    public void attendToEvent(@Named("idUser") long idUser, @Named("idEvent") long idEvent)
    {
        UserDB myUser = DB.users.get(idUser);

    }

    @ApiMethod(name = "undoAttendToEvent")
    public void undoAttendToEvent()
    {

    }

    @ApiMethod(name = "deleteComment") //OK1
    public StringResult deleteComment(@Named("id") long id)
    {
        CommentDB myComment = DB.comments.get(id);
        UserDB myUser = DB.users.get(myComment.idUser);
        PlaceDB myPlace= DB.places.get(myComment.idPlace);
        myUser.myComments.remove(id);
        myPlace.comments.remove(id);
        DB.comments.remove(id);
        return new StringResult("success");
    }

    @ApiMethod(name = "deleteEvent") //OK1 tener cuidado con los id que quedan en los users para los eventos a los que asistiran
    public StringResult deleteEvent(@Named("id") long id)
    {
        EventDB myEvent = DB.events.get(id);
        UserDB myUser = DB.users.get(myEvent.idUser);
        myUser.myEvents.remove(id);
        DB.events.remove(id);
        return new StringResult("success");
    }

    @ApiMethod(name = "updateEvent") //OK1
    public StringResult updateEvent(@Named("id") long id, @Named("content") String content)
    {
        EventDB myEvent= DB.events.get(id);
        myEvent.content = content;
        return new StringResult("success");
    }

    @ApiMethod(name = "updateComment") //OK1
    public ResultUptadeComment updateComment(@Named("id") long id, @Named("content") String content)
    {
        CommentDB myComment = DB.comments.get(id);
        myComment.content = content;
        return new ResultUptadeComment("success");
    }

    @ApiMethod(name = "getEvents") //OK1
    public EventDB[] getEvents()
    {
        Set keys = DB.events.keySet();
        EventDB[] result = new EventDB[keys.size()];
        Iterator iter= keys.iterator();
        long key;
        int index = 0;
        while( iter.hasNext() )
        {
            key = (Long)iter.next();
            result[index++] = DB.events.get(key);
        }
        return result;
    }

    @ApiMethod(name = "getPlaces")
    public PlaceDB[] getPlaces() //OK1 OK2
    {
        Set keys = DB.places.keySet();
        PlaceDB result[] = new PlaceDB[keys.size()];
        Iterator iter = keys.iterator();
        long key;
        int index = 0;
        while( iter.hasNext() )
        {
            key = (Long)iter.next();
            result[index++] = DB.places.get(key);
        }
        return result;
    }

    @ApiMethod(name = "getComments")
    public CommentDB[] getCommentsOfPlace(@Named("idPlace") long idPlace)
    {
        PlaceDB myPlace = DB.places.get(idPlace);
        Set<Long> keys = myPlace.comments.keySet();
        CommentDB result[] = new CommentDB[keys.size()];
        Iterator iter = keys.iterator();
        long key;
        int index = 0;
        while( iter.hasNext())
        {
            key = (Long)iter.next();
            result[index++] = DB.comments.get(key);
        }
        return result;
    }

    @ApiMethod(name = "getUser")
    public UserDB getUser(@Named("idUser") String idUser)
    {
        return DB.users.get(idUser);
    }

    //OK1 OK2
    @ApiMethod(name = "startDB") // esta funcion es obligatoria llamarla antes de iniciar el backend, se puede llamar desde la interfaz web
    public StringResult startDB(@Named("password") String password)
    {
        StringResult result;
        try {
            if (password.equals("7I930x82dxI") && !DB.state) {
                StartDB.startDB();
                result = new StringResult("Success");
            }
            else {
                result = new StringResult("Incorrect password");
            }
        }
        catch(Exception e)
        {
            result  = new StringResult("wrong");
        }
        return result;
    }
}
