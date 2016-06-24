package com.example.is2.test2qrventory.model;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adrian on 15.06.2016.
 */
public class Domain {

    private long IdDomain;
    private long IdRootCategory;
    private String Name;
    private String Description;
    private Bitmap Image;
    private List<Category> categorys = new ArrayList();

    public long getIdRootCategory() {
        return IdRootCategory;
    }

    public void setIdRootCategory(long idRootCategory) {
        IdRootCategory = idRootCategory;
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

    public Bitmap getImage() {
        return Image;
    }

    public void setImage(Bitmap image) {
        Image = image;
    }

    public List<Category> getCategorys() {
        return categorys;
    }

}
