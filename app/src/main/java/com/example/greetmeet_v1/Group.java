package com.example.greetmeet_v1;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class Group {
    private String groupName;
    private String groupDesc;
    private String groupLoc;
    private String categorySpinner;
    private String groupImgURL;
    private String groupId;
    private ArrayList<String> attendees;
    private boolean deleted;
    private Users hostid;
    private ArrayList<Integer> dateCreated;


    public Group(){

    }


    public ArrayList<Integer> getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(ArrayList<Integer> dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Users getHost() {
        return hostid;
    }

    public void setHost(Users host) {
        this.hostid = host;
    }

    public ArrayList<String> getAttendees() {
        return attendees;
    }

    public void setAttendees(ArrayList<String> attendees) {
        this.attendees = attendees;
    }

    //default false
    public boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupImgURL() {
        return groupImgURL;
    }

    public void setGroupImgURL(String imageURL) {
        this.groupImgURL = imageURL;
    }

    public String getCategorySpinner() {
        return categorySpinner;
    }

    public void setCategorySpinner(String categorySpinner) {
        this.categorySpinner = categorySpinner;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupDesc() {
        return groupDesc;
    }

    public void setGroupDesc(String groupDesc) {
        this.groupDesc = groupDesc;
    }

    public String getGroupLoc() {
        return groupLoc;
    }

    public void setGroupLoc(String groupLoc) {
        this.groupLoc = groupLoc;
    }
}
