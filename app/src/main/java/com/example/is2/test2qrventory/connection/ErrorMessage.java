package com.example.is2.test2qrventory.connection;

/**
 * Created by Adrian on 22.06.2016.
 */
public class ErrorMessage {

    private boolean error;
    private String message;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
