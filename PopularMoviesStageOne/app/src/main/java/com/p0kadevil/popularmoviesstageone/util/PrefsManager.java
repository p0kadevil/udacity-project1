package com.p0kadevil.popularmoviesstageone.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefsManager
{
    public static final String KEY_SORT_ORDER = "SORT_ORDER";

    public static void writeInt(Context context, String key, int value) {

        SharedPreferences settings = context.getSharedPreferences(KEY_SORT_ORDER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public  static int getInt(Context context, String key) {

        SharedPreferences settings = context.getSharedPreferences(KEY_SORT_ORDER, Context.MODE_PRIVATE);
        return settings.getInt(key, -1);
    }
}
