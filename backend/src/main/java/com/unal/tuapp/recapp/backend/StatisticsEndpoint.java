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
import com.unal.tuapp.recapp.backend.model.Statistics;

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
        name = "statisticsApi",
        version = "v1",
        resource = "statistics",
        namespace = @ApiNamespace(
                ownerDomain = "model.backend.recapp.tuapp.unal.com",
                ownerName = "model.backend.recapp.tuapp.unal.com",
                packagePath = ""
        )
)
public class StatisticsEndpoint {

    private static final Logger logger = Logger.getLogger(StatisticsEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(Statistics.class);
    }

    /**
     * Returns the {@link Statistics} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code Statistics} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "statistics/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public Statistics get(@Named("id") Long id) throws NotFoundException {
        logger.info("Getting Statistics with ID: " + id);
        Statistics statistics = ofy().load().type(Statistics.class).id(id).now();
        if (statistics == null) {
            throw new NotFoundException("Could not find Statistics with ID: " + id);
        }
        return statistics;
    }

    /**
     * Inserts a new {@code Statistics}.
     */
    @ApiMethod(
            name = "insert",
            path = "statistics",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Statistics insert(Statistics statistics) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that statistics.id has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        ofy().save().entity(statistics).now();
        logger.info("Created Statistics with ID: " + statistics.getId());
        try {
            new MessagingEndPoint().sendMessage("statistics");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ofy().load().entity(statistics).now();
    }

    /**
     * Updates an existing {@code Statistics}.
     *
     * @param id         the ID of the entity to be updated
     * @param statistics the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code Statistics}
     */
    @ApiMethod(
            name = "update",
            path = "statistics/{id}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public Statistics update(@Named("id") Long id, Statistics statistics) throws NotFoundException {
        checkExists(id);
        ofy().save().entity(statistics).now();
        logger.info("Updated Statistics: " + statistics);
        try {
            new MessagingEndPoint().sendMessage("statistics");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ofy().load().entity(statistics).now();
    }

    /**
     * Deletes the specified {@code Statistics}.
     *
     * @param id the ID of the entity to delete
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code Statistics}
     */
    @ApiMethod(
            name = "remove",
            path = "statistics/{id}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("id") Long id) throws NotFoundException {
        checkExists(id);
        ofy().delete().type(Statistics.class).id(id).now();
        try {
            new MessagingEndPoint().sendMessage("deleteStatics");
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("Deleted Statistics with ID: " + id);
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
            path = "statistics",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Statistics> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<Statistics> query = ofy().load().type(Statistics.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<Statistics> queryIterator = query.iterator();
        List<Statistics> statisticsList = new ArrayList<Statistics>(limit);
        while (queryIterator.hasNext()) {
            statisticsList.add(queryIterator.next());
        }
        return CollectionResponse.<Statistics>builder().setItems(statisticsList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(Long id) throws NotFoundException {
        try {
            ofy().load().type(Statistics.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find Statistics with ID: " + id);
        }
    }
}