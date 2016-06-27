package com.example.is2.test2qrventory.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.EventLogTags;

import java.util.Date;

/**
 * Created by Adrian on 15.06.2016.
 */
public class Event implements Parcelable {

    public Event () {

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
        dest.writeLong((StartDate != null) ? StartDate.getTime() : 0);
        dest.writeLong((EndDate != null) ? EndDate.getTime() : 0);
        dest.writeString(Description);
        dest.writeString(ImageURL);
    }

    /**
     * Retrieving Student data from Parcel object
     * This constructor is invoked by the method createFromParcel(Parcel source) of
     * the object CREATOR
     **/
    private Event(Parcel in){
        this.Id = in.readLong();
        this.Name = in.readString();
        this.StartDate = new Date(in.readLong());
        this.EndDate = new Date(in.readLong());
        this.Description = in.readString();
        this.ImageURL = in.readString();
    }

    public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() {

        @Override
        public Event createFromParcel(Parcel source) {
            return new Event(source);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    // --------------------------

    private long Id;
    private String Name;
    private String Description;
    private Date StartDate;
    private Date EndDate;
    private String ImageURL;
    private int Status; // 0=inaktive, 1=started, 2=finished
    private boolean AutoStart;

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public boolean isAutoStart() {
        return AutoStart;
    }

    public void setAutoStart(boolean autoStart) {
        AutoStart = autoStart;
    }

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

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public Date getStartDate() {
        return StartDate;
    }

    public void setStartDate(Date startDate) {
        StartDate = startDate;
    }

    public Date getEndDate() {
        return EndDate;
    }

    public void setEndDate(Date endDate) {
        EndDate = endDate;
    }

    public String getImageURL() {
        return ImageURL;
    }

    public void setImageURL(String imageURL) {
        ImageURL = imageURL;
    }
}
