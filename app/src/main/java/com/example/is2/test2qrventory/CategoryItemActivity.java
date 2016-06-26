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

import java.util.ArrayList;
import java.util.List;

public class CategoryItemActivity extends AppCompatActivity implements VolleyResponseListener {

    private ProgressDialog pDialog;
    private Category category = new Category();
    private List<Object> categories_items = new ArrayList<>();
    private ListView listView;
    private CustomCategoryListAdapter adapter;
    private int domain_index;
    private User user;
    private List<Integer> moveList = new ArrayList<>();
    //private int actual_category;

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
        adapter = new CustomCategoryListAdapter(this, categories_items); // category.getSubcategories()
        listView.setAdapter(adapter);

        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        int category_root_id = 1;
        moveList.add(category_root_id);

        String userApiKey = user.getApiKey();
        CategoryAccess categoryAccess = new CategoryAccess(userApiKey);
        categoryAccess.getRootCategory(this, 1, 1);
                //user.getDomains().get(domain_index).getIdDomain(),
                //user.getDomains().get(domain_index).getIdRootCategory());

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id){

                Object category_item =  adapter.getItemAtPosition(position);

                if(category_item.getClass() == Category.class) {
                    Category category = (Category) category_item;
                    moveList.add((int) category.getId());
                    onCategoryClick(category);
                } else {
                    //Intent intent = new Intent(getBaseContext(), CategoryItemActivity.class);
                    //based on item add info to intent
                    //intent.putExtra("user", user);
                    //intent.putExtra("domain_id", item.getIdDomain());
                    //startActivity(intent);
                }
            }


        });

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
            return super.onOptionsItemSelected(item);
        } else {
            String userApiKey = user.getApiKey();
            CategoryAccess categoryAccess = new CategoryAccess(userApiKey);
            categoryAccess.getRootCategory(this, 1,moveList.get(moveList.size() - 2));
            moveList.remove(moveList.size()-1);
            return true;
        }
    }

    private void onCategoryClick(Category category) {
        String userApiKey = user.getApiKey();
        CategoryAccess categoryAccess = new CategoryAccess(userApiKey);
        categoryAccess.getRootCategory(this, 1, category.getId());
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
