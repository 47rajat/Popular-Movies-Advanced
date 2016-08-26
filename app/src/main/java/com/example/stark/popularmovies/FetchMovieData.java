package com.example.stark.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

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
 * Created by stark on 24/8/16.
 */
public class FetchMovieData extends AsyncTask<Void, Void, MovieObject[]> {
    private final String LOG_TAG = FetchMovieData.class.getSimpleName();
    private final Context mContext;
    private CustomMovieAdapter mMovieAdapter;

    public FetchMovieData(Context context, CustomMovieAdapter movieAdapter){
        mContext = context;
        mMovieAdapter = movieAdapter;
    }

    public String getSortingOrder(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(context.getString(R.string.pref_sort_key), context.getString(R.string.pref_sort_default_value));
    }


    @Override
    protected MovieObject[] doInBackground(Void... voids) {

        HttpURLConnection httpURLConnection = null;
        BufferedReader bufferedReader = null;

        String movieJsonStr = null;


        try{
            //Construct URL for query from the website
            final String BASE_URL = "https://api.themoviedb.org/3/movie";
            final String FORMAT_PARAM = getSortingOrder(mContext);
            final String API_KEY_PARAM = "api_key"; //append your own API KEY here

            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(FORMAT_PARAM)
                    .appendQueryParameter(API_KEY_PARAM, BuildConfig.MOVIE_DATABASE_API)
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
        final String MOVIE_ID = "id";

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
            long id = individualResult.getLong(MOVIE_ID);
            resultList[i] = new MovieObject(title, poster, plot, releaseDate, rating, id);
        }

        return resultList;

    }
}
