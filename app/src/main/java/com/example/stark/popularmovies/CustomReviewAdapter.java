package com.example.stark.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by stark on 24/8/16.
 */
public class CustomReviewAdapter extends ArrayAdapter<ReviewObject> {
    private ListView mListView;

    public CustomReviewAdapter(Context context, List<ReviewObject> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ReviewObject reviewObject = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_review, parent, false);
        }

        TextView review = (TextView) convertView.findViewById(R.id.review_item_content);
        review.setText(reviewObject.getReview());

        TextView author = (TextView) convertView.findViewById(R.id.review_item_author);
        author.setText(reviewObject.getAuthor());

        return convertView;
    }

    public void setView(ListView listView){
        mListView = listView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if(mListView != null){
            Utility.setListViewHeightBasedOnItems(mListView);
        }

    }
}
