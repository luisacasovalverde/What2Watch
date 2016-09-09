package com.disainin.what2watch;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Shader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.apache.commons.lang3.ObjectUtils;

import info.movito.themoviedbapi.model.config.TmdbConfiguration;

public class Utility {

    public static int calculateNoOfColumns(Context context, int width) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (dpWidth / width);
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

    @Nullable
    public static TmdbConfiguration getConfigurationTMDBAPI() throws Exception {
        ConfigurationTaskTMDBAPI configurationTask = new ConfigurationTaskTMDBAPI();
        configurationTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return configurationTask.get();
    }

    public static String getBaseUrlTMDBAPI() throws NullPointerException {
        try {
            if (getConfigurationTMDBAPI() != null) {
                return getConfigurationTMDBAPI().getSecureBaseUrl();
            }
        } catch (Exception ignored) {
        }

        return "https://image.tmdb.org/t/p/";
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

    public static int getDeviceWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point size = new Point();
        wm.getDefaultDisplay().getSize(size);

        return size.x;
    }

    public static int getDeviceHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point size = new Point();
        wm.getDefaultDisplay().getSize(size);

        return size.y;
    }

    public static int getDeviceDpi(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);

        return metrics.densityDpi;
    }

    public static int getColumnsFromWidth(Context context) {
        return Math.round(Utility.getDeviceWidth(context) / (float) Utility.getDeviceDpi(context));
    }

    public static void showRequestPermissionAction(final Context context, View layout, int text) {
        Snackbar.make(layout, text, Snackbar.LENGTH_LONG)
                .setAction(R.string.general_settings, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                        intent.setData(uri);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        context.startActivity(intent);
                    }
                }).setActionTextColor(Color.WHITE).show();
    }

    public static int getWidthSizeColumns(Context context, float columns) {
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            return Math.round(Utility.getDeviceWidth(context.getApplicationContext()) / columns);
        } else {
            return Math.round(Utility.getDeviceHeight(context.getApplicationContext()) / columns);
        }
    }
}