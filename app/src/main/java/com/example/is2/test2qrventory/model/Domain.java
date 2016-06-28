package com.example.is2.test2qrventory.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adrian on 15.06.2016.
 */
public class Domain implements Parcelable {

    public Domain() {

    }

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
        dest.writeLong(IdDomain);
        dest.writeLong(IdCategoryRoot);
        dest.writeString(Name);
        dest.writeString(Description);
        dest.writeString(ImageURL);
    }

    /**
     * Retrieving Student data from Parcel object
     * This constructor is invoked by the method createFromParcel(Parcel source) of
     * the object CREATOR
     **/
    private Domain(Parcel in){
        this.IdDomain = in.readLong();
        this.IdCategoryRoot = in.readLong();
        this.Name = in.readString();
        this.Description = in.readString();
        this.ImageURL = in.readString();
    }

    public static final Parcelable.Creator<Domain> CREATOR = new Parcelable.Creator<Domain>() {

        @Override
        public Domain createFromParcel(Parcel source) {
            return new Domain(source);
        }

        @Override
        public Domain[] newArray(int size) {
            return new Domain[size];
        }
    };

    // --------------------------

    private long IdDomain;
    private long IdCategoryRoot;
    private String Name;
    private String Description;
    private String ImageURL;
    //private List<Category> categorys = new ArrayList();

    public long getIdCategoryRoot() {
        return IdCategoryRoot;
    }

    public void setIdCategoryRoot(long idCategoryRoot) {
        IdCategoryRoot = idCategoryRoot;
    }

    public long getIdDomain() {
        return IdDomain;
    }

    public void setIdDomain(long idDomain) {
        IdDomain = idDomain;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getImageURL() {
        return ImageURL;
    }

    public void setImageURL(String image) {
        ImageURL = image;
    }

    /*public List<Category> getCategorys() {
        return categorys;
    }*/

}
