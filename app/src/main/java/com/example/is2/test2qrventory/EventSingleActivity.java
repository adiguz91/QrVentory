package com.example.is2.test2qrventory;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.is2.test2qrventory.model.Event;
import com.example.is2.test2qrventory.model.Item;
import com.example.is2.test2qrventory.model.User;

public class EventSingleActivity extends AppCompatActivity {

    private User user;
    private Event singleEvent;
    private TextView textViewTitle;
    private TextView textViewDescription;
    private Button buttonEventStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_single);

        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewDescription = (TextView) findViewById(R.id.textViewDescription);
        buttonEventStart = (Button) findViewById(R.id.button_event_start);
        buttonEventStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do stuff
            }
        });

        user = getIntent().getParcelableExtra("user");
        singleEvent = getIntent().getParcelableExtra("event");

        textViewTitle.setText(singleEvent.getName());
        textViewDescription.setText(singleEvent.getDescription());
    }

}
