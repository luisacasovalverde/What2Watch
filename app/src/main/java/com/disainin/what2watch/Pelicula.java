package com.disainin.what2watch;

/**
 * Created by LUIS on 02/08/2016.
 */
public class Pelicula extends Item {

    private String
            release_date,
            original_title,
            title;

    private boolean adult, video;

    public Pelicula(String poster_path, String media_type, String original_lenguage, String backdrop_path, String overview, Integer[] genre_ids, Integer id, Integer vote_count, Float popularity, Float vote_average, String release_date, String original_title, String title, boolean adult, boolean video) {
        super(poster_path, media_type, original_lenguage, backdrop_path, overview, genre_ids, id, vote_count, popularity, vote_average);
        this.release_date = release_date;
        this.original_title = original_title;
        this.title = title;
        this.adult = adult;
        this.video = video;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }
}
