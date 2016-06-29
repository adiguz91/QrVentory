package com.example.is2.test2qrventory.connection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.example.is2.test2qrventory.CreateCategoryActivity;
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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Adrian on 15.06.2016.
 */
public class CategoryAccess {

    private String url ="http://qrventory.square7.ch/v1/category";
    private String userApiKey;
    private Category category;
    String category_json_body;

    public CategoryAccess(String userApiKey) {
        this.userApiKey = userApiKey;
    }

    public void getRootCategory(final VolleyResponseListener listener, long domain_id, long category_id) {

        String request_url = url + "/" + domain_id + "/" + category_id;

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
                    item.setIsQR((items.getJSONObject(i).getInt("IsQR") == 1) ? true : false);
                    item.setQRcodeURL(items.getJSONObject(i).getString("QRcode"));
                    item.setBarcodeURL(items.getJSONObject(i).getString("Barcode"));
                    category.getItems().add(item);
                }
            }

        } catch (JSONException e) {
            category = null;
            e.printStackTrace();
        }
        return category;
    }

    private JSONObject toJsonParser(Category category, long domain_id, int category_parent_id) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Name", category.getName());
            jsonObject.put("Description", category.getDescription());
            jsonObject.put("Image", category.getImageURL());
            jsonObject.put("Domain_IdDomain", domain_id);
            jsonObject.put("IdCategoryParent", category_parent_id);
            //JSONObject jsonObject2 = new JSONObject().put("answers", jsonObject);
            //jsonObject2.put("user_id", 12);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public void addCategory(final VolleyResponseListener listener, final Category category, long domain_id, int category_parent_id) {

        JSONObject category_json = toJsonParser(category, domain_id, category_parent_id);
        category_json_body = category_json.toString();

        // Request a string response from the provided URL.
        StringRequest addDomainRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Category category_new = null;
                        try {
                            JSONObject json_response = new JSONObject(response);
                            boolean isError = json_response.getBoolean("error");

                            if(!isError) {
                                long category_id = json_response.getLong("IdCategory");
                                //String image_url = json_response.getString("Image");

                                if(category_id > 0) {
                                    category_new = category;
                                    category_new.setId(category_id);
                                    //category_new.setImageURL(image_url);
                                } else {
                                    category_new = null;
                                }
                            }

                            //byte[] decodedString = Base64.decode(json_response.getString("image"), Base64.DEFAULT);
                            //Bitmap bitmap_decoded = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            //user.setImage(bitmap_decoded);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        listener.onResponse(category_new);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //textViewResponse = (TextView) findViewById(R.id.textViewResponse);
                        //textViewResponse.setText("That didn't work!");
                        listener.onError(error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("X-Authorization", userApiKey);
                return params;
            }

            @Override
            public String getBodyContentType() {
                return String.format("application/json; charset=utf-8");
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return category_json_body == null ? null : category_json_body.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                            category_json_body, "utf-8");
                    return null;
                }
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(addDomainRequest);
    }

}
