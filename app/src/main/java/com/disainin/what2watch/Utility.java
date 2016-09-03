package com.disainin.what2watch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

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

    public static void getImageRounded(Context context, String url, final int r, final int m, ImageView img, Integer placeholder) {

        Picasso.with(context).load(url).placeholder(placeholder).transform(new Transformation() {
            private final int radius = r;
            private final int margin = m;

            @Override
            public Bitmap transform(final Bitmap source) {
                final Paint paint = new Paint();
                paint.setAntiAlias(true);
                paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

                Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(output);
                canvas.drawRoundRect(new RectF(margin, margin, source.getWidth() - margin, source.getHeight() - margin), radius, radius, paint);

                if (source != output) {
                    source.recycle();
                }

                return output;
            }

            @Override
            public String key() {
                return "rounded";
            }
        }).into(img, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError() {
            }
        });
    }
}