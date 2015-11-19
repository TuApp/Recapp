package com.unal.tuapp.recapp.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.cmd.Query;
import com.unal.tuapp.recapp.backend.model.UserByPlace;

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
        name = "userByPlaceApi",
        version = "v1",
        resource = "userByPlace",
        namespace = @ApiNamespace(
                ownerDomain = "model.backend.recapp.tuapp.unal.com",
                ownerName = "model.backend.recapp.tuapp.unal.com",
                packagePath = ""
        )
)
public class UserByPlaceEndpoint {

    private static final Logger logger = Logger.getLogger(UserByPlaceEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;



    /**
     * Returns the {@link UserByPlace} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code UserByPlace} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "userByPlace/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public UserByPlace get(@Named("id") Long id) throws NotFoundException {
        logger.info("Getting UserByPlace with ID: " + id);
        UserByPlace userByPlace = ofy().load().type(UserByPlace.class).id(id).now();
        if (userByPlace == null) {
            throw new NotFoundException("Could not find UserByPlace with ID: " + id);
        }
        return userByPlace;
    }

    /**
     * Inserts a new {@code UserByPlace}.
     */
    @ApiMethod(
            name = "insert",
            path = "userByPlace",
            httpMethod = ApiMethod.HttpMethod.POST)
    public UserByPlace insert(UserByPlace userByPlace) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that userByPlace.id has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        ofy().save().entity(userByPlace).now();
        logger.info("Created UserByPlace with ID: " + userByPlace.getId());
        try {
            new MessagingEndPoint().sendMessage("favoritePlace");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ofy().load().entity(userByPlace).now();
    }

    /**
     * Updates an existing {@code UserByPlace}.
     *
     * @param id          the ID of the entity to be updated
     * @param userByPlace the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code UserByPlace}
     */
    @ApiMethod(
            name = "update",
            path = "userByPlace/{id}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public UserByPlace update(@Named("id") Long id, UserByPlace userByPlace) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(id);
        ofy().save().entity(userByPlace).now();
        logger.info("Updated UserByPlace: " + userByPlace);
        try {
            new MessagingEndPoint().sendMessage("favoritePlace");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ofy().load().entity(userByPlace).now();
    }

    /**
     * Deletes the specified {@code UserByPlace}.
     *
     * @param id the ID of the entity to delete
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code UserByPlace}
     */
    @ApiMethod(
            name = "remove",
            path = "userByPlace/{id}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("id") Long id) throws NotFoundException {
        checkExists(id);
        ofy().delete().type(UserByPlace.class).id(id).now();
        logger.info("Deleted UserByPlace with ID: " + id);
        try {
            new MessagingEndPoint().sendMessage("favoritePlace");
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
            path = "userByPlace",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<UserByPlace> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<UserByPlace> query = ofy().load().type(UserByPlace.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<UserByPlace> queryIterator = query.iterator();
        List<UserByPlace> userByPlaceList = new ArrayList<UserByPlace>(limit);
        while (queryIterator.hasNext()) {
            userByPlaceList.add(queryIterator.next());
        }
        return CollectionResponse.<UserByPlace>builder().setItems(userByPlaceList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }
    @ApiMethod(
            name = "listUser",
            path = "userByPlace/user",
            httpMethod = ApiMethod.HttpMethod.GET
    )
    public List<UserByPlace> listUser(@Named("userId")Long user_id){
        Query<UserByPlace> query = ofy().load().type(UserByPlace.class).filter("userId",user_id);
        QueryResultIterator<UserByPlace> queryResultIterator = query.iterator();
        List<UserByPlace> userByPlaceList = new ArrayList<>();
        while (queryResultIterator.hasNext()){
            userByPlaceList.add(queryResultIterator.next());
        }
        return userByPlaceList;
    }

    private void checkExists(Long id) throws NotFoundException {
        try {
            ofy().load().type(UserByPlace.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find UserByPlace with ID: " + id);
        }
    }
}