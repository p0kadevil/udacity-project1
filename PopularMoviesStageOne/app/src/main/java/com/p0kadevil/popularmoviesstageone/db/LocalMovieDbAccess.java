package com.p0kadevil.popularmoviesstageone.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class LocalMovieDbAccess extends SQLiteOpenHelper
{
    private static final String DB_NAME = "movie_db";
    private static final int DB_VERSION = 1;

    private static final String SQL_CREATE_TABLE_MOVIES =
            "CREATE TABLE " + MovieContract.TABLE_NAME + " (" +
                    MovieContract._ID + " INTEGER PRIMARY KEY," +
                    MovieContract.COLUMN_ORIGINAL_TITLE + " TEXT," +
                    MovieContract.COLUMN_POSTER_PATH + " TEXT," +
                    MovieContract.COLUMN_RELEASE_DATE + " TEXT," +
                    MovieContract.COLUMN_VOTE_AVG + " FLOAT," +
                    MovieContract.COLUMN_OVERVIEW + " TEXT," +
                    MovieContract.COLUMN_IS_FAV + " INTEGER DEFAULT 0)";

    private static final String SQL_CREATE_TABLE_REVIEWS =
            "CREATE TABLE " + ReviewContract.TABLE_NAME + " (" +
                    ReviewContract._ID + " TEXT PRIMARY KEY," +
                    ReviewContract.COLUMN_MOVIE_ID + " INTEGER," +
                    ReviewContract.COLUMN_REVIEW_TEXT + " TEXT," +
                    ReviewContract.COLUMN_AUTHOR + " TEXT )";

    private static final String SQL_CREATE_TABLE_TRAILERS =
            "CREATE TABLE " + TrailerContract.TABLE_NAME + " (" +
                    TrailerContract._ID + " TEXT PRIMARY KEY," +
                    TrailerContract.COLUMN_MOVIE_ID + " INTEGER," +
                    TrailerContract.COLUMN_NAME + " TEXT," +
                    TrailerContract.COLUMN_KEY + " TEXT )";


    public LocalMovieDbAccess(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(SQL_CREATE_TABLE_MOVIES);
        db.execSQL(SQL_CREATE_TABLE_REVIEWS);
        db.execSQL(SQL_CREATE_TABLE_TRAILERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }
}
