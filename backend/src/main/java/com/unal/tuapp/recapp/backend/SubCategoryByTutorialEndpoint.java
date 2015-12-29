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
import com.unal.tuapp.recapp.backend.model.SubCategoryByTutorial;

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
        name = "subCategoryByTutorialApi",
        version = "v1",
        resource = "subCategoryByTutorial",
        namespace = @ApiNamespace(
                ownerDomain = "model.backend.recapp.tuapp.unal.com",
                ownerName = "model.backend.recapp.tuapp.unal.com",
                packagePath = ""
        )
)
public class SubCategoryByTutorialEndpoint {

    private static final Logger logger = Logger.getLogger(SubCategoryByTutorialEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    /**
     * Returns the {@link SubCategoryByTutorial} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code SubCategoryByTutorial} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "subCategoryByTutorial/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public SubCategoryByTutorial get(@Named("id") Long id) throws NotFoundException {
        logger.info("Getting SubCategoryByTutorial with ID: " + id);
        SubCategoryByTutorial subCategoryByTutorial = ofy().load().type(SubCategoryByTutorial.class).id(id).now();
        if (subCategoryByTutorial == null) {
            throw new NotFoundException("Could not find SubCategoryByTutorial with ID: " + id);
        }
        return subCategoryByTutorial;
    }

    /**
     * Inserts a new {@code SubCategoryByTutorial}.
     */
    @ApiMethod(
            name = "insert",
            path = "subCategoryByTutorial",
            httpMethod = ApiMethod.HttpMethod.POST)
    public SubCategoryByTutorial insert(SubCategoryByTutorial subCategoryByTutorial) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that subCategoryByTutorial.id has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        ofy().save().entity(subCategoryByTutorial).now();
        logger.info("Created SubCategoryByTutorial with ID: " + subCategoryByTutorial.getId());
        try {
            new MessagingEndPoint().sendMessage("subCategoryByTutorial");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ofy().load().entity(subCategoryByTutorial).now();
    }


    /**
     * Updates an existing {@code SubCategoryByTutorial}.
     *
     * @param id                    the ID of the entity to be updated
     * @param subCategoryByTutorial the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code SubCategoryByTutorial}
     */
    @ApiMethod(
            name = "update",
            path = "subCategoryByTutorial/{id}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public SubCategoryByTutorial update(@Named("id") Long id, SubCategoryByTutorial subCategoryByTutorial) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(id);
        ofy().save().entity(subCategoryByTutorial).now();
        logger.info("Updated SubCategoryByTutorial: " + subCategoryByTutorial);
        try {
            new MessagingEndPoint().sendMessage("subCategoryByTutorial");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ofy().load().entity(subCategoryByTutorial).now();
    }

    /**
     * Deletes the specified {@code SubCategoryByTutorial}.
     *
     * @param id the ID of the entity to delete
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code SubCategoryByTutorial}
     */
    @ApiMethod(
            name = "remove",
            path = "subCategoryByTutorial/{id}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("id") Long id) throws NotFoundException {
        checkExists(id);
        ofy().delete().type(SubCategoryByTutorial.class).id(id).now();
        logger.info("Deleted SubCategoryByTutorial with ID: " + id);
        try {
            new MessagingEndPoint().sendMessage("deleteSubCategoryByTutorial");
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
            path = "subCategoryByTutorial",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<SubCategoryByTutorial> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<SubCategoryByTutorial> query = ofy().load().type(SubCategoryByTutorial.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<SubCategoryByTutorial> queryIterator = query.iterator();
        List<SubCategoryByTutorial> subCategoryByTutorialList = new ArrayList<SubCategoryByTutorial>(limit);
        while (queryIterator.hasNext()) {
            subCategoryByTutorialList.add(queryIterator.next());
        }
        return CollectionResponse.<SubCategoryByTutorial>builder().setItems(subCategoryByTutorialList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(Long id) throws NotFoundException {
        try {
            ofy().load().type(SubCategoryByTutorial.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find SubCategoryByTutorial with ID: " + id);
        }
    }
}