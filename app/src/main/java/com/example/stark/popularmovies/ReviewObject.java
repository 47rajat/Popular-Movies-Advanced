package com.example.stark.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by stark on 24/8/16.
 */
public class ReviewObject implements Parcelable {
    private String mAuthor;
    private String mReview;

    public ReviewObject(String author, String review){
        mAuthor = author;
        mReview = review;
    }

    protected ReviewObject(Parcel in) {
        mAuthor = in.readString();
        mReview = in.readString();
    }

    public static final Creator<ReviewObject> CREATOR = new Creator<ReviewObject>() {
        @Override
        public ReviewObject createFromParcel(Parcel in) {
            return new ReviewObject(in);
        }

        @Override
        public ReviewObject[] newArray(int size) {
            return new ReviewObject[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mAuthor);
        parcel.writeString(mReview);
    }

    public String getReview(){
        return mReview;
    }

    public String getAuthor(){
        return mAuthor;
    }
}
