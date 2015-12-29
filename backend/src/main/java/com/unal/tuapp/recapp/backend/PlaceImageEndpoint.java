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
import com.unal.tuapp.recapp.backend.model.PlaceImage;

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
        name = "placeImageApi",
        version = "v1",
        resource = "placeImage",
        namespace = @ApiNamespace(
                ownerDomain = "model.backend.recapp.tuapp.unal.com",
                ownerName = "model.backend.recapp.tuapp.unal.com",
                packagePath = ""
        )
)
public class PlaceImageEndpoint {

    private static final Logger logger = Logger.getLogger(PlaceImageEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    /**
     * Returns the {@link PlaceImage} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code PlaceImage} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "placeImage/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public PlaceImage get(@Named("id") Long id) throws NotFoundException {
        logger.info("Getting PlaceImage with ID: " + id);
        PlaceImage placeImage = ofy().load().type(PlaceImage.class).id(id).now();
        if (placeImage == null) {
            throw new NotFoundException("Could not find PlaceImage with ID: " + id);
        }
        return placeImage;
    }

    /**
     * Inserts a new {@code PlaceImage}.
     */
    @ApiMethod(
            name = "insert",
            path = "placeImage",
            httpMethod = ApiMethod.HttpMethod.POST)
    public PlaceImage insert(PlaceImage placeImage) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that placeImage.id has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        ofy().save().entity(placeImage).now();
        logger.info("Created PlaceImage with ID: " + placeImage.getId());
        try {
            new MessagingEndPoint().sendMessage("imagePlace");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ofy().load().entity(placeImage).now();
    }

    /**
     * Updates an existing {@code PlaceImage}.
     *
     * @param id         the ID of the entity to be updated
     * @param placeImage the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code PlaceImage}
     */
    @ApiMethod(
            name = "update",
            path = "placeImage/{id}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public PlaceImage update(@Named("id") Long id, PlaceImage placeImage) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(id);
        ofy().save().entity(placeImage).now();
        logger.info("Updated PlaceImage: " + placeImage);
        try {
            new MessagingEndPoint().sendMessage("imagePlace");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ofy().load().entity(placeImage).now();
    }

    /**
     * Deletes the specified {@code PlaceImage}.
     *
     * @param id the ID of the entity to delete
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code PlaceImage}
     */
    @ApiMethod(
            name = "remove",
            path = "placeImage/{id}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("id") Long id) throws NotFoundException {
        checkExists(id);
        ofy().delete().type(PlaceImage.class).id(id).now();
        try {
            new MessagingEndPoint().sendMessage("DeleteImagePlace");
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("Deleted PlaceImage with ID: " + id);
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
            path = "placeImage",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<PlaceImage> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<PlaceImage> query = ofy().load().type(PlaceImage.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<PlaceImage> queryIterator = query.iterator();
        List<PlaceImage> placeImageList = new ArrayList<PlaceImage>(limit);
        while (queryIterator.hasNext()) {
            placeImageList.add(queryIterator.next());
        }
        return CollectionResponse.<PlaceImage>builder().setItems(placeImageList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    @ApiMethod(
            name = "listPlaces",
            path =  "placeImage/place",
            httpMethod = ApiMethod.HttpMethod.GET
    )
    public List<PlaceImage> listPlace(@Named("placeId")Long place_id){
        Query<PlaceImage> query = ofy().load().type(PlaceImage.class).filter("placeId",place_id);
        QueryResultIterator<PlaceImage> queryResultIterator = query.iterator();
        List<PlaceImage> images = new ArrayList<>();
        while (queryResultIterator.hasNext()){
            images.add(queryResultIterator.next());
        }
        return images;
    }

    private void checkExists(Long id) throws NotFoundException {
        try {
            ofy().load().type(PlaceImage.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find PlaceImage with ID: " + id);
        }
    }
}