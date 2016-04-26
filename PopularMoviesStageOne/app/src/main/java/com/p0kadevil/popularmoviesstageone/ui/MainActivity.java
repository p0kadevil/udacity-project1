package com.p0kadevil.popularmoviesstageone.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import com.p0kadevil.popularmoviesstageone.R;
import com.p0kadevil.popularmoviesstageone.models.MovieInfo;
import com.p0kadevil.popularmoviesstageone.services.MovieDbIntentService;
import com.p0kadevil.popularmoviesstageone.util.PrefsManager;


public class MainActivity extends AppCompatActivity
{
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(getSupportFragmentManager().findFragmentByTag(MainFragment.TAG) == null)
        {
            FragmentTransaction ftMain = getSupportFragmentManager().beginTransaction();
            ftMain.add(R.id.fl_container_left, MainFragment.newInstance(), MainFragment.TAG);
            ftMain.commit();
        }

        if(findViewById(R.id.fl_container_right) != null && getSupportFragmentManager().findFragmentByTag(DetailFragment.TAG) == null)
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

    public DetailFragment getDetailFragment()
    {
        return (DetailFragment) getSupportFragmentManager().findFragmentByTag(DetailFragment.TAG);
    }

    public void showProgressDialog(String title, String message)
    {
        mProgressDialog = ProgressDialog.show(this, title, message, true);
    }

    public void dismissProgressDialog()
    {
        if(mProgressDialog != null && mProgressDialog.isShowing())
        {
            mProgressDialog.dismiss();
        }
    }
}
