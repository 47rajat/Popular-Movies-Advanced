package com.example.stark.popularmovies;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by stark on 24/8/16.
 */
public class TrailerObject implements Parcelable {
    private String title;
    private Uri trailerLink;
    private final String BASE_URL = "https://www.youtube.com/watch?v=";

    public TrailerObject(String name, String key){
        title = name;
        trailerLink = Uri.parse(BASE_URL + key);
    }

    protected TrailerObject(Parcel in) {
        title = in.readString();
        trailerLink = in.readParcelable(Uri.class.getClassLoader());
    }

    public static final Creator<TrailerObject> CREATOR = new Creator<TrailerObject>() {
        @Override
        public TrailerObject createFromParcel(Parcel in) {
            return new TrailerObject(in);
        }

        @Override
        public TrailerObject[] newArray(int size) {
            return new TrailerObject[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeParcelable(trailerLink, i);
    }

    public String getTitle(){
        return title;
    }

    public Uri getTrailerLink(){
        return trailerLink;
    }
}
