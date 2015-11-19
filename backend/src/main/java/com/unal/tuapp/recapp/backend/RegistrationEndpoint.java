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
import com.unal.tuapp.recapp.backend.model.Registration;

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
        name = "registrationApi",
        version = "v1",
        resource = "registration",
        namespace = @ApiNamespace(
                ownerDomain = "model.backend.recapp.tuapp.unal.com",
                ownerName = "model.backend.recapp.tuapp.unal.com",
                packagePath = ""
        )
)
public class RegistrationEndpoint {

    private static final Logger logger = Logger.getLogger(RegistrationEndpoint.class.getName());



    @ApiMethod(
            name = "register",
            path = "register"
    )
    public void registerDevice(@Named("regId")String regId){
        if(findRecord(regId)!=null){
            logger.info("Device " + regId + " already registered, skipping register");
            return;
        }
        Registration record = new Registration();
        record.setRegId(regId);
        ofy().save().entity(record).now();

    }
    @ApiMethod(
            name = "unregister",
            path = "unregister"
    )
    public void unregisterDevice(@Named("regId")String regId){
        Registration record = findRecord(regId);
        if(record==null){
            logger.info("Device " + regId + " not registered, skipping unregister");
            return;
        }
        ofy().delete().entity(record).now();
    }

    @ApiMethod(
            name = "list",
            path = "list"
    )
    public CollectionResponse<Registration> list(@Named("count")int count){
        List<Registration> records = ofy().load().type(Registration.class).limit(count).list();
        return CollectionResponse.<Registration>builder().setItems(records).build();
    }

    private Registration findRecord(String regId) {
        return ofy().load().type(Registration.class).filter("regId", regId).first().now();
    }
}