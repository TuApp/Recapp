package com.unal.tuapp.recapp.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;
import com.unal.tuapp.recapp.backend.model.Comment;
import com.unal.tuapp.recapp.backend.model.Event;
import com.unal.tuapp.recapp.backend.model.EventByUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;

import static com.unal.tuapp.recapp.backend.OfyService.ofy;

/**
 * WARNING: This generated code is intended as a sample or starting point for using a
 * Google Cloud Endpoints RESTful API with an Objectify entity. It provides no data access
 * restrictions and no data validation.
 * <p/>
 * DO NOT deploy this code unchanged as part of a real application to real users.
 */
@Api(
        name = "eventByUserApi",
        version = "v1",
        resource = "eventByUser",
        namespace = @ApiNamespace(
                ownerDomain = "model.backend.recapp.tuapp.unal.com",
                ownerName = "model.backend.recapp.tuapp.unal.com",
                packagePath = ""
        )
)
public class EventByUserEndpoint {

    private static final Logger logger = Logger.getLogger(EventByUserEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    /**
     * Returns the {@link EventByUser} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code EventByUser} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "eventByUser/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public EventByUser get(@Named("id") Long id) throws NotFoundException {
        logger.info("Getting EventByUser with ID: " + id);
        EventByUser eventByUser = ofy().load().type(EventByUser.class).id(id).now();
        if (eventByUser == null) {
            throw new NotFoundException("Could not find EventByUser with ID: " + id);
        }
        return eventByUser;
    }

    /**
     * Inserts a new {@code EventByUser}.
     */
    @ApiMethod(
            name = "insert",
            path = "eventByUser",
            httpMethod = ApiMethod.HttpMethod.POST)
    public EventByUser insert(EventByUser eventByUser) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that eventByUser.id has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        ofy().save().entity(eventByUser).now();
        logger.info("Created EventByUser with ID: " + eventByUser.getId());
        try {
            new MessagingEndPoint().sendMessage("eventByUser");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ofy().load().entity(eventByUser).now();
    }

    

    /**
     * Updates an existing {@code EventByUser}.
     *
     * @param id          the ID of the entity to be updated
     * @param eventByUser the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code EventByUser}
     */
    @ApiMethod(
            name = "update",
            path = "eventByUser/{id}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public EventByUser update(@Named("id") Long id, EventByUser eventByUser) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(id);
        ofy().save().entity(eventByUser).now();
        logger.info("Updated EventByUser: " + eventByUser);
        try {
            new MessagingEndPoint().sendMessage("eventByUser");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ofy().load().entity(eventByUser).now();
    }

    /**
     * Deletes the specified {@code EventByUser}.
     *
     * @param id the ID of the entity to delete
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code EventByUser}
     */
    @ApiMethod(
            name = "remove",
            path = "eventByUser/{id}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("id") Long id) throws NotFoundException {
        checkExists(id);
        ofy().delete().type(EventByUser.class).id(id).now();
        logger.info("Deleted EventByUser with ID: " + id);
        try {
            new MessagingEndPoint().sendMessage("deleteEventByUser");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @ApiMethod(
            name = "removeUsers",
            path = "eventByUser/users",
            httpMethod = ApiMethod.HttpMethod.DELETE
    )
    public void removeUsers(@Named("eventId") Long event_id) throws NotFoundException{
        Query<EventByUser> query = ofy().load().type(EventByUser.class).filter("eventId",event_id);
        QueryResultIterator<EventByUser> queryResultIterator = query.iterator();
        List<EventByUser> eventByUserList =  new ArrayList<>();
        while (queryResultIterator.hasNext()){
            eventByUserList.add(queryResultIterator.next());
        }
        ofy().delete().entities(eventByUserList);
        try {
            new MessagingEndPoint().sendMessage("deleteEventByUser");
        }catch (IOException e){

        }
    }

    /**
     * List all entities.
     *
     * @param cursor used for pagination to determine which page to return
     * @param limit  the maximum number of entries to return
     * @return a response that encapsulates the result list and the next page token/cursor
     */
    @ApiMethod(
            name = "list",
            path = "eventByUser",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<EventByUser> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<EventByUser> query = ofy().load().type(EventByUser.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<EventByUser> queryIterator = query.iterator();
        List<EventByUser> eventByUserList = new ArrayList<EventByUser>(limit);
        while (queryIterator.hasNext()) {
            eventByUserList.add(queryIterator.next());
        }
        return CollectionResponse.<EventByUser>builder().setItems(eventByUserList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }
    @ApiMethod(
            name = "listEvent",
            path = "eventByUser/events",
            httpMethod = ApiMethod.HttpMethod.GET
    )
    public List<EventByUser> listEvent (@Named("email")String email){
        Query<EventByUser> query = ofy().load().type(EventByUser.class).filter("email",email);
        QueryResultIterator<EventByUser> queryResultIterator = query.iterator();
        List<EventByUser> eventList = new ArrayList<>();
        while (queryResultIterator.hasNext()){
            eventList.add(queryResultIterator.next());
        }
        return eventList;
    }

    private void checkExists(Long id) throws NotFoundException {
        try {
            ofy().load().type(EventByUser.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find EventByUser with ID: " + id);
        }
    }
}