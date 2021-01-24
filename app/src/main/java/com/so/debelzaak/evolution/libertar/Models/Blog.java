package com.so.debelzaak.evolution.libertar.Models;

public class Blog {
    private String Title;
    private String DESCRIPTION;
    private String IMAGE;
    private String username;
    private String likke;
    private String notifi;
    private String uid;
    private String temads;

    public Blog(String title, String DESCRIPTION, String IMAGE, String username, String likke, String notifi, String uid, String temads) {
        this.Title = title;
        this.DESCRIPTION = DESCRIPTION;
        this.IMAGE = IMAGE;
        this.username = username;
        this.likke = likke;
        this.notifi = notifi;
        this.uid = uid;
        this.temads = temads;
    }

    public Blog() {

    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDESCRIPTION() {
        return DESCRIPTION;
    }

    public void setDESCRIPTION(String DESCRIPTION) {
        this.DESCRIPTION = DESCRIPTION;
    }

    public String getIMAGE() {
        return IMAGE;
    }

    public void setIMAGE(String IMAGE) {
        this.IMAGE = IMAGE;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLikke() {
        return likke;
    }

    public void setLikke(String likke) {
        this.likke = likke;
    }

    public String getNotifi() {
        return notifi;
    }

    public void setNotifi(String notifi) {
        this.notifi = notifi;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTemads() {
        return temads;
    }

    public void setTemads(String temads) {
        this.temads = temads;
    }
}
