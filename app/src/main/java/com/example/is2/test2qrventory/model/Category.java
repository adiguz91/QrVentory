package com.example.is2.test2qrventory.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adrian on 15.06.2016.
 */
public class Category {

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

    public void setImageURL(String imageURL) {
        ImageURL = imageURL;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public List<Category> getSubcategories() {
        return subcategories;
    }

    public void setSubcategories(List<Category> subcategories) {
        this.subcategories = subcategories;
    }

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    private long Id;
    private String Name;
    private String Description;
    private String ImageURL;
    private List<Item> items = new ArrayList<>();
    private List<Category> subcategories = new ArrayList<>();
}
