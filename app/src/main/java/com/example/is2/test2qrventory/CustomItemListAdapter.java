package com.example.is2.test2qrventory;

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
import com.example.is2.test2qrventory.model.Event;
import com.example.is2.test2qrventory.model.Item;

import java.util.List;

/**
 * Created by Kevin on 29.06.2016.
 */
public class CustomItemListAdapter extends BaseAdapter{
    private AppCompatActivity activity;
    private LayoutInflater inflater;
    private List<Item> items;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public CustomItemListAdapter(AppCompatActivity activity, List<Item> items) {
        this.activity = activity;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int location) {
        return items.get(location);
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
        TextView description = (TextView) convertView.findViewById(R.id.rating);

        // create row
        Item item = null;
        item = (Item) items.get(position);

        // thumbnail image
        //thumbNail.setImageUrl(d.getThumbnailUrl(), imageLoader);

        title.setText(item.getName());
        description.setText("Beschreibung: " + String.valueOf(item.getDescription()));

        return convertView;
    }
}
