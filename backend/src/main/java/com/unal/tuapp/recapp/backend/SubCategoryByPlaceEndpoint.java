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
import com.unal.tuapp.recapp.backend.model.SubCategoryByPlace;

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
        name = "subCategoryByPlaceApi",
        version = "v1",
        resource = "subCategoryByPlace",
        namespace = @ApiNamespace(
                ownerDomain = "model.backend.recapp.tuapp.unal.com",
                ownerName = "model.backend.recapp.tuapp.unal.com",
                packagePath = ""
        )
)
public class SubCategoryByPlaceEndpoint {

    private static final Logger logger = Logger.getLogger(SubCategoryByPlaceEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    /**
     * Returns the {@link SubCategoryByPlace} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code SubCategoryByPlace} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "subCategoryByPlace/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public SubCategoryByPlace get(@Named("id") Long id) throws NotFoundException {
        logger.info("Getting SubCategoryByPlace with ID: " + id);
        SubCategoryByPlace subCategoryByPlace = ofy().load().type(SubCategoryByPlace.class).id(id).now();
        if (subCategoryByPlace == null) {
            throw new NotFoundException("Could not find SubCategoryByPlace with ID: " + id);
        }
        return subCategoryByPlace;
    }

    /**
     * Inserts a new {@code SubCategoryByPlace}.
     */
    @ApiMethod(
            name = "insert",
            path = "subCategoryByPlace",
            httpMethod = ApiMethod.HttpMethod.POST)
    public SubCategoryByPlace insert(SubCategoryByPlace subCategoryByPlace) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that subCategoryByPlace.id has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        ofy().save().entity(subCategoryByPlace).now();
        logger.info("Created SubCategoryByPlace.");
        try {
            new MessagingEndPoint().sendMessage("subCategoryByPlace");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ofy().load().entity(subCategoryByPlace).now();
    }

    /**
     * Updates an existing {@code SubCategoryByPlace}.
     *
     * @param id                 the ID of the entity to be updated
     * @param subCategoryByPlace the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code SubCategoryByPlace}
     */
    @ApiMethod(
            name = "update",
            path = "subCategoryByPlace/{id}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public SubCategoryByPlace update(@Named("id") Long id, SubCategoryByPlace subCategoryByPlace) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(id);
        ofy().save().entity(subCategoryByPlace).now();
        logger.info("Updated SubCategoryByPlace: " + subCategoryByPlace);
        try {
            new MessagingEndPoint().sendMessage("subCategoryByPlace");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ofy().load().entity(subCategoryByPlace).now();
    }

    /**
     * Deletes the specified {@code SubCategoryByPlace}.
     *
     * @param id the ID of the entity to delete
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code SubCategoryByPlace}
     */
    @ApiMethod(
            name = "remove",
            path = "subCategoryByPlace/{id}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("id") Long id) throws NotFoundException {
        checkExists(id);
        ofy().delete().type(SubCategoryByPlace.class).id(id).now();
        try {
            new MessagingEndPoint().sendMessage("subCategoryByPlace");
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("Deleted SubCategoryByPlace with ID: " + id);
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
            path = "subCategoryByPlace",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<SubCategoryByPlace> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<SubCategoryByPlace> query = ofy().load().type(SubCategoryByPlace.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<SubCategoryByPlace> queryIterator = query.iterator();
        List<SubCategoryByPlace> subCategoryByPlaceList = new ArrayList<SubCategoryByPlace>(limit);
        while (queryIterator.hasNext()) {
            subCategoryByPlaceList.add(queryIterator.next());
        }
        return CollectionResponse.<SubCategoryByPlace>builder().setItems(subCategoryByPlaceList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(Long id) throws NotFoundException {
        try {
            ofy().load().type(SubCategoryByPlace.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find SubCategoryByPlace with ID: " + id);
        }
    }
}