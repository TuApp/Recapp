package com.unal.tuapp.recapp.backend;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yeisondavid on 07/10/2015.
 */
public class EventDB implements Comparable<EventDB>
{
    private static long nextId = 0;
    public static long getnextID() { return nextId++; }
    long id;
    String idUser;
    String name;
    String description;
    String address;
    long date;
    Double log;
    Double lng;
    public List<String> assistants;
    public EventDB(long id, String idUser, String name, String description, String address, long date, double log, double lng)
    {
        this.idUser = idUser;
        this.id = id;
        this.name = name;
        this.description = description;
        this.address = address;
        this.date = date;
        this.log = log;
        this.lng = lng;
        assistants = new ArrayList<String>();
    }

    @Override
    public int compareTo(EventDB o) {
        return new Long(id).compareTo(o.id);
    }

    public static long getNextId() {
        return nextId;
    }

    public static void setNextId(long nextId) {
        EventDB.nextId = nextId;
    }

    public List<String> getAssistants() {
        return assistants;
    }

    public void setAssistants(List<String> assistants) {
        this.assistants = assistants;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Double getLog() {
        return log;
    }

    public void setLog(Double log) {
        this.log = log;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
