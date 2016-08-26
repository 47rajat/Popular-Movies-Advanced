package com.example.stark.popularmovies.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by stark on 23/8/16.
 */
public class MovieProvider extends ContentProvider {
    private static final String LOG_TAG = MovieProvider.class.getSimpleName();
    private static final UriMatcher sUrimatcher = buildUriMatcher();
    private MovieDbHelper movieDbHelper;

    static final int FAVOURITE = 100;
    static final int FAVOURITE_WITH_ID = 101;

    private final static SQLiteQueryBuilder sFavouriteMoviesQueryBuilder;

    static {
        sFavouriteMoviesQueryBuilder = new SQLiteQueryBuilder();

        sFavouriteMoviesQueryBuilder.setTables(MovieContract.FavouriteEntry.TABLE_NAME);
    }

    private static final String sSelectionById = MovieContract.FavouriteEntry.TABLE_NAME +
            "."+ MovieContract.FavouriteEntry.MOVIE_ID + " = ? ";

    private Cursor getFavouriteById(Uri uri, String[] projection, String sortOrder){
        long id = MovieContract.FavouriteEntry.getIdFromUri(uri);
        return sFavouriteMoviesQueryBuilder.query(movieDbHelper.getReadableDatabase(),
                projection,
                sSelectionById,
                new String[] {Long.toString(id)},
                null,
                null,
                sortOrder);
    }

    @Override
    public boolean onCreate()
    {
        movieDbHelper = new MovieDbHelper(getContext());
        return true;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;

        switch (sUrimatcher.match(uri)){
            case FAVOURITE:
            {
                cursor = movieDbHelper.getReadableDatabase().query(MovieContract.FavouriteEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }

            case FAVOURITE_WITH_ID:
            {
                cursor = getFavouriteById(uri, projection, sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }


    @Override
    public String getType(Uri uri) {
        final int match = sUrimatcher.match(uri);

        switch (match){
            case FAVOURITE:
                return MovieContract.FavouriteEntry.CONTENT_TYPE;
            case FAVOURITE_WITH_ID:
                return MovieContract.FavouriteEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri : "+ uri);
        }
    }


    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase database = movieDbHelper.getWritableDatabase();
        int match = sUrimatcher.match(uri);
        Uri returnUri;
        switch (match){
            case FAVOURITE:
            {
                long _id = database.insert(MovieContract.FavouriteEntry.TABLE_NAME, null, contentValues);
                if (_id > 0){
                    returnUri = MovieContract.FavouriteEntry.buildFavouriteUri(_id);
                }else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri : " + uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        final SQLiteDatabase database = movieDbHelper.getWritableDatabase();
        final int match = sUrimatcher.match(uri);
        int rowsDeleted;
        if(s == null) s = "1";

        switch (match){
            case FAVOURITE:
            {
                rowsDeleted = database.delete(MovieContract.FavouriteEntry.TABLE_NAME, s, strings);
                break;
            }
            case FAVOURITE_WITH_ID:
            {
                rowsDeleted = database.delete(MovieContract.FavouriteEntry.TABLE_NAME,s, strings);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknow uri : " + uri);
        }
        if (rowsDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        final SQLiteDatabase database = movieDbHelper.getWritableDatabase();
        final  int match = sUrimatcher.match(uri);
        int rowsUpdated;

        switch (match){
            case FAVOURITE:
            {
                rowsUpdated = database.update(MovieContract.FavouriteEntry.TABLE_NAME, contentValues, s, strings);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri : " + uri);
        }
        if(rowsUpdated != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    static UriMatcher buildUriMatcher(){
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_FAV, FAVOURITE);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_FAV + "/" + "#", FAVOURITE_WITH_ID);

        return uriMatcher;
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        movieDbHelper.close();
        super.shutdown();
    }
}
