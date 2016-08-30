package com.disainin.what2watch;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.util.DisplayMetrics;
import android.widget.Toast;

import info.movito.themoviedbapi.model.config.TmdbConfiguration;

public class Utility {

    public static int calculateNoOfColumns(Context context, int width) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / width);
        return noOfColumns;
    }

    public static boolean isNetworkConnected(Context context) {
        try {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            return mNetworkInfo != null;
        } catch (NullPointerException e) {
            return false;
        }
    }

    public static TmdbConfiguration getConfigurationTMDBAPI(Context context) {
        try {
            ConfigurationTaskTMDBAPI configurationTask = new ConfigurationTaskTMDBAPI();
            configurationTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            return configurationTask.get();
        } catch (Exception e) {
            Toast.makeText(context, "Hay problemas de conexión con servidores externos", Toast.LENGTH_SHORT).show();
        }

        return null;
    }
}