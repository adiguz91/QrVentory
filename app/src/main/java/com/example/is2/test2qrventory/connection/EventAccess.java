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
import com.example.is2.test2qrventory.model.Category;
import com.example.is2.test2qrventory.model.Domain;
import com.example.is2.test2qrventory.model.Event;
import com.example.is2.test2qrventory.model.Item;
import com.example.is2.test2qrventory.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
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
    private String url_event_status = "http://qrventory.square7.ch/v1/event-status";
    private String userApiKey;
    private List<Event> events = new ArrayList<>();
    private String event_json_body;

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

    public void setEventItemChecked(final VolleyResponseListener listener, long event_id, long item_id) {

        String url_new = url + "/" + event_id + "/" + item_id;

        // Request a string response from the provided URL.
        StringRequest getRootCategoryRequest = new StringRequest(Request.Method.GET, url_new,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        boolean result = false;
                        try {
                            JSONObject response_json = new JSONObject(response);
                            boolean isError = response_json.getBoolean("error");
                            if(!isError) {
                                result = true;
                            }
                        } catch (JSONException e) {
                            result = false;
                            e.printStackTrace();
                        }
                        listener.onResponse(result);
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

    public void updateEventStatus(final VolleyResponseListener listener, long event_id, int status) {

        String url_new = url_event_status + "/" + event_id + "/" + status;

        // Request a string response from the provided URL.
        StringRequest updateEventStatusRequest = new StringRequest(Request.Method.GET, url_new,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        boolean result = false;
                        try {
                            JSONObject json_response = new JSONObject(response);
                            boolean isError = json_response.getBoolean("error");

                            if(!isError) {
                                result = true;
                            }

                        } catch (JSONException e) {
                            result = false;
                            e.printStackTrace();
                        }
                        listener.onResponse(result);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onError(error.toString());
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("X-Authorization", userApiKey);
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(updateEventStatusRequest);
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

    private JSONObject toJsonParser(Event event) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Name", event.getName());
            jsonObject.put("Description", event.getDescription());
            jsonObject.put("Image", event.getImageURL());
            jsonObject.put("StartDate", dateToString(event.getStartDate()));
            jsonObject.put("EndDate", dateToString(event.getEndDate()));
            jsonObject.put("Status", event.getStatus());
            jsonObject.put("AutoStart", (event.getAutoStart() == true) ? 1 : 0);
            jsonObject.put("Domain_IdDomain", event.getIdDomain());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public void addEvent(final VolleyResponseListener listener, final Event event) {

        JSONObject event_json = toJsonParser(event);
        event_json_body = event_json.toString();

        // Request a string response from the provided URL.
        StringRequest addEventRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Event event_new = null;
                        try {
                            JSONObject json_response = new JSONObject(response);
                            boolean isError = json_response.getBoolean("error");

                            if(!isError) {
                                long event_id = json_response.getLong("IdEvent");
                                //String image_url = json_response.getString("Image");

                                if(event_id > 0) {
                                    event_new = event;
                                    event_new.setId(event_id);
                                    //category_new.setImageURL(image_url);
                                } else {
                                    event_new = null;
                                }
                            }

                            //byte[] decodedString = Base64.decode(json_response.getString("image"), Base64.DEFAULT);
                            //Bitmap bitmap_decoded = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            //user.setImage(bitmap_decoded);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        listener.onResponse(event_new);
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
                    return event_json_body == null ? null : event_json_body.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                            event_json_body, "utf-8");
                    return null;
                }
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(addEventRequest);
    }

    private String dateToString(Date date) {
        String date_string = "";
        if(date != null) {
            date_string = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        }
        return date_string;
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
