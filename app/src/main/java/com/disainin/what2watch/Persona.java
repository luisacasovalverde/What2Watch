package com.disainin.what2watch;

import com.google.gson.JsonObject;

public class Persona {

    private String name, media_type, profile_path;
    private Float popularity;
    private boolean adult;
    private Integer id;
    private JsonObject[] known_for;


    public Persona(String name, String media_type, String profile_path, Float popularity, boolean adult, Integer id, JsonObject[] known_for) {
        this.name = name;
        this.media_type = media_type;
        this.profile_path = profile_path;
        this.popularity = popularity;
        this.adult = adult;
        this.id = id;
        this.known_for = known_for;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMedia_type() {
        return media_type;
    }

    public void setMedia_type(String media_type) {
        this.media_type = media_type;
    }

    public String getProfile_path() {
        return profile_path;
    }

    public void setProfile_path(String profile_path) {
        this.profile_path = profile_path;
    }

    public Float getPopularity() {
        return popularity;
    }

    public void setPopularity(Float popularity) {
        this.popularity = popularity;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public JsonObject[] getKnown_for() {
        return known_for;
    }

    public void setKnown_for(JsonObject[] known_for) {
        this.known_for = known_for;
    }
}
