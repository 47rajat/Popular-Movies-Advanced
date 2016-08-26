package com.example.stark.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stark.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {
    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
    private MovieObject movieObject;
    private CustomTrailerAdapter mTrailerAdapter;
    private ArrayList<TrailerObject> mTrailerList;
    private static final String PARCELABLE_TRAILER_LIST = "trailers";
    public static final String DETAIL_PARCELABLE = "Parcelable";
    private CustomReviewAdapter mReviewAdapter;
    private ArrayList<ReviewObject> mReviewList;
    private static final String PARCELABLE_REVIEW_LIST = "review";
    private Toast mToast;
    @Override
    public void onSaveInstanceState(Bundle outState) {
        ArrayList<ReviewObject> reviewList = new ArrayList<>();
        ArrayList<TrailerObject> trailerList = new ArrayList<>();

        for(int i = 0; i < mReviewAdapter.getCount(); i++)
            reviewList.add(mReviewAdapter.getItem(i));

        for (int i = 0; i < mTrailerAdapter.getCount(); i++)
            trailerList.add(mTrailerAdapter.getItem(i));

        outState.putParcelableArrayList(PARCELABLE_TRAILER_LIST, trailerList);
        outState.putParcelableArrayList(PARCELABLE_REVIEW_LIST, reviewList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if(args != null){
            movieObject = args.getParcelable(DETAIL_PARCELABLE);
        }
        if(savedInstanceState == null || !savedInstanceState.containsKey(PARCELABLE_REVIEW_LIST) || !savedInstanceState.containsKey(PARCELABLE_TRAILER_LIST)){
            mTrailerAdapter = new CustomTrailerAdapter(getActivity(),new ArrayList<TrailerObject>());
            mReviewAdapter = new CustomReviewAdapter(getActivity(), new ArrayList<ReviewObject>());
            if(Utility.isOnline(getActivity()) && movieObject != null){
                new FetchMovieReview(getActivity(),mReviewAdapter, movieObject.movieId).execute();
                new FetchMovieTrailer(getActivity(), mTrailerAdapter, movieObject.movieId).execute();
            }
        } else {
            mTrailerList = savedInstanceState.getParcelableArrayList(PARCELABLE_TRAILER_LIST);
            mTrailerAdapter = new CustomTrailerAdapter(getActivity(), mTrailerList);

            mReviewList = savedInstanceState.getParcelableArrayList(PARCELABLE_REVIEW_LIST);
            mReviewAdapter = new CustomReviewAdapter(getActivity(), mReviewList);
        }
    }

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        TextView title = (TextView) rootView.findViewById(R.id.grid_item_title);
        ImageView poster = (ImageView) rootView.findViewById(R.id.grid_item_icon);
        TextView rating = (TextView) rootView.findViewById(R.id.grid_item_rating);
        TextView releaseDate = (TextView) rootView.findViewById(R.id.grid_item_release_date);
        TextView plot = (TextView) rootView.findViewById(R.id.grid_item_plot);
        final Button markFavourite = (Button) rootView.findViewById(R.id.mark_favourite);
        if(movieObject != null){
            title.setText(movieObject.title);
            Picasso.with(getContext()).load(movieObject.thumbnail).into(poster);
            rating.setText(getActivity().getString(R.string.format_rating, movieObject.rating));
            releaseDate.setText(movieObject.releaseDate);
            plot.setText(movieObject.plot);
            if(Utility.isFavourite(getActivity(), movieObject.movieId)){
                markFavourite.setText(getString(R.string.remove_from_fav));
            } else {
                markFavourite.setText(getString(R.string.add_to_fav));
            }
        }

        ListView trailerListView = (ListView) rootView.findViewById(R.id.trailer_list);
        trailerListView.setAdapter(mTrailerAdapter);
        mTrailerAdapter.setView(trailerListView);


        ListView reviewListView = (ListView) rootView.findViewById(R.id.review_list);
        reviewListView.setAdapter(mReviewAdapter);

        trailerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(Intent.ACTION_VIEW);

                intent.setData(mTrailerAdapter.getItem(i).getTrailerLink());

                if(intent.resolveActivity(getActivity().getPackageManager()) != null){
                    startActivity(intent);
                }
                else{
                    Log.d(LOG_TAG, "Could not call " + mTrailerAdapter.getItem(i).getTrailerLink().toString() + ", no receiving apps installed!");
                }
            }
        });
        Utility.setListViewHeightBasedOnItems(reviewListView);
        Utility.setListViewHeightBasedOnItems(trailerListView);

        markFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Utility.isFavourite(getActivity(), movieObject.movieId)){
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MovieContract.FavouriteEntry.MOVIE_ID, movieObject.movieId);
                    contentValues.put(MovieContract.FavouriteEntry.MOVIE_TITLE, movieObject.title);
                    contentValues.put(MovieContract.FavouriteEntry.MOVIE_RELEASE_DATE, movieObject.releaseDate);
                    contentValues.put(MovieContract.FavouriteEntry.MOVIE_POSTER, movieObject.thumbnail);
                    contentValues.put(MovieContract.FavouriteEntry.MOVIE_PLOT, movieObject.plot);
                    contentValues.put(MovieContract.FavouriteEntry.MOVIE_RATING, movieObject.rating);

                    Uri check = getActivity().getContentResolver().insert(MovieContract.FavouriteEntry.CONTENT_URI, contentValues);
                    markFavourite.setText(getString(R.string.remove_from_fav));
                    if(mToast != null){
                        mToast.cancel();
                    }
                    mToast = Toast.makeText(getActivity(), getString(R.string.added_to_fav_message), Toast.LENGTH_SHORT);
                    mToast.show();
                } else {
                    String selection = MovieContract.FavouriteEntry.TABLE_NAME +
                            "."+ MovieContract.FavouriteEntry.MOVIE_ID + " = ? ";
                    int check = getActivity().getContentResolver().delete(MovieContract.FavouriteEntry.CONTENT_URI,
                            selection,
                            new String[]{Long.toString(movieObject.movieId)});
                    markFavourite.setText(getString(R.string.add_to_fav));
                    if(mToast != null){
                        mToast.cancel();
                    }
                    mToast = Toast.makeText(getActivity(), getString(R.string.removed_from_fav_message), Toast.LENGTH_SHORT);
                    mToast.show();
                }
            }
        });

        return rootView;
    }


}
