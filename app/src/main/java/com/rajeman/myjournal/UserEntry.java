package com.rajeman.myjournal;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class UserEntry {

    Map<String, Object> timeStamp;
    String title;
    String summary;
    String location;
    String entryId;
    String imageLink;

    public UserEntry() {
    }

    public UserEntry( String title, String summary, String location,  String imageLink) {

        Map<String, Object> currentTime = new HashMap<>();
        currentTime.put("timestamp", ServerValue.TIMESTAMP);
        this.timeStamp = currentTime;
        this.title = title;
        this.summary = summary;
        this.location = location;
        this. imageLink = imageLink;
    }


    public Map<String, Object> getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Map<String, Object> timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Exclude
    public long getTimestampLong(){
        return (long)timeStamp.get("timestamp");
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEntryId() {
        return entryId;
    }

    public void setEntryId(String entryId) {
        this.entryId = entryId;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }
}
