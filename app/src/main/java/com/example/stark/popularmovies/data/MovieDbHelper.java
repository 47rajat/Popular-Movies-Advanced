package com.example.stark.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by stark on 23/8/16.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "moive.db";

    public MovieDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_FAVOURITE_TABLE = "CREATE TABLE " + MovieContract.FavouriteEntry.TABLE_NAME + " (" +
                MovieContract.FavouriteEntry._ID + " INTEGER PRIMARY KEY," +
                MovieContract.FavouriteEntry.MOVIE_ID + " INTEGER UNIQUE NOT NULL, "+
                MovieContract.FavouriteEntry.MOVIE_TITLE + " TEXT NOT NULL, "+
                MovieContract.FavouriteEntry.MOVIE_RELEASE_DATE + " TEXT NOT NULL, "+
                MovieContract.FavouriteEntry.MOVIE_PLOT + " TEXT NOT NULL, "+
                MovieContract.FavouriteEntry.MOVIE_RATING + " REAL NOT NULL, "+
                MovieContract.FavouriteEntry.MOVIE_POSTER + " TEXT NOT NULL "+
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_FAVOURITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.FavouriteEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);

    }
}
