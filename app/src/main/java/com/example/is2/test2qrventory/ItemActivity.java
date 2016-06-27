package com.example.is2.test2qrventory;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.epson.lwprint.sdk.nsd.dns.DNSRecord;
import com.example.is2.test2qrventory.model.Item;
import com.example.is2.test2qrventory.model.User;

public class ItemActivity extends AppCompatActivity {

    private User user;
    private Item single_item;
    private TextView textViewTitle;
    private TextView textViewDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewDescription = (TextView) findViewById(R.id.textViewDescription);

        // enable UP navigation
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Fetching data from a parcelable object passed from CategoryItemActivity
        user = getIntent().getParcelableExtra("user");
        single_item = getIntent().getParcelableExtra("item");

        textViewTitle.setText(single_item.getName());
        textViewDescription.setText(single_item.getDescription());
    }
}
