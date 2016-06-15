package com.example.is2.test2qrventory.connection;

import com.example.is2.test2qrventory.model.User;

/**
 * Created by Adrian on 15.06.2016.
 */
public interface VolleyResponseListener {
    void onError(String message);
    void onResponse(Object response);
}
