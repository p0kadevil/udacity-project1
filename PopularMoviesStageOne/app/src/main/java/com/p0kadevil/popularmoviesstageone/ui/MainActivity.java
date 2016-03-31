package com.p0kadevil.popularmoviesstageone.ui;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import com.google.gson.Gson;
import com.p0kadevil.popularmoviesstageone.R;
import com.p0kadevil.popularmoviesstageone.adapters.PosterAdapter;
import com.p0kadevil.popularmoviesstageone.models.MovieDbResponse;
import com.p0kadevil.popularmoviesstageone.services.MovieDbIntentService;
import com.p0kadevil.popularmoviesstageone.util.PrefsManager;

public class MainActivity extends AppCompatActivity
{
    public static final String TAG = MainActivity.class.getSimpleName();

    public static final String EXTRA_MOVIE_DETAIL_OBJECT = "EXTRA_MOVIE_DETAIL_OBJECT";

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

                Intent detailActivityIntent = new Intent(MainActivity.this, DetailActivity.class);
                detailActivityIntent.putExtra(EXTRA_MOVIE_DETAIL_OBJECT, mPosterAdapter.getMovieInfoAtIndex(position));
                startActivity(detailActivityIntent);
            }
        });

        int lastSortOrder = PrefsManager.getInt(this, PrefsManager.KEY_SORT_ORDER);

        if(getSupportActionBar() != null)
        {
            String title = getString(R.string.app_name);
            title += lastSortOrder == MovieDbIntentService.SortFilter.POPULAR.ordinal() ?
                    " (" + getString(R.string.menu_main_sort_by_popular) + ")" :
                    " (" + getString(R.string.menu_main_sort_by_top_rated) + ")";

            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        mMovieDbResultReceiver = new MovieDbResultReceiver();

        IntentFilter filter = new IntentFilter(MovieDbIntentService.BROADCAST_MOVIE_DB_RESULT);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMovieDbResultReceiver, filter);

        if(mMovieDbResponse == null)
        {
            int lastSortOrder = PrefsManager.getInt(this, PrefsManager.KEY_SORT_ORDER);

            showProgressDialog(getResources().getString(R.string.please_wait), getResources().getString(R.string.loading_get_images));
            Intent movieDbIntent = new Intent(this, MovieDbIntentService.class);
            movieDbIntent.putExtra(MovieDbIntentService.EXTRA_SORT_FILTER, lastSortOrder);
            startService(movieDbIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == R.id.menu_sort_order)
        {
            return true;
        }

        MovieDbIntentService.SortFilter selectedFilter;

        switch(item.getItemId())
        {
            case R.id.menu_sort_popular:
                selectedFilter = MovieDbIntentService.SortFilter.POPULAR;

                if(getSupportActionBar() != null)
                    getSupportActionBar().setTitle(getString(R.string.app_name) + " (" + getString(R.string.menu_main_sort_by_popular) + ")");

                break;
            case R.id.menu_sort_top_rated:
                selectedFilter = MovieDbIntentService.SortFilter.TOP_RATED;

                if(getSupportActionBar() != null)
                    getSupportActionBar().setTitle(getString(R.string.app_name) + " (" + getString(R.string.menu_main_sort_by_top_rated) + ")");

                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        PrefsManager.writeInt(this, PrefsManager.KEY_SORT_ORDER, selectedFilter.ordinal());

        showProgressDialog(getResources().getString(R.string.please_wait), getResources().getString(R.string.loading_get_images));
        Intent movieDbIntent = new Intent(this, MovieDbIntentService.class);
        movieDbIntent.putExtra(MovieDbIntentService.EXTRA_SORT_FILTER, selectedFilter.ordinal());
        startService(movieDbIntent);

        return true;
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

                    if(mPosterAdapter == null)
                    {
                        mPosterAdapter = new PosterAdapter(context, mMovieDbResponse.getResults());
                        mGridView.setAdapter(mPosterAdapter);
                    }
                    else
                    {
                        mPosterAdapter.setDataSource(mMovieDbResponse.getResults());
                    }
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
