package com.disainin.what2watch;

import android.os.AsyncTask;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.model.config.TmdbConfiguration;

public class ConfigurationTaskTMDBAPI extends AsyncTask<Integer, Integer, TmdbConfiguration> {

    protected TmdbConfiguration doInBackground(Integer... code) {
        return new TmdbApi("1947a2516ec6cb3cf97ef1da21fdaa87").getConfiguration();
    }

}