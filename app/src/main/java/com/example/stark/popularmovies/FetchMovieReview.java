package com.example.stark.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
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
public class FetchMovieReview extends AsyncTask<Void, Void, ReviewObject[]> {
    private final String LOG_TAG = FetchMovieReview.class.getSimpleName();
    private final Context mContext;
    private CustomReviewAdapter mAdapter;
    private long mMovieId;

    public FetchMovieReview(Context context, CustomReviewAdapter reviewAdapter, long id){
        mContext = context;
        mAdapter = reviewAdapter;
        mMovieId = id;

    }
    @Override
    protected ReviewObject[] doInBackground(Void... voids) {
        HttpURLConnection httpURLConnection = null;
        BufferedReader bufferedReader = null;

        String movieJsonStr = null;


        try{
            //Construct URL for query from the website
            final String BASE_URL = "https://api.themoviedb.org/3/movie";
            final String MOVIE_ID = Long.toString(mMovieId);
            final String DETAIL = "reviews";
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
            return getReviewDataFromJson(movieJsonStr);
        } catch(JSONException e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(ReviewObject[] Details) {
        super.onPostExecute(Details);
        mAdapter.clear();
        ArrayList<ReviewObject> movie_list = new ArrayList<>(Arrays.asList(Details));
        for(ReviewObject item:movie_list) {
            mAdapter.add(item);
        }
        mAdapter.notifyDataSetChanged();

    }

    private ReviewObject[] getReviewDataFromJson(String movieJsonStr) throws JSONException{

        final String MDB_LIST = "results";
        final String REVIEW_AUTHOR = "author";
        final String MOVIE_REVIEW = "content";

        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieJson.getJSONArray(MDB_LIST);


        ReviewObject[] resultReview = new ReviewObject[movieArray.length()];


        for(int i = 0; i < movieArray.length(); i++){
            JSONObject individualResult = movieArray.getJSONObject(i);
            String review = individualResult.getString(MOVIE_REVIEW);
            String author = individualResult.getString(REVIEW_AUTHOR);
            resultReview[i] = new ReviewObject(author,review);
        }
        return resultReview;
    }
}
