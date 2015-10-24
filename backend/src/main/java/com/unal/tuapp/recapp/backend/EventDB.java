package com.unal.tuapp.recapp.backend;

/**
 * Created by yeisondavid on 07/10/2015.
 */
public class EventDB implements Comparable<EventDB>
{
    private static long nextId = 0;
    public static long getnextID() { return nextId++; }
    long id;
    String idUser;
    String content;
    public EventDB(long id, String idUser, String content)
    {
        this.idUser = idUser;
        this.content = content;
        this.id = id;
    }

    @Override
    public int compareTo(EventDB o) {
        return new Long(id).compareTo(o.id);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
