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
        TOP_RATED
    }

    public static final String TAG = MovieDbIntentService.class.getSimpleName();

    public static final String EXTRA_SORT_FILTER = "MOVIE_DB_EXTRA_SORT_FILTER";
    public static final String EXTRA_RESULT_JSON = "EXTRA_RESULT_JSON";
    public static final String BROADCAST_MOVIE_DB_RESULT = "com.p0kadevil.popularmoviesstageone.services.MOVIE_DB_RESULT";

    private static final String API_KEY = "";

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

            HttpURLConnection urlConnection = null;
            StringBuilder resultJson = new StringBuilder();

            try{

                int filter = intent.getIntExtra(EXTRA_SORT_FILTER, SortFilter.POPULAR.ordinal());

                URL url = new URL(filter == SortFilter.POPULAR.ordinal() ?
                        MOVIE_DB_POPULAR_URL + "?api_key=" + API_KEY : MOVIE_DB_TOP_RATED_URL + "?api_key=" + API_KEY);

                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    resultJson.append(line);
                }

            }catch(Exception e){

                Log.e(TAG, "Error during calling the API: " + e.getMessage());
                e.printStackTrace();
            }
            finally{

                if(urlConnection != null){
                    urlConnection.disconnect();
                }

                Intent resultIntent = new Intent(BROADCAST_MOVIE_DB_RESULT);
                resultIntent.putExtra(MovieDbIntentService.EXTRA_RESULT_JSON, resultJson.toString());
                LocalBroadcastManager.getInstance(this).sendBroadcast(resultIntent);
            }
        }
    }
}
