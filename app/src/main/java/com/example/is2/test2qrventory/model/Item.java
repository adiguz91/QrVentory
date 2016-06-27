package com.example.is2.test2qrventory.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Adrian on 15.06.2016.
 */
public class Item implements Parcelable {

    public Item() {

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
        dest.writeLong(Id);
        dest.writeString(Name);
        dest.writeByte((byte) (IsQR ? 1 : 0)); //if myBoolean == true, byte == 1
        dest.writeString(QRcodeURL);
        dest.writeString(BarcodeURL);
        dest.writeString(Description);
        dest.writeString(ImageURL);
    }

    /**
     * Retrieving Student data from Parcel object
     * This constructor is invoked by the method createFromParcel(Parcel source) of
     * the object CREATOR
     **/
    private Item(Parcel in){
        this.Id = in.readLong();
        this.Name = in.readString();
        this.IsQR = in.readByte() != 0; //myBoolean == true if byte != 0
        this.QRcodeURL = in.readString();
        this.BarcodeURL = in.readString();
        this.Description = in.readString();
        this.ImageURL = in.readString();
    }

    public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {

        @Override
        public Item createFromParcel(Parcel source) {
            return new Item(source);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    // --------------------------

    private long Id;
    private String Name;
    private Boolean IsQR;
    private String QRcodeURL;
    private String BarcodeURL;
    private String Description;
    private String ImageURL;

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Boolean getIsQR() {
        return IsQR;
    }

    public void setIsQR(Boolean QR) {
        IsQR = QR;
    }

    public String getBarcodeURL() {
        return BarcodeURL;
    }

    public void setBarcodeURL(String barcodeURL) {
        BarcodeURL = barcodeURL;
    }

    public String getQRcodeURL() {
        return QRcodeURL;
    }

    public void setQRcodeURL(String QRcodeURL) {
        this.QRcodeURL = QRcodeURL;
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

    public void setImageURL(String imageURL) {
        ImageURL = imageURL;
    }
}
