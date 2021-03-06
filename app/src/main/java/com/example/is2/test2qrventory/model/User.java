package com.example.is2.test2qrventory.model;


import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Adrian on 06.06.2016.
 */
public class User implements Parcelable {

    // Parcelable ---------------

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * Storing the Student data to Parcel object
     **/
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(IdUser);
        dest.writeString(Email);
        dest.writeString(Password);
        dest.writeString(Firstname);
        dest.writeString(Lastname);
        //dest.writeParcelable(Image, flags);
        dest.writeString(ImageURL);
        dest.writeString(ApiKey);
        //dest.writeList(Domains);
    }

    /**
     * Retrieving Student data from Parcel object
     * This constructor is invoked by the method createFromParcel(Parcel source) of
     * the object CREATOR
     **/
    private User(Parcel in){
        this.IdUser = in.readLong();
        this.Email = in.readString();
        this.Password = in.readString();
        this.Firstname = in.readString();
        this.Lastname = in.readString();
        //this.Image = in.readParcelable(null);
        this.ImageURL = in.readString();
        this.ApiKey = in.readString();
        //this.Domains = in.readArrayList(null);
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {

        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    // --------------------------

    public User() {

    }

    public User(String email, String password) {
        this.Email = email;
        this.Password = password;
    }

    private long IdUser;
    private String Email;
    private String Password;
    private String Firstname;
    private String Lastname;
    private String ImageURL;
    private String ApiKey;
    //private List<Domain> Domains;

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getLastname() {
        return Lastname;
    }

    public void setLastname(String lastname) {
        Lastname = lastname;
    }

    public String getImageURL() {
        return ImageURL;
    }

    public void setImageURL(String image) {
        ImageURL = image;
    }

    public String getApiKey() {
        return ApiKey;
    }

    public void setApiKey(String apiKey) {
        ApiKey = apiKey;
    }

    public String getEmail() {
        return Email;
    }
    public void setEmail(String email) {
        Email = email;
    }

    public String getFirstname() {
        return Firstname;
    }
    public void setFirstname(String firstname) {
        Firstname = firstname;
    }

    public long getIdUser() {
        return IdUser;
    }

    public void setIdUser(long idUser) {
        IdUser = idUser;
    }

    /*public List<Domain> getDomains() {
        return Domains;
    }*/

    /*public void setDomains(List<Domain> domains) {
        Domains = domains;
    }*/
}