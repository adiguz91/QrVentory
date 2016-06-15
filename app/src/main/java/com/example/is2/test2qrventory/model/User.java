package com.example.is2.test2qrventory.model;


import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

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
        dest.writeParcelable(Image, flags);
        dest.writeString(ApiKey);
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
        this.Image = in.readParcelable(null);
        this.ApiKey = in.readString();
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

    public User(String email, String password) {
        this.Email = email;
        this.Password = password;
    }

    private static long IdUser;
    private static String Email;
    private static String Password;
    private static String Firstname;
    private static String Lastname;
    private static Bitmap Image;
    private static String ApiKey;

    public static String getPassword() {
        return Password;
    }

    public static void setPassword(String password) {
        Password = password;
    }

    public static String getLastname() {
        return Lastname;
    }

    public static void setLastname(String lastname) {
        Lastname = lastname;
    }

    public static Bitmap getImage() {
        return Image;
    }

    public static void setImage(Bitmap image) {
        Image = image;
    }

    public static String getApiKey() {
        return ApiKey;
    }

    public static void setApiKey(String apiKey) {
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

    public static long getIdUser() {
        return IdUser;
    }

    public static void setIdUser(long idUser) {
        IdUser = idUser;
    }

}