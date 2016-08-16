package com.disainin.what2watch;

public class Item {

    private String poster_path,
            media_type,
            original_lenguage,
            backdrop_path,
            overview;

    private Integer[] genre_ids;
    private Integer id, vote_count;
    private Float popularity, vote_average;

    public Item(String poster_path, String media_type, String original_lenguage, String backdrop_path, String overview, Integer[] genre_ids, Integer id, Integer vote_count, Float popularity, Float vote_average) {
        this.poster_path = poster_path;
        this.media_type = media_type;
        this.original_lenguage = original_lenguage;
        this.backdrop_path = backdrop_path;
        this.overview = overview;
        this.genre_ids = genre_ids;
        this.id = id;
        this.vote_count = vote_count;
        this.popularity = popularity;
        this.vote_average = vote_average;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getMedia_type() {
        return media_type;
    }

    public void setMedia_type(String media_type) {
        this.media_type = media_type;
    }

    public String getOriginal_lenguage() {
        return original_lenguage;
    }

    public void setOriginal_lenguage(String original_lenguage) {
        this.original_lenguage = original_lenguage;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public Integer[] getGenre_ids() {
        return genre_ids;
    }

    public void setGenre_ids(Integer[] genre_ids) {
        this.genre_ids = genre_ids;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getVote_count() {
        return vote_count;
    }

    public void setVote_count(Integer vote_count) {
        this.vote_count = vote_count;
    }

    public Float getPopularity() {
        return popularity;
    }

    public void setPopularity(Float popularity) {
        this.popularity = popularity;
    }

    public Float getVote_average() {
        return vote_average;
    }

    public void setVote_average(Float vote_average) {
        this.vote_average = vote_average;
    }
}