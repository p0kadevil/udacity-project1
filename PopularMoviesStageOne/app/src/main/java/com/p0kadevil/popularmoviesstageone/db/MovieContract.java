package com.p0kadevil.popularmoviesstageone.db;

import android.provider.BaseColumns;


public class MovieContract implements BaseColumns
{
    public static final String TABLE_NAME = "movies";

    public static final String COLUMN_POSTER_PATH = "poster_path";
    public static final String COLUMN_ORIGINAL_TITLE = "original_title";
    public static final String COLUMN_RELEASE_DATE = "release_date";
    public static final String COLUMN_VOTE_AVG = "vote_avg";
    public static final String COLUMN_OVERVIEW = "overview";
    public static final String COLUMN_IS_FAV = "is_fav";
}
