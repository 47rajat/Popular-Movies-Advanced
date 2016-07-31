package com.example.stark.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private ArrayAdapter<MovieObject> mMovieAdapter = null;
    private ArrayList<MovieObject> mMovieList;
    private final String mParcelableListKey = "movies";
    private String mOrder = "";

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null || !savedInstanceState.containsKey("movies")) {
            mMovieAdapter = new CustomMovieAdapter(getActivity(), new ArrayList<MovieObject>());
        } else{
            mMovieList = savedInstanceState.getParcelableArrayList(mParcelableListKey);
            mMovieAdapter = new CustomMovieAdapter(getActivity(), mMovieList);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        ArrayList<MovieObject> movieList = new ArrayList<>();
        for(int i = 0; i< mMovieAdapter.getCount(); i++){
            movieList.add(mMovieAdapter.getItem(i));
        }
        outState.putParcelableArrayList(mParcelableListKey, movieList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String order = sharedPreferences.getString(getString(R.string.pref_sort_key),getString(R.string.pref_sort_default_key));
        if(!mOrder.equals(order) && isOnline()){
            mOrder = order;
            new FetchMovieData().execute();
        }

    }

    private boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_moives);
        gridView.setAdapter(mMovieAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT,mMovieAdapter.getItem(i));
                startActivity(intent);
            }
        });

        return rootView;
    }


    private class FetchMovieData extends AsyncTask<Void, Void, MovieObject[]>{
        private final String LOG_TAG = FetchMovieData.class.getSimpleName();


        @Override
        protected MovieObject[] doInBackground(Void... voids) {

            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;

            String movieJsonStr = null;


            try{
                //Construct URL for query from the website
                final String BASE_URL = "https://api.themoviedb.org/3/movie";
                final String FORMAT_PARAM = mOrder;
                final String API_KEY_PARAM = "api_key";
                final String MOVIE_DATABASE_API = ""; //append your own API KEY here

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendPath(FORMAT_PARAM)
                        .appendQueryParameter(API_KEY_PARAM, MOVIE_DATABASE_API)
                        .build();

                URL url = new URL(builtUri.toString());

                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();


                //Reading the input stream to a string
                InputStream inputStream = httpURLConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if(inputStream == null){
                    movieJsonStr = null;
                }

                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line;

                while ((line = bufferedReader.readLine()) != null){
                    // makes debugging easier

                    buffer.append(line + "\n");
                }

                if(buffer.length() == 0) {
                    movieJsonStr = null;
                }
                movieJsonStr = buffer.toString();
            } catch (IOException e){
                Log.e(LOG_TAG, "Error ",e);
                //code successfully didn't got the weather data
                movieJsonStr = null;
            } finally {
                if (httpURLConnection != null){
                    httpURLConnection.disconnect();
                }
                if (bufferedReader != null){
                    try{
                        bufferedReader.close();
                    } catch (IOException e){
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
            try {
                return getMovieDataFromJson(movieJsonStr);
            } catch(JSONException e){
                e.printStackTrace();
                return null;
            }
        }


        @Override
        protected void onPostExecute(MovieObject[] movieObjects) {
            super.onPostExecute(movieObjects);
            mMovieAdapter.clear();
            ArrayList<MovieObject> movie_list = new ArrayList<>(Arrays.asList(movieObjects));
            for(MovieObject item:movie_list) {
                mMovieAdapter.add(item);
            }

        }

        private MovieObject[] getMovieDataFromJson(String movieJsonStr) throws JSONException{

            final String MDB_LIST = "results";
            final String MOVIE_TITLE = "original_title";
            final String MOVIE_RELEASE_DATE = "release_date";
            final String MOVIE_THUMBNAIL = "poster_path";
            final String MOVIE_PLOT = "overview";
            final String MOVIE_RATING = "vote_average";

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(MDB_LIST);

            MovieObject[] resultList = new MovieObject[movieArray.length()];
            final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
            final String IMAGE_SIZE = "w342";

            for(int i = 0; i < movieArray.length(); i++){
                JSONObject individualResult = movieArray.getJSONObject(i);
                String poster = individualResult.getString(MOVIE_THUMBNAIL);
                String title = individualResult.getString(MOVIE_TITLE);
                String releaseDate = individualResult.getString(MOVIE_RELEASE_DATE);
                String plot = individualResult.getString(MOVIE_PLOT);
                float rating = (float)individualResult.getDouble(MOVIE_RATING);
                poster = BASE_IMAGE_URL + IMAGE_SIZE + "/" + poster;
                resultList[i] = new MovieObject(title, poster, plot, releaseDate, rating);
            }

            return resultList;

        }
    }
}
