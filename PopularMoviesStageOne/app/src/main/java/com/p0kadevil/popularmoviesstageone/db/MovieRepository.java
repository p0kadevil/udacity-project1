package com.p0kadevil.popularmoviesstageone.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.p0kadevil.popularmoviesstageone.models.MovieDbResponse;
import com.p0kadevil.popularmoviesstageone.models.MovieInfo;
import com.p0kadevil.popularmoviesstageone.models.ReviewInfo;
import java.util.ArrayList;


public class MovieRepository
{
    public static final String TAG = MovieRepository.class.getSimpleName();

    public static MovieDbResponse getMovies(Context context, boolean onlyFavs)
    {
        LocalMovieDbAccess dbAccess = new LocalMovieDbAccess(context);
        SQLiteDatabase db = dbAccess.getReadableDatabase();

        Cursor cursorMovies = null;
        Cursor cursorReviews = null;

        MovieDbResponse simulatedMovieDbResponse = new MovieDbResponse();

        try
        {
            ArrayList<MovieInfo> movies = new ArrayList<>();

            cursorMovies = db.query(MovieContract.TABLE_NAME,
                    null,
                    onlyFavs ? MovieContract.COLUMN_IS_FAV + " = ?" : null,
                    onlyFavs ? new String[]{"1"}: null,
                    null,
                    null,
                    null);

            while(cursorMovies.moveToNext())
            {
                int movieId = cursorMovies.getInt(cursorMovies.getColumnIndex(MovieContract._ID));
                String posterPath = cursorMovies.getString(cursorMovies.getColumnIndex(MovieContract.COLUMN_POSTER_PATH));
                String originalTitle = cursorMovies.getString(cursorMovies.getColumnIndex(MovieContract.COLUMN_ORIGINAL_TITLE));
                String releaseDate = cursorMovies.getString(cursorMovies.getColumnIndex(MovieContract.COLUMN_RELEASE_DATE));
                double voteAvg = cursorMovies.getDouble(cursorMovies.getColumnIndex(MovieContract.COLUMN_VOTE_AVG));
                String overview = cursorMovies.getString(cursorMovies.getColumnIndex(MovieContract.COLUMN_OVERVIEW));

                MovieInfo movieInfo = new MovieInfo(movieId, posterPath, originalTitle, releaseDate,
                        voteAvg, overview);

                cursorReviews = db.query(ReviewContract.TABLE_NAME,
                        new String[]{ReviewContract.COLUMN_REVIEW_TEXT, ReviewContract.COLUMN_AUTHOR},
                        ReviewContract.COLUMN_MOVIE_ID + " = ?",
                        new String[]{String.valueOf(movieId)},
                        null,
                        null,
                        null);

                ArrayList<ReviewInfo> reviews = new ArrayList<>();

                while(cursorReviews.moveToNext())
                {
                    String reviewText = cursorReviews.getString(cursorReviews.getColumnIndex(ReviewContract.COLUMN_REVIEW_TEXT));
                    String author = cursorReviews.getString(cursorReviews.getColumnIndex(ReviewContract.COLUMN_AUTHOR));
                    reviews.add(new ReviewInfo(reviewText, author));
                }

                movieInfo.setReviews(reviews);
                cursorReviews.close();

                movies.add(movieInfo);
            }

            simulatedMovieDbResponse.setResults(movies);
        }
        catch(Exception e)
        {
            Log.e(TAG, "Error while reading movie infos from local DB: " + e.getMessage());
            e.printStackTrace();
        }
        finally
        {
            if(cursorMovies != null && !cursorMovies.isClosed())
            {
                cursorMovies.close();
            }

            if(cursorReviews != null && !cursorReviews.isClosed())
            {
                cursorReviews.close();
            }
        }

        return simulatedMovieDbResponse;
    }

    public static void insertMovies(Context context, ArrayList<MovieInfo> movies)
    {
        if(movies == null || movies.size() == 0)
        {
            return;
        }

        LocalMovieDbAccess dbAccess = new LocalMovieDbAccess(context);
        SQLiteDatabase db = dbAccess.getWritableDatabase();

        db.beginTransaction();

        try
        {
            for(MovieInfo movie : movies)
            {
                ContentValues contentValues = new ContentValues();
                contentValues.put(MovieContract._ID, movie.getId());
                contentValues.put(MovieContract.COLUMN_ORIGINAL_TITLE, movie.getOriginalTitle());
                contentValues.put(MovieContract.COLUMN_OVERVIEW, movie.getOverview());
                contentValues.put(MovieContract.COLUMN_POSTER_PATH, movie.getPosterPath());
                contentValues.put(MovieContract.COLUMN_RELEASE_DATE, movie.getReleaseDate());
                contentValues.put(MovieContract.COLUMN_VOTE_AVG, movie.getVoteAverage());
                db.insertWithOnConflict(MovieContract.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
            }

            db.setTransactionSuccessful();
        }
        catch(Exception e)
        {
            Log.e(TAG, "An error occured while inserting movies: " + e.getMessage());
            e.printStackTrace();
        }
        finally
        {
            db.endTransaction();
        }
    }

    public static boolean addFavorite(Context context, MovieInfo movieInfo)
    {
        if(movieInfo == null)
        {
            return false;
        }

        LocalMovieDbAccess dbAccess = new LocalMovieDbAccess(context);
        SQLiteDatabase db = dbAccess.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.COLUMN_IS_FAV, true);

        return db.update(MovieContract.TABLE_NAME,
                contentValues,
                MovieContract._ID + " = ?",
                new String[]{String.valueOf(movieInfo.getId())}) > 0;
    }

    public static boolean removeFavorite(Context context, MovieInfo movieInfo)
    {
        if(movieInfo == null)
        {
            return false;
        }

        LocalMovieDbAccess dbAccess = new LocalMovieDbAccess(context);
        SQLiteDatabase db = dbAccess.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.COLUMN_IS_FAV, false);

        return db.update(MovieContract.TABLE_NAME,
                contentValues,
                MovieContract._ID + " = ?",
                new String[]{String.valueOf(movieInfo.getId())}) > 0;
    }

    public static boolean isFavorite(Context context, MovieInfo movieInfo)
    {
        if(movieInfo == null)
        {
            return false;
        }

        LocalMovieDbAccess dbAccess = new LocalMovieDbAccess(context);
        SQLiteDatabase db = dbAccess.getReadableDatabase();

        Cursor cursor = null;

        try
        {
            cursor = db.query(MovieContract.TABLE_NAME,
                    null,
                    MovieContract._ID + " = ? AND " + MovieContract.COLUMN_IS_FAV + " = ?",
                    new String[]{String.valueOf(movieInfo.getId()), "1"},
                    null, null, null);

            return cursor.moveToFirst();
        }
        catch(Exception e)
        {
            Log.e(TAG, "An error occured while checking favorite: " + e.getMessage());
            e.printStackTrace();
        }
        finally
        {
            if(cursor != null && !cursor.isClosed())
            {
                cursor.close();
            }
        }

        return false;
    }
}
