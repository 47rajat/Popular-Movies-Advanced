package com.example.stark.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by stark on 30/7/16.
 */
public class MovieObject implements Parcelable{

    String title;
    String thumbnail;
    String plot;
    String releaseDate;
    float rating;
    long movieId;

    MovieObject(String Title, String Poster, String Plot, String ReleaseDate, float Rating,long id){
        this.title = Title;
        this.thumbnail = Poster;
        this.plot = Plot;
        this.releaseDate = ReleaseDate;
        this.rating = Rating;
        this.movieId = id;
    }

    public MovieObject(Parcel parcel) {
        this.title = parcel.readString();
        this.thumbnail = parcel.readString();
        this.plot = parcel.readString();
        this.releaseDate = parcel.readString();
        this.rating = parcel.readFloat();
        this.movieId = parcel.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(thumbnail);
        parcel.writeString(plot);
        parcel.writeString(releaseDate);
        parcel.writeFloat(rating);
        parcel.writeLong(movieId);

    }

    public static final Parcelable.Creator<MovieObject> CREATOR = new Parcelable.Creator<MovieObject>(){
        @Override
        public MovieObject createFromParcel(Parcel parcel) {
            return new MovieObject(parcel);
        }

        @Override
        public MovieObject[] newArray(int i) {
            return new MovieObject[i];
        }
    };
}
