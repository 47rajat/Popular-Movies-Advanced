package com.example.stark.popularmovies;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.stark.popularmovies.data.MovieContract;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private CustomMovieAdapter mMovieAdapter;
    private ArrayList<MovieObject> mMovieList;
    private final String PARCELABLE_LIST_KEY = "movies";
    private final String SORTING_ORDER_KEY = "Order";
    private String mOrder;
    private static final String SHOW_FAVOURITES = "favourites";

    private MovieAdapter mFavouritesAdapter;
    private static final int FAVOURITES_LOADER_ID = 0;

    private static final String[] MOVIE_COLUMNS = {
            MovieContract.FavouriteEntry.TABLE_NAME + "." + MovieContract.FavouriteEntry._ID,
            MovieContract.FavouriteEntry.MOVIE_POSTER,
            MovieContract.FavouriteEntry.MOVIE_PLOT,
            MovieContract.FavouriteEntry.MOVIE_RELEASE_DATE,
            MovieContract.FavouriteEntry.MOVIE_TITLE,
            MovieContract.FavouriteEntry.MOVIE_RATING,
            MovieContract.FavouriteEntry.MOVIE_ID
    };

    public static final int COLUMN_ID = 0;
    public static final int COLUMN_ICON = 1;
    public static final int COLUMN_PLOT = 2;
    public static final int COLUMN_RELEASE_DATE = 3;
    public static final int COLUMN_TITLE = 4;
    public static final int COLUMN_RATING = 5;
    public static final int COLUMN_MOVIE_ID = 6;

    private GridView mGridView;
    private int mPosition;
    private final String LAST_POSITION = "Position";

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!Utility.getSortingOrder(getActivity()).equals(SHOW_FAVOURITES)){
            if(savedInstanceState == null || !savedInstanceState.containsKey(PARCELABLE_LIST_KEY)) {
                mMovieAdapter = new CustomMovieAdapter(getActivity(), new ArrayList<MovieObject>());
                mOrder = Utility.getSortingOrder(getActivity());
                if(Utility.isOnline(getActivity())){
                    new FetchMovieData(getActivity(), mMovieAdapter).execute();
                }

            } else{
                mMovieList = savedInstanceState.getParcelableArrayList(PARCELABLE_LIST_KEY);
                mOrder = savedInstanceState.getString(SORTING_ORDER_KEY);
                mMovieAdapter = new CustomMovieAdapter(getActivity(), mMovieList);
                if(mMovieList.size() == 0 && Utility.isOnline(getActivity())){
                    onSortOrderChange();
                }
            }
        } else{
            mOrder = Utility.getSortingOrder(getActivity());
        }

        if(savedInstanceState != null && savedInstanceState.containsKey(LAST_POSITION)){
            mPosition = savedInstanceState.getInt(LAST_POSITION);
            if(!Utility.getSortingOrder(getActivity()).equals(SHOW_FAVOURITES) && mGridView != null){
                mGridView.smoothScrollToPosition(mPosition);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(!Utility.getSortingOrder(getActivity()).equals(SHOW_FAVOURITES)){
            ArrayList<MovieObject> movieList = new ArrayList<>();
            for(int i = 0; i< mMovieAdapter.getCount(); i++){
                movieList.add(mMovieAdapter.getItem(i));
            }
            outState.putParcelableArrayList(PARCELABLE_LIST_KEY, movieList);
            if(mOrder != null) {
                outState.putString(SORTING_ORDER_KEY, mOrder);
            }
        }
        if(mPosition != GridView.INVALID_POSITION){
            outState.putInt(LAST_POSITION, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if(Utility.getSortingOrder(getActivity()).equals(SHOW_FAVOURITES)){
            getLoaderManager().initLoader(FAVOURITES_LOADER_ID,null, this);
        }
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        String order = Utility.getSortingOrder(getActivity());
        if(!order.equals(mOrder)){

            if(!order.equals(SHOW_FAVOURITES)){

                if(mOrder.equals(SHOW_FAVOURITES)){
                    onLoaderReset(null);
                    mOrder = order;
                    mMovieAdapter = new CustomMovieAdapter(getActivity(), new ArrayList<MovieObject>());
                    if(Utility.isOnline(getActivity())){
                        onSortOrderChange();
                    }
                    mGridView.setAdapter(mMovieAdapter);
                } else {
                    onSortOrderChange();
                }
            } else {
                mMovieAdapter.clear();
                mFavouritesAdapter = new MovieAdapter(getActivity(), null, 0);
                getLoaderManager().initLoader(FAVOURITES_LOADER_ID, null, this);
                mGridView.setAdapter(mFavouritesAdapter);
                mOrder = order;
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_main, container, false);
        mGridView = (GridView) rootView.findViewById(R.id.gridview_moives);

        if(!Utility.getSortingOrder(getActivity()).equals(SHOW_FAVOURITES)) {
            mGridView.setAdapter(mMovieAdapter);
        } else {
            mFavouritesAdapter = new MovieAdapter(getActivity(),null, 0);
            mGridView.setAdapter(mFavouritesAdapter);
        }
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(!Utility.getSortingOrder(getActivity()).equals(SHOW_FAVOURITES)){
                    mPosition = i;
                    ((Callback) getActivity()).onItemSelected(mMovieAdapter.getItem(i));
                } else {
                    mPosition = i;
                    Cursor cursor = (Cursor) adapterView.getItemAtPosition(i);
                    MovieObject movieObject = new MovieObject(cursor.getString(COLUMN_TITLE),
                            cursor.getString(COLUMN_ICON),
                            cursor.getString(COLUMN_PLOT),
                            cursor.getString(COLUMN_RELEASE_DATE),
                            cursor.getFloat(COLUMN_RATING),
                            cursor.getLong(COLUMN_MOVIE_ID));
                    ((Callback) getActivity()).onItemSelected(movieObject);
                }
            }
        });

        return rootView;
    }

    public void onSortOrderChange(){
        new FetchMovieData(getActivity(), mMovieAdapter).execute();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                MovieContract.FavouriteEntry.CONTENT_URI,
                MOVIE_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(Utility.getSortingOrder(getActivity()).equals(SHOW_FAVOURITES)){
            mFavouritesAdapter.swapCursor(data);
            mGridView.smoothScrollToPosition(mPosition);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mFavouritesAdapter.swapCursor(null);
    }

    public interface Callback{
        void  onItemSelected(Parcelable parcelable);
    }
}
