package com.example.is2.test2qrventory;

import android.app.ProgressDialog;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.is2.test2qrventory.connection.CategoryAccess;
import com.example.is2.test2qrventory.connection.DomainAccess;
import com.example.is2.test2qrventory.connection.VolleyResponseListener;
import com.example.is2.test2qrventory.model.Category;
import com.example.is2.test2qrventory.model.Domain;
import com.example.is2.test2qrventory.model.Item;
import com.example.is2.test2qrventory.model.User;

import java.util.ArrayList;
import java.util.List;

public class CategoryItemActivity extends AppCompatActivity implements VolleyResponseListener {

    private ProgressDialog pDialog;
    private Category category = new Category();
    private List<Category> categories = new ArrayList<>();
    private ListView listView;
    private CustomCategoryListAdapter adapter;
    private int domain_index;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_item);
/*
        // my_child_toolbar is defined in the layout file
        Toolbar myChildToolbar =
                (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myChildToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
*/
        // If your minSdkVersion is below 11 use:
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // If your minSdkVersion is 11 or higher use:
        //getActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Fetching data from a parcelable object passed from LoginActivity
        user = getIntent().getParcelableExtra("user");
        domain_index = getIntent().getIntExtra("domain_index", -1);

        initListView();
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id== R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
*/

    private void initListView() {
        listView = (ListView) findViewById(R.id.list_category);
        adapter = new CustomCategoryListAdapter(this, categories); // category.getSubcategories()
        listView.setAdapter(adapter);

        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        String userApiKey = user.getApiKey();
        CategoryAccess categoryAccess = new CategoryAccess(userApiKey);
        categoryAccess.getRootCategory(this, 1, 1);
                //user.getDomains().get(domain_index).getIdDomain(),
                //user.getDomains().get(domain_index).getIdRootCategory());
    }

    @Override
    public void onResponse(Object response) {

        hidePDialog();

        category = (Category) response;
        categories.addAll(category.getSubcategories());

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
