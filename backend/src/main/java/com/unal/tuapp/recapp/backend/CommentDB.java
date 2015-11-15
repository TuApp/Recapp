package com.unal.tuapp.recapp.backend;

/**
 * Created by yeisondavid on 30/09/2015.
 */
public class CommentDB implements Comparable<CommentDB>{
    private static long nextId = 0;
    public static long getnextID() { return nextId++; }
    long id;
    String idUser;
    long idPlace;
    String content;
    public CommentDB(long id, String idUser, long idPlace, String content)
    {
        this.idUser = idUser;
        this.idPlace = idPlace;
        this.content = content;
        this.id = id;
    }

    @Override
    public int compareTo(CommentDB o) {
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

    public long getIdPlace() {
        return idPlace;
    }

    public void setIdPlace(long idPlace) {
        this.idPlace = idPlace;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
