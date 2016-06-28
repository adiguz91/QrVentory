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
public class DomainAccess {

    private String url ="http://qrventory.square7.ch/v1/domain";
    private String userApiKey;
    private List<Domain> domain_list= new ArrayList<>();

    public DomainAccess(String userApiKey) {
        this.userApiKey = userApiKey;
    }
    public void getDomains(final VolleyResponseListener listener) {

        // Request a string response from the provided URL.
        StringRequest getDomainsRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Log.d("DEBUG", response);

                        JSONObject json_response = null;
                        try {
                            json_response = new JSONObject(response);
                            //IdUser = person.getLong("id");
                            boolean isError = json_response.getBoolean("error");

                            if(!isError) {
                                JSONArray domains = json_response.getJSONArray("Domains");

                                Domain domain = null;
                                for (int i = 0; i < domains.length(); i++) {
                                    domain = new Domain();
                                    domain.setIdDomain(domains.getJSONObject(i).getLong("IdDomain"));
                                    domain.setName(domains.getJSONObject(i).getString("Name"));
                                    domain.setDescription(domains.getJSONObject(i).getString("Description"));
                                    domain.setImageURL(domains.getJSONObject(i).getString("Image"));

                                    JSONObject category_root = domains.getJSONObject(i).getJSONObject("CategoryRoot");
                                    domain.setIdCategoryRoot(category_root.getLong("IdCategory"));

                                    domain_list.add(domain);
                                }
                            }

                            //byte[] decodedString = Base64.decode(json_response.getString("image"), Base64.DEFAULT);
                            //Bitmap bitmap_decoded = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            //user.setImage(bitmap_decoded);

                            listener.onResponse(domain_list);

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
        AppController.getInstance().addToRequestQueue(getDomainsRequest);
    }
}
