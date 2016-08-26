package com.example.stark.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by stark on 24/8/16.
 */
public class CustomTrailerAdapter extends ArrayAdapter<TrailerObject> {
    private ListView mListView;
    public CustomTrailerAdapter(Context context, List<TrailerObject> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TrailerObject trailerObject = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_trailer,parent,false);
        }

        ImageView icon = (ImageView) convertView.findViewById(R.id.trailer_item_icon);
        icon.setImageResource(android.R.drawable.ic_media_play);

        TextView name = (TextView) convertView.findViewById(R.id.trailer_item_name);
        name.setText(trailerObject.getTitle());

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
