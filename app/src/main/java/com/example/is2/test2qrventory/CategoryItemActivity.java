package com.example.is2.test2qrventory;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.is2.test2qrventory.connection.CategoryAccess;
import com.example.is2.test2qrventory.connection.VolleyResponseListener;
import com.example.is2.test2qrventory.model.Category;
import com.example.is2.test2qrventory.model.Domain;
import com.example.is2.test2qrventory.model.Item;
import com.example.is2.test2qrventory.model.User;
import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class CategoryItemActivity extends AppCompatActivity implements VolleyResponseListener {

    private ProgressDialog pDialog;
    private Category category = new Category();
    private List<Object> categories_items = new ArrayList<>();
    private ListView listView;
    private CustomCategoryListAdapter adapter;
    private User user;
    private Domain domain;
    private List<Integer> moveList = new ArrayList<>();
    //private int actual_category;
    private FloatingActionButton fab_add_item;
    private FloatingActionButton fab_add_category;
    private FloatingActionButton fab_discover_events;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_item);

        // If your minSdkVersion is below 11 use:
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // If your minSdkVersion is 11 or higher use:
        //getActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Fetching data from a parcelable object passed from LoginActivity
        user = getIntent().getParcelableExtra("user");
        domain = getIntent().getParcelableExtra("domain");

        initListView();

        fab_add_item = (FloatingActionButton) findViewById(R.id.menu_item_add_item);
        fab_add_category = (FloatingActionButton) findViewById(R.id.menu_item_add_category);
        fab_discover_events = (FloatingActionButton) findViewById(R.id.menu_item_discover_events);

        fab_add_item.setOnClickListener(onAddItemHandler);
        fab_add_category.setOnClickListener(onAddCategoryHandler);
        fab_discover_events.setOnClickListener(onDiscoverEventsHandler);
    }

    View.OnClickListener onAddItemHandler = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(getBaseContext(), CreateItemActivity.class);
            intent.putExtra("user", user);
            intent.putExtra("domain_id", domain.getIdDomain());
            intent.putExtra("category_parent_id", moveList.get(moveList.size() - 1));
            startActivity(intent);
        }
    };

    View.OnClickListener onAddCategoryHandler = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(getBaseContext(), CreateCategoryActivity.class);
            intent.putExtra("user", user);
            intent.putExtra("domain_id", domain.getIdDomain());
            intent.putExtra("category_parent_id", moveList.get(moveList.size() - 1));
            startActivity(intent);
        }
    };

    View.OnClickListener onDiscoverEventsHandler = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(getBaseContext(), TabbedEventsActivity.class);
            //based on item add info to intent
            intent.putExtra("user", user);
            intent.putExtra("domain", domain);

            startActivity(intent);
        }
    };

    private void initListView() {
        listView = (ListView) findViewById(R.id.list_category);
        adapter = new CustomCategoryListAdapter(this, categories_items); // category.getSubcategories()
        listView.setAdapter(adapter);

        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        moveList.add((int) domain.getIdCategoryRoot());

        String userApiKey = user.getApiKey();
        CategoryAccess categoryAccess = new CategoryAccess(userApiKey);
        categoryAccess.getRootCategory(this, domain.getIdDomain(), domain.getIdCategoryRoot());

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id){

                Object category_item =  adapter.getItemAtPosition(position);

                if(category_item.getClass() == Category.class) {
                    Category category = (Category) category_item;
                    moveList.add((int) category.getId());
                    onCategoryClick(category);
                } else {
                    nextActivity(ItemActivity.class, category_item);
                }
            }


        });
    }

    private void nextActivity(Class activity_class, Object object) {
        Intent intent = new Intent(getBaseContext(), activity_class);
        //based on item add info to intent
        intent.putExtra("user", user);

        if(object.getClass() == Item.class)
            intent.putExtra("item", (Item) object);

        startActivity(intent);
    }

    private void nextActivity(Class activity_class) {
        Intent intent = new Intent(getBaseContext(), activity_class);
        //based on item add info to intent
        intent.putExtra("user", user);

        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //switch (item.getItemId()) {
        //    case android.R.id.home:
                //setResult(RESULT_CANCELED);
                //finish();
                //return true;
        //}

        if(moveList.size() < 2) {
            //return super.onOptionsItemSelected(item);
            nextActivity(MainActivity.class);
        } else {
            String userApiKey = user.getApiKey();
            CategoryAccess categoryAccess = new CategoryAccess(userApiKey);
            categoryAccess.getRootCategory(this, domain.getIdDomain(), moveList.get(moveList.size() - 2));
            moveList.remove(moveList.size()-1);
        }
        return true;
    }

    private void onCategoryClick(Category category) {
        String userApiKey = user.getApiKey();
        CategoryAccess categoryAccess = new CategoryAccess(userApiKey);
        categoryAccess.getRootCategory(this, domain.getIdDomain(), category.getId());
    }

    @Override
    public void onResponse(Object response) {

        hidePDialog();

        category = (Category) response;

        categories_items.clear();
        categories_items.addAll(category.getSubcategories());
        categories_items.addAll(category.getItems());

        // notifying list adapter about data changes
        // so that it renders the list view with updated data
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onError(String message) {

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
