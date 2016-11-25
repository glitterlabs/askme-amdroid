package com.glitterlabs.videoqnaapp.model;

public class SingaltonUser {
    String userType;
    String strUID;

    //create an object of SingleObject
    private static SingaltonUser instance = new SingaltonUser();

    //make the constructor private so that this class cannot be
    //instantiated
    private SingaltonUser(){}

    //Get the only object available
    public static SingaltonUser getInstance(){
        return instance;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getStrUID() {
        return strUID;
    }

    public void setStrUID(String strUID) {
        this.strUID = strUID;
    }

}
