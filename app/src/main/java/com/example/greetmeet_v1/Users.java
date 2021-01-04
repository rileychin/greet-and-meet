package com.example.greetmeet_v1;

import java.util.ArrayList;

public class Users {
    private String Name, Email, DateTime;
    private ArrayList<String> Categories, GroupsCreated, GroupsJoined;
    private Boolean  profilePic;
    private String id;
    private Group bookmarked;
    private ArrayList<Group> attending;

    public Users(){}

    public Users(String name,String id) {
        this.Name = name;
        this.id = id;
//        this.Email = email;
//        Categories = categories;
//        GroupsCreated = groupsCreated;
//        GroupsJoined = groupsJoined;
//        DateTime = datetime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        this.Email = email;
    }

    public ArrayList<String> getCategories() {
        return Categories;
    }

    public void setCategories(ArrayList<String> categories) {
        Categories = categories;
    }

    public ArrayList<String> getGroupsCreated() {
        return GroupsCreated;
    }

    public void setGroupsCreated(ArrayList<String> groupsCreated) {
        GroupsCreated = groupsCreated;
    }

    public ArrayList<String> getGroupsJoined() {
        return GroupsJoined;
    }

    public void setGroupsJoined(ArrayList<String> groupsJoined) {
        GroupsJoined = groupsJoined;
    }

    public String getDateTime() { return DateTime; }

    public void setDateTime(String dateTime) { DateTime = dateTime; }

    public Boolean getProfilePic() { return profilePic; }

    public void setProfilePic(Boolean profilePic) { this.profilePic = profilePic; }
}
