package com.example.pranay.demoapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ReviewAdapter extends ArrayAdapter<Review> {
    private Activity context;
    List<Review> reviews;

    public ReviewAdapter(Activity context, List<Review> reviews) {
        super(context, R.layout.list_layout, reviews);
        this.context = context;
        this.reviews = reviews;
    }

    public View getView(int position, View convertView, ViewGroup parent){

        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.list_layout, null, true);

        TextView textViewName = (TextView) listViewItem.findViewById(R.id.name);
        TextView textViewReview= (TextView) listViewItem.findViewById(R.id.review);

        Review artist = reviews.get(position);
        textViewName.setText(artist.getName());
        textViewReview.setText(artist.getReview());

        return listViewItem;

    }

}