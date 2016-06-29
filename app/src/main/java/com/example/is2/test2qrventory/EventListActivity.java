package com.example.is2.test2qrventory;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.is2.test2qrventory.connection.CategoryAccess;
import com.example.is2.test2qrventory.connection.EventAccess;
import com.example.is2.test2qrventory.connection.VolleyResponseListener;
import com.example.is2.test2qrventory.model.Category;
import com.example.is2.test2qrventory.model.Event;
import com.example.is2.test2qrventory.model.Item;
import com.example.is2.test2qrventory.model.User;

import java.util.ArrayList;
import java.util.List;

public class EventListActivity extends AppCompatActivity implements VolleyResponseListener {

    private ListView listView;
    private CustomEventListAdapter adapter;
    private ProgressDialog pDialog;
    private User user;
    private List<Event> events = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Fetching data from a parcelable object passed from LoginActivity
        user = getIntent().getParcelableExtra("user");

        initListView();
    }

    private void initListView() {
        listView = (ListView) findViewById(R.id.list_event);
        adapter = new CustomEventListAdapter(this, events); // category.getSubcategories()
        listView.setAdapter(adapter);

        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        EventAccess eventAccess = new EventAccess(user.getApiKey());
        eventAccess.getAllEvents(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id){
                Event event = (Event) adapter.getItemAtPosition(position);
                nextActivity(TabbedEventSingleActivity.class, event);
            }
        });
    }

    private void nextActivity(Class activity_class, Object object) {
        Intent intent = new Intent(getBaseContext(), activity_class); //ItemActivity.class
        //based on item add info to intent
        intent.putExtra("user", user);

        if(object.getClass() == Event.class)
            intent.putExtra("event", (Event) object);

        startActivity(intent);
    }

    @Override
    public void onError(String message) {

    }

    @Override
    public void onResponse(Object response) {
        if(response != null)
        {
            hidePDialog();

            events.clear();
            events.addAll((List<Event>) response);

            // notifying list adapter about data changes
            // so that it renders the list view with updated data
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }
}
