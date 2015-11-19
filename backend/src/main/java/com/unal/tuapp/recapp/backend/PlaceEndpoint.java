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
import com.unal.tuapp.recapp.backend.model.Place;

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
        name = "placeApi",
        version = "v1",
        resource = "place",
        namespace = @ApiNamespace(
                ownerDomain = "model.backend.recapp.tuapp.unal.com",
                ownerName = "model.backend.recapp.tuapp.unal.com",
                packagePath = ""
        )
)
public class PlaceEndpoint {

    private static final Logger logger = Logger.getLogger(PlaceEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;



    /**
     * Returns the {@link Place} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code Place} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "place/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public Place get(@Named("id") Long id) throws NotFoundException {
        logger.info("Getting Place with ID: " + id);
        Place place = ofy().load().type(Place.class).id(id).now();
        if (place == null) {
            throw new NotFoundException("Could not find Place with ID: " + id);
        }
        return place;
    }

    /**
     * Inserts a new {@code Place}.
     */
    @ApiMethod(
            name = "insert",
            path = "place",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Place insert(Place place) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that place.id has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        ofy().save().entity(place).now();
        logger.info("Created Place with ID: " + place.getId());
        try {
            new MessagingEndPoint().sendMessage("place");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ofy().load().entity(place).now();
    }

    /**
     * Updates an existing {@code Place}.
     *
     * @param id    the ID of the entity to be updated
     * @param place the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code Place}
     */
    @ApiMethod(
            name = "update",
            path = "place/{id}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public Place update(@Named("id") Long id, Place place) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(id);
        ofy().save().entity(place).now();
        logger.info("Updated Place: " + place);
        try {
            new MessagingEndPoint().sendMessage("place");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ofy().load().entity(place).now();
    }

    /**
     * Deletes the specified {@code Place}.
     *
     * @param id the ID of the entity to delete
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code Place}
     */
    @ApiMethod(
            name = "remove",
            path = "place/{id}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("id") Long id) throws NotFoundException {
        checkExists(id);
        ofy().delete().type(Place.class).id(id).now();
        try {
            new MessagingEndPoint().sendMessage("place");
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("Deleted Place with ID: " + id);
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
            path = "place",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Place> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<Place> query = ofy().load().type(Place.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<Place> queryIterator = query.iterator();
        List<Place> placeList = new ArrayList<Place>(limit);
        while (queryIterator.hasNext()) {
            placeList.add(queryIterator.next());
        }
        return CollectionResponse.<Place>builder().setItems(placeList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(Long id) throws NotFoundException {
        try {
            ofy().load().type(Place.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find Place with ID: " + id);
        }
    }
}