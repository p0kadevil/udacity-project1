package com.p0kadevil.popularmoviesstageone.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.p0kadevil.popularmoviesstageone.models.TrailerInfo;
import java.util.ArrayList;


public class TrailerRepository
{
    public static final String TAG = TrailerRepository.class.getSimpleName();

    public static void insertTrailers(Context context, ArrayList<TrailerInfo> trailerInfos, int movieId)
    {
        if(trailerInfos == null || trailerInfos.size() == 0 || movieId < 0)
        {
            return;
        }

        LocalMovieDbAccess dbAccess = new LocalMovieDbAccess(context);
        SQLiteDatabase db = dbAccess.getWritableDatabase();

        db.beginTransaction();

        try
        {
            for(TrailerInfo trailerInfo : trailerInfos)
            {
                ContentValues contentValues = new ContentValues();
                contentValues.put(TrailerContract._ID, trailerInfo.getId());
                contentValues.put(TrailerContract.COLUMN_KEY, trailerInfo.getKey());
                contentValues.put(TrailerContract.COLUMN_MOVIE_ID, movieId);
                contentValues.put(TrailerContract.COLUMN_NAME, trailerInfo.getName());
                db.insertWithOnConflict(TrailerContract.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
            }

            db.setTransactionSuccessful();
        }
        catch(Exception e)
        {
            Log.e(TAG, "An error occured while inserting trailer: " + e.getMessage());
            e.printStackTrace();
        }
        finally
        {
            db.endTransaction();
        }
    }
}