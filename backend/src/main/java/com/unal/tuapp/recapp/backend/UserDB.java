package com.unal.tuapp.recapp.backend;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by yeisondavid on 30/09/2015.
 */
public class UserDB implements Comparable<UserDB> {
    String name;
    String lastName;
    String email;
    String profileImage;
    LinkedList<Long> myComments;
    LinkedList<Long> myEvents;
    LinkedList<Long> attendToEvents;
    public UserDB(String name, String lastName, String email, String profileImage)
    {


        this.name = name;
        this.lastName  = lastName;
        this.email = email;
        this.profileImage = profileImage;
        myComments = new LinkedList<Long>();
        myEvents = new LinkedList<Long>();
        attendToEvents = new LinkedList<Long>();
    }
    @Override
    public int compareTo(UserDB arg)
    {
        return email.compareTo(arg.email);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LinkedList<Long> getMyComments() {
        return myComments;
    }

    public void setMyComments(LinkedList<Long> myComments) {
        this.myComments = myComments;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public LinkedList<Long> getMyEvents() {
        return myEvents;
    }

    public void setMyEvents(LinkedList<Long> myEvents) {
        this.myEvents = myEvents;
    }

    public LinkedList<Long> getAttendToEvents() {
        return attendToEvents;
    }

    public void setAttendToEvents(LinkedList<Long> attendToEvents) {
        this.attendToEvents = attendToEvents;
    }
}
