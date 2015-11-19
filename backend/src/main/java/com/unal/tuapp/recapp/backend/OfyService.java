package com.unal.tuapp.recapp.backend;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.unal.tuapp.recapp.backend.model.Category;
import com.unal.tuapp.recapp.backend.model.Comment;
import com.unal.tuapp.recapp.backend.model.Event;
import com.unal.tuapp.recapp.backend.model.EventByUser;
import com.unal.tuapp.recapp.backend.model.Place;
import com.unal.tuapp.recapp.backend.model.PlaceImage;
import com.unal.tuapp.recapp.backend.model.Registration;
import com.unal.tuapp.recapp.backend.model.Reminder;
import com.unal.tuapp.recapp.backend.model.SubCategory;
import com.unal.tuapp.recapp.backend.model.SubCategoryByPlace;
import com.unal.tuapp.recapp.backend.model.SubCategoryByTutorial;
import com.unal.tuapp.recapp.backend.model.Tutorial;
import com.unal.tuapp.recapp.backend.model.User;
import com.unal.tuapp.recapp.backend.model.UserByPlace;

/**
 * Created by andresgutierrez on 11/15/15.
 */
public class OfyService {
    static {
        ObjectifyService.register(User.class);
        ObjectifyService.register(Place.class);
        ObjectifyService.register(Comment.class);
        ObjectifyService.register(PlaceImage.class);
        ObjectifyService.register(Tutorial.class);
        ObjectifyService.register(Category.class);
        ObjectifyService.register(SubCategory.class);
        ObjectifyService.register(SubCategoryByPlace.class);
        ObjectifyService.register(SubCategoryByTutorial.class);
        ObjectifyService.register(UserByPlace.class);
        ObjectifyService.register(Event.class);
        ObjectifyService.register(EventByUser.class);
        ObjectifyService.register(Reminder.class);
        ObjectifyService.register(Registration.class);

    }

    public static Objectify ofy() {
        return ObjectifyService.ofy();
    }

    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }
}
