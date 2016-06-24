package com.example.is2.test2qrventory.model;

/**
 * Created by Adrian on 15.06.2016.
 */
public class Item {

    public Item() {

    }

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

    public Boolean getQR() {
        return IsQR;
    }

    public void setQR(Boolean QR) {
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
