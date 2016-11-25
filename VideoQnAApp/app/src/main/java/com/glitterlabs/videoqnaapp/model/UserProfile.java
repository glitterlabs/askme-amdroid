package com.glitterlabs.videoqnaapp.model;

/**
 * Created by GlitterLabs on 11-11-2016.
 */

public class UserProfile {

    String userUId;
    String fName;
    String lName;
    String phoneNumber;
    String userType;
    String profileUrl;


    public UserProfile() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public UserProfile( String fName, String lName, String phoneNumber, String userType,String profileUrl,String userUId) {
        this.userUId = userUId;
        this.fName = fName;
        this.lName = lName;
        this.phoneNumber = phoneNumber;
        this.userType = userType;
        this.profileUrl = profileUrl;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }







}
