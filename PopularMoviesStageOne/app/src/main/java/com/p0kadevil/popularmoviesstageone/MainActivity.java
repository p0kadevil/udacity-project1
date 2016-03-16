package com.p0kadevil.popularmoviesstageone;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import com.google.gson.Gson;
import com.p0kadevil.popularmoviesstageone.adapters.PosterAdapter;
import com.p0kadevil.popularmoviesstageone.models.MovieDbResponse;
import com.p0kadevil.popularmoviesstageone.services.MovieDbIntentService;


public class MainActivity extends AppCompatActivity
{
    public static final String TAG = MainActivity.class.getSimpleName();

    private MovieDbResultReceiver mMovieDbResultReceiver;
    private PosterAdapter mPosterAdapter;
    private MovieDbResponse mMovieDbResponse;

    private GridView mGridView;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGridView = (GridView) findViewById(R.id.gv_posters);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                //TODO: Details Screen
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        mMovieDbResultReceiver = new MovieDbResultReceiver();

        IntentFilter filter = new IntentFilter(MovieDbIntentService.BROADCAST_MOVIE_DB_RESULT);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMovieDbResultReceiver, filter);

        showProgressDialog(getResources().getString(R.string.please_wait), getResources().getString(R.string.loading_get_images));

        if(mMovieDbResponse == null)
        {
            Intent movieDbIntent = new Intent(this, MovieDbIntentService.class);
            movieDbIntent.putExtra(MovieDbIntentService.EXTRA_SORT_FILTER, MovieDbIntentService.SortFilter.POPULAR);
            startService(movieDbIntent);
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMovieDbResultReceiver);
    }

    private void showProgressDialog(String title, String message)
    {
        mProgressDialog = ProgressDialog.show(this, title, message, true);
    }

    private class MovieDbResultReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if(intent != null && intent.hasExtra(MovieDbIntentService.EXTRA_RESULT_JSON))
            {
                Gson gson = new Gson();

                try
                {
                    mMovieDbResponse = gson.fromJson(intent.getStringExtra(MovieDbIntentService.EXTRA_RESULT_JSON), MovieDbResponse.class);
                    mPosterAdapter = new PosterAdapter(context, mMovieDbResponse.getResults());
                    mGridView.setAdapter(mPosterAdapter);
                }
                catch(Exception e)
                {
                    Log.e(TAG, "An exception was thrown while parsing the JSON and setting the Adapter for the gridView: " + e.getMessage());
                    e.printStackTrace();
                }
                finally
                {
                    if(mProgressDialog != null){
                        mProgressDialog.dismiss();
                    }
                }
            }
            else
            {
                //TODO: Error Screen
            }
        }
    }
}
