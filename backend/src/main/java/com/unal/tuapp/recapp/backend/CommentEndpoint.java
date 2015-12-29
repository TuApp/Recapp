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
        name = "commentApi",
        version = "v1",
        resource = "comment",
        namespace = @ApiNamespace(
                ownerDomain = "model.backend.recapp.tuapp.unal.com",
                ownerName = "model.backend.recapp.tuapp.unal.com",
                packagePath = ""
        )
)
public class CommentEndpoint {

    private static final Logger logger = Logger.getLogger(CommentEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;



    /**
     * Returns the {@link Comment} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code Comment} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "comment/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public Comment get(@Named("id") Long id) throws NotFoundException {
        logger.info("Getting Comment with ID: " + id);
        Comment comment = ofy().load().type(Comment.class).id(id).now();
        if (comment == null) {
            throw new NotFoundException("Could not find Comment with ID: " + id);
        }
        return comment;
    }

    /**
     * Inserts a new {@code Comment}.
     */
    @ApiMethod(
            name = "insert",
            path = "comment",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Comment insert(Comment comment) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that comment.id has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        ofy().save().entity(comment).now();
        logger.info("Created Comment.");
        try {
            new MessagingEndPoint().sendMessage("comment");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ofy().load().entity(comment).now();
    }



    /**
     * Updates an existing {@code Comment}.
     *
     * @param id      the ID of the entity to be updated
     * @param comment the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code Comment}
     */
    @ApiMethod(
            name = "update",
            path = "comment/{id}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public Comment update(@Named("id") Long id, Comment comment) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(id);
        ofy().save().entity(comment).now();
        logger.info("Updated Comment: " + comment);
        try {
            new MessagingEndPoint().sendMessage("comment");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ofy().load().entity(comment).now();
    }

    /**
     * Deletes the specified {@code Comment}.
     *
     * @param id the ID of the entity to delete
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code Comment}
     */
    @ApiMethod(
            name = "remove",
            path = "comment/{id}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("id") Long id) throws NotFoundException {
        checkExists(id);
        ofy().delete().type(Comment.class).id(id).now();
        try {
            new MessagingEndPoint().sendMessage("deleteComment");
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("Deleted Comment with ID: " + id);
    }
    @ApiMethod(
            name = "removeComments",
            path = "comment/placesIds",
            httpMethod = ApiMethod.HttpMethod.DELETE
    )
    public void removeComments(@Named("ids")List<Long> ids) throws NotFoundException{
        for (Long i : ids) {
            checkExists(i);
            Comment comment = ofy().load().type(Comment.class).filter("placeId",i).first().now();
            ofy().delete().entity(comment).now();

        }
        try {
            new MessagingEndPoint().sendMessage("comment");
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
            path = "comment",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Comment> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<Comment> query = ofy().load().type(Comment.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<Comment> queryIterator = query.iterator();
        List<Comment> commentList = new ArrayList<Comment>(limit);
        while (queryIterator.hasNext()) {
            commentList.add(queryIterator.next());
        }
        return CollectionResponse.<Comment>builder().setItems(commentList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    @ApiMethod(
            name = "listUser",
            path = "comment/user",
            httpMethod = ApiMethod.HttpMethod.GET
    )
    public List<Comment> listUser(@Named("userId")Long user_id){
        Query<Comment> query = ofy().load().type(Comment.class).filter("userId", user_id);
        QueryResultIterator<Comment> queryResultIterator = query.iterator();
        List<Comment> commentList = new ArrayList<>();
        while (queryResultIterator.hasNext()){
            commentList.add(queryResultIterator.next());
        }
        return commentList;
    }

    @ApiMethod(
            name = "listPlace",
            path = "comment/place",
            httpMethod = ApiMethod.HttpMethod.GET
    )
    public List<Comment> listPlace(@Named("placeId")Long place_id){
        Query<Comment> query = ofy().load().type(Comment.class).filter("placeId",place_id);
        QueryResultIterator<Comment> queryResultIterator = query.iterator();
        List<Comment> commentList = new ArrayList<>();
        while (queryResultIterator.hasNext()){
            commentList.add(queryResultIterator.next());
        }
        return commentList;
    }

    private void checkExists(Long id) throws NotFoundException {
        try {
            ofy().load().type(Comment.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find Comment with ID: " + id);
        }
    }
}