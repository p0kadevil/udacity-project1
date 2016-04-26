package com.p0kadevil.popularmoviesstageone.ui;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import com.google.gson.Gson;
import com.p0kadevil.popularmoviesstageone.R;
import com.p0kadevil.popularmoviesstageone.models.MovieDbResponse;
import com.p0kadevil.popularmoviesstageone.models.MovieInfo;
import com.p0kadevil.popularmoviesstageone.services.MovieDbIntentService;
import com.p0kadevil.popularmoviesstageone.util.PrefsManager;


public class MainActivity extends AppCompatActivity
{
    public static final String TAG = MainActivity.class.getSimpleName();

    private MovieDbResultReceiver mMovieDbResultReceiver;
    private MovieDbResponse mMovieDbResponse;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTransaction ftMain = getSupportFragmentManager().beginTransaction();
        ftMain.add(R.id.fl_container_left, MainFragment.newInstance(), MainFragment.TAG);
        ftMain.commit();

        if(findViewById(R.id.fl_container_right) != null)
        {
            FragmentTransaction ftDetail = getSupportFragmentManager().beginTransaction();
            ftDetail.add(R.id.fl_container_right, DetailFragment.newInstance(null), DetailFragment.TAG);
            ftDetail.commit();
        }

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
        else
        {
            getMainFragment().reloadGridViewWithPosters(mMovieDbResponse);
        }
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

    public void showMovieDetail(MovieInfo movieInfo)
    {
        if(getDetailFragment() == null)
        {
            FragmentTransaction ftDetail = getSupportFragmentManager().beginTransaction();
            ftDetail.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ftDetail.replace(R.id.fl_container_left, DetailFragment.newInstance(movieInfo), DetailFragment.TAG);
            ftDetail.addToBackStack(MainFragment.TAG);
            ftDetail.commit();
            return;
        }

        getDetailFragment().fillDetailFragmentWithMovieInfo(movieInfo);
    }

    private MainFragment getMainFragment()
    {
        return (MainFragment) getSupportFragmentManager().findFragmentByTag(MainFragment.TAG);
    }

    private DetailFragment getDetailFragment()
    {
        return (DetailFragment) getSupportFragmentManager().findFragmentByTag(DetailFragment.TAG);
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
                    getMainFragment().setErrorTextViewVisibility(false);
                    getMainFragment().reloadGridViewWithPosters(mMovieDbResponse);

                    if(getDetailFragment() != null)
                    {
                        getDetailFragment().fillDetailFragmentWithMovieInfo(mMovieDbResponse.getResults().get(0));
                    }
                }
                catch(Exception e)
                {
                    Log.e(TAG, "An exception was thrown while parsing the JSON and setting the Adapter for the gridView: " + e.getMessage());
                    e.printStackTrace();

                    getMainFragment().setErrorTextViewVisibility(true);
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
                getMainFragment().setErrorTextViewVisibility(true);
            }
        }
    }
}
