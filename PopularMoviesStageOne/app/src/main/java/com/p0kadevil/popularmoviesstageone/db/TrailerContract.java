package com.p0kadevil.popularmoviesstageone.db;

import android.provider.BaseColumns;


public class TrailerContract implements BaseColumns
{
    public static final String TABLE_NAME = "trailers";

    public static final String COLUMN_MOVIE_ID = "movie_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_KEY = "key";
}
