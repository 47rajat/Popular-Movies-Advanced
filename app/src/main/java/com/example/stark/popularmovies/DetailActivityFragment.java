package com.example.stark.popularmovies;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {
    private MovieObject movieObject;

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();
        movieObject = intent.getParcelableExtra(Intent.EXTRA_TEXT);
        TextView title = (TextView) rootView.findViewById(R.id.grid_item_title);
        ImageView poster = (ImageView) rootView.findViewById(R.id.grid_item_icon);
        TextView rating = (TextView) rootView.findViewById(R.id.grid_item_rating);
        TextView releaseDate = (TextView) rootView.findViewById(R.id.grid_item_release_date);
        TextView plot = (TextView) rootView.findViewById(R.id.grid_item_plot);
        title.setText(movieObject.title);
        Picasso.with(getContext()).load(movieObject.thumbnail).into(poster);
        rating.setText("RATING: "+movieObject.rating+"");
        releaseDate.setText("RELEASE DATE: "+movieObject.releaseDate);
        plot.setText("PLOT: \n"+movieObject.plot);
        return rootView;
    }
}
