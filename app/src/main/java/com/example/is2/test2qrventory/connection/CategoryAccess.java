package com.example.is2.test2qrventory.connection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.is2.test2qrventory.LoginActivity;
import com.example.is2.test2qrventory.MainActivity;
import com.example.is2.test2qrventory.controller.AppController;
import com.example.is2.test2qrventory.model.Category;
import com.example.is2.test2qrventory.model.Domain;
import com.example.is2.test2qrventory.model.Item;
import com.example.is2.test2qrventory.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Adrian on 15.06.2016.
 */
public class CategoryAccess {

    private String url ="http://qrventory.square7.ch/v1/category/";
    private String userApiKey;
    private Category category;

    public CategoryAccess(String userApiKey) {
        this.userApiKey = userApiKey;
    }

    public void getRootCategory(final VolleyResponseListener listener, long domain_id, long category_id) {

        String request_url = url + domain_id + "/" + category_id;

        // Request a string response from the provided URL.
        StringRequest getRootCategoryRequest = new StringRequest(Request.Method.GET, request_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        category = handleCategoryResponse(response);
                        listener.onResponse(category);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onError(error.toString());
                    }
                }) {

            /*@Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("email", user.getEmail());
                params.put("password", user.getPassword());
                return params;
            }*/

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("X-Authorization", userApiKey);
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(getRootCategoryRequest);
    }

    private Category handleCategoryResponse(String response) {
        category = null;
        try {
            JSONObject root_category = new JSONObject(response);
            boolean isError = root_category.getBoolean("error");
            if(!isError) {
                category = new Category();
                category.setId(root_category.getLong("IdCategory"));
                category.setName(root_category.getString("Name"));
                category.setDescription(root_category.getString("Description"));
                category.setImageURL(root_category.getString("Image"));

                JSONArray sub_categories = root_category.getJSONArray("SubCategories");
                Category sub_category;
                for (int i = 0; i < sub_categories.length(); i++) {
                    sub_category = new Category();
                    sub_category.setId(sub_categories.getJSONObject(i).getLong("IdCategory"));
                    sub_category.setName(sub_categories.getJSONObject(i).getString("Name"));
                    sub_category.setDescription(sub_categories.getJSONObject(i).getString("Description"));
                    sub_category.setImageURL(sub_categories.getJSONObject(i).getString("Image"));
                    category.getSubcategories().add(sub_category);
                }

                JSONArray items = root_category.getJSONArray("Items");
                Item item;
                for (int i = 0; i < items.length(); i++) {
                    item = new Item();
                    item.setId(items.getJSONObject(i).getLong("IdItem"));
                    item.setName(items.getJSONObject(i).getString("Name"));
                    item.setDescription(items.getJSONObject(i).getString("Description"));
                    item.setImageURL(items.getJSONObject(i).getString("Image"));
                    category.getItems().add(item);
                }
            }

        } catch (JSONException e) {
            category = null;
            e.printStackTrace();
        }
        return category;
    }

}
