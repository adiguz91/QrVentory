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
import com.example.is2.test2qrventory.model.Event;
import com.example.is2.test2qrventory.model.Item;
import com.example.is2.test2qrventory.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Adrian on 15.06.2016.
 */
public class EventAccess {

    private String url ="http://qrventory.square7.ch/v1/event";
    private String userApiKey;
    private List<Event> events = new ArrayList<>();

    public EventAccess(String userApiKey) {
        this.userApiKey = userApiKey;
    }

    public void getEventsFromDomain(final VolleyResponseListener listener, long domain_id) {

        String request_url = url + "/" + domain_id;

        // Request a string response from the provided URL.
        StringRequest getRootCategoryRequest = new StringRequest(Request.Method.GET, request_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        events = handleEventsResponse(response);
                        listener.onResponse(events);
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

    public void getAllEvents(final VolleyResponseListener listener) {

        // Request a string response from the provided URL.
        StringRequest getRootCategoryRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        events = handleEventsResponse(response);
                        listener.onResponse(events);
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

    private Date toDate(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currentDate;
        try {
            currentDate = formatter.parse(date);
        } catch (Exception ex) {
            currentDate = null;
        }
        return currentDate;
    }

    private List<Event> handleEventsResponse(String response) {

        try {
            JSONObject response_json = new JSONObject(response);
            boolean isError = response_json.getBoolean("error");
            if(!isError) {

                JSONArray events_response = response_json.getJSONArray("Events");
                Event event;
                for (int i = 0; i < events_response.length(); i++) {
                    event = new Event();
                    event.setId(events_response.getJSONObject(i).getLong("IdEvent"));
                    event.setIdDomain(events_response.getJSONObject(i).getLong("Domain_IdDomain"));
                    event.setName(events_response.getJSONObject(i).getString("Name"));
                    event.setDescription(events_response.getJSONObject(i).getString("Description"));
                    event.setImageURL(events_response.getJSONObject(i).getString("Image"));
                    event.setAutoStart((events_response.getJSONObject(i).getInt("AutoStart") == 1) ? true : false);
                    event.setStatus(events_response.getJSONObject(i).getInt("Status"));
                    event.setStartDate(toDate(events_response.getJSONObject(i).getString("StartDate")));
                    event.setEndDate(toDate(events_response.getJSONObject(i).getString("EndDate")));
                    events.add(event);
                }
            }

        } catch (JSONException e) {
            events = null;
            e.printStackTrace();
        }
        return events;
    }

}
