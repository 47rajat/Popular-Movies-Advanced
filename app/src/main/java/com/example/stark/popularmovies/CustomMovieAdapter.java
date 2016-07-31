package com.example.stark.popularmovies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by stark on 30/7/16.
 */
public class CustomMovieAdapter extends ArrayAdapter<MovieObject> {

    public CustomMovieAdapter(Activity context, ArrayList<MovieObject> movieObjects) {
        super(context, 0, movieObjects);



    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MovieObject movieObject = getItem(position);


        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_movie, parent, false);
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.grid_item_movie_icon);
        Picasso.with(getContext()).load(movieObject.thumbnail).into(imageView);

        return convertView;
    }
}
