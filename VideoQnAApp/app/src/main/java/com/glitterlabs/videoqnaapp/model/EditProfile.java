package com.glitterlabs.videoqnaapp.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class EditProfile {
    String fName;
    String lName;
    String phoneNumber;
    String profileUrl;
    String userType;
    String strBusinessAdd,strBusinessDec,strBusinessName,strBusinessZip,strImgUrl,strPhoneNumber,userUId;
    private String strlatitude;

    public String getStrlangitude() {
        return strlangitude;
    }

    public void setStrlangitude(String strlangitude) {
        this.strlangitude = strlangitude;
    }

    public String getStrlatitude() {
        return strlatitude;
    }

    public void setStrlatitude(String strlatitude) {
        this.strlatitude = strlatitude;
    }

    private String strlangitude;

    public String getUserType() {
        return userType;
    }

    public EditProfile(){

    }

    public EditProfile(String fName,String lName,String phoneNumber,String userType,String profileUrl,String userUId){
        this.fName = fName;
        this.lName = lName;
        this.phoneNumber = phoneNumber;
        this.userType = userType;
        this.profileUrl = profileUrl;
        this.userUId = userUId;
    }

    public EditProfile(String strlatitude,String strlangitude,String strBusinessName,String strPhoneNumber,String strBusinessAdd,String strBusinessZip,String strBusinessDec,String strImgUrl,String userType,String userUId){
        this.strBusinessAdd = strBusinessAdd;
        this.strBusinessName = strBusinessName;
        this.strBusinessDec = strBusinessDec;
        this.strBusinessZip = strBusinessZip;
        this.strPhoneNumber = strPhoneNumber;
        this.userType = userType;
        this.strImgUrl = strImgUrl;
        this.userUId = userUId;
        this.strlangitude = strlangitude;
        this.strlatitude = strlatitude;
    }

    public EditProfile(String strBusinessName,String strPhoneNumber,String strBusinessAdd,String strBusinessZip,String strBusinessDec,String strImgUrl,String userUId,String userType){
        this.strBusinessAdd = strBusinessAdd;
        this.strBusinessName = strBusinessName;
        this.strBusinessDec = strBusinessDec;
        this.strBusinessZip = strBusinessZip;
        this.strPhoneNumber = strPhoneNumber;
        this.userUId = userUId;
        this.strImgUrl = strImgUrl;
        this.userType = userType;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("fName", fName);
        result.put("lName", lName);
        result.put("phoneNumber", phoneNumber);
        result.put("profileUrl", profileUrl);
        result.put("userType",userType);
        result.put("userUId",userUId);
        return result;
    }

    @Exclude
    public Map<String, Object> toMapB() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("strBusinessAdd", strBusinessAdd);
        result.put("strBusinessDec", strBusinessDec);
        result.put("strBusinessName", strBusinessName);
        result.put("strBusinessZip", strBusinessZip);
        result.put("strImgUrl",strImgUrl);
        result.put("strPhoneNumber",strPhoneNumber);
        result.put("userType",userType);
        result.put("userUId",userUId);

        return result;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserUId() {
        return userUId;
    }

    public void setUserUId(String userUId) {
        this.userUId = userUId;
    }

   /* public String getStrUserType() {
        return strUserType;
    }

    public void setStrUserType(String strUserType) {
        this.strUserType = strUserType;
    }*/

    public String getStrPhoneNumber() {
        return strPhoneNumber;
    }

    public void setStrPhoneNumber(String strPhoneNumber) {
        this.strPhoneNumber = strPhoneNumber;
    }

    public String getStrImgUrl() {
        return strImgUrl;
    }

    public void setStrImgUrl(String strImgUrl) {
        this.strImgUrl = strImgUrl;
    }

    public String getStrBusinessZip() {
        return strBusinessZip;
    }

    public void setStrBusinessZip(String strBusinessZip) {
        this.strBusinessZip = strBusinessZip;
    }

    public String getStrBusinessName() {
        return strBusinessName;
    }

    public void setStrBusinessName(String strBusinessName) {
        this.strBusinessName = strBusinessName;
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

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }



}
