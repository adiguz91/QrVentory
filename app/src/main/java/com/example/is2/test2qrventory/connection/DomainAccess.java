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
import com.example.is2.test2qrventory.LoginActivity;
import com.example.is2.test2qrventory.MainActivity;
import com.example.is2.test2qrventory.controller.AppController;
import com.example.is2.test2qrventory.model.Domain;
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
public class DomainAccess {

    private String url ="http://qrventory.square7.ch/v1/domain";
    private String userApiKey;
    private List<Domain> domain_list= new ArrayList<>();
    String domain_json_body;

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

    private JSONObject toJsonParser(Domain domain) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Name", domain.getName());
            jsonObject.put("Description", domain.getDescription());
            jsonObject.put("Image", domain.getImageURL());
            //JSONObject jsonObject2 = new JSONObject().put("answers", jsonObject);
            //jsonObject2.put("user_id", 12);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public void addDomain(final VolleyResponseListener listener, final Domain domain) {

        JSONObject domain_json = toJsonParser(domain);
        domain_json_body = domain_json.toString();

        // Request a string response from the provided URL.
        StringRequest addDomainRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Domain domain_new = null;
                        try {
                            JSONObject json_response = new JSONObject(response);
                            boolean isError = json_response.getBoolean("error");

                            if(!isError) {
                                long domain_id = json_response.getLong("IdDomain");
                                long category_root_id = json_response.getLong("IdCategoryRoot");
                                String image_url = json_response.getString("Image");

                                if(domain_id > 0) {
                                    domain_new = domain;
                                    domain_new.setIdDomain(domain_id);
                                    domain_new.setImageURL(image_url);
                                    domain_new.setIdCategoryRoot(category_root_id);
                                } else {
                                    domain_new = null;
                                }
                            }

                            //byte[] decodedString = Base64.decode(json_response.getString("image"), Base64.DEFAULT);
                            //Bitmap bitmap_decoded = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            //user.setImage(bitmap_decoded);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        listener.onResponse(domain_new);
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
                                return domain_json_body == null ? null : domain_json_body.getBytes("utf-8");
                            } catch (UnsupportedEncodingException uee) {
                                VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                                        domain_json_body, "utf-8");
                                return null;
                            }
                        }
                    };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(addDomainRequest);
    }
}
