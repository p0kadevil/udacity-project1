package com.p0kadevil.popularmoviesstageone.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MovieDbIntentService extends IntentService
{
    public enum SortFilter{

        POPULAR,
        TOP_RATED,
        FAVS
    }

    public static final String TAG = MovieDbIntentService.class.getSimpleName();

    public static final String EXTRA_SORT_FILTER = "MOVIE_DB_EXTRA_SORT_FILTER";
    public static final String EXTRA_TRAILERS_AND_REVIEWS = "MOVIE_DB_EXTRA_TRAILERS_AND_REVIEWS";
    public static final String EXTRA_TRAILER_AND_REVIEW_ID = "MOVIE_DB_EXTRA_TRAILER_AND_REVIEW_ID";
    public static final String EXTRA_RESULT_JSON = "EXTRA_RESULT_JSON";
    public static final String EXTRA_RESULT_JSON_TRAILERS = "EXTRA_RESULT_JSON_TRAILERS";
    public static final String EXTRA_RESULT_JSON_REVIEWS = "EXTRA_RESULT_JSON_REVIEWS";
    public static final String BROADCAST_MOVIE_DB_RESULT = "com.p0kadevil.popularmoviesstageone.services.MOVIE_DB_RESULT";
    public static final String BROADCAST_TRAILERS_AND_REVIEWS_RESULT = "com.p0kadevil.popularmoviesstageone.services.MOVIE_DB_TRAILERS_AND_REVIEWS_RESULT";

    private static final String API_KEY = "";

    private final String MOVIE_DB_TRAILERS = "http://api.themoviedb.org/3/movie/%@/videos";
    private final String MOVIE_DB_REVIEWS = "http://api.themoviedb.org/3/movie/%@/reviews";
    private final String MOVIE_DB_POPULAR_URL = "http://api.themoviedb.org/3/movie/popular";
    private final String MOVIE_DB_TOP_RATED_URL = "http://api.themoviedb.org/3/movie/top_rated";

    public MovieDbIntentService()
    {
        super(MovieDbIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        if(intent != null){

            String resultMovies = null;
            String resultTrailers = null;
            String resultReviews = null;

            try{

                if(intent.getBooleanExtra(EXTRA_TRAILERS_AND_REVIEWS, false))
                {
                    resultTrailers = getJsonWithUrl(new URL(MOVIE_DB_TRAILERS.replace("%@",
                            String.valueOf(intent.getIntExtra(EXTRA_TRAILER_AND_REVIEW_ID, 0))) + "?api_key=" + API_KEY));

                    resultReviews = getJsonWithUrl(new URL(MOVIE_DB_REVIEWS.replace("%@",
                            String.valueOf(intent.getIntExtra(EXTRA_TRAILER_AND_REVIEW_ID, 0))) + "?api_key=" + API_KEY));
                }
                else
                {
                    int filter = intent.getIntExtra(EXTRA_SORT_FILTER, SortFilter.POPULAR.ordinal());

                    resultMovies = getJsonWithUrl(new URL(filter == SortFilter.POPULAR.ordinal() ?
                            MOVIE_DB_POPULAR_URL + "?api_key=" + API_KEY : MOVIE_DB_TOP_RATED_URL + "?api_key=" + API_KEY));
                }

            }catch(Exception e){

                Log.e(TAG, "Error during calling the movieDB API: " + e.getMessage());
                e.printStackTrace();
            }
            finally{

                Intent resultIntent;

                if(intent.getBooleanExtra(EXTRA_TRAILERS_AND_REVIEWS, false))
                {
                    resultIntent = new Intent(BROADCAST_TRAILERS_AND_REVIEWS_RESULT);
                    resultIntent.putExtra(MovieDbIntentService.EXTRA_RESULT_JSON_TRAILERS, resultTrailers);
                    resultIntent.putExtra(MovieDbIntentService.EXTRA_RESULT_JSON_REVIEWS, resultReviews);
                }
                else
                {
                    resultIntent = new Intent(BROADCAST_MOVIE_DB_RESULT);
                    resultIntent.putExtra(MovieDbIntentService.EXTRA_RESULT_JSON, resultMovies);
                }

                LocalBroadcastManager.getInstance(this).sendBroadcast(resultIntent);
            }
        }
    }

    private String getJsonWithUrl(URL url)
    {
        HttpURLConnection urlConnection = null;
        StringBuilder resultJson = new StringBuilder();

        try
        {
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while((line = reader.readLine()) != null)
            {
                resultJson.append(line);
            }

        }
        catch(Exception e)
        {
            Log.e(TAG, "Error during calling the movieDB API: " + e.getMessage());
            e.printStackTrace();
        }
        finally
        {
            if(urlConnection != null){
                urlConnection.disconnect();
            }
        }

        return resultJson.toString();
    }
}
