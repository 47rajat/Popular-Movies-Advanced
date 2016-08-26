package com.example.stark.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callback {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String DETAIL_FRAGMENT_TAG = "DTAG";
    private static final String SORTING_ORDER = "Sorting";
    private boolean mTwoPane;
    private String mSortOrder;

    @Override
    protected void onResume() {
        super.onResume();
        String sortOrder = Utility.getSortingOrder(this);
        if(mTwoPane && !sortOrder.equals(mSortOrder)){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_detail, new NoItemSelectedFragment())
                    .commit();
            mSortOrder = sortOrder;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(findViewById(R.id.fragment_detail) != null){
            mTwoPane = true;
            if(savedInstanceState == null){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_detail, new NoItemSelectedFragment(), DETAIL_FRAGMENT_TAG)
                        .commit();
            } else {
                mSortOrder = savedInstanceState.getString(SORTING_ORDER);
            }
        }else {
            mTwoPane = false;
//            getSupportActionBar().setElevation(0f);
        }
        setSupportActionBar(toolbar);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(mTwoPane && mSortOrder != null){
            outState.putString(SORTING_ORDER, mSortOrder);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemSelected(Parcelable parcelable) {
        if(mTwoPane){
            Bundle args = new Bundle();
            args.putParcelable(DetailActivityFragment.DETAIL_PARCELABLE, parcelable);

            DetailActivityFragment detailActivityFragment = new DetailActivityFragment();
            detailActivityFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_detail, detailActivityFragment, DETAIL_FRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class).putExtra(Intent.EXTRA_TEXT, parcelable);
            startActivity(intent);
        }
    }
}
