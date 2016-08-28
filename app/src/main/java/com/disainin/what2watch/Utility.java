package com.disainin.what2watch;

import android.content.Context;
import android.util.DisplayMetrics;

public class Utility {

    public static int calculateNoOfColumns(Context context, int width) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / width);
        return noOfColumns;
    }

}