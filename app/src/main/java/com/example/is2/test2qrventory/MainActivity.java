package com.example.is2.test2qrventory;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.example.is2.test2qrventory.connection.DomainAccess;
import com.example.is2.test2qrventory.connection.VolleyResponseListener;
import com.example.is2.test2qrventory.model.Domain;
import com.example.is2.test2qrventory.model.User;
import com.example.is2.test2qrventory.notification.NotificationReceiver;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, VolleyResponseListener {

    //private static MainActivity activity_instance;

    // Instantiate the RequestQueue.
    RequestQueue queue = null;

    private ProgressDialog pDialog;
    private List<Domain> domains = new ArrayList<Domain>();
    private ListView listView;
    private CustomListAdapter adapter;

    private User user = null;
    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;

    private com.github.clans.fab.FloatingActionButton fab_add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab_add = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.menu_item_update);
        fab_add.setOnClickListener(onAddDomainHandler);


        // Fetching data from a parcelable object passed from LoginActivity
        user = getIntent().getParcelableExtra("user");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //activity_instance = this;

        initListView();

        Intent myIntent = new Intent(MainActivity.this, NotificationReceiver.class);
        myIntent.putExtra("user", user);
        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, myIntent,0);

        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        //alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis() , pendingIntent);
        long interval = 60 * 1000;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
    }

    View.OnClickListener onAddDomainHandler = new View.OnClickListener() {
        public void onClick(View v) {
            // it was the 1st button
            nextActivity(CreateDomainActivity.class, user);
        }
    };

    public void cancelAlarm(View view) {
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
            //Toast.makeText(this, "Alarm Canceled", Toast.LENGTH_SHORT).show();
        }
    }

    private void initListView() {
        listView = (ListView) findViewById(R.id.list);
        adapter = new CustomListAdapter(this, domains);
        listView.setAdapter(adapter);

        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        String userApiKey = user.getApiKey();
        DomainAccess domainAccess = new DomainAccess(userApiKey);
        domainAccess.getDomains(this);
    }

    /*public static synchronized MainActivity getInstance() {
        return activity_instance;
    }*/

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        nextActivity(SettingsActivity.class);

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void nextActivity(Class class_name, Object object) {
        Intent i = new Intent(getBaseContext(), class_name);
        i.putExtra("user", (User) object);
        startActivity(i);
    }

    public void nextActivity(Class class_name) {
        Intent i = new Intent(getBaseContext(), class_name);
        //i.putExtra("user", (User) object);
        startActivity(i);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            nextActivity(EventListActivity.class, user); //EventListActivity
        } else if (id == R.id.nav_printer) {
            nextActivity(PrintActivity.class);
        } else if (id == R.id.nav_scanner) {
            nextActivity(ScanActivity.class);
        } else if (id == R.id.nav_settings) {
            nextActivity(SettingsActivity.class);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onError(String message) {

    }

    @Override
    public void onResponse(Object response) {

        hidePDialog();

        List<Domain> new_domains = (List<Domain>) response;
        domains.addAll(new_domains);

        // notifying list adapter about data changes
        // so that it renders the list view with updated data
        adapter.notifyDataSetChanged();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapter,View v, int position, long id) {
                Domain domain = (Domain) adapter.getItemAtPosition(position);
                Intent intent = new Intent(getBaseContext(), CategoryItemActivity.class);
                intent.putExtra("user", user);
                intent.putExtra("domain", domain);
                startActivity(intent);
            }
        });
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
