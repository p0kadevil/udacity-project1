package com.p0kadevil.popularmoviesstageone.db;

import android.provider.BaseColumns;


public class ReviewContract implements BaseColumns
{
    public static final String TABLE_NAME = "reviews";

    public static final String COLUMN_MOVIE_ID = "movie_id";
    public static final String COLUMN_REVIEW_TEXT = "review_text";
    public static final String COLUMN_AUTHOR = "author";

}
