package com.unal.tuapp.recapp.backend;

import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiNamespace;
import com.unal.tuapp.recapp.backend.model.Registration;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Named;
import static com.unal.tuapp.recapp.backend.OfyService.ofy;
/**
 * Created by andresgutierrez on 11/18/15.
 */

@Api(
        name = "messagingApi",
        version = "v1",
        resource = "messaging",
        namespace = @ApiNamespace(
                ownerDomain = "model.backend.recapp.tuapp.unal.com",
                ownerName = "model.backend.recapp.tuapp.unal.com",
                packagePath = ""
        )
)
public class MessagingEndPoint {
    private static final Logger log = Logger.getLogger(MessagingEndPoint.class.getName());

    /**
     * Api Keys can be obtained from the google cloud console
     */
    private static final String API_KEY = System.getProperty("gcm.api.key");

    public void sendMessage(@Named("message") String message) throws IOException {
        if (message == null || message.trim().length() == 0) {
            log.warning("Not sending message because it is empty");
            return;
        }
        // crop longer messages
        if (message.length() > 1000) {
            message = message.substring(0, 1000) + "[...]";
        }
        Sender sender = new Sender(API_KEY);
        Message msg = new Message.Builder().addData("message", message)
                .delayWhileIdle(true)
                .timeToLive(2419200)
                .collapseKey(message)
                .build();
        List<Registration> records = ofy().load().type(Registration.class).list();
        for (Registration record : records) {
            Result result = sender.send(msg, record.getRegId(), 15);
            if (result.getMessageId() != null) {
                log.info("Message sent to " + record.getRegId());
                String canonicalRegId = result.getCanonicalRegistrationId();
                if (canonicalRegId != null) {
                    // if the regId changed, we have to update the datastore
                    log.info("Registration Id changed for " + record.getRegId() + " updating to " + canonicalRegId);
                    record.setRegId(canonicalRegId);
                    ofy().save().entity(record).now();
                }
            } else {
                String error = result.getErrorCodeName();
                if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
                    log.warning("Registration Id " + record.getRegId() + " no longer registered with GCM, removing from datastore");
                    // if the device is no longer registered with Gcm, remove it from the datastore
                    ofy().delete().entity(record).now();
                } else {
                    log.warning("Error when sending message : " + error);
                }
            }
        }
    }
}
