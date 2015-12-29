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
import com.unal.tuapp.recapp.backend.model.Tutorial;

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
        name = "tutorialApi",
        version = "v1",
        resource = "tutorial",
        namespace = @ApiNamespace(
                ownerDomain = "model.backend.recapp.tuapp.unal.com",
                ownerName = "model.backend.recapp.tuapp.unal.com",
                packagePath = ""
        )
)
public class TutorialEndpoint {

    private static final Logger logger = Logger.getLogger(TutorialEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;



    /**
     * Returns the {@link Tutorial} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code Tutorial} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "tutorial/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public Tutorial get(@Named("id") Long id) throws NotFoundException {
        logger.info("Getting Tutorial with ID: " + id);
        Tutorial tutorial = ofy().load().type(Tutorial.class).id(id).now();
        if (tutorial == null) {
            throw new NotFoundException("Could not find Tutorial with ID: " + id);
        }
        return tutorial;
    }

    /**
     * Inserts a new {@code Tutorial}.
     */
    @ApiMethod(
            name = "insert",
            path = "tutorial",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Tutorial insert(Tutorial tutorial) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that tutorial.id has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        ofy().save().entity(tutorial).now();
        logger.info("Created Tutorial with ID: " + tutorial.getId());
        try {
            new MessagingEndPoint().sendMessage("tutorial");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ofy().load().entity(tutorial).now();
    }



    /**
     * Updates an existing {@code Tutorial}.
     *
     * @param id       the ID of the entity to be updated
     * @param tutorial the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code Tutorial}
     */
    @ApiMethod(
            name = "update",
            path = "tutorial/{id}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public Tutorial update(@Named("id") Long id, Tutorial tutorial) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(id);
        ofy().save().entity(tutorial).now();
        logger.info("Updated Tutorial: " + tutorial);
        try {
            new MessagingEndPoint().sendMessage("tutorial");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ofy().load().entity(tutorial).now();
    }

    /**
     * Deletes the specified {@code Tutorial}.
     *
     * @param id the ID of the entity to delete
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code Tutorial}
     */
    @ApiMethod(
            name = "remove",
            path = "tutorial/{id}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("id") Long id) throws NotFoundException {
        checkExists(id);
        ofy().delete().type(Tutorial.class).id(id).now();
        logger.info("Deleted Tutorial with ID: " + id);
        try {
            new MessagingEndPoint().sendMessage("deleteTutorial");
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
            path = "tutorial",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Tutorial> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<Tutorial> query = ofy().load().type(Tutorial.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<Tutorial> queryIterator = query.iterator();
        List<Tutorial> tutorialList = new ArrayList<Tutorial>(limit);
        while (queryIterator.hasNext()) {
            tutorialList.add(queryIterator.next());
        }
        return CollectionResponse.<Tutorial>builder().setItems(tutorialList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(Long id) throws NotFoundException {
        try {
            ofy().load().type(Tutorial.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find Tutorial with ID: " + id);
        }
    }
}