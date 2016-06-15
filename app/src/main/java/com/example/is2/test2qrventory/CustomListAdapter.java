package com.example.is2.test2qrventory;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.is2.test2qrventory.controller.AppController;
import com.example.is2.test2qrventory.model.Domain;

public class CustomListAdapter extends BaseAdapter {
    private AppCompatActivity activity;
    private LayoutInflater inflater;
    private List<Domain> domains;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public CustomListAdapter(AppCompatActivity activity, List<Domain> domains) {
        this.activity = activity;
        this.domains = domains;
    }

    @Override
    public int getCount() {
        return domains.size();
    }

    @Override
    public Object getItem(int location) {
        return domains.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView thumbNail = (NetworkImageView) convertView
                .findViewById(R.id.thumbnail);
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView rating = (TextView) convertView.findViewById(R.id.rating);
        //TextView genre = (TextView) convertView.findViewById(R.id.genre);
        //TextView year = (TextView) convertView.findViewById(R.id.releaseYear);

        // getting movie data for the row
        Domain d = domains.get(position);

        // thumbnail image
        //thumbNail.setImageUrl(d.getThumbnailUrl(), imageLoader);

        // title
        title.setText(d.getName());

        // rating
        rating.setText("Rating: " + String.valueOf(d.getDescription()));

        // genre
        /*String genreStr = "";
        for (String str : d.getGenre()) {
            genreStr += str + ", ";
        }
        genreStr = genreStr.length() > 0 ? genreStr.substring(0,
                genreStr.length() - 2) : genreStr;
        genre.setText(genreStr);*/

        // release year
        //year.setText(String.valueOf(m.getYear()));

        return convertView;
    }

}