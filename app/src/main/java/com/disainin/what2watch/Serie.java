package com.disainin.what2watch;

/**
 * Created by LUIS on 02/08/2016.
 */
public class Serie extends Item {

    private String first_air_date, original_name, name;
    private String[] origin_country;

    public Serie(String poster_path, String media_type, String original_lenguage, String backdrop_path, String overview, Integer[] genre_ids, Integer id, Integer vote_count, Float popularity, Float vote_average, String first_air_date, String original_name, String name, String[] origin_country) {
        super(poster_path, media_type, original_lenguage, backdrop_path, overview, genre_ids, id, vote_count, popularity, vote_average);
        this.first_air_date = first_air_date;
        this.original_name = original_name;
        this.name = name;
        this.origin_country = origin_country;
    }

    public String getFirst_air_date() {
        return first_air_date;
    }

    public void setFirst_air_date(String first_air_date) {
        this.first_air_date = first_air_date;
    }

    public String getOriginal_name() {
        return original_name;
    }

    public void setOriginal_name(String original_name) {
        this.original_name = original_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getOrigin_country() {
        return origin_country;
    }

    public void setOrigin_country(String[] origin_country) {
        this.origin_country = origin_country;
    }
}
