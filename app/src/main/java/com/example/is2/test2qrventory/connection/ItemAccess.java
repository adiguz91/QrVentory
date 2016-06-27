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
public class ItemAccess {

    private String url ="http://qrventory.square7.ch/v1/item/";
    private String userApiKey;
    private Item item;

    public ItemAccess(String userApiKey, String item_id) {
        this.userApiKey = userApiKey;
        this.url += item_id;
    }

    public void getItem(final VolleyResponseListener listener) {

        // Request a string response from the provided URL.
        StringRequest getItemRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Log.d("DEBUG", response);

                        JSONObject item_response = null;
                        try {
                            item_response = new JSONObject(response);

                            item.setId(item_response.getLong("IdItem"));
                            item.setName(item_response.getString("Name"));
                            item.setDescription(item_response.getString("Description"));
                            item.setBarcodeURL(item_response.getString("Barcode"));
                            item.setQRcodeURL(item_response.getString("QRcode"));
                            item.setImageURL(item_response.getString("Image"));
                            item.setIsQR((item_response.getInt("IsQR") == 1) ? true : false);

                            //byte[] decodedString = Base64.decode(json_response.getString("image"), Base64.DEFAULT);
                            //Bitmap bitmap_decoded = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            //user.setImage(bitmap_decoded);

                            listener.onResponse(item);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //textViewResponse = (TextView) findViewById(R.id.textViewResponse);
                        //textViewResponse.setText("That didn't work!");
                        listener.onError(error.toString());
                    }
                }){
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

        // Add the request to the RequestQueue.
        //MainActivity.getInstance().getRequestQueue().add(stringRequest);

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(getItemRequest);
    }


}
