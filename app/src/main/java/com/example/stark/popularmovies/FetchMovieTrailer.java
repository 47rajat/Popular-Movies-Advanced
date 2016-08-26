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
public class FetchMovieTrailer extends AsyncTask<Void, Void, TrailerObject[]> {

    private final String LOG_TAG = FetchMovieData.class.getSimpleName();
    private final Context mContext;
    private CustomTrailerAdapter mAdapter;
    private long mMovieId;

    public FetchMovieTrailer(Context context, CustomTrailerAdapter trailerAdapter, long id){
        mContext = context;
        mAdapter = trailerAdapter;
        mMovieId = id;
    }

    public String getSortingOrder(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(context.getString(R.string.pref_sort_key), context.getString(R.string.pref_sort_default_value));
    }
    @Override
    protected TrailerObject[] doInBackground(Void... voids) {

        HttpURLConnection httpURLConnection = null;
        BufferedReader bufferedReader = null;

        String movieJsonStr = null;


        try{
            //Construct URL for query from the website
            final String BASE_URL = "https://api.themoviedb.org/3/movie";
            final String MOVIE_ID = Long.toString(mMovieId);
            final String DETAIL = "videos";
            final String API_KEY_PARAM = "api_key"; //append your own API KEY here

            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(MOVIE_ID)
                    .appendPath(DETAIL)
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
            return getTrailerDataFromJson(movieJsonStr);
        } catch(JSONException e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(TrailerObject[] Details) {
        super.onPostExecute(Details);
        mAdapter.clear();
        ArrayList<TrailerObject> movie_list = new ArrayList<>(Arrays.asList(Details));
        for(TrailerObject item:movie_list) {
            mAdapter.add(item);
        }
        mAdapter.notifyDataSetChanged();

    }

    private TrailerObject[] getTrailerDataFromJson(String movieJsonStr) throws JSONException{

        final String MDB_LIST = "results";
        final String REVIEW_AUTHOR = "author";
        final String MOVIE_REVIEW = "content";
        final String MOVIE_TRAILER = "key";
        final String TRAILER_TITLE = "name";
        final String MOVIE_RATING = "vote_average";
        final String MOVIE_ID = "id";

        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieJson.getJSONArray(MDB_LIST);


        TrailerObject[] resultTrailer = new TrailerObject[movieArray.length()];


        for(int i = 0; i < movieArray.length(); i++){
            JSONObject individualResult = movieArray.getJSONObject(i);
            String url_key = individualResult.getString(MOVIE_TRAILER);
            String name = individualResult.getString(TRAILER_TITLE);
            resultTrailer[i] = new TrailerObject(name,url_key);
        }
        return resultTrailer;
    }
}