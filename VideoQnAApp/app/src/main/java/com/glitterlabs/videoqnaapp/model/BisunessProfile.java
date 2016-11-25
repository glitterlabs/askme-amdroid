package com.glitterlabs.videoqnaapp.model;

/**
 * Created by GlitterLabs on 12-11-2016.
 */

public class BisunessProfile {
    private String strBusinessName;
    private String strBusinessAdd;
    private String strBusinessDec;
    private String strPhoneNumber;
    private String userUId;
    private String strImgUrl;
    private String latitude;
    private String langitude;
    private String userType;
    private String strBusinessZip;
    private String strUserType;

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getStrBusinessZip() {
        return strBusinessZip;
    }

    public void setStrBusinessZip(String strBusinessZip) {
        this.strBusinessZip = strBusinessZip;
    }

    public String getStrUserType() {
        return strUserType;
    }

    public void setStrUserType(String strUserType) {
        this.strUserType = strUserType;
    }

    public String getLangitude() {
        return langitude;
    }

    public void setLangitude(String langitude) {
        this.langitude = langitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getStrImgUrl() {
        return strImgUrl;
    }

    public void setStrImgUrl(String strImgUrl) {
        this.strImgUrl = strImgUrl;
    }

    public String getUserUId() {
        return userUId;
    }

    public void setUserUId(String userUId) {
        this.userUId = userUId;
    }

    public String getStrPhoneNumber() {
        return strPhoneNumber;
    }

    public void setStrPhoneNumber(String strPhoneNumber) {
        this.strPhoneNumber = strPhoneNumber;
    }

    public String getStrBusinessDec() {
        return strBusinessDec;
    }

    public void setStrBusinessDec(String strBusinessDec) {
        this.strBusinessDec = strBusinessDec;
    }

    public String getStrBusinessAdd() {
        return strBusinessAdd;
    }

    public void setStrBusinessAdd(String strBusinessAdd) {
        this.strBusinessAdd = strBusinessAdd;
    }

    public String getStrBusinessName() {
        return strBusinessName;
    }

    public void setStrBusinessName(String strBusinessName) {
        this.strBusinessName = strBusinessName;
    }

    public BisunessProfile() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public BisunessProfile(String latitude,String langitude,String strBusinessName,String strBusinessAdd,String strBusinessZip,String strBusinessDec,String strPhoneNumber,String userUId,String strImgUrl,String strUserType) {

        this.latitude = latitude;
        this.langitude = langitude;
        this.strBusinessName = strBusinessName;
        this.strBusinessAdd = strBusinessAdd;
        this.strBusinessZip = strBusinessZip;
        this.strBusinessDec = strBusinessDec;
        this.strPhoneNumber = strPhoneNumber;
        this.strImgUrl = strImgUrl;
        this.userUId = userUId;
        this.strUserType = strUserType;
    }
}
