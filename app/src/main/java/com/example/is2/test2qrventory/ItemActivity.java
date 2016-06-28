package com.example.is2.test2qrventory;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.epson.lwprint.sdk.nsd.dns.DNSRecord;
import com.example.is2.test2qrventory.model.Item;
import com.example.is2.test2qrventory.model.User;
import com.github.clans.fab.FloatingActionButton;

public class ItemActivity extends AppCompatActivity {

    private User user;
    private Item single_item;
    private TextView textViewTitle;
    private TextView textViewDescription;

    private FloatingActionButton fab_update;
    private FloatingActionButton fab_delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewDescription = (TextView) findViewById(R.id.textViewDescription);

        fab_update = (FloatingActionButton) findViewById(R.id.menu_item_update);
        fab_delete = (FloatingActionButton) findViewById(R.id.menu_item_delete);

        fab_update.setOnClickListener(onUpdateHandler);
        fab_delete.setOnClickListener(onDeleteHandler);

        /*fab_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String test = "test";
            }
        });*/

        // enable UP navigation
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Fetching data from a parcelable object passed from CategoryItemActivity
        user = getIntent().getParcelableExtra("user");
        single_item = getIntent().getParcelableExtra("item");

        textViewTitle.setText(single_item.getName());
        textViewDescription.setText(single_item.getDescription());
    }

    View.OnClickListener onDeleteHandler = new View.OnClickListener() {
        public void onClick(View v) {
            String test = "delete";
        }
    };

    View.OnClickListener onUpdateHandler = new View.OnClickListener() {
        public void onClick(View v) {
            // it was the 1st button
            String test = "update";
        }
    };

}
