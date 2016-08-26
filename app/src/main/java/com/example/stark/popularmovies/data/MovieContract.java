package com.example.stark.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by stark on 23/8/16.
 */
public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.example.stark.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_FAV = "favourites";

    public static final class FavouriteEntry implements BaseColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAV).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAV;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAV;

        public static final String TABLE_NAME = "favourites";
        public static final String MOVIE_ID = "movie_id";
        public static final String MOVIE_TITLE = "movie_title";
        public static final String MOVIE_RELEASE_DATE = "movie_release_date";
        public static final String MOVIE_POSTER = "movie_poster";
        public static final String MOVIE_PLOT = "movie_plot";
        public static final String MOVIE_RATING = "movie_rating";

        public static Uri buildFavouriteUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }

        public static Uri buildFavouriteWithMoiveId(long moiveId){
            return CONTENT_URI.buildUpon().appendPath(Long.toString(moiveId)).build();
        }

        public static long getIdFromUri(Uri uri){
            return Long.parseLong(uri.getPathSegments().get(1));
        }


    }

}
