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
import com.unal.tuapp.recapp.backend.model.Reminder;

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
        name = "reminderApi",
        version = "v1",
        resource = "reminder",
        namespace = @ApiNamespace(
                ownerDomain = "model.backend.recapp.tuapp.unal.com",
                ownerName = "model.backend.recapp.tuapp.unal.com",
                packagePath = ""
        )
)
public class ReminderEndpoint {

    private static final Logger logger = Logger.getLogger(ReminderEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;


    /**
     * Returns the {@link Reminder} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code Reminder} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "reminder/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public Reminder get(@Named("id") Long id) throws NotFoundException {
        logger.info("Getting Reminder with ID: " + id);
        Reminder reminder = ofy().load().type(Reminder.class).id(id).now();
        if (reminder == null) {
            throw new NotFoundException("Could not find Reminder with ID: " + id);
        }
        return reminder;
    }

    /**
     * Inserts a new {@code Reminder}.
     */
    @ApiMethod(
            name = "insert",
            path = "reminder",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Reminder insert(Reminder reminder) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that reminder.id has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        ofy().save().entity(reminder).now();
        logger.info("Created Reminder with ID: " + reminder.getId());
        try {
            new MessagingEndPoint().sendMessage("reminder");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ofy().load().entity(reminder).now();
    }

    /**
     * Updates an existing {@code Reminder}.
     *
     * @param id       the ID of the entity to be updated
     * @param reminder the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code Reminder}
     */
    @ApiMethod(
            name = "update",
            path = "reminder/{id}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public Reminder update(@Named("id") Long id, Reminder reminder) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(id);
        ofy().save().entity(reminder).now();
        logger.info("Updated Reminder: " + reminder);
        try {
            new MessagingEndPoint().sendMessage("reminder");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ofy().load().entity(reminder).now();
    }

    /**
     * Deletes the specified {@code Reminder}.
     *
     * @param id the ID of the entity to delete
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code Reminder}
     */
    @ApiMethod(
            name = "remove",
            path = "reminder/{id}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("id") Long id) throws NotFoundException {
        checkExists(id);
        ofy().delete().type(Reminder.class).id(id).now();
        logger.info("Deleted Reminder with ID: " + id);
        try {
            new MessagingEndPoint().sendMessage("reminder");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @ApiMethod(
            name = "removeReminders",
            path = "reminder/date",
            httpMethod = ApiMethod.HttpMethod.DELETE
    )
    public void removeReminders(@Named("date")Long date){
        Query<Reminder> query = ofy().load().type(Reminder.class).filter("endDate < ", date);
        QueryResultIterator<Reminder> queryResultIterator =  query.iterator();
        List<Long> reminderList =  new ArrayList<>();
        while (queryResultIterator.hasNext()){
            reminderList.add(queryResultIterator.next().getId());
        }

        ofy().delete().type(Reminder.class).ids(reminderList).now();
        try {
            new MessagingEndPoint().sendMessage("reminder");
        } catch (IOException e) {
            e.printStackTrace();
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
            path = "reminder",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Reminder> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<Reminder> query = ofy().load().type(Reminder.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<Reminder> queryIterator = query.iterator();
        List<Reminder> reminderList = new ArrayList<Reminder>(limit);
        while (queryIterator.hasNext()) {
            reminderList.add(queryIterator.next());
        }
        return CollectionResponse.<Reminder>builder().setItems(reminderList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    @ApiMethod(
            name = "listUser",
            path = "reminder/user",
            httpMethod = ApiMethod.HttpMethod.GET
    )
    public List<Reminder> listUser(@Named("userId")Long user_id){
        Query<Reminder> query = ofy().load().type(Reminder.class).filter("userId",user_id);
        QueryResultIterator<Reminder> queryResultIterator = query.iterator();
        List<Reminder> reminderList = new ArrayList<>();
        while (queryResultIterator.hasNext()){
            reminderList.add(queryResultIterator.next());
        }
        return reminderList;
    }

    private void checkExists(Long id) throws NotFoundException {
        try {
            ofy().load().type(Reminder.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find Reminder with ID: " + id);
        }
    }
}