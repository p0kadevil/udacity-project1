package com.p0kadevil.popularmoviesstageone.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.p0kadevil.popularmoviesstageone.models.ReviewInfo;
import java.util.ArrayList;


public class ReviewRepository
{
    public static final String TAG = ReviewRepository.class.getSimpleName();

    public static void insertReviews(Context context, ArrayList<ReviewInfo> reviewInfos, int movieId)
    {
        if(reviewInfos == null || reviewInfos.size() == 0 || movieId < 0)
        {
            return;
        }

        LocalMovieDbAccess dbAccess = new LocalMovieDbAccess(context);
        SQLiteDatabase db = dbAccess.getWritableDatabase();

        db.beginTransaction();

        try
        {
            for(ReviewInfo revInfo : reviewInfos)
            {
                ContentValues contentValues = new ContentValues();
                contentValues.put(ReviewContract._ID, revInfo.getId());
                contentValues.put(ReviewContract.COLUMN_MOVIE_ID, movieId);
                contentValues.put(ReviewContract.COLUMN_REVIEW_TEXT, revInfo.getContent());
                contentValues.put(ReviewContract.COLUMN_AUTHOR, revInfo.getAuthor());
                db.insertWithOnConflict(ReviewContract.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
            }

            db.setTransactionSuccessful();
        }
        catch(Exception e)
        {
            Log.e(TAG, "An error occured while inserting review: " + e.getMessage());
            e.printStackTrace();
        }
        finally
        {
            db.endTransaction();
        }
    }
}
