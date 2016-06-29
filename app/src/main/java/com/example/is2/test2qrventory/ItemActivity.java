package com.example.is2.test2qrventory;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.epson.lwprint.sdk.nsd.dns.DNSRecord;
import com.example.is2.test2qrventory.connection.CategoryAccess;
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
    private FloatingActionButton fab_print;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewDescription = (TextView) findViewById(R.id.textViewDescription);

        fab_update = (FloatingActionButton) findViewById(R.id.menu_item_update);
        fab_delete = (FloatingActionButton) findViewById(R.id.menu_item_delete);
        fab_print = (FloatingActionButton) findViewById(R.id.menu_item_print);

        fab_update.setOnClickListener(onUpdateHandler);
        fab_delete.setOnClickListener(onDeleteHandler);
        fab_print.setOnClickListener(onPrintHandler);

        // enable UP navigation
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

    View.OnClickListener onPrintHandler = new View.OnClickListener() {
        public void onClick(View v) {
            nextActivity(PrintActivity.class, single_item);
        }
    };

    private void nextActivity(Class activity_class, Object object) {
        Intent intent = new Intent(getBaseContext(), activity_class); //ItemActivity.class
        //based on item add info to intent
        intent.putExtra("user", user);

        if(object.getClass() == Item.class)
            intent.putExtra("item", (Item) object);

        startActivity(intent);
    }

}
