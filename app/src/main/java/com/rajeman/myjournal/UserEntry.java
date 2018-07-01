package com.rajeman.myjournal;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
@IgnoreExtraProperties
public class UserEntry {

    private Map<String, Object> timeStamp;
    private String title;
    private String story;
    private String location;
    private String entryId;
    private String imageLink;

    public UserEntry() {
    }

    public UserEntry( String title, String story, String location,  String imageLink) {

        Map<String, Object> currentTime = new HashMap<>();
        currentTime.put("timestamp", ServerValue.TIMESTAMP);
        this.timeStamp = currentTime;
        this.title = title;
        this.story = story;
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

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
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
