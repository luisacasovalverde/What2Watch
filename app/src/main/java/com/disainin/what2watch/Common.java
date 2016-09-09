package com.disainin.what2watch;

import java.util.Locale;

public class Common {

    public static final String
//            DEVICE_LANG = Locale.getDefault().getLanguage(), // es
            DEVICE_LANG = Locale.getDefault().toString().replace("_", "-"), // es-ES
            TMDB_APIKEY = "1947a2516ec6cb3cf97ef1da21fdaa87";

    public static final int PERMISSION_RECORD_AUDIO = 1;

    public static final int TMDB_CODE_MOVIES = 0,
            TMDB_CODE_SERIES = 2,
            TMDB_CODE_PEOPLE = 1;


}
