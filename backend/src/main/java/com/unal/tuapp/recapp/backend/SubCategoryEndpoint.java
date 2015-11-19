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
import com.unal.tuapp.recapp.backend.model.SubCategory;

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
        name = "subCategoryApi",
        version = "v1",
        resource = "subCategory",
        namespace = @ApiNamespace(
                ownerDomain = "model.backend.recapp.tuapp.unal.com",
                ownerName = "model.backend.recapp.tuapp.unal.com",
                packagePath = ""
        )
)
public class SubCategoryEndpoint {

    private static final Logger logger = Logger.getLogger(SubCategoryEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;


    /**
     * Returns the {@link SubCategory} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code SubCategory} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "subCategory/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public SubCategory get(@Named("id") Long id) throws NotFoundException {
        logger.info("Getting SubCategory with ID: " + id);
        SubCategory subCategory = ofy().load().type(SubCategory.class).id(id).now();
        if (subCategory == null) {
            throw new NotFoundException("Could not find SubCategory with ID: " + id);
        }
        return subCategory;
    }

    /**
     * Inserts a new {@code SubCategory}.
     */
    @ApiMethod(
            name = "insert",
            path = "subCategory",
            httpMethod = ApiMethod.HttpMethod.POST)
    public SubCategory insert(SubCategory subCategory) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that subCategory.id has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        ofy().save().entity(subCategory).now();
        logger.info("Created SubCategory with ID: " + subCategory.getId());
        try {
            new MessagingEndPoint().sendMessage("subCategory");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ofy().load().entity(subCategory).now();
    }

    /**
     * Updates an existing {@code SubCategory}.
     *
     * @param id          the ID of the entity to be updated
     * @param subCategory the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code SubCategory}
     */
    @ApiMethod(
            name = "update",
            path = "subCategory/{id}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public SubCategory update(@Named("id") Long id, SubCategory subCategory) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(id);
        ofy().save().entity(subCategory).now();
        logger.info("Updated SubCategory: " + subCategory);
        try {
            new MessagingEndPoint().sendMessage("subCategory");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ofy().load().entity(subCategory).now();
    }

    /**
     * Deletes the specified {@code SubCategory}.
     *
     * @param id the ID of the entity to delete
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code SubCategory}
     */
    @ApiMethod(
            name = "remove",
            path = "subCategory/{id}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("id") Long id) throws NotFoundException {
        checkExists(id);
        ofy().delete().type(SubCategory.class).id(id).now();
        logger.info("Deleted SubCategory with ID: " + id);
        try {
            new MessagingEndPoint().sendMessage("subCategory");
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
            path = "subCategory",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<SubCategory> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<SubCategory> query = ofy().load().type(SubCategory.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<SubCategory> queryIterator = query.iterator();
        List<SubCategory> subCategoryList = new ArrayList<SubCategory>(limit);
        while (queryIterator.hasNext()) {
            subCategoryList.add(queryIterator.next());
        }
        return CollectionResponse.<SubCategory>builder().setItems(subCategoryList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    @ApiMethod(
            name = "listCategory",
            path = "subCategory/category",
            httpMethod = ApiMethod.HttpMethod.GET
    )
    public List<SubCategory> listCategory(@Named("categoryId")Long category_id){
        Query<SubCategory> query = ofy().load().type(SubCategory.class).filter("categoryId",category_id);
        QueryResultIterator<SubCategory> subCategoryQueryResultIterator =  query.iterator();
        List<SubCategory> subCategoryList =  new ArrayList<>();
        while (subCategoryQueryResultIterator.hasNext()){
            subCategoryList.add(subCategoryQueryResultIterator.next());
        }
        return subCategoryList;
    }

    private void checkExists(Long id) throws NotFoundException {
        try {
            ofy().load().type(SubCategory.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find SubCategory with ID: " + id);
        }
    }
}